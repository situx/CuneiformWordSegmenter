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
import de.unifrankfurt.cs.acoli.akkad.util.Tuple;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.ClassificationMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.EvaluationMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Files;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;

/**
 * Determines the differences between transliterations and/or cuneiform texts and weights them in various measurement methods.
 * User: Timo Homburg
 * Date: 25.10.13
 * Time: 18:20
 */
public class Evaluation extends EvaluationAPI{
    private final BoundaryEditDistance boundaryEditDist;
    /**The dicthandler to use.*/
    private final DictHandling dictHandler;
    /**The generated file to analyze.*/
    private final File generatedCuneiFile;
    /**The original file to analyze.*/
    private final File originalCuneiFile;
    /**The evaluation result to produce.*/
    private EvalResult evalresult;
    /**FileReader for reading the generated file.*/
    private BufferedReader generatedFileReader;
    /**The file containing the generated text.*/
    private File generatedTranslitFile;
    /**Line counter for printing debug results.*/
    private Double linecounter;
    /**FileReader for reading the original file.*/
    private BufferedReader originalFileReader;
    /**The original file containing the correct text.*/
    private File originalTranslitFile;
    /**The resultWriter for writing evaluationResults to disk.*/
    private BufferedWriter resultWriter;
    /**Counts the windows used for sliding window evaluate methods.*/
    private Double windowcount;
    /**Penalties of the last k windows.*/
    private Queue<Integer> windowpenalty;
    /**Size of the window used for sliding window evaluate methods.*/
    private Double windowsize;

    /**
     * Constructor for this class.
     * @param originalFile the original file
     * @param generatedFile the generated file
     */
    public Evaluation(final String originalFile,final String generatedFile,final String originalCunei,final String generatedCunei,final DictHandling dicthandler) throws IOException {
        System.out.println("Originalfile: "+originalFile);
        this.originalTranslitFile =new File(originalFile);
        this.generatedTranslitFile =new File(generatedFile);
        this.originalCuneiFile=new File(originalCunei);
        this.generatedCuneiFile=new File(generatedCunei);
        System.out.println("Generated File "+generatedFile);
        this.windowsize=dicthandler.getAvgWordLength()==null?4:dicthandler.getAvgWordLength()*2;
        this.windowsize=(double)this.windowsize.intValue();
        this.windowpenalty=new LinkedList<Integer>();
        this.dictHandler=dicthandler;
        this.boundaryEditDist=new BoundaryEditDistance(dictHandler);
        System.out.println("======================Evaluating: "+this.generatedTranslitFile.toString()+"===========================");
    }

    @Override
    public String binaryDecisionEvaluation(Boolean append,ClassificationMethod classmethod) throws IOException {
        return EvaluationMethod.BINARYEVALUATION.getEvalString()+System.lineSeparator()+this.initEvaluation(EvaluationMethod.BINARYEVALUATION,append,classmethod);
    }

