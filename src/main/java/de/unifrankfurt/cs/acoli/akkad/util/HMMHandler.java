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

package de.unifrankfurt.cs.acoli.akkad.util;

import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.ClassificationMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.FeatureSets;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.TransliterationMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Files;
import org.xml.sax.SAXException;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.HMM;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Debug;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.*;

/**
 * Created by timo on 1/2/15.
 * Implements the generation of a HMM FeatureSet.
 */
public class HMMHandler implements ArffGen{

    Integer entry;
    Map<String,Integer> intToStrMap;
    List<String> lines;
    List<String> linesNeg;

    public HMMHandler(){
        this.intToStrMap=new TreeMap<>();
        this.lines=new LinkedList<>();
        this.linesNeg=new LinkedList<>();
        this.entry=0;
    }

    public static void main(String[] args) throws IOException {
        HMMHandler handler=new HMMHandler();
        handler.parseArffFile("trainingdata/first20_crf.arff","trainingdata/first20_crf.arff",CharTypes.AKKADIAN,true);
        handler.parseArffFile("trainingdata/first20_crf.arff","trainingdata/first20_crf.arff",CharTypes.AKKADIAN,false);
        handler.parseToBiGram("reformatted/cuneiform_segmented/foreigntext/Lcmc2_reformatted.txt",CharTypes.CHINESE,true,null);
        handler.parseToBiGram("reformatted/cuneiform_segmented/foreigntext/Lcmc2Test_reformatted.txt",CharTypes.CHINESE,true,null);
        ConverterUtils.DataSource datasource = null;
        try {
            datasource = new ConverterUtils.DataSource("reformatted/cuneiform_segmented/foreigntext/Lcmc2_reformatted_out_bi.arff");
            Instances trainingSet = datasource.getDataSet();
            trainingSet.setClassIndex(2);
            datasource = new ConverterUtils.DataSource("reformatted/cuneiform_segmented/foreigntext/Lcmc2Test_reformatted_out_bi.arff");
            Instances testSet = datasource.getDataSet();
            testSet.setClassIndex(2);
            FilteredClassifier classifier=new FilteredClassifier();
            classifier.setClassifier(new HMM());
            classifier.buildClassifier(trainingSet);
            Debug.saveToFile("testhmm.model", classifier);
            Evaluation eval = new Evaluation(trainingSet);
            eval.evaluateModel(classifier, testSet);
            Instances labeled = new Instances(testSet);
            System.out.println("TestSet Instances: "+testSet.numInstances());
            BufferedWriter writer =new BufferedWriter(new FileWriter(new File("result.txt")));
            // label instances
            for (int i = 0; i < testSet.numInstances(); i++) {
                if(i%500==0) {
                    System.out.println("Instance " + i);
                    //MainGUI.refreshProgressBarMessage(Main.bundle.getString("classifying") + " " + method+" ("+ method+Main.bundle.getString("instance")+" "+i+" "+Main.bundle.getString("of")+" "+testSet.numInstances()+")");
                }
                double clsLabel = classifier.classifyInstance(testSet.instance(i));
                System.out.println(clsLabel);
                labeled.instance(i).setClassValue(clsLabel);
                writer.write(clsLabel+"");
            }
            writer.close();


        BufferedWriter outwriter = new BufferedWriter(
                new FileWriter("resultfile"));
        outwriter.write(labeled.toString());
        outwriter.newLine();
            outwriter.flush();
            outwriter.close();
        ClassificationMethod method=ClassificationMethod.HMM;
        FeatureSets featureSet=FeatureSets.CRF;
        CharTypes charType=CharTypes.CHINESE;
        BufferedWriter cuneiResultWriter=new BufferedWriter(new FileWriter(new File("realres")));
        BufferedReader reader=new BufferedReader(new FileReader(new File("reformatted/cuneiform/foreigntext/Lcmc2_reformatted.txt")));
            BufferedReader reader2=new BufferedReader(new FileReader(new File("resultfile")));
            String temp,temp2,towrite,translit;
            while(!reader2.readLine().contains("@data"));

            while((temp=reader.readLine())!=null){
            boolean[] values=new boolean[temp.length()/charType.getChar_length()];
            //for(int i=0;i<temp.length();i++){
            int j=0;
            for(int i=charType.getChar_length();i<=temp.length()-charType.getChar_length();i+=charType.getChar_length()) {
                temp2 = reader2.readLine();
                System.out.println("Wekaline: " + temp2);
                //System.out.println("CharA(0)="+temp2.charAt(0)+" "+temp2.charAt(1));
                if (temp2 != null && method==ClassificationMethod.HMM) {
                    System.out.println(temp2.contains(",2 1")+" !");
                    values[j] = temp2.contains(",2 1");
                } else if (temp2 != null && featureSet != FeatureSets.META)
                    values[j] = temp2.charAt(1) == '0';
                else if(temp2!=null){
                    values[j]=temp2.charAt(0)=='1';
                }
                j++;
            }
            towrite=retransFormResultToCunei(temp, values, charType);
            System.out.println("ToWrite: " + towrite);
            cuneiResultWriter.write(towrite + " \n");
            //translit=this.assignTransliteration(towrite.split(" "), this.dictHandler, transliterationMethod);
            //this.translitResultWriter.write(translit + " \n");
            //this.transcriptResultWriter.write(TranscriptionMethods.translitTotranscript(translit) + " \n");
        }
        reader.close();
        reader2.close();
        cuneiResultWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*try {
            datasource = new ConverterUtils.DataSource("trainingdata/first20_crf_out.arff");
            Instances trainingSet = datasource.getDataSet();
            trainingSet.setClassIndex(2);

            FilteredClassifier classifier=new FilteredClassifier();
            classifier.setClassifier(new HMM());
            classifier.buildClassifier(trainingSet);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    /**
     * Retransforms a classification result to cuneiform using the original text file.
     * @param cuneiline the current line to retransform
     * @param wekaline  the line containing booleans for segmentations
     * @param charType the language to use
     * @return the retransformed String
     */
    public static String retransFormResultToCunei(String cuneiline,boolean[] wekaline,CharTypes charType){
        StringBuffer resultbuffer=new StringBuffer(cuneiline.length());
        System.out.print("Wekaline: ");
        for(int i=0;i<wekaline.length;i++){
            System.out.println(wekaline[i]);
        }
        int j=0;
        for(int i=0;i<=cuneiline.length()-charType.getChar_length();i+=charType.getChar_length()){
            resultbuffer.append(cuneiline.substring(i,i+charType.getChar_length()));
            if(wekaline[j++]){
                resultbuffer.append(" ");
            }
        }
        return resultbuffer.toString();
    }

    /**
     * Parses an arff file that has been converted to a StringToWordVector.
     * @param testfile
     */
    public String parseArffFile(final String testfile,final String testfileWOStrToWord,final CharTypes charTypes,Boolean trainOrTest) throws IOException {
        BufferedReader reader=new BufferedReader(new FileReader(new File(testfile)));
        BufferedWriter writer;
        String path=testfile.substring(0,testfile.lastIndexOf("."))+"_out.arff";
        if(!trainOrTest){
            path=testfile.substring(0,testfile.lastIndexOf("."))+"_out_test.arff";
        }
        writer=new BufferedWriter(new FileWriter(new File(path)));
        String temp;
        int i=0;
        while((temp=reader.readLine())!=null){
             if(temp.contains("@attribute") && !temp.contains("class")){
                 writer.write(temp+System.lineSeparator());
                 this.intToStrMap.put(temp.substring(temp.indexOf("@attribute "),temp.indexOf("numeric")).trim(),i++);
             }
        }
        reader.close();
        reader=new BufferedReader(new FileReader(new File(testfileWOStrToWord)));
        boolean start=false,posneg=false,att=false;
        String collect;
        String classs="";
        while((temp=reader.readLine())!=null){
            collect="";
            if(temp.startsWith("@data")){
               start=true;
               writer.write("@end bag"+System.lineSeparator());
                if(trainOrTest)
                    writer.write(classs+System.lineSeparator());
                writer.write("@data"+System.lineSeparator());
               continue;
            }
            if(start){
                for(String str:temp.split(",")){
                    if(!str.contains("'") && str.contains("0")){
                       posneg=false;
                    }else if(!str.contains("'") && str.contains("1")){
                       posneg=true;
                    }
                    if(str.contains("'")){
                        collect+=this.replaceCharsWithNumber(str.replace("'",""))+",";
                    }
                }
                System.out.println("Collect: "+collect);
                if(posneg){
                    this.lines.add(collect.substring(0,collect.length()-1));
                }else{
                    this.linesNeg.add(collect.substring(0,collect.length()-1));
                }
            }else{
                System.out.println("Temp: "+temp);
                if(!att && temp.contains("@ATTRIBUTE")){
                    classs=temp+System.lineSeparator();
                    writer.write("@attribute molecule_name {MUSK-nonsep,MUSK-sep}"+System.lineSeparator());
                    writer.write("@attribute bag relational"+System.lineSeparator());
                    att=true;
                }else{
                    writer.write("  "+temp.replace("string","numeric")+System.lineSeparator());
                }

            }
        }
        System.out.println(this.lines);

        for(String line:this.lines){
            writer.write("MUSK-sep,\"");
            if(trainOrTest)
                writer.write(line+"\",1"+System.lineSeparator());
            else{
                writer.write(line+"\""+System.lineSeparator());
            }
        }
        //writer.write("\",1"+System.lineSeparator());

        for(String line:this.linesNeg){
            writer.write("MUSK-nonsep,\"");
            if(trainOrTest)
                writer.write(line+"\",0"+System.lineSeparator());
            else
                writer.write(line+"\""+System.lineSeparator());
        }
        reader.close();
        writer.close();
        return path;
    }

    public String parseToBiGram(final String testfile, final CharTypes akkadian,Boolean trainOrTest,final DictHandling dictHandling) throws IOException {
        BufferedReader reader=new BufferedReader(new FileReader(new File(testfile)));
        BufferedWriter writer;
        String path=testfile.substring(0,testfile.lastIndexOf("."))+"_out_bi.arff";
        if(!trainOrTest){
           path=testfile.substring(0,testfile.lastIndexOf("."))+"_out_bi_test.arff";
        }
        writer=new BufferedWriter(new FileWriter(new File(path)));
        int counter=MetaArffHandler.countLines(testfile);
        writer.write("@relation CRF"+System.lineSeparator()+"@attribute molecule_name {");

        System.out.println(
                System.lineSeparator()+"@attribute bag relational"+System.lineSeparator()+"@attribute att0 numeric"+
                System.lineSeparator()+"@attribute att1 numeric"+System.lineSeparator()+
                "@attribute att2 numeric"+System.lineSeparator()+
                "@attribute att3 numeric"+System.lineSeparator()+
                "@attribute att4 numeric"+System.lineSeparator()+
                "@attribute att5 numeric"+System.lineSeparator()+
                "@attribute att6 numeric"+System.lineSeparator()+
                "@attribute att7 numeric"+System.lineSeparator()+
                "@end bag"+System.lineSeparator());
        /*if(trainOrTest)
            writer.write("@attribute class {0,1}"+System.lineSeparator());
        writer.write("@data"+System.lineSeparator());*/
        String temp,lastword;
        int countLines=0;
        boolean withsep=false;
        StringBuilder sep=new StringBuilder(),nonsep=new StringBuilder(),filebuilder=new StringBuilder();
        sep.append("MUSK-sep,\"");
        nonsep.append("MUSK-nonsep,\"");

        while((temp=reader.readLine())!=null){
            lastword="";
            for(String word:temp.split(" ")){
                System.out.println("Temp: "+word);
                for(int i=akkadian.getChar_length();i<=word.length()-akkadian.getChar_length();i+=akkadian.getChar_length()){
                    String one="",two="";
                    one=word.substring(i-akkadian.getChar_length(),i);
                    two=word.substring(i,i+akkadian.getChar_length());
                    //Integer[] values=new Integer[]{((dictHandling.matchChar(one)!=null && dictHandling.matchChar(one).getFollowingWords()!=null && dictHandling.matchChar(one).getFollowingWords().containsKey(two))?dictHandling.matchChar(one).getFollowingWords().get(two).getFollowing().getOne().intValue():0),
                    //               ((dictHandling.matchChar(one)!=null && dictHandling.matchChar(one).getFollowingWords()!=null && dictHandling.matchChar(one).getFollowingWords().containsKey(two))?dictHandling.matchChar(one).getFollowingWords().get(two).getFollowing().getTwo().intValue():0)
                            /*0,
                            ((dictHandling.matchChar(one)!=null && dictHandling.matchChar(one).getFollowingWords()!=null && dictHandling.matchChar(one).getFollowingWords().containsKey(two))?dictHandling.matchChar(one).getFollowingWords().get(two).getBoundariesFollowContinuations().intValue():0),
                            ((dictHandling.matchChar(one)!=null && dictHandling.matchChar(one).getFollowingWords()!=null && dictHandling.matchChar(one).getFollowingWords().containsKey(two))?dictHandling.matchChar(one).getFollowingWords().get(two).getContinuationFollowsBoundary().intValue():0),
                            ((dictHandling.matchChar(one)!=null && dictHandling.matchChar(one).getFollowingWords()!=null && dictHandling.matchChar(one).getFollowingWords().containsKey(two))?dictHandling.matchChar(one).getFollowingWords().get(two).getContinuationFollowsContinuation().intValue():0)*/

                    //};
                    //values[2]=(values[0]+values[1])-(values[3]+values[4]+values[5]);
                    String valStr=one+","+two;//values[0]+","+values[1];//+","+values[2]+","+values[3]+","+values[4]+","+values[5];
                    System.out.println(valStr);
                    /*System.out.println(","+
                                )+","++
                            ","+((dictHandling.matchChar(one)!=null && dictHandling.matchChar(one).getFollowingWords()!=null && dictHandling.matchChar(one).getFollowingWords().containsKey(two))?dictHandling.matchChar(one).getFollowingWords().get(two).getBoundariesFollowBoundaries().intValue():0)+
                            ","++
                            ","++
                            ","+
                    );*/
                    if(trainOrTest) {
                        nonsep.append(this.replaceCharsWithNumber(one) + ","
                                + this.replaceCharsWithNumber(two) /*+ "," + valStr/*+","+((dictHandling.matchChar(one)!=null && dictHandling.matchChar(one).getFollowingWords()!=null && dictHandling.matchChar(one).getFollowingWords().containsKey(two))?dictHandling.matchChar(one).getFollowingWords().get(two).getFollowing().getOne() .intValue()
                                :0)+","+((dictHandling.matchChar(one)!=null && dictHandling.matchChar(one).getFollowingWords()!=null && dictHandling.matchChar(one).getFollowingWords().containsKey(two))?dictHandling.matchChar(one).getFollowingWords().get(two).getFollowing().getTwo().intValue():0)*/
                                //+"\",0"+System.lineSeparator());
                                + "\n");
                        if(!withsep) {
                            filebuilder.append("seq-"+countLines+++",\"" + this.replaceCharsWithNumber(one) + ","
                                    + this.replaceCharsWithNumber(two) /*+ "," + valStr/*+","+((dictHandling.matchChar(one)!=null && dictHandling.matchChar(one).getFollowingWords()!=null && dictHandling.matchChar(one).getFollowingWords().containsKey(two))?dictHandling.matchChar(one).getFollowingWords().get(two).getFollowing().getOne() .intValue()
                                :0)+","+((dictHandling.matchChar(one)!=null && dictHandling.matchChar(one).getFollowingWords()!=null && dictHandling.matchChar(one).getFollowingWords().containsKey(two))?dictHandling.matchChar(one).getFollowingWords().get(two).getFollowing().getTwo().intValue():0)*/
                                    //+"\",0"+System.lineSeparator());
                                    + "\",0" + System.lineSeparator());
                        }
                    }else{
                        nonsep.append(this.replaceCharsWithNumber(one) + ","
                                + this.replaceCharsWithNumber(two) /*+ "," + valStr/*+","+((dictHandling.matchChar(one)!=null && dictHandling.matchChar(one).getFollowingWords()!=null && dictHandling.matchChar(one).getFollowingWords().containsKey(two))?dictHandling.matchChar(one).getFollowingWords().get(two).getFollowing().getOne() .intValue()
                                :0)+","+((dictHandling.matchChar(one)!=null && dictHandling.matchChar(one).getFollowingWords()!=null && dictHandling.matchChar(one).getFollowingWords().containsKey(two))?dictHandling.matchChar(one).getFollowingWords().get(two).getFollowing().getTwo().intValue():0)*/
                                //+"\",0"+System.lineSeparator());
                                + "\n");
                        if(!withsep) {
                            filebuilder.append("seq-"+countLines+++",\""+this.replaceCharsWithNumber(one) + "," + this.replaceCharsWithNumber(two) + "," + valStr/*+","
                                +((dictHandling.matchChar(one)!=null && dictHandling.matchChar(one).getFollowingWords()!=null
                                && dictHandling.matchChar(one).getFollowingWords().containsKey(two))?dictHandling.matchChar(one).getFollowingWords().get(two).getFollowing().getOne().intValue()
                                :0)+","+((dictHandling.matchChar(one)!=null && dictHandling.matchChar(one).getFollowingWords()!=null && dictHandling.matchChar(one).getFollowingWords().containsKey(two))?dictHandling.matchChar(one).getFollowingWords().get(two).getFollowing().getTwo():0)*/
                                    + "\",0" + System.lineSeparator());
                        }
                    }

                    }
                if(!lastword.isEmpty()) {
                    String one="",two="";
                    one=!(lastword.length()>akkadian.getChar_length())?lastword.substring(lastword.length() - akkadian.getChar_length(), lastword.length()):"";
                    two=!word.isEmpty()?word.substring(word.length() - akkadian.getChar_length(), word.length()):"";
//                    Integer[] values=new Integer[]{((dictHandling.matchChar(one)!=null && dictHandling.matchChar(one).getFollowingWords()!=null && dictHandling.matchChar(one).getFollowingWords().containsKey(two))?dictHandling.matchChar(one).getFollowingWords().get(two).getFollowing().getOne().intValue():0),
//                            ((dictHandling.matchChar(one)!=null && dictHandling.matchChar(one).getFollowingWords()!=null && dictHandling.matchChar(one).getFollowingWords().containsKey(two))?dictHandling.matchChar(one).getFollowingWords().get(two).getFollowing().getTwo().intValue():0)
                            /*0,
                            ((dictHandling.matchChar(one)!=null && dictHandling.matchChar(one).getFollowingWords()!=null && dictHandling.matchChar(one).getFollowingWords().containsKey(two))?dictHandling.matchChar(one).getFollowingWords().get(two).getBoundariesFollowContinuations().intValue():0),
                            ((dictHandling.matchChar(one)!=null && dictHandling.matchChar(one).getFollowingWords()!=null && dictHandling.matchChar(one).getFollowingWords().containsKey(two))?dictHandling.matchChar(one).getFollowingWords().get(two).getContinuationFollowsBoundary().intValue():0),
                            ((dictHandling.matchChar(one)!=null && dictHandling.matchChar(one).getFollowingWords()!=null && dictHandling.matchChar(one).getFollowingWords().containsKey(two))?dictHandling.matchChar(one).getFollowingWords().get(two).getContinuationFollowsContinuation().intValue():0)*/

//                    };
                    //values[2]=(values[0]+values[1])-(values[3]+values[4]+values[5]);
                    String valStr=one+","+two;//+","+values[2]+","+values[3]+","+values[4]+","+values[5];
                    System.out.println(valStr);
                    if(trainOrTest) {
                        sep.append(this.replaceCharsWithNumber(one) + "," +
                                this.replaceCharsWithNumber(two) /*+ "," + valStr/*+","
                                +
                                ((dictHandling.matchChar(one)!=null && dictHandling.matchChar(one).getFollowingWords()!=null && dictHandling.matchChar(one).getFollowingWords().containsKey(two))?dictHandling.matchChar(one).getFollowingWords().get(two).getFollowing().getOne().intValue()
                                :0)+","+((dictHandling.matchChar(one)!=null && dictHandling.matchChar(one).getFollowingWords()!=null && dictHandling.matchChar(one).getFollowingWords().containsKey(two))?dictHandling.matchChar(one).getFollowingWords().get(two).getFollowing().getTwo().intValue():0)*/
                                + "\n");
                        if(!withsep) {
                            filebuilder.append("seq-"+countLines+++",\""+ this.replaceCharsWithNumber(one) + "," +
                                    this.replaceCharsWithNumber(two)/* + "," + valStr/*+","
                                +
                                ((dictHandling.matchChar(one)!=null && dictHandling.matchChar(one).getFollowingWords()!=null && dictHandling.matchChar(one).getFollowingWords().containsKey(two))?dictHandling.matchChar(one).getFollowingWords().get(two).getFollowing().getOne().intValue()
                                :0)+","+((dictHandling.matchChar(one)!=null && dictHandling.matchChar(one).getFollowingWords()!=null && dictHandling.matchChar(one).getFollowingWords().containsKey(two))?dictHandling.matchChar(one).getFollowingWords().get(two).getFollowing().getTwo().intValue():0)*/
                                    + "\",1" + System.lineSeparator());
                        }
                    }else {
                        sep.append(this.replaceCharsWithNumber(one) + "," +
                                this.replaceCharsWithNumber(two)/*+","+valStr/*+","
                                +
                                ((dictHandling.matchChar(one)!=null && dictHandling.matchChar(one).getFollowingWords()!=null && dictHandling.matchChar(one).getFollowingWords().containsKey(two))?dictHandling.matchChar(one).getFollowingWords().get(two).getFollowing().getOne().intValue()
                                :0)+","+((dictHandling.matchChar(one)!=null && dictHandling.matchChar(one).getFollowingWords()!=null && dictHandling.matchChar(one).getFollowingWords().containsKey(two))?dictHandling.matchChar(one).getFollowingWords().get(two).getFollowing().getTwo().intValue():0)*/
                                +"\n");
                        if(!withsep) {
                           filebuilder.append("seq-"+countLines+++",\""+ this.replaceCharsWithNumber(one) + "," +
                                    this.replaceCharsWithNumber(two) /*+ "," + valStr/*+","+((dictHandling.matchChar(one)!=null && dictHandling.matchChar(one).getFollowingWords()!=null && dictHandling.matchChar(one).getFollowingWords().containsKey(two))?dictHandling.matchChar(one).getFollowingWords().get(two).getFollowing().getOne().intValue()
                                :0)+","+((dictHandling.matchChar(one)!=null && dictHandling.matchChar(one).getFollowingWords()!=null && dictHandling.matchChar(one).getFollowingWords().containsKey(two))?dictHandling.matchChar(one).getFollowingWords().get(two).getFollowing().getTwo().intValue():0)*/ +
                                    "\",1" + System.lineSeparator());
                        }
                    }
                }
                lastword=word;
            }
        }
        if(withsep){
            writer.write(sep.toString().substring(0,sep.length()-2)+"\",1" + System.lineSeparator());
            writer.write(nonsep.toString().substring(0,nonsep.length()-2)+"\",1" + System.lineSeparator());
        }else{
            boolean first=true;
            for(int i=0;i<countLines;i++){
                if(!first){
                    writer.write(",");
                }
                writer.write("seq-"+i);
                first=false;
            }
            writer.write("}"+System.lineSeparator()+"@attribute bag relational"+System.lineSeparator()+"@attribute att0 numeric"+
                    System.lineSeparator()+"@attribute att1 numeric"+System.lineSeparator()+
                    /*"@attribute att2 numeric"+System.lineSeparator()+
                    "@attribute att3 numeric"+System.lineSeparator()+
                    "@attribute att4 numeric"+System.lineSeparator()+
                    "@attribute att5 numeric"+System.lineSeparator()+
                    "@attribute att6 numeric"+System.lineSeparator()+
                    "@attribute att7 numeric"+System.lineSeparator()+*/
                    "@end bag"+System.lineSeparator());
            if(trainOrTest)
                writer.write("@attribute class {0,1}"+System.lineSeparator());
            writer.write("@data"+System.lineSeparator());
            writer.write(filebuilder.toString());
        }

        reader.close();
        writer.close();
        return path;
    }

    /**
     * Helper function to perform a manual StringToWordVector.
     * @param chars the characters to transform
     * @return the number to be associated with the characters
     */
    private Integer replaceCharsWithNumber(String chars){
        if(this.intToStrMap.containsKey(chars)){
            return this.intToStrMap.get(chars);
        }
        this.intToStrMap.put(chars,this.entry++);
        return entry-1;
    }

    /**
     * Saves the generated result as text.
     * @param method
     * @param transliterationMethod
     * @param featureSet
     * @param sourcefilename
     * @param testfilename
     * @param charType
     * @throws IOException
     * @throws org.xml.sax.SAXException
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws javax.xml.stream.XMLStreamException
     */
    public void saveResult(BufferedWriter cuneiResultWriter,ClassificationMethod method,TransliterationMethod transliterationMethod,FeatureSets featureSet,String sourcefilename,String testfilename,CharTypes charType) throws IOException, SAXException, ParserConfigurationException, XMLStreamException {
        BufferedReader reader=new BufferedReader(new FileReader(new File(sourcefilename)));
        System.out.println("Testfilename: "+testfilename);
        testfilename=testfilename+"_t";
        BufferedReader reader2=new BufferedReader(new FileReader(new File(Files.TRAININGDATADIR.toString() + testfilename.substring(testfilename.lastIndexOf("/")+1, testfilename.lastIndexOf("_")) +"_"+method.getShortname().toLowerCase()+ "_" + featureSet.toString().toLowerCase() + "_" + method.getFramework().toString().toLowerCase() + Files.XMLSUFFIX)));
        String temp,temp2,towrite,translit;
        while(!reader2.readLine().contains("@data"));
        while((temp=reader.readLine())!=null){
            boolean[] values=new boolean[temp.length()/charType.getChar_length()];
            //for(int i=0;i<temp.length();i++){
            int j=0;
            for(int i=charType.getChar_length();i<=temp.length()-charType.getChar_length();i+=charType.getChar_length()) {
                temp2 = reader2.readLine();
                System.out.println("Wekaline: " + temp2);
                //System.out.println("CharA(0)="+temp2.charAt(0)+" "+temp2.charAt(1));
                if (temp2 != null && method==ClassificationMethod.HMM) {
                    System.out.println(temp2.contains(",2 1")+" !");
                    values[j] = temp2.contains(",2 1");
                } else if (temp2 != null && featureSet != FeatureSets.META)
                    values[j] = temp2.charAt(1) == '0';
                else if(temp2!=null){
                    values[j]=temp2.charAt(0)=='1';
                }
                j++;
            }
            towrite=this.retransFormResultToCunei(temp, values,charType);
            System.out.println("ToWrite: " + towrite);
            cuneiResultWriter.write(towrite + " \n");
            //translit=this.assignTransliteration(towrite.split(" "), this.dictHandler, transliterationMethod);
            //this.translitResultWriter.write(translit + " \n");
            //this.transcriptResultWriter.write(TranscriptionMethods.translitTotranscript(translit) + " \n");
        }
        reader.close();
        reader2.close();
        cuneiResultWriter.close();
        //this.translitResultWriter.close();
        //this.transcriptResultWriter.close();
        new Scanner(System.in).next();
    }
}
