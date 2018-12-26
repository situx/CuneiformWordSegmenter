/*
 *
 *  *
 *  *  * This file is part of CuneiformWordSegmenter.
 *  *  *
 *  *  *   CuneiformWordSegmenter is free software: you can redistribute it and/or modify
 *  *  *     it under the terms of the GNU General Public License as published by
 *  *  *     the Free Software Foundation, either version 3 of the License, or
 *  *  *     (at your option) any later version.
 *  *  *
 *  *  *    CuneiformWordSegmenter is distributed in the hope that it will be useful,
 *  *  *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  *  *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  *  *     GNU General Public License for more details.
 *  *  *
 *  *  *     You should have received a copy of the GNU General Public License
 *  *  *     along with CuneiformWordSegmenter.  If not, see <http://www.gnu.org/licenses/>.
 *  *  *
 *  *
 *
 */

package de.unifrankfurt.cs.acoli.akkad.eval;

import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.util.enums.EvalResultType;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.ClassificationMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.EvaluationMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.FeatureSets;
import jsc.contingencytables.ContingencyTable2x2;
import jsc.contingencytables.FishersExactTest;
import org.apache.commons.math3.stat.inference.ChiSquareTest;

/**
 * Created by timo on 11.06.14.
 */
public class EvalResult implements Comparable<EvalResult>{

    private final DictHandling dictHandler;
    public Double chisquared;
    public ClassificationMethod classmethod;
    /**Counts the amount of correct segmentations the algorithm should predict.*/
    public Double correctSegmentations;
    public Double countmatches;
    /**Integer for counting the mismatched words.
     * (falsepositive+falsenegative)*/
    public Double countmisses;
    public Double errorterm=0.;
    public EvalResultType evalResultType;
    /**No Boundary set where no boundary should be.*/
    public Double falsenegative;
    /**No boundary set where a boundary should be.*/
    public Double falsepositive;
    public FeatureSets featureSet;
    public Double fisherTest;
    public EvaluationMethod method;
    public Double occurances=1.;
    public Double relCountMatches;
    public Double relCountMisses;
    public Double result=-1.;
    /**Integer for counting the mismatched words.*/
    public Double total,relTotal;
    /**Boundary set where no boundary should be.*/
    public Double truenegative;
    /**Boundary set where a boundary should be.*/
    public Double truepositive;

    public EvalResult(EvaluationMethod method,ClassificationMethod classificationMethod,DictHandling dictHandler){
        this.truenegative=0.;
        this.truepositive=0.;
        this.falsenegative=0.;
        this.falsepositive=0.;
        this.correctSegmentations=0.;
        this.total=0.;
        this.countmatches=0.;
        this.countmisses=0.;
        this.relTotal=0.;
        this.relCountMatches=0.;
        this.relCountMisses=0.;
        this.method=method;
        this.chisquared=0.;
        this.fisherTest=0.;
        this.classmethod=classificationMethod;
        this.dictHandler=dictHandler;
    }

    /** Calculate a p-value for Fisher's Exact Test. */
    public static double fisher(int a, int b, int c, int d, int test, double[] logFactorial) {
        if (a * d > b * c) {
            a = a + b; b = a - b; a = a - b;
            c = c + d; d = c - d; c = c - d;
        }
        if (a > d) { a = a + d; d = a - d; a = a - d; }
        if (b > c) { b = b + c; c = b - c; b = b - c; }

        int a_org = a;
        double p_sum = 0.0d;

        double p = fisherSub(a, b, c, d, logFactorial);
        double p_1 = p;

        while (a >= 0) {
            p_sum += p;
            if (a == 0) break;
            --a; ++b; ++c; --d;
            p = fisherSub(a, b, c, d, logFactorial);
        }
        if (test == 0) return p_sum;

        a = b; b = 0; c = c - a; d = d + a;
        p = fisherSub(a, b, c, d, logFactorial);

        while (p < p_1) {
            if (a == a_org) break;
            p_sum += p;
            --a; ++b; ++c; --d;
            p = fisherSub(a, b, c, d, logFactorial);
        }
        return p_sum;
    }

    private static double fisherSub(int a, int b, int c, int d, double[] logFactorial) {
        return Math.exp(logFactorial[a + b] +
                logFactorial[c + d] +
                logFactorial[a + c] +
                logFactorial[b + d] -
                logFactorial[a + b + c + d] -
                logFactorial[a] -
                logFactorial[b] -
                logFactorial[c] -
                logFactorial[d]);
    }