    /**
     * Implements the binary decision method.
     * @param originalarray the original array
     * @param comparearray the comparison array
     */
    private void binaryDecisionEvaluation(final String originalarray,final String comparearray){

        for(int j=0;j<originalarray.length();j++){
            /*if(i>=comparearray.length()){
                i=originalarray.length();
                break;
            }
            int counter1=0,counter2=0;
/*            if(looparray[i].length()<comparearray[i].length()){
                looparray2=comparearray[i];
                comparearray2=looparray[i];
            }else{
                comparearray2=comparearray[i];
                looparray2=looparray[i];
            }
*/
            //Wortbasiertes Vergleichen(Arrays der Silben)

            //for( int j=0; j<wordOfOriginalArray.length(); j++ ) {
                if(originalarray.charAt(j)=='-'){
                    this.evalresult.correctSegmentations++;
                }
                if( j<comparearray.length()  && originalarray.charAt(j)==' ' && comparearray.charAt(j) == ' ') {
                    j+=2;
                    this.evalresult.truepositive++;
                    this.evalresult.countmatches++;
                }else if(j<comparearray.length() && originalarray.charAt(j)==' ' && !(comparearray.charAt(j)==' ')){
                    this.evalresult.falsenegative++;
                    this.evalresult.countmisses++;
                }else if(j<comparearray.length() && !(originalarray.charAt(j)==' ') && !(comparearray.charAt(j)==' ') ){
                    this.evalresult.truenegative++;
                    this.evalresult.countmatches++;
                }else if(j<comparearray.length() && !(originalarray.charAt(j)==' ') && (comparearray.charAt(j)==' ')){
                    this.evalresult.falsepositive++;
                    this.evalresult.countmisses++;
                }
                /*else if( j<wordOfCompareArray.length()  && wordOfOriginalArray.charAt(j)=='-' && wordOfCompareArray.charAt(j) == '-') {
                    counter2++;
                    this.truepositive++;
                    this.countmatches++;
                }else if(j<wordOfCompareArray.length() && wordOfOriginalArray.charAt(j)=='-' && !(wordOfCompareArray.charAt(j)=='-')){
                    this.falsenegative++;
                    this.countmisses++;
                }else if(j<wordOfCompareArray.length() && !(wordOfOriginalArray.charAt(j)=='-') && !(wordOfCompareArray.charAt(j)=='-') ){
                    this.truenegative++;
                    this.countmatches++;
                }else if(j<wordOfCompareArray.length() && !(wordOfOriginalArray.charAt(j)=='-') && (wordOfCompareArray.charAt(j)=='-')){
                    this.falsepositive++;
                    this.countmisses++;
                }*/
                this.evalresult.total++;
            }
            if(originalarray.length()<comparearray.length()){
                this.evalresult.countmisses+=comparearray.length()-originalarray.length();
                this.evalresult.falsenegative+=comparearray.length()-originalarray.length();
                this.evalresult.total+=comparearray.length()-originalarray.length();
            }else{
                this.evalresult.countmisses+=originalarray.length()-comparearray.length();
                for(int k=comparearray.length();k<originalarray.length();k++){
                    if(originalarray.charAt(k)=='-'){
                        this.evalresult.falsepositive++;
                        this.evalresult.correctSegmentations++;
                    }else if(!(originalarray.charAt(k)=='-')){
                        this.evalresult.falsenegative++;
                    }
                    this.evalresult.total++;
                }
            }

            //System.out.println(looparray2+" - "+comparearray2);
            //System.out.println(counter1+" - "+counter2);
            /*if(counter1==counter2){
                this.countmatches++;
                this.truepositive+=counter1;
                this.total+=counter1;
                this.correctSegmentations+=counter1;
            }else{
                if(counter1>counter2){
                    this.countmatches+=counter2;
                    this.countmisses+=counter1-counter2;
                    this.falsenegative+=counter1-counter2;
                    this.total+=counter1;
                    this.correctSegmentations+=counter2+(counter1-counter2);
                }else{
                    this.countmatches+=counter1;
                    this.total+=counter2;
                    this.countmisses+=counter2-counter1;
                    this.correctSegmentations+=counter1+(counter2-counter1);
                }
            }*/

    }

    @Override
    public String boundaryBasedEvaluation(Boolean append, ClassificationMethod classmethod) throws IOException {
        return EvaluationMethod.BOUNDARYEVALUATION.getEvalString()+System.lineSeparator()+this.initEvaluation(EvaluationMethod.BOUNDARYEVALUATION,append,classmethod);
    }

    @Override
    public String boundaryEditDistance(Boolean append, ClassificationMethod classmethod) throws IOException {
        return EvaluationMethod.BOUNDARYEDITDISTANCE.getEvalString()+System.lineSeparator()+this.initEvaluation(EvaluationMethod.BOUNDARYEDITDISTANCE,append,classmethod);
    }

    /**
     * Evaluates the given result using the Edit distance evaluate method.
     * @throws IOException
     */
    private void boundaryEditDistance(String original,String revised,final Integer transpositionSize) throws IOException{
        boundaryEditDist.boundaryEditDistance(original,revised,transpositionSize);
        this.evalresult.countmatches+=this.boundaryEditDist.matches;
        this.evalresult.total+=this.boundaryEditDist.total;
    }

