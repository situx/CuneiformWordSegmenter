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

package de.unifrankfurt.cs.acoli.akkad.methods.segmentation.stat;

import de.unifrankfurt.cs.acoli.akkad.dict.chars.LangChar;
import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.cuneiform.CuneiDictHandler;
import de.unifrankfurt.cs.acoli.akkad.main.Main;
import de.unifrankfurt.cs.acoli.akkad.main.gui.MainGUI;
import de.unifrankfurt.cs.acoli.akkad.methods.segmentation.SegmentationMethods;
import de.unifrankfurt.cs.acoli.akkad.methods.segmentation.stat.featureset.FeatureSetManager;
import de.unifrankfurt.cs.acoli.akkad.methods.transcription.TranscriptionMethods;
import de.unifrankfurt.cs.acoli.akkad.util.ArffGen;
import de.unifrankfurt.cs.acoli.akkad.util.ArffHandler;
import de.unifrankfurt.cs.acoli.akkad.util.HMMHandler;
import de.unifrankfurt.cs.acoli.akkad.util.Tuple;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.*;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Files;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Options;
import opennlp.maxent.GISModel;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: timo
 * Date: 09.11.13
 * Time: 18:15
 * To change this template use File | Settings | File Templates.
 */
public class StatMethods extends SegmentationMethods implements StatMethodsAPI {

    Boolean propOrFirst=true;
    private GISModel gisModel;
    private LangChar lastchar;
    private MalletMethods mallet;
    private java.util.Map<Integer,String> maxtranslitStrings;
    private double maxvalue;
    private double[] maxvalues;
    private java.util.Map<Integer,String> maxwordStrings;
    private ArffGen setGenerator;
    private WekaMethods weka;

    /**
     * Constructor for StatMethods.
     */
    public StatMethods(){
        super();
        this.maxvalues=new double[50];
        this.maxwordStrings=new TreeMap<>();
    }

    public static void doubleArrayToStr(String[] array){
        for(int i=0;i<array.length;i++){
            System.out.print("Array["+i+"]: "+array[i]+"\n");
        }
    }

    public static void doubleArrayToStr(double[] array){
               for(int i=0;i<array.length;i++){
                   System.out.print("Array["+i+"]: "+array[i]+"\n");
               }
    }

    public void bigramHMMMatching(final String filepath, final DictHandling dicthandler, final TransliterationMethod transliterationMethod,final CharTypes chartype,final TestMethod testMethod) throws IOException, WekaException {
        this.initParsing(filepath, filepath.substring(filepath.lastIndexOf("/")+1,filepath.lastIndexOf('.'))+"_"+ ClassificationMethod.BIGRAMHMM.toString().toLowerCase()+"_"+transliterationMethod.getShortlabel().toLowerCase()+ Files.RESULT.toString(),"",null,dicthandler, ClassificationMethod.BIGRAMHMM,FeatureSets.NOFEATURE,transliterationMethod,chartype,testMethod);
    }