    @Override
    public int compareTo(final EvalResult evalResult) {
        return (this.classmethod.getShortname()+" "+this.method.getShortname()).compareTo(evalResult.classmethod.getShortname()+" "+evalResult.method.getShortname());
    }

    public Double getAccuracy(){
        Double result=((this.truepositive+this.truenegative)/(this.truepositive+this.truenegative+this.falsenegative+this.falsepositive))*100;
        return result.equals(Double.NaN)?0.:result;
    }

    public Double getChiSquared() {
        ChiSquareTest chisquare=new ChiSquareTest();
        return ((this.getMCC()/100)*(this.getMCC()/100))*this.total;
        /*ChiSquareTest chisquare=new ChiSquareTest();
        try {
            double allchars = dictHandler.getContinuationFollowsBoundary() + dictHandler.getContinuationFollowsContinuation() + dictHandler.getBoundariesFollowContinuations() + dictHandler.getBoundariesFollowBoundaries();
            this.chisquared = chisquare.chiSquareTest(new double[]{this.truepositive + this.falsenegative, 1., this.falsepositive + this.truenegative, 1.}, new long[]{this.truepositive.longValue(), this.falsepositive.longValue(), this.truenegative.longValue(), this.falsenegative.longValue()});
        }catch (NotStrictlyPositiveException e){

        }
        return this.chisquared;*/
    }

    public Double getCorrectSegmentations() {
        return correctSegmentations;
    }

    public void setCorrectSegmentations(final Double correctSegmentations) {
        this.correctSegmentations = correctSegmentations;
    }

    public Double getCountmatches() {
        return countmatches;
    }

    public void setCountmatches(final Double countmatches) {
        this.countmatches = countmatches;
        this.relCountMatches=(this.countmatches/total)*100;
    }

    public Double getCountmisses() {
        return countmisses;
    }

    public void setCountmisses(final Double countmisses) {
        this.countmisses = countmisses;
        this.relCountMisses=(this.countmisses/total)*100;
    }

    public DictHandling getDictHandler() {
        return dictHandler;
    }

    public Double getFScore(){
        Double precision=this.getPrecision();
        Double recall=this.getRecall();
        Double result=(2*precision*recall)/(precision+recall);
        return result.equals(Double.NaN)?0.:result;
    }

    public Double getFalsenegative() {
        return falsenegative;
    }

    public void setFalsenegative(final Double falsenegative) {
        this.falsenegative = falsenegative;
    }

    public Double getFalsepositive() {
        return falsepositive;
    }

    public void setFalsepositive(final Double falsepositive) {
        this.falsepositive = falsepositive;
    }

    public Double getFisher() {
        try {
            jsc.contingencytables.FishersExactTest fishersExactTest = new FishersExactTest(new ContingencyTable2x2(this.truepositive.intValue(), this.falsepositive.intValue(), this.truenegative.intValue(), this.falsenegative.intValue()));
            this.fisherTest = fishersExactTest.getApproxSP();
        }catch(IllegalArgumentException e){

        }
        return this.fisherTest*100;
    }

    public Double getGScore(){
        Double precision=this.getPrecision();
        Double recall=this.getRecall();
        Double result=Math.sqrt(precision*recall);
        return result.equals(Double.NaN)?0.:result;
    }

    public Double getMCC(){
        Double result=((this.truepositive*this.truenegative)-(this.falsepositive*this.falsenegative))/(Math.sqrt((this.truepositive+this.falsepositive)*(this.truepositive+this.falsenegative)*(this.truenegative+this.falsepositive)*(this.truenegative+this.falsenegative)));
        System.out.println(this.method.toString()+" MCC: "+((this.truepositive*this.truenegative)-(this.falsepositive*this.falsenegative))+"/"+(Math.sqrt((this.truepositive+this.falsepositive)*(this.truepositive+this.falsenegative)*(this.truenegative+this.falsepositive)*(this.truenegative+this.falsenegative))+"="+result));
        return result*100;
    }

    public Double getNPV(){
        Double result=(this.truenegative/(this.truenegative+this.falsenegative))*100;
        return result.equals(Double.NaN)?0.:result;
    }