    private void boundaryEvaluation(final String originalarray, final String comparearray){
        boolean tempmatch;
        Set<Integer> boundaries=new HashSet<>();
        Set<Integer> boundaries2=new HashSet<>();

        int strcounter=0;
        for(String split:originalarray.split(" ")){
            strcounter+=split.length();
            boundaries.add(strcounter);

        }
        int truenegative1=(originalarray.length()/this.dictHandler.getChartype().getChar_length())-originalarray.split(" ").length;
        strcounter=0;
        for(String split:comparearray.split(" ")){
            strcounter+=split.length();
            boundaries2.add(strcounter);

        }
        int truenegative2=(comparearray.length()/this.dictHandler.getChartype().getChar_length())-comparearray.split(" ").length;
        /*System.out.println("Line: "+linecounter);
        System.out.println("Original: "+boundaries);
        System.out.println("Generated: "+boundaries2);*/
        for(Integer refInt:boundaries){
            if(boundaries2.contains(refInt)){
                this.evalresult.truepositive++;
                this.evalresult.countmatches++;
                this.evalresult.correctSegmentations++;
                this.evalresult.total++;
            }else{
                this.evalresult.falsenegative++;
                this.evalresult.countmisses++;
                this.evalresult.total++;
            }
        }
        for(Integer refInt:boundaries2){
            if(!boundaries.contains(refInt)){
                this.evalresult.falsepositive++;
                this.evalresult.countmisses++;
                this.evalresult.total++;
            }else{

            }
        }
        //this.evalresult.truenegative=Double.valueOf(Math.min(truenegative1,truenegative2));
        for(Integer bound:boundaries){
            //if()
        }

    }

    @Override
    public String boundarySimilarity(Boolean append, ClassificationMethod classmethod) throws IOException {
        return EvaluationMethod.BOUNDARYSIMILARITY.getEvalString()+System.lineSeparator()+this.initEvaluation(EvaluationMethod.BOUNDARYSIMILARITY,append,classmethod);
    }

    /**
     * Evaluates the given result using the Edit distance evaluate method.
     * @throws IOException
     */
    private void boundarySimilarity(final String original,final String revised,final Integer nearMissBoundary) throws IOException {
        this.boundaryEditDist.boundarySimilarity(original,revised,nearMissBoundary);
        this.evalresult.countmatches+=this.boundaryEditDist.matches;
        this.evalresult.total+=this.boundaryEditDist.total;
    }

    private Integer countBoundary(String str,String boundary){
        return StringUtils.countMatches(str, boundary);
    }

    public List<Tuple<String,String>> createPairedWindow(final String original,final String revised,final Integer windowsize){
        List<Tuple<String,String>> tuples=new LinkedList<>();
        Tuple<String,String> tuple;
        if(original.length()>revised.length()){
            for(int i=0;i<original.length()-windowsize;i++){
                if(i<revised.length()-windowsize)
                    tuple=new Tuple<>(original.substring(i,i+windowsize),revised.substring(i,i+windowsize));
                else
                    tuple=new Tuple<>(original.substring(i,i+windowsize),"");
                tuples.add(tuple);
            }
        }else{
            for(int i=0;i<revised.length()-windowsize;i++){
                if(i<original.length()-windowsize)
                    tuple=new Tuple<>(original.substring(i,i+windowsize),revised.substring(i,i+windowsize));
                else
                    tuple=new Tuple<>("",revised.substring(i,i+windowsize));
                tuples.add(tuple);
            }
        }
       return tuples;
    }

    /**
     * Evaluates the transliteration as well as the segmentation of the generated text.
     * @param looparray the original array
     * @param comparearray the compare array
     */
    private void exactTransliterationEvaluation(final String[] looparray, final String[] comparearray){
        int allmatches=0,truepositive=0,falsepositive=0;
        ArrayList<String> list= new ArrayList<>();
        for(String str:looparray){
            list.add(str);
        }
        for(String str:comparearray){
            if(list.contains(str)){
                allmatches++;
            }
        }
        for(int i=0;i<looparray.length;i++){
            if(i>=comparearray.length){
                //this.countmisses+=looparray.length-i;
                i=looparray.length;
                break;
            }
            //System.out.println(looparray[i]+" - "+comparearray[i]);
            if(looparray[i].equals(comparearray[i])){
                truepositive++;
                this.evalresult.countmatches++;
                this.evalresult.total++;
            }
            else{
                this.evalresult.truenegative++;
                this.evalresult.countmisses++;
                this.evalresult.total++;
            }
        }
        this.evalresult.truepositive+=truepositive;
        this.evalresult.falsepositive+=allmatches-truepositive;
        //this.evalresult.countmisses+=allmatches-truepositive;
    }

    private String getCharLengthStr(){
        String charlengthstr="";
        for(int i=0;i<dictHandler.getChartype().getChar_length();i++){
            charlengthstr+=" ";
        }
        return charlengthstr;
    }