    private void bigramHMMMatching(LangChar tempchar,String currentline,final DictHandling dicthandler,final TransliterationMethod transliterationMethod,final CharTypes chartype) throws IOException {
        String tempchar1,tempchar2;
        int charlength=chartype.getChar_length();
        Boolean probOrFirst=true;
        LangChar tempword;
            System.out.println("Currentstr: "+currentline);
            String[] stringarray=new String[currentline.length()/charlength];
            for(int i=0;i<stringarray.length;i++){
                stringarray[i]=" ";
            }
            double[] probs=new double[currentline.length()/charlength];
            for(int i=0;i<currentline.length()-1;i+=charlength){
                probs[i-(i/charlength)]=0.;
                for(int j=i+charlength;j<=currentline.length();j+=charlength){
                    tempchar1= currentline.substring(i,i+charlength);
                    tempchar2= currentline.substring(i+charlength,j);
                    System.out.println("Tempchars: "+tempchar1+tempchar2);
                    //System.out.println(dicthandler.matchChar(tempchar1)+" "+dicthandler.matchChar(tempchar2));
                    if((tempword=dicthandler.matchChar(tempchar1))!=null){
                        //System.out.println(i-(i/2)-(tempword.length()-1)/2);

                        if((i-(i/charlength))-((tempword.length()-1)/charlength)<=0){
                            if(((CuneiDictHandler)dicthandler).isFollowingWord(tempword,tempchar2)){
                                System.out.print(tempword.getFollowingWords().get(tempchar2).getFollowing().getOne()+" - "+tempword.getFollowingWords().get(tempchar2).getFollowing().getTwo());
                                if(tempword.getFollowingWords().get(tempchar2).getFollowing().getOne()>tempword.getFollowingWords().get(tempchar2).getFollowing().getTwo()){
                                    probs[i-(i/charlength)]=tempword.getFollowingWords().get(tempchar2).getFollowing().getOne();
                                    stringarray[i-(i/charlength)]=tempchar1+tempchar2;
                                }else{
                                    probs[i-(i/charlength)]=tempword.getFollowingWords().get(tempchar2).getFollowing().getTwo();
                                    stringarray[i-(i/charlength)]=tempchar1+"  "+tempchar2;
                                }

                            }else{
                                probs[i-(i/charlength)]=0;
                                stringarray[i-(i/charlength)]=tempchar1+tempchar2;
                            }
                        }
                        //else if(tempword.getOccurances()+probs[i-(i/2)-(tempword.length()-1)/2]>probs[(i-1)/2]){

                        else if(((CuneiDictHandler)dicthandler).isFollowingWord(tempword,tempchar2)){
                            System.out.println("Following Word Check: "+tempword.getCharacter()+" - "+tempchar2);
                            System.out.println(((CuneiDictHandler)dicthandler).isFollowingWord(tempword,tempchar2));
                                System.out.println(tempword.getFollowingWords().get(tempchar2).getFollowing().getOne()+" - "+tempword.getFollowingWords().get(tempchar2).getFollowing().getTwo());
                                if(tempword.getFollowingWords().get(tempchar2).getFollowing().getOne()>tempword.getFollowingWords().get(tempchar2).getFollowing().getTwo()){
                                    probs[i-(i/charlength)]=tempword.getFollowingWords().get(tempchar2).getFollowing().getOne()+probs[i-(i/charlength)-tempword.length()/charlength];
                                    stringarray[i-(i/charlength)]=tempchar1+tempchar2;
                                }else{
                                    probs[i-(i/charlength)]=tempword.getFollowingWords().get(tempchar2).getFollowing().getTwo()+probs[i-(i/charlength)-tempword.length()/charlength];
                                    stringarray[i-(i/charlength)]=tempchar1+"] ["+tempchar2;
                                }

                            }else{
                                probs[i-(i/charlength)]=0+probs[i-(i/charlength)-tempword.length()/charlength];
                                stringarray[i-(i/charlength)]=tempchar1+tempchar2;
                            }
                        }
                        System.out.println("Probs: "+probs[i-(i/charlength)]);
                        System.out.println("Stringarray: "+stringarray[i-(i/charlength)]);

                }
            }
        /*String resultstr="";
        int length;
        if(stringarray.length>0){
            System.out.println("Stringarray["+0+"]: "+stringarray[0]);
            resultstr="["+stringarray[0]+"] ";
            tempword=dicthandler.matchWord(stringarray[0]);
            if(tempword!=null)
                this.translitResultWriter.write("["+tempword.getTransliterationSet().iterator().next()+"] ");
            length=stringarray[0].length()/2;
        }else{
            length=0;
        }
        for(int i=length;i<stringarray.length;){
            System.out.println("Stringarray["+i+"]: "+stringarray[i]);
            if(i==stringarray.length-1){
                resultstr+="["+currentline.substring(i*2)+"] ";
                tempword=dicthandler.matchChar(currentline.substring(i*2));
                if(tempword!=null)
                    this.translitResultWriter.write("["+((CuneiChar)tempword).getFirstSingleTransliteration()+"] ");
                i++;
            }
            else if(stringarray[i].length()/2==0){
                resultstr+="["+stringarray[i]+"] ";
                tempword=dicthandler.matchWord(stringarray[i]);
                if(tempword!=null)
                    this.translitResultWriter.write("["+tempword.getTransliterationSet().iterator().next()+"] ");
                i++;
            }else{
                resultstr+="["+stringarray[i]+"] ";
                tempword=dicthandler.matchWord(stringarray[i]);
                if(tempword!=null)
                    this.translitResultWriter.write("["+tempword.getTransliterationSet().iterator().next()+"] ");
                i+=stringarray[i].length()/2;
            }
        }
        System.out.println("Resultstring: " + resultstr);
        System.out.println("Originalstring: "+currentline);
        this.translitResultWriter.write("\n");*/
        for(int i=0;i<stringarray.length;i++){
            System.out.println("Stringarray["+i+"]: "+stringarray[i]);
        }
        String translit=this.assignTransliteration(stringarray.length<=0?new String[]{""}:stringarray[0].split(" "), dicthandler, transliterationMethod);
        this.cuneiResultWriter.write(stringarray[0]+" ");
        this.translitResultWriter.write(translit);
        this.transcriptResultWriter.write(TranscriptionMethods.translitTotranscript(translit));

    }

    private void bigramSegmenting(LangChar tempchar,String currentline,final DictHandling dicthandler,final TransliterationMethod transliterationMethod,final CharTypes chartype) throws IOException {
        String collectword="";
        int charlength=chartype.getChar_length();
        if(currentline.length()<2){
            return;
        }
        lastchar=dicthandler.matchChar(currentline.substring(0,charlength));
        collectword+=lastchar==null?"":lastchar.getCharacter();
        for(int i=charlength;i<=currentline.length()-charlength;i+=charlength){
            tempchar=dicthandler.matchChar(currentline.substring(i,i+charlength));
            System.out.println("Tempchar: "+lastchar);
            if(tempchar!=null){
                if(lastchar!=null){
                    if(!lastchar.getFollowingWords().containsKey(tempchar.getCharacter()) || lastchar.getFollowingWords().get(tempchar.getCharacter()).getFollowing().getOne()<lastchar.getFollowingWords().get(tempchar.getCharacter()).getFollowing().getTwo()){
                        collectword+=tempchar.getCharacter()+" ";
                    }else /*if(lastchar.getFollowingWords().get(tempchar.getCharacter()).getOne()>lastchar.getFollowingWords().get(tempchar.getCharacter()).getTwo())*/{
                        collectword+=tempchar.getCharacter();
                    }
                }
                lastchar=tempchar;
            }

        }
        String translit=this.assignTransliteration(collectword.split(" "), dicthandler, transliterationMethod);
        this.translitResultWriter.write(translit);
        this.cuneiResultWriter.write(collectword);
        this.transcriptResultWriter.write(TranscriptionMethods.translitTotranscript(translit));
    }

    public void bigramSegmenting(final String filepath, final DictHandling dicthandler, final TransliterationMethod transliterationMethod,final CharTypes chartype,final TestMethod testMethod) throws IOException, WekaException {
        this.initParsing(filepath, filepath.substring(filepath.lastIndexOf("/")+1,filepath.lastIndexOf('.'))+"_"+ ClassificationMethod.BIGRAM.toString().toLowerCase()+"_"+transliterationMethod.getShortlabel().toLowerCase()+ Files.RESULT.toString(),"",null,dicthandler, ClassificationMethod.BIGRAM,FeatureSets.NOFEATURE,transliterationMethod,chartype,testMethod);
    }