    public Double getNormalizedMCC(){
        Double result=((this.truepositive*this.truenegative)-(this.falsepositive*this.falsenegative))/(Math.sqrt((this.truepositive+this.falsepositive)*(this.truepositive+this.falsenegative)*(this.truenegative+this.falsepositive)*(this.truenegative+this.falsenegative)));
        System.out.println(this.method.toString()+" MCC: "+((this.truepositive*this.truenegative)-(this.falsepositive*this.falsenegative))+"/"+(Math.sqrt((this.truepositive+this.falsepositive)*(this.truepositive+this.falsenegative)*(this.truenegative+this.falsepositive)*(this.truenegative+this.falsenegative))+"="+result));
        if(result>100){
            result%=100;//(result/this.total)*100;
        }else if(result<-100){
            result%=-100;
            //result=(result/this.total)*100;
        }
        return result.equals(Double.NaN)?0.:(result+100)/2;
    }

    public Double getPrecision(){
        Double result=(this.truepositive/(this.truepositive+this.falsepositive))*100;
        return result.equals(Double.NaN)?0.:result;
    }

    public Double getRecall(){
        Double result=(this.truepositive/(this.truepositive+this.falsenegative))*100;
        return result.equals(Double.NaN)?0.:result;
    }

    public Double getResult() {
        return result;
    }

    public void setResult(final Double result) {
        this.result = result;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(final Double total) {
        this.total = total;
        this.relTotal=100.;
    }

    public Double getTruenegative() {
        return truenegative;
    }

    public void setTruenegative(final Double truenegative) {
        this.truenegative = truenegative;
    }

    public Double getTruepositive() {
        return truepositive;
    }

    public void setTruepositive(final Double truepositive) {
        this.truepositive = truepositive;
    }

    public Double normalize(Double value,Double max){
        return (value/max)*100;
    }

    /**
     * Performs a chisuqare test for a given word to test its significance.
     */
    /*public void chisquare(final String word,final DictHandling dicthandler,final Double corpusAsize,final Double corpusBsize,final Integer absoccurance){
         /*LangChar word2=dicthandler.matchWord(word);
        if(word2==null){
            word2=dicthandler.matchChar(word);
        }
        if(word2==null){
            System.out.println("No match found in the dictionary.... no significance analysis possible");
            return;
        }
        Double chisquarevalue=0.,expectedvalueCorpusA=0.,expectedvalueCorpusB=0.;
        expectedvalueCorpusA=(corpusAsize*word2.getOccurances())/(corpusAsize+corpusBsize);
        expectedvalueCorpusB=(corpusBsize*word2.getOccurances())/(corpusAsize+corpusBsize);
        chisquarevalue=Math.pow((absoccurance-expectedvalueCorpusA),2)/expectedvalueCorpusA;
        chisquarevalue+=Math.pow((absoccurance-expectedvalueCorpusB),2)/expectedvalueCorpusB;
        chisquarevalue+=Math.pow(((corpusAsize-absoccurance)-expectedvalueCorpusA),2)/expectedvalueCorpusA;
        chisquarevalue+=Math.pow(((corpusBsize-absoccurance)-expectedvalueCorpusB),2)/expectedvalueCorpusB;
        System.out.println("Significance Value for " + word + ": " + chisquarevalue);
    }*/

    @Override
    public String toString() {
            double recall=this.getRecall();
            double precision=this.getPrecision();
            double accuracy=this.getAccuracy();
            StringBuffer resultbuffer=new StringBuffer();
            resultbuffer.append("Matched: "+this.countmatches.toString()+"\n");
            resultbuffer.append("Missed: "+this.countmisses.toString()+"\n");
            resultbuffer.append("Exp. Correct Segmentations: "+this.correctSegmentations.toString()+"\n");
            resultbuffer.append("Total: "+this.total.toString()+"\n");
            resultbuffer.append("Matches(%): "+((this.countmatches/this.total)*100)+"\n");
            resultbuffer.append("Misses(%): "+((this.countmisses/this.total)*100)+"\n");
            resultbuffer.append("Accuracy(%): "+accuracy+"\n");
            resultbuffer.append("Precision(%):"+precision+"\n");
            resultbuffer.append("Recall(%): "+recall+"\n");
            resultbuffer.append("F-Measure(%): "+this.getFScore()+"\n");
            resultbuffer.append("G-Measure(%): "+this.getGScore()+"\n");
            resultbuffer.append("TruePositive: "+this.truepositive+"\n");
            resultbuffer.append("FalsePositive: "+this.falsepositive+"\n");
            resultbuffer.append("TrueNegative: "+this.truenegative+"\n");
            resultbuffer.append("FalseNegative: "+this.falsenegative+"\n");
            System.out.println(resultbuffer.toString());
            return resultbuffer.toString();
    }
}