    /**
     * Initializes the evaluate process.
     * @param method the evaluate method as specified in EvaluationMethod
     * @throws IOException on error
     */
    private String initEvaluation(final EvaluationMethod method,Boolean append,final ClassificationMethod classmethod) throws IOException {
        System.out.println(method.getEvalString());
        String originalline,generatedline;
        String[] originalarray,generatedarray,looparray,comparearray;
        this.windowcount=0.;
        this.linecounter=0.;
        this.evalresult=new EvalResult(method,classmethod,dictHandler);
        String resultpath;
        switch (method){
            case SEGMENTATIONEVALUATION:
            case WINDOWDIFFEVALUATION:
            case PKEVALUATION:
            case BOUNDARYEDITDISTANCE:
            case BOUNDARYEVALUATION:
            case WORDBOUNDARYEVALUATION:
            case BOUNDARYSIMILARITY:
                this.originalFileReader =new BufferedReader(new FileReader(this.originalCuneiFile));
                this.generatedFileReader =new BufferedReader(new FileReader(this.generatedCuneiFile));
                resultpath= this.generatedCuneiFile.getAbsolutePath().substring(0, generatedCuneiFile.getAbsolutePath().lastIndexOf('/'));
                resultpath=resultpath.substring(0,resultpath.lastIndexOf('/')+1)+ Files.EVALDIR+ generatedCuneiFile.getAbsolutePath().substring(generatedCuneiFile.getAbsolutePath().lastIndexOf('/'), generatedCuneiFile.getAbsolutePath().lastIndexOf('_'))+Files.EVAL.toString();
                break;
            default:
                this.originalFileReader =new BufferedReader(new FileReader(this.originalTranslitFile));
                this.generatedFileReader =new BufferedReader(new FileReader(this.generatedTranslitFile));
                resultpath= generatedTranslitFile.getAbsolutePath().substring(0, generatedTranslitFile.getAbsolutePath().lastIndexOf('/'));
                resultpath=resultpath.substring(0,resultpath.lastIndexOf('/')+1)+ Files.EVALDIR+ generatedTranslitFile.getAbsolutePath().substring(generatedTranslitFile.getAbsolutePath().lastIndexOf('/'), generatedTranslitFile.getAbsolutePath().lastIndexOf('_'))+Files.EVAL.toString();

        }

        this.resultWriter=new BufferedWriter(new FileWriter(new File(resultpath),append));
        this.resultWriter.write("======================Evaluating: "+this.generatedTranslitFile.toString()+"===========================\n");
        this.resultWriter.write(method.toString() + System.lineSeparator());
        while((originalline= this.originalFileReader.readLine())!=null && (generatedline= this.generatedFileReader.readLine())!=null){
            originalarray=originalline.split(" ");
            generatedarray=generatedline.split(" ");
            if(originalarray.length>generatedarray.length){
                looparray=originalarray;
                comparearray=generatedarray;
            }else{
                looparray=generatedarray;
                comparearray=originalarray;
            }
            switch (method){
                case TRANSLITEVALUATION: //this.evalresult.correctSegmentations+=originalarray.length;

                       this.exactTransliterationEvaluation(looparray, comparearray);break;
                case SEGMENTATIONEVALUATION://this.evalresult.correctSegmentations+=originalarray.length;
                    this.segmentationEvaluation2(originalline,generatedline);
                    /*this.segmentationEvaluation(looparray,comparearray);*/break;
                case BINARYEVALUATION: this.binaryDecisionEvaluation(originalline,generatedline);break;
                case BOUNDARYEVALUATION:this.boundaryEvaluation(originalline, generatedline);break;
                case WORDBOUNDARYEVALUATION:this.wordBoundaryEvaluation(originalline, generatedline);break;
                case PKEVALUATION:
                    this.pkEvaluation(originalline,generatedline);
                break;
                case WINDOWDIFFEVALUATION:
                    this.windowDiffEvaluation(originalline, generatedline, windowsize.intValue(), false);
                break;
                case WINPR:
                    this.windowDiffEvaluation(originalline,generatedline,windowsize.intValue(),true);
                    break;
                case BOUNDARYEDITDISTANCE:
                    this.boundaryEditDistance(originalline,generatedline,this.dictHandler.getChartype().getChar_length());
                break;
                case BOUNDARYSIMILARITY:
                    this.boundarySimilarity(originalline,generatedline,this.dictHandler.getChartype().getChar_length());
                break;
                default:
            }
            this.linecounter++;
        }
        switch (method){
            case BINARYEVALUATION:
                System.out.println("Binary Decision Score: "+this.evalresult.countmatches/this.evalresult.total);
                this.resultWriter.write("Binary Decision Score: "+this.evalresult.countmatches/this.evalresult.total);
                break;
            case PKEVALUATION: this.evalresult.setResult(this.evalresult.countmisses>0?(/*1.0-*/(this.evalresult.countmisses / this.evalresult.total))*100:0);
                break;
            case WINDOWDIFFEVALUATION:
                this.evalresult.setResult(this.evalresult.countmisses>0?(/*1.0-*/(this.evalresult.countmisses / (this.evalresult.total/*-windowsize*/)))*100:0);
                break;
            case BOUNDARYSIMILARITY:
                this.evalresult.setResult((/*1-*/(this.evalresult.countmatches/(this.evalresult.total+this.evalresult.countmatches)))*100);
                break;
            default:
        }
        this.evalresult.setTotal(this.evalresult.total);
        this.evalresult.setCountmatches(this.evalresult.countmatches);
        this.evalresult.setCountmisses(this.evalresult.countmisses);
        String result=this.evalresult.toString();
        this.resultWriter.write(result);
        System.out.println("GeneratedResultFile: "+this.generatedTranslitFile.toString());
        EvalStatistics.getInstance().addResult(this.generatedTranslitFile.toString(),evalresult,classmethod);
        this.originalFileReader.close();
        this.generatedFileReader.close();
        this.resultWriter.close();
        return result;
    }