    @Override
    public void c45Segmenting(final String sourcepath, final String trainpath,final String modelfile, final DictHandling dicthandler, final FeatureSets featureSet,final TransliterationMethod transliterationMethod,final CharTypes chartype,final TestMethod testMethod) throws IOException, WekaException {
        this.initParsing(sourcepath,sourcepath.substring(sourcepath.lastIndexOf("/") + 1, sourcepath.lastIndexOf('.'))+"_"+ ClassificationMethod.C45.toString().toLowerCase()+"_"+featureSet.toString().toLowerCase()+"_"+transliterationMethod.getShortlabel().toLowerCase()+Files.RESULT.toString(),trainpath,modelfile,dicthandler, ClassificationMethod.C45,featureSet,transliterationMethod,chartype,testMethod);
    }

    @Override
    public void conditionalRandomFields(final String sourcepath,final String trainpath,final String modelfile, final DictHandling dicthandler,final FeatureSets featureSet,final TransliterationMethod transliterationMethod,final CharTypes chartype,final TestMethod testMethod) throws IOException, WekaException {
           this.initParsing(sourcepath,sourcepath.substring(sourcepath.lastIndexOf("/") + 1, sourcepath.lastIndexOf('.'))+"_"+ ClassificationMethod.CRF.toString().toLowerCase()+"_"+featureSet.toString().toLowerCase()+"_"+transliterationMethod.getShortlabel().toLowerCase()+Files.RESULT.toString(),trainpath,modelfile,dicthandler, ClassificationMethod.CRF,featureSet,transliterationMethod,chartype,testMethod);
    }

    /**
     * Segmentation against the highest occurance of words.
     * @param filepath the filepath to use
     * @param dict the dicthandler to use
     */
    public void highestOccuranceSegmenting(final String filepath,final DictHandling dict,final TransliterationMethod transliterationMethod) throws IOException {
        String temp,tempchar="";
        Integer j,bottomline;
        Map<Double,Set<String>> currentCharFrequencies;
        BufferedReader reader=new BufferedReader(new FileReader(new File(filepath)));
        while((temp=reader.readLine())!=null){
            bottomline=0;
            j=0;
            this.maxvalues=new double[200];
            this.maxwordStrings=new TreeMap<>();
            this.maxwordStrings.put(0,"");
            this.maxtranslitStrings=new TreeMap<>();
            this.maxtranslitStrings.put(0,"");
            currentCharFrequencies=new TreeMap<>();
             for(int i=0;i<temp.length()-1;i+=2) {
                 System.out.println(bottomline);
                 System.out.println(temp);
                 tempchar += temp.substring(i, i + 2);
                 System.out.println(temp.substring(i, i + 2));
                 currentCharFrequencies = dict.getFreqCandidatesForChar(tempchar);
                 System.out.println(currentCharFrequencies);
                 if (dict.matchWord(tempchar) != null || currentCharFrequencies.isEmpty()) {
                     bottomline += tempchar.replace(" ", "").length();
                     i = bottomline;
                     if (j == 0) {
                         this.maxvalues[j] = 0;
                     } else {
                         this.maxvalues[j] = this.maxvalues[j - 1];
                         this.maxwordStrings.put(j, this.maxwordStrings.get(j - 1) + " " + tempchar);
                         this.maxtranslitStrings.put(j, this.maxtranslitStrings.get(j - 1) + " " + dict.matchWord(tempchar.replace(" ", "")));
                     }
                     tempchar = tempchar.substring(tempchar.length() - 2);
                     j = bottomline / 2;
                     j++;
                     System.out.println("New J: " + j);
                     System.out.println("Maxvalue: " + this.maxvalue);
                     System.out.println("MaxString: " + this.maxwordStrings);
                     System.out.println("MaxString2: " + this.maxtranslitStrings);
                     continue;
                 } else if (dict.matchWord(tempchar) == null) {
                     j++;
                     continue;
                 }
             }
                 System.out.println("Bottomline: "+bottomline);
                 System.out.println("J: "+j);
                 for(Double value:currentCharFrequencies.keySet()){
                     System.out.println(currentCharFrequencies);
                     System.out.println("Tempchar: "+tempchar+": "+j+" :"+dict.matchWord(tempchar));
                     if(j>0)
                     System.out.println(this.maxvalues[j-1]);
                     if(j>0 && (value+this.maxvalues[j-1])>this.maxvalue && dict.matchWord(tempchar)!=null && tempchar.equals(currentCharFrequencies.get(value))){
                         this.maxvalue=value+this.maxvalues[j-1];
                         this.maxvalues[j]=this.maxvalues[j-1]+value;
                         this.maxvalue=this.maxvalues[j];
                         this.maxwordStrings.put(j,this.maxwordStrings.get(j-1)+" "+currentCharFrequencies.get(value).iterator().next());
                         this.maxtranslitStrings.put(j,this.maxtranslitStrings.get(j-1)+" "+dict.matchWord(tempchar.replace(" ","")).getTransliterationSet().iterator().next());
                         System.out.println("Maxvalue: "+this.maxvalue);
                         System.out.println("MaxString: "+this.maxwordStrings);
                         bottomline+=2;
                         tempchar="";
                     }else if(value>this.maxvalue){
                         this.maxvalue=value;
                         this.maxvalues[j]=value;
                         this.maxwordStrings.put(j,currentCharFrequencies.get(value).iterator().next());
                         this.maxtranslitStrings.put(j,this.maxtranslitStrings.get(j-1)+" "+dict.matchWord(tempchar.replace(" ","")));
                         System.out.println("Maxvalue: "+this.maxvalue);
                         System.out.println("MaxString: "+this.maxwordStrings);
                     }
                 }
                 j++;
                 System.out.println("Maxvalue: "+this.maxvalue);
                 System.out.println("MaxString: "+this.maxwordStrings);
                 System.out.println("MaxString2: "+this.maxtranslitStrings);
                 System.out.print("[");
                 for(Double d:this.maxvalues){
                     System.out.print(d+"    ,");
                 }
                 System.out.println("]");

             }
    }

