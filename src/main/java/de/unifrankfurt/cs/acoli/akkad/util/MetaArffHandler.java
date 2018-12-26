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

import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.FeatureSets;

import java.io.*;
import java.util.*;


/**
 * Created by timo on 12/16/14.
 * Creates Arff Files for a meta classifier classifying Texts by using votes of several other classifiers.
 */
public class MetaArffHandler implements ArffGen {
    /**The boundary positions to remember.*/
    private List<Integer> boundaries;
    /**The path of the boundaryFile.*/
    private String boundaryFile;
    /**The counter for columns.*/
    private Integer colCounter;
    /**The evaluationMatrix to consider.*/
    private Integer[][] evalMatrix;
    /**LeaveOuts to build out meta tests.*/
    private List<List<Integer>> leaveOuts;
    /**Map of evalMatrix columns fpr convenient access.*/
    private Map<Integer,List<Integer>> matrixCols;

    /**
     * Constructor for this class.
     * @param numInstances the number of instances to process
     * @param numCols the number of columns included in the file
     * @param boundaryFile the boundary file as gold standard
     */
    public MetaArffHandler(Integer numInstances, Integer numCols,String boundaryFile){
        this.evalMatrix=new Integer[numCols][numInstances];
        this.leaveOuts=new LinkedList<>();
        this.boundaryFile=boundaryFile;
        this.colCounter=0;
        this.matrixCols=new TreeMap<>();
    }