    @Override
    public String pkEvaluation(Boolean append,ClassificationMethod classmethod) throws IOException {
          return EvaluationMethod.PKEVALUATION.getEvalString()+System.lineSeparator()+this.initEvaluation(EvaluationMethod.PKEVALUATION,append,classmethod);
    }

    /**
     * Evaluates the given result using the pk Evaluation method.
     * @param originalarray the original array
     * @param comparearray the comparison array
     */
    private void pkEvaluation(String originalarray, String comparearray){
        if(originalarray.length()<2 || comparearray.length()<2)
            return;
        String originalwindowstart=originalarray.substring(0,dictHandler.getChartype().getChar_length());
        String originalwindowend=originalarray.substring(originalarray.length()-dictHandler.getChartype().getChar_length(),originalarray.length());
        String comparewindowstart=comparearray.substring(0,dictHandler.getChartype().getChar_length());
        String comparewindowend=comparearray.substring(comparearray.length()-dictHandler.getChartype().getChar_length(),comparearray.length());
        String originalMass=this.stringToMasses(originalarray.replaceAll("[ ]+",this.getCharLengthStr()));
        String revisedMass=this.stringToMasses(comparearray.replaceAll("[ ]+",this.getCharLengthStr()));
        System.out.println("Originalmass/Originalarray: "+originalMass+" - "+originalarray.replaceAll("[ ]+",this.getCharLengthStr()));
        System.out.println("Revisedmass/Revisedarray: "+revisedMass+" - "+comparearray.replaceAll("[ ]+",this.getCharLengthStr()));
        originalarray=originalarray.replaceAll("[ ]+",this.getCharLengthStr());
        comparearray=comparearray.replaceAll("[ ]+",this.getCharLengthStr());
        Integer currentmisses=0;
        String originaltemp,revisedtemp;
        for(int i=0;i<originalarray.length()-windowsize.intValue() && i<comparearray.length()-windowsize.intValue();i+=dictHandler.getChartype().getChar_length()){
            originaltemp=originalarray.substring(i,i+windowsize.intValue());
            revisedtemp=comparearray.substring(i,i+windowsize.intValue());
            originaltemp=originaltemp.replaceAll("[ ]+",this.getCharLengthStr());
            revisedtemp=revisedtemp.replaceAll("[ ]+",this.getCharLengthStr());
            if(originaltemp.isEmpty())
                continue;
            System.out.println("Originaltemp: "+originaltemp+" "+originaltemp.substring(0,dictHandler.getChartype().getChar_length())+" "+originaltemp.substring(originaltemp.length()-dictHandler.getChartype().getChar_length(),originaltemp.length()));
            System.out.println("Revisedtemp: "+revisedtemp+" "+revisedtemp.substring(0,dictHandler.getChartype().getChar_length())+" "+revisedtemp.substring(revisedtemp.length()-dictHandler.getChartype().getChar_length(),revisedtemp.length()));
            System.out.println("OriginalMass: "+originalMass.substring(i,i+1)+" "+originalMass.substring(i+windowsize.intValue()-1,i+windowsize.intValue()));
            System.out.println("RevisedMass: "+revisedMass.substring(i,i+1)+" "+revisedMass.substring(i+windowsize.intValue()-1,i+windowsize.intValue()));
            /*Boolean originalIsValidWindow2=originalMass.substring(i,i+1).equals(originalMass.substring(i+windowsize.intValue()-1,i+windowsize.intValue()));
            Boolean revisedIsValidWindow2=revisedMass.substring(i,i+1).equals(revisedMass.substring(i+windowsize.intValue()-1,i+windowsize.intValue()));*/
            Boolean originalIsValidWindow2=this.countBoundary(originaltemp, this.getCharLengthStr())>0;
            Boolean revisedIsValidWindow2=this.countBoundary(revisedtemp, this.getCharLengthStr())>0;
            System.out.println("OriginalMass: "+originalIsValidWindow2);
            System.out.println("RevisedMass: "+revisedIsValidWindow2);
            Boolean originalIsValidWindow=originaltemp.substring(0,dictHandler.getChartype().getChar_length()).equals(originaltemp.substring(originaltemp.length()-dictHandler.getChartype().getChar_length(),originaltemp.length()));
            Boolean revisedIsValidWindow=revisedtemp.substring(0,dictHandler.getChartype().getChar_length()).equals(revisedtemp.substring(revisedtemp.length()-dictHandler.getChartype().getChar_length(),revisedtemp.length()));
            if(originalIsValidWindow2!=revisedIsValidWindow2){
                this.evalresult.countmisses++;
            }
            this.evalresult.total++;
        }
    }