    public void hmmSegmenting(final String sourcepath, final String trainpath,final String modelfile, final DictHandling dicthandler, final FeatureSets featureSet,final TransliterationMethod transliterationMethod,final CharTypes chartype,final TestMethod testMethod) throws IOException, WekaException {
        this.initParsing(sourcepath, sourcepath.substring(sourcepath.lastIndexOf("/")+1,sourcepath.lastIndexOf('.'))+"_"+ ClassificationMethod.HMM.toString().toLowerCase()+"_"+featureSet.toString().toLowerCase()+"_"+transliterationMethod.getShortlabel().toLowerCase()+Files.RESULT.toString(),trainpath,modelfile,dicthandler, ClassificationMethod.HMM,featureSet,transliterationMethod,chartype,testMethod);
    }

    public void initParsing(final String sourcepath,final String destfilename,String trainpath,final String modelfile,final DictHandling dicthandler,final ClassificationMethod method,final FeatureSets featureset,final TransliterationMethod transliterationMethod,final CharTypes chartype,final TestMethod testMethod) throws IOException, WekaException {
        this.initParsing(sourcepath,destfilename,trainpath,modelfile,dicthandler,method,featureset,transliterationMethod,chartype,testMethod,false);
    }
        /**
         * Initializes parameters needed for parsing.
         * @param sourcepath The path of the original file to classify (usually found in the testdata folder)
         * @param destfilename The path of the result file to manifest (usually to be in the results folder)
         * @param trainpath The path of trainingdata to use for the specific method (trainingdata may or may not be in the desired format yet)
         * @param dicthandler the dicthandler to use
         * @param method the classification method to use
         * @param featureset the featureset to use (if any)
         * @throws IOException
         */
    public void initParsing(final String sourcepath,final String destfilename,String trainpath,final String modelfile,final DictHandling dicthandler,final ClassificationMethod method,final FeatureSets featureset,final TransliterationMethod transliterationMethod,final CharTypes chartype,final TestMethod testMethod,Boolean arffOrBiGram) throws IOException, WekaException {
        String currentline;
        LangChar tempchar=null;
        int currentposition;
        Set<String> stopwords=new HashSet<String>(chartype.getStopchars());
        List<String> segments=new LinkedList<>();
        this.words.clear();
        this.wordboundaries.clear();
        this.reader=new BufferedReader(new FileReader(new File(sourcepath)));
        this.translitResultWriter =new BufferedWriter(new FileWriter(new File(Files.RESULTDIR.toString()+Files.TRANSLITDIR.toString()+destfilename)));
        this.transcriptResultWriter =new BufferedWriter(new FileWriter(new File(Files.RESULTDIR.toString()+Files.TRANSCRIPTDIR.toString()+destfilename)));
        this.cuneiResultWriter=new BufferedWriter(new FileWriter(new File(Files.RESULTDIR.toString()+Files.CUNEIFORMDIR.toString()+destfilename)));
        File trainingfile=new File("");
        File testfile=new File("");
        switch (method.getFramework()){
            case MALLET: trainingfile=new File(Files.TRAININGDATADIR.toString()+trainpath.substring(trainpath.lastIndexOf('/'),trainpath.lastIndexOf('.'))+"_"+featureset.toString().toLowerCase()+Files.MALLETSUFFIX);
                         testfile=new File(Files.TESTDATADIR.toString()+sourcepath.substring(sourcepath.lastIndexOf('/'),sourcepath.lastIndexOf('.'))+"_"+featureset.toString().toLowerCase()+Files.MALLETSUFFIX);
                break;
            case WEKA:trainingfile=new File(Files.TRAININGDATADIR.toString()+trainpath.substring(trainpath.lastIndexOf('/'),trainpath.lastIndexOf('.'))+"_"+featureset.toString().toLowerCase()+Files.ARFFSUFFIX);
                testfile=new File(Files.TESTDATADIR.toString()+sourcepath.substring(sourcepath.lastIndexOf('/'),sourcepath.lastIndexOf('.'))+"_"+featureset.toString().toLowerCase()+Files.ARFFSUFFIX);
                break;

        }
        System.out.println("Trainpath: "+trainpath+" Testfile: "+testfile.getAbsolutePath()+" Destfilename: "+destfilename+" Sourcepath: "+sourcepath+" Method: "+method+" FeatureSet: "+featureset.toString());
        /*if(trainpath.isEmpty()){
            trainpath="";
        }else{
            trainpath=trainpath.substring(trainpath.lastIndexOf('/'),trainpath.lastIndexOf('.'))+"_";
        }*/
        File boundaryfile=new File(Files.REFORMATTEDDIR.toString()+Files.BOUNDARYDIR.toString()+testMethod.toString().toLowerCase()+File.separator
                +sourcepath.substring(sourcepath.lastIndexOf('/'),sourcepath.lastIndexOf('.')));
        if(method.getHasFeatureSet() && !boundaryfile.exists()){
           this.fileToBoundaries(Files.REFORMATTEDDIR.toString()+Files.TRANSLITDIR.toString()+testMethod.toString().toLowerCase()+File.separator+
                   sourcepath.substring(sourcepath.lastIndexOf('/')+1),chartype,testMethod);
        }
        System.out.println("Trainingfile: "+trainingfile.getAbsolutePath());
        if(method.getHasFeatureSet() && !trainingfile.exists()){
            MainGUI.refreshProgressBarMessage(Main.bundle.getString("generateTrainTest"));
            this.setGenerator = new ArffHandler(trainpath, Files.REFORMATTEDDIR.toString() + Files.BOUNDARYDIR.toString()
                        + testMethod.toString().toLowerCase() + File.separator + trainpath.substring(trainpath.lastIndexOf('/') + 1)
                        , method.getFramework(), Options.TRAININGSET, featureset, dicthandler, chartype);
                int events=((ArffHandler)this.setGenerator).getEvents().length;
            if(method==ClassificationMethod.HMM){
                this.setGenerator=new HMMHandler();
                if(arffOrBiGram){
                    trainingfile=new File(((HMMHandler)this.setGenerator).parseArffFile(trainingfile.getAbsolutePath(),trainingfile.getAbsolutePath(),chartype,true));
                }else{
                    trainingfile=new File(((HMMHandler)this.setGenerator).parseToBiGram(trainpath.replace("cuneiform","cuneiform_segmented"),chartype,true,dicthandler));
                }
            }
        }else if(trainingfile.exists() && method==ClassificationMethod.HMM){
            File file=new File(trainingfile.getAbsolutePath().substring(0,trainingfile.getAbsolutePath().lastIndexOf('.'))+"_out.arff");
            if(!file.exists()){
                this.setGenerator=new HMMHandler();
                if(arffOrBiGram){
                    trainingfile=new File(((HMMHandler)this.setGenerator).parseArffFile(trainingfile.getAbsolutePath(),trainingfile.getAbsolutePath(),chartype,true));
                }else{
                    trainingfile=new File(((HMMHandler)this.setGenerator).parseToBiGram(trainpath.replace("cuneiform","cuneiform_segmented"),chartype,true,dicthandler));
                }
            }else{
                trainingfile=file;
            }
        }
        System.out.println("Testfile: "+testfile.getAbsolutePath());
        if(method.getHasFeatureSet() && !testfile.exists()){
            MainGUI.refreshProgressBarMessage(Main.bundle.getString("generateTrainTest"));
            this.setGenerator =new ArffHandler(sourcepath,Files.REFORMATTEDDIR.toString()+Files.BOUNDARYDIR.toString()
                    +testMethod.toString().toLowerCase()+File.separator+sourcepath.substring(sourcepath.lastIndexOf('/')+1), method.getFramework(),Options.TESTINGSET,featureset,dicthandler,chartype);
            int events=((ArffHandler)this.setGenerator).getEvents().length;
            if(method==ClassificationMethod.HMM){
                this.setGenerator=new HMMHandler();
                if(arffOrBiGram){
                    testfile=new File(((HMMHandler)this.setGenerator).parseArffFile(testfile.getAbsolutePath(),testfile.getAbsolutePath(),chartype,true));
                }else{
                    testfile=new File(((HMMHandler)this.setGenerator).parseToBiGram(sourcepath.replace("cuneiform","cuneiform_segmented"),chartype,true,dicthandler));
                }
            }
        }else if(testfile.exists() && method==ClassificationMethod.HMM){
            File file=new File(testfile.getAbsolutePath().substring(0,testfile.getAbsolutePath().lastIndexOf('.'))+"_out.arff");
            if(!file.exists()){
                this.setGenerator=new HMMHandler();
                if(arffOrBiGram){
                    testfile=new File(((HMMHandler)this.setGenerator).parseArffFile(testfile.getAbsolutePath(),testfile.getAbsolutePath(),chartype,true));
                }else{
                    testfile=new File(((HMMHandler)this.setGenerator).parseToBiGram(sourcepath.replace("cuneiform","cuneiform_segmented"),chartype,true,dicthandler));
                }
            }else{
                testfile=file;
            }
        }
        /*if(method==MethodEnum.MAXENT){
            this.setGenerator =new ArffHandler(Files.TESTDATADIR.toString()+ Files.CORPUSOUT.toString(),Files.CORPUSBOUNDARIESREFORMATTED.toString(),Options.NATIVE,Options.TRAININGSET,FeatureSets.MAXENT);
            EventStream evc =
                    new EventCollectorAsStream(setGenerator);
            this.gisModel = GIS.trainModel(evc, 100, 10);
        }*/
        //trainingfile=new File("");
        switch(method){
            case C45:
            case IB1:
            //case AODE:
            case SVM:
            case BAYESNET:
            case KMEANS:
            //case CLUSTERING_META:
            case PERCEPTRON:
            case LOGISTICREGRESSION:
            case LOGISTIC:
            case MAXENT:
            case NAIVEBAYES:
            //case NAIVEBAYESSIMPLE:
                //trainingfile=new File(Files.TRAININGDATADIR.toString()+trainpath.substring(trainpath.lastIndexOf('/'),trainpath.lastIndexOf('.'))+trainpath.substring(trainpath.lastIndexOf('.'))/*+Files.MALLETSUFFIX.toString()*/);
                //testfile=new File(Files.TESTDATADIR.toString()+testfile.getPath().substring(testfile.getPath().lastIndexOf('/'),testfile.getPath().lastIndexOf('.'))+testfile.getPath().substring(testfile.getPath().lastIndexOf('.')));
                System.out.println("Trainingfile: "+trainingfile.getPath());
                System.out.println("Testingfile: "+testfile.getPath());
            break;
            default:
        }
        System.out.println("Sourcepath: "+sourcepath);
        System.out.println("Trainpath: "+trainpath);
        this.mallet=new MalletMethods(sourcepath,dicthandler);
        try {
            this.weka=new WekaMethods(this.cuneiResultWriter,this.translitResultWriter,this.transcriptResultWriter,dicthandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch(method){
            case C45:
                try {
                    this.weka.classify(trainingfile, testfile,new File(sourcepath),(modelfile!=null&& !modelfile.isEmpty()?modelfile:null), ClassificationMethod.C45, featureset, 10,transliterationMethod,chartype);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CRF:  this.mallet.classify(trainpath,"testdata/first20_maxent.mallet", ClassificationMethod.CRF,featureset,10,transliterationMethod);break;
            //case DECISIONTREE: this.mallet.classify(sourcepath,destfilename, ClassificationMethod.DECISIONTREE,featureset,10,transliterationMethod);break;
            case HMM:  this.weka.classify(trainingfile, testfile,new File(sourcepath),(modelfile!=null&& !modelfile.isEmpty()?modelfile:null), ClassificationMethod.HMM, featureset, 10,transliterationMethod,chartype);break;
            //case MAXENT:this.mallet.classify(trainingfile.getPath(),testfile.getPath(),MethodEnum.MAXENT,FeatureSets.MAXENT,10);break;
            //case MAXMCENT:this.mallet.classify(sourcepath,destfilename, ClassificationMethod.MAXMCENT,featureset,10,transliterationMethod);break;
            case PERCEPTRON: this.weka.classify(trainingfile, testfile,new File(sourcepath),(modelfile!=null&& !modelfile.isEmpty()?modelfile:null), ClassificationMethod.PERCEPTRON, featureset, 10,transliterationMethod,chartype);break;
            case LOGISTIC: this.weka.classify(trainingfile, testfile,new File(sourcepath),(modelfile!=null&& !modelfile.isEmpty()?modelfile:null), ClassificationMethod.LOGISTIC, featureset, 10,transliterationMethod,chartype);break;
            case KMEANS: this.weka.classify(trainingfile, testfile,new File(sourcepath),(modelfile!=null&& !modelfile.isEmpty()?modelfile:null), ClassificationMethod.KMEANS, featureset, 10,transliterationMethod,chartype);break;
            case VOTE:  this.weka.classify(trainingfile, testfile,new File(sourcepath),(modelfile!=null&& !modelfile.isEmpty()?modelfile:null), ClassificationMethod.VOTE, featureset, 10,transliterationMethod,chartype);break;
            case IB1:
                this.weka.classify(trainingfile,testfile,new File(sourcepath),(modelfile!=null&& !modelfile.isEmpty()?modelfile:null), ClassificationMethod.IB1,featureset,10,transliterationMethod,chartype);
                break;
            case NAIVEBAYES:
                    this.weka.classify(trainingfile,testfile,new File(sourcepath),(modelfile!=null&& !modelfile.isEmpty()?modelfile:null), ClassificationMethod.NAIVEBAYES,featureset,10,transliterationMethod,chartype);
                break;
            case SVM:
                    this.weka.classify(trainingfile,testfile,new File(sourcepath),(modelfile!=null&& !modelfile.isEmpty()?modelfile:null), ClassificationMethod.SVM,featureset,10,transliterationMethod,chartype);
                break;
            //case WINNOW:this.mallet.classify(sourcepath, destfilename, ClassificationMethod.WINNOW, featureset, 10,transliterationMethod);break;
            default:
                while((currentline=this.reader.readLine())!=null){
                    segments.clear();
                    currentposition=0;
                    this.linecounter++;
                    for(int i=0;i<currentline.length()-chartype.getChar_length();i+=chartype.getChar_length()){
                        String currentchar=currentline.substring(i,i+=chartype.getChar_length());
                        if(stopwords.contains(currentchar)){
                            segments.add(currentline.substring(currentposition,i));
                            segments.add(currentchar);
                            currentposition=i+2;
                        }
                    }
                    if(segments.isEmpty()){
                        segments.add(currentline);
                    }
                    for(String segment:segments) {
                        this.linecounter++;
                        switch (method) {
                            case MAXPROB:
                                this.maxProbSegmenting(tempchar, currentline, dicthandler, transliterationMethod, chartype);
                                break;
                            case BIGRAM:
                                this.bigramSegmenting(tempchar, currentline, dicthandler, transliterationMethod, chartype);
                                break;
                            case BIGRAMHMM:
                                this.bigramHMMMatching(tempchar, currentline, dicthandler, transliterationMethod, chartype);
                                break;
                            case MAXENT:
                                this.maxEntropyMatching(tempchar, currentline, dicthandler, transliterationMethod, chartype);
                                break;
                            default:
                        }
                    }
                    this.transcriptResultWriter.write("\n");
                    this.cuneiResultWriter.write("\n");
                    this.translitResultWriter.write("\n");
            }
        }
        int sumwordboundaries=0;
        for(int j:wordboundaries){
            sumwordboundaries+=j;
        }
        System.out.println("Sum of Word Boundaries: "+sumwordboundaries);
        this.translitResultWriter.close();
        this.transcriptResultWriter.close();
        this.cuneiResultWriter.close();
        this.reader.close();
    }

    public void kmeans(final String sourcepath, final String trainpath,final String modelfile, final DictHandling dicthandler, final FeatureSets featureSet,final TransliterationMethod transliterationMethod,final CharTypes chartype,final TestMethod testMethod) throws IOException, WekaException {
        this.initParsing(sourcepath, sourcepath.substring(sourcepath.lastIndexOf("/")+1,sourcepath.lastIndexOf('.'))+"_"+ ClassificationMethod.KMEANS.toString().toLowerCase()+"_"+featureSet.toString().toLowerCase()+"_"+transliterationMethod.getShortlabel().toLowerCase()+Files.RESULT.toString(),trainpath,modelfile,dicthandler, ClassificationMethod.KMEANS,featureSet,transliterationMethod,chartype,testMethod);
    }

    public void knn(final String sourcepath, final String trainpath, final String modelfile, final DictHandling dicthandler, final FeatureSets featureSet,final TransliterationMethod transliterationMethod,final CharTypes chartype,final TestMethod testMethod) throws IOException, WekaException {
        this.initParsing(sourcepath, sourcepath.substring(sourcepath.lastIndexOf("/")+1,sourcepath.lastIndexOf('.'))+"_"+ ClassificationMethod.IB1.toString().toLowerCase()+"_"+featureSet.toString().toLowerCase()+"_"+transliterationMethod.getShortlabel().toLowerCase()+Files.RESULT.toString(),trainpath,modelfile,dicthandler, ClassificationMethod.IB1,featureSet,transliterationMethod,chartype,testMethod);
    }

    public void logisticRegression(final String sourcepath, final String trainpath,final String modelfile, final DictHandling dicthandler, final FeatureSets featureSet, final TransliterationMethod transliterationMethod, final CharTypes chartype, final TestMethod testMethod) throws IOException, WekaException {
        this.initParsing(sourcepath, sourcepath.substring(sourcepath.lastIndexOf("/")+1,sourcepath.lastIndexOf('.'))+"_"+ClassificationMethod.LOGISTIC.toString().toLowerCase()+"_"+featureSet.toString().toLowerCase()+"_"+transliterationMethod.getShortlabel().toLowerCase()+ Files.RESULT.toString(),trainpath,modelfile,dicthandler, ClassificationMethod.LOGISTIC,featureSet,transliterationMethod,chartype,testMethod);
    }

    @Override
    public void maxEntropyMatching(final String sourcepath,final String trainpath,final String modelfile,final DictHandling dicthandler,final FeatureSets featureSet,final TransliterationMethod transliterationMethod,final CharTypes chartype,final TestMethod testMethod) throws IOException, WekaException {
        this.initParsing(sourcepath, sourcepath.substring(sourcepath.lastIndexOf("/")+1,sourcepath.lastIndexOf('.'))+"_"+ClassificationMethod.MAXENT.toString().toLowerCase()+"_"+featureSet.toString().toLowerCase()+"_"+transliterationMethod.getShortlabel().toLowerCase()+ Files.RESULT.toString(),trainpath,modelfile,dicthandler, ClassificationMethod.MAXENT,featureSet,transliterationMethod,chartype,testMethod);
    }

    /**
     * Implements max entropy matching.
     * @param tempchar
     * @param currentline
     * @param dicthandler
     * @throws IOException
     */
    private void maxEntropyMatching(LangChar tempchar,final String currentline, final DictHandling dicthandler,final TransliterationMethod transliterationMethod,final CharTypes chartype) throws IOException {
        String writeline="";
        String writeline2="[";
        String collectword="";
        int charlength=chartype.getChar_length();
        String[] spaceToks = currentline.split("(?<=\\\\G..)");
        ArffHandler.arrayToStr(spaceToks);
        String segmented="";
        for(int i=0;i<spaceToks.length;i++){
            if(spaceToks[i]=="0"){
                segmented+=currentline.substring(i,i+chartype.getChar_length());
            }else{
                segmented+=currentline.substring(i,i+chartype.getChar_length())+" ";
            }
        }
        for (int tok=0; tok<spaceToks.length; tok++) {
            StringBuffer sb = new StringBuffer(spaceToks[tok]);
            for (int id=0; id<sb.length(); id+=charlength) {
                writeline+=sb.substring(id,id+charlength);
                collectword+=sb.substring(id,id+charlength);
                if(dicthandler.matchChar(sb.substring(id,id+charlength))!=null && !dicthandler.matchChar(sb.substring(id,id+charlength)).getTransliterationSet().isEmpty())
                    writeline2+=dicthandler.matchChar(sb.substring(id,id+charlength)).getTransliterationSet().iterator().next();
                System.out.println(sb.substring(id,id+charlength));
                String[] context =new FeatureSetManager().getContext(new Tuple<>(sb, id), FeatureSets.MAXENT,segmented, dicthandler, chartype);
                System.out.println(this.gisModel.getOutcome(0) + " " + this.gisModel.getOutcome(1));
                double[]eval=this.gisModel.eval(context);
                if(eval[1]>eval[0]){
                    this.translitResultWriter.write(dicthandler.getNoDictTransliteration(collectword,transliterationMethod));
                    this.cuneiResultWriter.write(collectword);
                    writeline+="--";
                    writeline2+="] [";
                    collectword="";
                }else{
                    writeline2+="-";
                }
                System.out.print("[");
                for(Double doub:eval){
                    System.out.print(doub+",");
                }
                System.out.println("]");
            }
        }
        if(writeline2.substring(writeline2.length()-1).equals("-")){
            writeline2=writeline2.substring(0,writeline2.length()-1);
        }else if(writeline2.length()>3 && writeline2.substring(writeline2.length()-1).equals("[")){
            writeline2=writeline2.substring(0,writeline2.length()-4);
        }
        if(!collectword.isEmpty()) {
            translitResultWriter.write(dicthandler.getNoDictTransliteration(collectword, transliterationMethod));
            this.cuneiResultWriter.write(collectword);
        }
        //translitResultWriter.write(writeline2+"]\n");
        System.out.println("Line: "+currentline);
        System.out.println("Line: " + writeline);
        if(writeline2.length()>1){
            System.out.println("Line: "+writeline2.substring(0,writeline2.length()-1)+"]");
        }
    }

    /**
     * Implements max probability segmenting.
     * @param tempword
     * @param currentline
     * @param dicthandler
     * @throws IOException
     */
    private String[] maxProbSegmenting(LangChar tempword,final String currentline, final DictHandling dicthandler,final TransliterationMethod transliterationMethod,final CharTypes chartype) throws IOException {
        String tempwordstr;
        int charlength=chartype.getChar_length();
        Boolean probOrFirst=true;
        System.out.println("Currentstr: "+currentline+" ("+currentline.length()+")");
        String[] stringarray=new String[currentline.length()/charlength];
        for(int i=0;i<stringarray.length;i++){
            stringarray[i]=" ";
        }
        double[] probs=new double[(currentline.length()/charlength)+1];
        for(int i=currentline.length()-charlength;i>-1;i-=charlength){
            int val=charlength==1?i:i-(i/charlength);
            probs[val]=0.;
            for(int j=i+charlength;j<=currentline.length();j+=charlength){
                tempwordstr= currentline.substring(i,j);
                if((tempword=dicthandler.matchWord(tempwordstr))!=null){
                    if(val-((tempword.length()-1)/charlength)<=0){
                        probs[val]=tempword.getOccurances()+probs[0];
                        stringarray[val]=tempwordstr;
                    }
                    else if(tempword.getOccurances()+probs[val-(tempword.length()-1)/charlength]>probs[val]){
                        probs[val]=tempword.getOccurances()+probs[val-(tempword.length()-1)/charlength];
                        stringarray[val]=tempwordstr;
                    }
                }else if((tempword=dicthandler.matchChar(tempwordstr))!=null){
                    if((val)-((tempword.length()-1)/charlength)<=0){
                        probs[val]=1.;
                        stringarray[val]=tempwordstr;
                    }
                    else if(tempword.getOccurances()+probs[val-(tempword.length()-1)/charlength]>=probs[val]){
                        probs[val]=1.+probs[val-(tempword.length()-1)/charlength];
                        stringarray[val]=tempwordstr;
                    }
                }
            }
        }

        System.out.println("Resultarray Maxprob: ");
        doubleArrayToStr(stringarray);
        doubleArrayToStr(probs);
        List<String> result=new LinkedList<String>();
        for(int i=0;i<stringarray.length;){
            this.cuneiResultWriter.write(stringarray[i]+" ");
            result.add(stringarray[i]);
            i+=(stringarray[i].length()/charlength)==0?1:(stringarray[i].length()/charlength);
        }
        System.out.println("Result:"+result);
        for(int i=0;i<result.size();i++){
            System.out.println("Result["+i+"] "+result.get(i));
        }
        String translit=this.assignTransliteration(result.toArray(new String[result.size()]), dicthandler, transliterationMethod);
        this.translitResultWriter.write(translit);
        this.transcriptResultWriter.write(TranscriptionMethods.translitTotranscript(translit));
        return new String[]{"",translit};
    }

    @Override
    public void maxProbSegmenting(final String filepath, final DictHandling dicthandler,final TransliterationMethod transliterationMethod,final CharTypes chartype,final TestMethod testMethod) throws IOException, WekaException {
        this.initParsing(filepath, filepath.substring(filepath.lastIndexOf("/")+1,filepath.lastIndexOf('.'))+"_"+ ClassificationMethod.MAXPROB.toString().toLowerCase()+"_"+transliterationMethod.getShortlabel().toLowerCase()+ Files.RESULT.toString(),"",null,dicthandler, ClassificationMethod.MAXPROB,FeatureSets.NOFEATURE,transliterationMethod,chartype,testMethod);
    }

    @Override
    public void naiveBayesSegmenting(final String sourcepath, final String trainpath,final String modelfile, final DictHandling dicthandler, final FeatureSets featureSet,final TransliterationMethod transliterationMethod,final CharTypes chartype,final TestMethod testMethod) throws IOException, WekaException {
        this.initParsing(sourcepath, sourcepath.substring(sourcepath.lastIndexOf("/")+1,sourcepath.lastIndexOf('.'))+"_"+ ClassificationMethod.NAIVEBAYES.toString().toLowerCase()+"_"+featureSet.toString().toLowerCase()+"_"+transliterationMethod.getShortlabel().toLowerCase()+ Files.RESULT.toString(),trainpath,modelfile,dicthandler, ClassificationMethod.NAIVEBAYES,featureSet,transliterationMethod,chartype,testMethod);
    }

    public void perceptron(final String sourcepath, final String trainpath,final String modelfile, final DictHandling dicthandler, final FeatureSets featureSet, final TransliterationMethod transliterationMethod, final CharTypes chartype, final TestMethod testMethod) throws IOException, WekaException {
        this.initParsing(sourcepath, sourcepath.substring(sourcepath.lastIndexOf("/")+1,sourcepath.lastIndexOf('.'))+"_"+ ClassificationMethod.PERCEPTRON.toString().toLowerCase()+"_"+featureSet.toString().toLowerCase()+"_"+transliterationMethod.getShortlabel().toLowerCase()+ Files.RESULT.toString(),trainpath,modelfile,dicthandler, ClassificationMethod.PERCEPTRON,featureSet,transliterationMethod,chartype,testMethod);

    }

    @Override
    public void svmSegmenting(final String sourcepath, final String trainpath,final String modelfile, final DictHandling dicthandler, final FeatureSets featureSet,final TransliterationMethod transliterationMethod,final CharTypes chartype,final TestMethod testMethod) throws IOException, WekaException {
        this.initParsing(sourcepath, sourcepath.substring(sourcepath.lastIndexOf("/")+1,sourcepath.lastIndexOf('.'))+"_"+ ClassificationMethod.SVM.toString().toLowerCase()+"_"+featureSet.toString().toLowerCase()+"_"+transliterationMethod.getShortlabel().toLowerCase()+Files.RESULT.toString(),trainpath,modelfile,dicthandler, ClassificationMethod.SVM,featureSet,transliterationMethod,chartype,testMethod);
    }

    public void voteSegmenting(final String sourcepath, final String trainpath,final String modelfile, final DictHandling dicthandler, final FeatureSets featureSet,final TransliterationMethod transliterationMethod,final CharTypes chartype,final TestMethod testMethod) throws IOException, WekaException {
        this.initParsing(sourcepath, sourcepath.substring(sourcepath.lastIndexOf("/")+1,sourcepath.lastIndexOf('.'))+"_"+ ClassificationMethod.VOTE.toString().toLowerCase()+"_"+featureSet.toString().toLowerCase()+"_"+transliterationMethod.getShortlabel().toLowerCase()+Files.RESULT.toString(),trainpath,modelfile,dicthandler, ClassificationMethod.VOTE,featureSet,transliterationMethod,chartype,testMethod);
    }

    @Override
    public void winnowSegmenting(final String sourcepath, final String trainpath, final DictHandling dicthandler, final FeatureSets featureSet,final TransliterationMethod transliterationMethod,final CharTypes chartype,final TestMethod testMethod) throws IOException, WekaException {
        //this.initParsing(sourcepath, sourcepath.substring(sourcepath.lastIndexOf("/")+1,sourcepath.lastIndexOf('.'))+"_"+ ClassificationMethod.WINNOW.toString().toLowerCase()+"_"+featureSet.toString().toLowerCase()+"_"+transliterationMethod.getShortlabel().toLowerCase()+Files.RESULT.toString(),trainpath,dicthandler, ClassificationMethod.WINNOW,featureSet,transliterationMethod,chartype,testMethod);
    }

}