    /**
     * Helper method to count the amount of lines of a file for further processing.
     * @param filename the file name to count lines
     * @return
     * @throws IOException
     */
    public static int countLines(String filename) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            is.close();
        }
    }

    /**
     * Main method Build meta classifier files.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Integer lines=countLines("meta/init/initfile.arff");
        System.out.println("Lines: "+lines);
        MetaArffHandler handler=new MetaArffHandler(lines,30,"meta/init/boundaryfile.txt");
        handler.fillLeaveOuts();
        handler.processBoundaryFile();
        List<String> files= Arrays.asList(new File("meta/data/").list());
        Collections.sort(files);
        System.out.println("Files: "+files);
        for(String file:files) {
            handler.addClassificationCol("meta/data/" + file, CharTypes.AKKADIAN);
            System.out.println("Added Col:");
        }
        System.out.println(handler.evalMatrix.length+" "+handler.evalMatrix[0].length);
        for(List<Integer> col:handler.matrixCols.values()){
            System.out.println("Col: "+col.size());
        }
        handler.generateOutputs("meta/out/test1.arff");

    }

    /**
     * Adds a classification column to the classification matrixs
     * @param resultfile
     * @param chartype
     * @throws IOException
     */
    public void addClassificationCol(final String resultfile,final CharTypes chartype) throws IOException {
          BufferedReader reader=new BufferedReader(new FileReader(new File(resultfile)));
          String temp="";
          Integer positioncounter=0;
          this.matrixCols.put(matrixCols.size(),new LinkedList<Integer>());
          while((temp=reader.readLine())!=null){
                System.out.println("CurList: "+this.matrixCols.get(matrixCols.size()-1).size());
                this.matrixCols.get(matrixCols.size()-1).addAll(this.binaryDecisionEvaluation(temp,positioncounter,colCounter,chartype));
          }
          reader.close();
          colCounter++;
    }

    /**
     * Implements the binary decision method.
     * @param comparearray the comparison array
     */
    private List<Integer> binaryDecisionEvaluation(final String comparearray, Integer positionCounter,final Integer col,final CharTypes charType){
       List<Integer> comparelist=new LinkedList<>();
       for(String splitit:comparearray.split(" ")){
           for(int i=0;i<splitit.length();i+=charType.getChar_length()){
               if(i<splitit.length()-charType.getChar_length()){
                   comparelist.add(0);
               }else{
                   comparelist.add(1);
               }
           }
       }
       int j=positionCounter;
       for(Integer item:comparelist){
           this.evalMatrix[positionCounter++][col]=item;
       }
        System.out.println("Comparelist: "+comparelist);
        return comparelist;
       /*List<Integer> originalarray=this.boundaries.subList(positionCounter,positionCounter+comparearray.length());
       for(int j=0;j<comparearray.length();j++){
            if(j<comparelist.size()  && originalarray.get(j)==1 && comparelist.get(j) ==1) {
                j+=2;
                //TruePositive
                this.evalMatrix[col][positionCounter]=0;
            }else if(j<comparelist.size() && originalarray.get(j)==1 && !(comparelist.get(j)==1)){
                this.evalMatrix[col][positionCounter]=3;
                //FalseNegative
            }else if(j<comparelist.size() && !(originalarray.get(j)==1) && !(comparelist.get(j)==1)){
                //TrueNegative
                this.evalMatrix[col][positionCounter]=2;
            }else if(j<comparelist.size() && !(originalarray.get(j)==1) && (comparelist.get(j)==1)){
                //FalsePositive
                this.evalMatrix[col][positionCounter]=1;
            }
        } */
        /*if(originalarray.length()<comparearray.length()){
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
                    this.evalMatrix[col]
                }
                this.evalresult.total++;
            }
        } */
    }

    public void buildArffFiles(List<String> boundaries,CharTypes chartype) throws IOException {
          this.fillLeaveOuts();
          this.processBoundaryFile();

          for(String boundfile:boundaries){
              this.addClassificationCol(boundfile,chartype);
          }

    }

    /**
     * Fills the leave out list according to the needs.
     */
    private void fillLeaveOuts(){
        //Leave Out Nothing
        this.leaveOuts.add(new LinkedList<Integer>());
        //Leave Out Stat
        this.leaveOuts.add(Arrays.asList(new Integer[]{6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29}));
        //Leave Out Rule
        this.leaveOuts.add(Arrays.asList(new Integer[]{0,1,2,3,4,5}));
        //Leave Out SVM
        this.leaveOuts.add(Arrays.asList(new Integer[]{6,10,15,21,24,27}));
        //Leave Out Naive Bayes
        this.leaveOuts.add(Arrays.asList(new Integer[]{7,11,16,22,25,28}));
        //Leave Out KNN
        this.leaveOuts.add(Arrays.asList(new Integer[]{8,12,17,23,26,29}));
        //Leave Out MaxEnt
        this.leaveOuts.add(Arrays.asList(new Integer[]{9,13,18}));
        //Leave Out C45
        this.leaveOuts.add(Arrays.asList(new Integer[]{10,14,19}));
        //Leave Out CRF
        this.leaveOuts.add(Arrays.asList(new Integer[]{6,7,8,9,10,20,21,22}));
        //Leave Out MAXENTPREV
        this.leaveOuts.add(Arrays.asList(new Integer[]{11,12,13,14,15,23,24,25}));
        //Leave Out MAXENTSIGHAN
        this.leaveOuts.add(Arrays.asList(new Integer[]{16,17,18,19,20,26,27,28}));
        //Leave Out Tiny
        this.leaveOuts.add(Arrays.asList(new Integer[]{6,7,8,9,10,11,12,13,14,15,16,17,18,19,20}));
        //Leave Out VerySmall
        this.leaveOuts.add(Arrays.asList(new Integer[]{21,22,23,24,25,26,27,28,29}));
        //Leave Out SVM + VerySmall
        this.leaveOuts.add(Arrays.asList(new Integer[]{6,10,15,21,22,23,24,25,26,27,28,29}));
        //Leave Out NaiveBayes + VerySmall
        this.leaveOuts.add(Arrays.asList(new Integer[]{7,11,16,21,22,23,24,25,26,27,28,29}));
        //Leave Out KNN + VerySmall
        this.leaveOuts.add(Arrays.asList(new Integer[]{8,12,17,21,22,23,24,25,26,27,28,29}));
        //Leave Out MaxEnt + VerySmall
        this.leaveOuts.add(Arrays.asList(new Integer[]{9,13,18,21,22,23,24,25,26,27,28,29}));
        //Leave Out C45 + VerySmall
        this.leaveOuts.add(Arrays.asList(new Integer[]{10,14,19,21,22,23,24,25,26,27,28,29}));
        //Leave Out SVM + Tiny
        this.leaveOuts.add(Arrays.asList(new Integer[]{6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,24,27}));
        //Leave Out Naive Bayes + Tiny
        this.leaveOuts.add(Arrays.asList(new Integer[]{6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,22,25,28}));
        //Leave Out KNN + Tiny
        this.leaveOuts.add(Arrays.asList(new Integer[]{6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,23,26,29}));
        //Leave Out SVM + Tiny +Rule
        this.leaveOuts.add(Arrays.asList(new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,24,27}));
        //Leave Out Naive Bayes + Tiny + Rule
        this.leaveOuts.add(Arrays.asList(new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,22,25,28}));
        //Leave Out KNN + Tiny + Rule
        this.leaveOuts.add(Arrays.asList(new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,23,26,29}));
        //Leave Out SVM + VerySmall +Rule
        this.leaveOuts.add(Arrays.asList(new Integer[]{1,2,3,4,5,6,10,15,21,22,23,24,25,26,27,28,29}));
        //Leave Out NaiveBayes + VerySmall + Rule
        this.leaveOuts.add(Arrays.asList(new Integer[]{1,2,3,4,5,7,11,16,21,22,23,24,25,26,27,28,29}));
        //Leave Out KNN + VerySmall + Rule
        this.leaveOuts.add(Arrays.asList(new Integer[]{1,2,3,4,5,8,12,17,21,22,23,24,25,26,27,28,29}));
        //Leave Out MaxEnt + VerySmall + Rule
        this.leaveOuts.add(Arrays.asList(new Integer[]{1,2,3,4,5,9,13,18,21,22,23,24,25,26,27,28,29}));
        //Leave Out C45 + VerySmall + Rule
        this.leaveOuts.add(Arrays.asList(new Integer[]{1,2,3,4,5,10,14,19,21,22,23,24,25,26,27,28,29}));
    }

    /**
     * Generates output sets for the meta classifier from previously acquired data.
     * @param initialFileName the file name of the initial file to generate
     * @throws IOException
     */
    public void generateOutputs(String initialFileName) throws IOException {
        int k=0;
        for(List<Integer> leave:leaveOuts){
           BufferedWriter writer=new BufferedWriter(new FileWriter(new File(initialFileName+"_"+k++)));
           this.writeArffHeader(FeatureSets.META,true,writer,this.matrixCols.size()-leave.size());
           String collect="";
                   for(int i=0;i<this.matrixCols.get(0).size() && i<this.boundaries.size();i++){

                       collect=""+this.boundaries.get(i)+",";
                       for(int j=0;j<this.matrixCols.size();j++) {
                           if(!leave.contains(j)) {
                               //System.out.println("this.matrixCols.get("+j+")("+i+")"+this.matrixCols.get(j).size());
                               collect+=!this.matrixCols.containsKey(j) || i>=this.matrixCols.get(j).size()?"0,":this.matrixCols.get(j).get(i).toString()+",";
                           }
                       }
                       collect=collect.substring(0,collect.length()-1);
                       collect+=System.lineSeparator();
                       System.out.println("Collect: "+collect);
                       writer.write(collect);
                   }
            writer.close();

        }
        k=0;
        for(List<Integer> leave:leaveOuts){
            BufferedWriter writer=new BufferedWriter(new FileWriter(new File(initialFileName+"_test_"+k++)));
            this.writeArffHeader(FeatureSets.META,false,writer,this.matrixCols.size()-leave.size());
            String collect="";
            for(int i=0;i<this.matrixCols.get(0).size() && i<this.boundaries.size();i++){

                collect="";
                for(int j=0;j<this.matrixCols.size();j++) {
                    if(!leave.contains(j)) {
                        //System.out.println("this.matrixCols.get("+j+")("+i+")"+this.matrixCols.get(j).size());
                        collect+=!this.matrixCols.containsKey(j) || i>=this.matrixCols.get(j).size()?"0,":this.matrixCols.get(j).get(i).toString()+",";
                    }
                }
                collect=collect.substring(0,collect.length()-1);
                collect+=System.lineSeparator();
                System.out.println("Collect: "+collect);
                writer.write(collect);
            }
            writer.close();

        }

    }

    /**
     * Processes a boundary file and remembers its boundaries in order.
     * @throws IOException
     */
    private void processBoundaryFile() throws IOException {
         BufferedReader reader=new BufferedReader(new FileReader(new File(boundaryFile)));
         String temp="";
         this.boundaries=new LinkedList<>();
         while((temp=reader.readLine())!=null){
             for(String num:temp.split(",")){
                 this.boundaries.add(Integer.valueOf(num));
             }
         }
        reader.close();
    }

    /**
     * Writes the header for an arff file including the correct number of attributes.
     * Attributes are simply named as att(Counter)
     * @param feature the feature set to be used
     * @throws IOException
     */
    private void writeArffHeader(final FeatureSets feature,final Boolean trainOrTest, final BufferedWriter writer,final Integer num) throws IOException {
        writer.write("% 1. Title: Word Boundary Detection\n");
        writer.write("% 2. Sources:\n");
        writer.write("%      (a) Creator: Automatically generated\n");
        writer.write("@RELATION " + feature.toString() + "\n");
        if(trainOrTest) {
            writer.write("@ATTRIBUTE class {");
            String[] statusvalues = feature.getFeatureSet().getClassValues();
            for (int i = 0; i < statusvalues.length; i++) {
                if (i == statusvalues.length - 1) {
                    writer.write(statusvalues[i]);
                    writer.write("}\n");
                } else {
                    writer.write(statusvalues[i]);
                    writer.write(",");
                }
            }
        }
        for(int i=0;i<num;i++){
            writer.write("@ATTRIBUTE att"+i+" numeric\n");
        }
        writer.write("@data\n");
    }


}