    @Override
    public String segmentationEvaluation(Boolean append,ClassificationMethod classmethod) throws IOException {
        return EvaluationMethod.SEGMENTATIONEVALUATION.getEvalString()+System.lineSeparator()+this.initEvaluation(EvaluationMethod.SEGMENTATIONEVALUATION,append,classmethod);
    }

    /**
     * Implements the segmentation evaluate.
     * @param looparray the original array
     * @param comparearray the compare array
     */
    private void segmentationEvaluation(final String[] looparray,final String[]comparearray){
        String looparray2,comparearray2;
        int allmatches=0,truepositive=0,falsepositive=0;
        ArrayList<String> list= new ArrayList<>();
        for(String str:looparray){
            list.add(str);
        }
        /*for(String str:comparearray){
            if(list.){
                allmatches++;
            }
        }*/
        for(int i=0;i<looparray.length;i++){
            if(i>=comparearray.length){
                i=looparray.length;
                break;
            }
            int counter1=0,counter2=0;
            if(looparray[i].length()<comparearray[i].length()){
                looparray2=comparearray[i];
                comparearray2=looparray[i];
            }else{
                comparearray2=comparearray[i];
                looparray2=looparray[i];
            }
            for( int j=0; j<looparray2.length(); j++ ) {
                if( looparray2.charAt(j) == '-' ) {
                    counter1++;
                }
                if( j<comparearray2.length() && comparearray2.charAt(j) == '-' ) {
                    counter2++;
                }
            }
            //System.out.println(looparray2+" - "+comparearray2);
            //System.out.println(counter1+" - "+counter2);
            if(counter1==counter2){
                this.evalresult.countmatches++;
                this.evalresult.truepositive++;
                this.evalresult.total++;
            }
            else{
                this.evalresult.countmisses++;
                this.evalresult.truenegative++;
                this.evalresult.total++;
            }
        }
    }

