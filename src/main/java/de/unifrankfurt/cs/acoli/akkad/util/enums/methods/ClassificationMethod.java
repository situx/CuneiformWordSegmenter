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

package de.unifrankfurt.cs.acoli.akkad.util.enums.methods;

import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Options;

/**
 * Enumeration for storing methods names for parsing.
 * User: Timo Homburg
 * Date: 03.12.13
 * Time: 22:46
 */
public enum ClassificationMethod implements MethodEnum {
    /**Average word length segmentation method.*/
    AVGWORDLEN("AVGWORDLEN","AVG","Average Word Length",false, Options.NATIVE),

    //AODE("AODE","AODE",true, Options.WEKA),
    BAYESNET("BAYESNET","BANET","BayesNet",true, Options.WEKA),
    /**Bigram segmentation method.*/
    BIGRAM("BIGRAM","BIG","Bigram Segmenting",false, Options.NATIVE),
    /**Bigram hmm segmentation method.*/
    BIGRAMHMM("BIGRAMHMM","BIGHMM","BigrammHMM",false, Options.NATIVE),
    /**Breakpointmatching segmentation method.*/
    BREAKPOINT("BREAKPOINT","BRK","Breakpoint Segmenting",false, Options.NATIVE),
    /**C45 word segmentation method.*/
    C45("C45","C45","C45",true, Options.WEKA),
    /**Char segment parse segmentation method.*/
    CHARSEGMENTPARSE("CHARSEGMENTPARSE","CHAR","Charbased Segmenting",false, Options.NATIVE),
    /**Conditional Random Fields segmentation method.*/
    CRF("CRF","CRF","Conditional Random Fields",true, Options.MALLET),
    /**Decision Tree segmentation method.*/
    DECISIONTREE("DECISIONTREE","DTREE","Decision Trees",true, Options.WEKA),
    /**Highest Occurance segmentation method.*/
    HIGHESTOCCURANCE("HIGHESTOCCURANCE","HOCC","Highest Occurance Segmenting",false, Options.NATIVE),
    /**Hidden Markov Model segmentation method.*/
    HMM("HMM","HMM","Hidden Markov Models",true, Options.WEKA),
    KMEANS("KMeans","kMeans","kMeans",true, Options.WEKA),
    IB1("IB1","IB1","kNN",true, Options.WEKA),
    LCUMATCHING("LCUMATCHING","LCU","LCU Segmenting",false, Options.NATIVE),
    /**MaxMatch segmentation method.*/
    MAXMATCH("MAXMATCH","MAXM","Max Match Segmenting",false, Options.NATIVE),
    /**MaxMatch2 segmentation method.*/
    MAXMATCH2("MAXMATCH2","MAXM2","Max Match2 Segmenting",false, Options.NATIVE),
    /**MaxMatch2 segmentation method.*/
    MAXMATCHCOMBINED("MAXMATCHCOMBINED","MAXMC","MaxMatchCombined Segmenting",false, Options.NATIVE),
    /**MaxProbability segmentation method.*/
    MAXPROB("MAXPROB","MAXP","Maximum Probability Segmenting",false, Options.NATIVE),
    /**Maximum Entropy segmentation method.*/
    MAXENT("MAXENT","MAXENT","Maximum Entropy Segmentation",true, Options.MALLET),
    /**Maximum Entropy MC segmentation method.*/
    //MAXMCENT("MAXMCENT","MAXMCENT",true, Options.MALLET),
    /**MinMatch segmentation method.*/
    MINMATCH("MINMATCH","MINM","MinMatch Segmenting",false, Options.NATIVE),
    /**Min Wordcount Matching segmentation method.*/
    MINWCMATCH("MINWCMATCH","MINWC","Minimum Wordcount Matching",false, Options.NATIVE),
    /**Min Wordcount Matching segmentation method.*/
    MINWCMATCH2("MINWCMATCH2","MINWC2","MinWCMatch2",false, Options.NATIVE),
    /**Morfessor segmentation method.*/
    MORFESSOR("MORFESSOR","MORF","Morfessor",false, Options.NATIVE),
    /**Naive Bayes segmentation method.*/
    NAIVEBAYES("NAIVEBAYES","BAY","NaiveBayes",true, Options.WEKA),
    /**Perceptron segmentation method.*/
    PERCEPTRON("PERCEPTRON","PERCEP","Multilayer Perceptron",true, Options.WEKA),
    /**Prefix Suffix segmentation method.*/
    PREFSUFF("PREFSUFF","PRSF","Prefix/Suffix Segmenting",false, Options.NATIVE),
    /**Random Segment Parse segmentation method.*/
    RANDOMSEGMENTPARSE("RANDOMSEGMENTPARSE","RAND","Random Segmentation",false, Options.NATIVE),
    /**SVM segmentation method.*/
    SVM("SVM","SVM","Support Vector Machines",true, Options.WEKA),
    /**Tango segmentation method.*/
    TANGO("TANGO","TANGO","TANGO Algorithm",false, Options.NATIVE),
    /**SVM segmentation method.*/
    VOTE("VOTE","VOTE","Voting",true, Options.WEKA),
    /**Winnow segmentation method.*/
    //WINNOW("WINNOW","WNW",true, Options.WEKA),
    //NAIVEBAYESSIMPLE("NAIVEBAYESSIMPLE","BAYSI",true, Options.WEKA),

    //CLUSTERING_META("Clustering-Meta","CLUSM",true, Options.WEKA),
    LOGISTICREGRESSION("LogisticRegression","LOGREG","LogisticRegression Classifier",true, Options.WEKA),
    LOGISTIC("Logistic","LOGREG","Logistic",true, Options.WEKA);

    private Options framework;
    private Boolean hasFeatureSet;
    /**String value (name of the method).*/
    private String value,shortLabel,printLabel;
    /**Constructor for this class.*/
    private ClassificationMethod(){

    }


    /**Constructor using a description parameter.*/
    private ClassificationMethod(String value,String shortLabel,String printLabel,Boolean hasFeatureSet,Options framework){
        this.hasFeatureSet=hasFeatureSet;
        this.shortLabel=shortLabel;
        this.printLabel=printLabel;
        this.value=value;
        this.framework=framework;
    }

    public Options getFramework() {
        return framework;
    }

    public Boolean getHasFeatureSet(){
        return this.hasFeatureSet;
    }

    public String getShortLabel(){return this.shortLabel;}

    @Override
    public String getShortname() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