    public void segmentationEvaluation2(final String original,final String revised){
            String charline=original.replaceAll(" ","");
            /*for(String stopChar:dictHandler.getStopchars().keySet()){
                charline=charline.replaceAll(stopChar,"");
            }*/
            String currentoriginal,currentrevised;
            int revisedoffset=0,originaloffset=0;

            for(int j=0;j<charline.length()-1 && j+revisedoffset+1<revised.length() && j+originaloffset+1<original.length();j+=dictHandler.getChartype().getChar_length()){
                currentrevised=revised.substring(j+revisedoffset,j+revisedoffset+dictHandler.getChartype().getChar_length());
                currentoriginal=original.substring(j+originaloffset,j+originaloffset+dictHandler.getChartype().getChar_length());
                System.out.println("Currentrevised: "+currentrevised+" RevisedOffset: "+revisedoffset+" J:"+j);
                System.out.println("Currentoriginal: "+currentoriginal+" Originaloffset: "+originaloffset+" J:"+j);
                if(currentoriginal.substring(0,1).matches("[ ]+")){
                    if(currentrevised.substring(0,1).matches("[ ]+")){
                        while(j+originaloffset+1<original.length() && original.substring(j+originaloffset,j+originaloffset+1).equals(" ")){
                            originaloffset++;
                        }
                        while(j+revisedoffset+1<revised.length() && revised.substring(j + revisedoffset, j + revisedoffset + 1).equals(" ")){
                            revisedoffset++;
                        }
                        this.evalresult.truepositive++;
                        this.evalresult.total++;
                    }else{
                        while(j+originaloffset+1<original.length() && original.substring(j+originaloffset,j+originaloffset+1).equals(" ")){
                            originaloffset++;
                        }
                        this.evalresult.falsenegative++;
                        this.evalresult.total++;
                        j-=dictHandler.getChartype().getChar_length();
                    }

                }else{
                    if(currentrevised.substring(0,1).matches("[ ]+")){
                        while(j+revisedoffset+1<revised.length() && revised.substring(j+revisedoffset,j+revisedoffset+1).equals(" ")){
                            revisedoffset++;
                        }
                        this.evalresult.falsepositive++;
                        this.evalresult.total++;
                        j-=dictHandler.getChartype().getChar_length();
                    }else{
                        this.evalresult.truenegative++;
                        this.evalresult.total++;
                    }
                }
            }
    }

    private List<Integer> stringToMass(String str){
        str=str.replaceAll("[ ]+",this.getCharLengthStr());
        List<Integer> result=new LinkedList<>();
        int counter=0;
        String temp;
        for(int i=0;i<str.length();i+=dictHandler.getChartype().getChar_length()){
            temp=str.substring(i,i+dictHandler.getChartype().getChar_length());
            if(temp.substring(0,1).equals(" ")){
                result.add(counter);
                counter=0;
            }else{
                counter++;
            }
        }
        return result;
    }

    private String stringToMasses(String s){
         String result="";
         Integer counter=0;
         for(int i=0;i<s.length();i++){
             if(s.substring(i,i+1).equals(" ")){
                 result+=(++counter).toString();
             }else{
                 result+=counter.toString();
             }
         }
        return result;
    }

    /**
     * Comparison function to determine the hits and misses.
     * @throws FileNotFoundException on error
     */
    @Override
    public String transliterationEvaluation(Boolean append,ClassificationMethod classmethod) throws IOException {
        return EvaluationMethod.TRANSLITEVALUATION.getEvalString()+System.lineSeparator()+this.initEvaluation(EvaluationMethod.TRANSLITEVALUATION,append,classmethod);
    }

    @Override
    public String winPR(Boolean append,ClassificationMethod classmethod) throws IOException {
        return EvaluationMethod.WINPR.getEvalString()+System.lineSeparator()+this.initEvaluation(EvaluationMethod.WINPR,append,classmethod);
    }

    private void winPR(final Integer originalcount,final Integer revisedcount,final Integer windowSize){
        this.evalresult.truepositive += Math.min( originalcount, revisedcount );
        this.evalresult.truenegative += Math.max( 0, windowSize - Math.max( originalcount, revisedcount ) );

        if( revisedcount- originalcount > 0 )
            this.evalresult.falsepositive += revisedcount - originalcount;
        else
            this.evalresult.falsenegative += originalcount - revisedcount;
    }

    @Override
    public String windowDiff(Boolean append,ClassificationMethod classmethod) throws IOException {
         return EvaluationMethod.WINDOWDIFFEVALUATION.getEvalString()+System.lineSeparator()+this.initEvaluation(EvaluationMethod.WINDOWDIFFEVALUATION,append,classmethod);
    }

    /**
     * Modification of the pkEvaluation algorithm which is more accurate in some cases
     * @param originalarray the original array
     * @param comparearray the comparison array
     */
    private void windowDiffEvaluation(String originalarray, String comparearray,final Integer windowSize,final Boolean winPR) {
        originalarray=originalarray.replaceAll("[ ]+",this.getCharLengthStr());
        comparearray=comparearray.replaceAll("[ ]+",this.getCharLengthStr());
        List<Tuple<String, String>> units_ref_hyp = this.createPairedWindow(originalarray, comparearray, windowSize);
        String originalwindow,revisedwindow,currentoriginal,currentrevised;
        System.out.println("OriginalArray: "+originalarray);
        System.out.println("RevisedArray: "+comparearray);
        for (int i = 0; i < units_ref_hyp.size(); i++) {
            int ref_boundaries = 0;
            int hyp_boundaries = 0;
            originalwindow = units_ref_hyp.get(i).getOne();
            revisedwindow=units_ref_hyp.get(i).getTwo();
            System.out.println("Originalwindow: "+originalwindow);
            System.out.println("Revisedwindow: "+revisedwindow);
            /*int diff=this.countBoundary(originalwindow,this.getCharLengthStr())-this.countBoundary(revisedwindow,this.getCharLengthStr());
            if(diff<0){
                diff*=-1;
            }
            this.evalresult.countmisses+=diff;
            this.evalresult.total+=originalwindow.length();*/
            for (int j = 0; j <= originalwindow.length()-dictHandler.getChartype().getChar_length() && j<=revisedwindow.length()-dictHandler.getChartype().getChar_length(); j += dictHandler.getChartype().getChar_length()) {
                currentoriginal=originalwindow.substring(j,j+dictHandler.getChartype().getChar_length());
                currentrevised=revisedwindow.substring(j,j+dictHandler.getChartype().getChar_length());
                System.out.println("Currentoriginal: "+currentoriginal);
                System.out.println("Currentrevised: "+currentrevised);
                if (currentoriginal.equals(this.getCharLengthStr())) {
                    ref_boundaries++;
                }
                if (currentrevised.equals(this.getCharLengthStr())) {
                    hyp_boundaries++;
                }
            }
            if(winPR){
                this.winPR(ref_boundaries,hyp_boundaries,windowSize);
            }else if (ref_boundaries != hyp_boundaries) {
                this.evalresult.countmisses++;
            }
            if(!winPR)
                this.evalresult.total++;
        }
    }

    @Override
    public String wordBoundaryBasedEvaluation(Boolean append,ClassificationMethod classmethod) throws IOException {
        return EvaluationMethod.WORDBOUNDARYEVALUATION.getEvalString()+System.lineSeparator()+this.initEvaluation(EvaluationMethod.WORDBOUNDARYEVALUATION,append,classmethod);
    }

    /**
     * Implementation of the word boundary evaluate method.
     * @param originalarray the original array
     * @param comparearray the comparison array
     */
    private void wordBoundaryEvaluation(final String originalarray, final String comparearray){
        List<Tuple<Integer,Integer>> chunckboundaries=new LinkedList<>();
        List<Tuple<Integer,Integer>> chunckboundaries2=new LinkedList<>();
        int strcounter=0;
        for(String split:originalarray.split(" ")){
            chunckboundaries.add(new Tuple<Integer,Integer>(strcounter,strcounter+split.length()));
            strcounter+=split.length();
        }
        strcounter=0;
        for(String split:comparearray.split(" ")){
            chunckboundaries2.add(new Tuple<Integer,Integer>(strcounter,strcounter+split.length()));
            strcounter+=split.length();
        }
        /*for(int i=0;i< originalarray.length()-this.dictHandler.getChartype().getChar_length() && i< comparearray.length()-this.dictHandler.getChartype().getChar_length();i+=this.dictHandler.getChartype().getChar_length()){
            if(originalarray.substring(i,i+this.dictHandler.getChartype().getChar_length()).equals(comparearray.substring(i,i+this.dictHandler.getChartype().getChar_length())) && !" ".equals(comparearray.substring(i,i+this.dictHandler.getChartype().getChar_length())) && !" ".equals(originalarray.substring(i,i+this.dictHandler.getChartype().getChar_length()))){
                this.evalresult.truenegative++;
            }
        }*/
        /*System.out.println("Line: "+linecounter);
        System.out.println("Original: "+chunckboundaries);
        System.out.println("Generated: "+chunckboundaries2);*/
        for(Tuple<Integer,Integer> refInt:chunckboundaries){
            if(chunckboundaries2.contains(refInt)){
                this.evalresult.truepositive++;
                this.evalresult.countmatches++;
                this.evalresult.correctSegmentations++;
                this.evalresult.total++;
            }else{
                this.evalresult.falsenegative++;
                this.evalresult.countmisses++;
                this.evalresult.total++;
            }
        }
        for(Tuple refInt:chunckboundaries2){
            if(!chunckboundaries.contains(refInt)){
                this.evalresult.falsepositive++;
                this.evalresult.countmisses++;
                this.evalresult.total++;
            }
        }
    }

}
