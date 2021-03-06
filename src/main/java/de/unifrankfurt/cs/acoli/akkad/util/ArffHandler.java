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
import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.cuneiform.AkkadDictHandler;
import de.unifrankfurt.cs.acoli.akkad.methods.segmentation.stat.featureset.FeatureSetManager;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.FeatureSets;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Files;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Options;
import opennlp.model.Event;
import opennlp.model.EventCollector;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Handler for creating training and test data sets for machine learning tools.
 * User: Timo Homburg
 * Date: 30.11.13
 * Time: 17:45
 * To change this template use File | Settings | File Templates.
 */
public class ArffHandler implements EventCollector,ArffGen{
    /**The feature set to be used.*/
    private final FeatureSets feature;
    /**The options to use for the Framework.*/
    private final Options options;
    /**Indicates if a train or testset should be generated.*/
    private final Options trainOrTest;
    /**Reader for reading correct boundaries out of the boundary file.*/
    private BufferedReader boundaryReader;
    /**The chartype to choose.*/
    private CharTypes chartype;
    /**The feature set generated by the contextGenerator.*/
    private FeatureSetManager contextGenerator;
    /**The dicthandler to use.*/
    private DictHandling dicthandler;
    /**Writer for the set to be generated.*/
    private BufferedWriter generatedSetWriter;
    /**Reader for the original file to be analyzed.*/
    private BufferedReader originalFileReader;

    /**
     * Handler for handling the generation of TestSets and TrainingSets given a specified feature set.
     * @param filepath the file to create a testset/trainingset from
     * @param boundarypath file including the correct resolution for the given file
     * @param option Indicates if mallet or arff files should be produced
     * @param trainOrTest indicates if a training or testing file should be produced
     * @param feature the feature set to be used
     * @throws IOException
     */
    public ArffHandler(final String filepath,final String boundarypath,final Options option,final Options trainOrTest,final FeatureSets feature,final DictHandling dicthandler,CharTypes chartype) throws IOException {
        System.out.println("ArffHandler (NEW Train/Testsets) FilePath: "+filepath+" Boundarypath: "+boundarypath);
        this.options =option;
        this.trainOrTest=trainOrTest;
        this.feature=feature;
        this.dicthandler=dicthandler;
        this.chartype=chartype;
        this.initReaders(filepath,boundarypath);
    }

    /**
     * toString for a string array for debug purposes.
     * @param array the string array
     */
    public static void arrayToStr(final String[] array){
        StringBuffer result=new StringBuffer(array.length*10);
        result.append("[");
        for(String item:array){
            result.append(item+",");
        }
        result.deleteCharAt(result.length()-1);
        result.append("]");
        System.out.println(result.toString());
    }

    public static void main(final String[] args) throws IOException, SAXException, ParserConfigurationException, XMLStreamException {
        AkkadDictHandler dictHandler=new AkkadDictHandler(CharTypes.AKKADIAN.getStopchars());
        dictHandler.parseDictFile(new File(Files.AKKADXML.toString()));
        ArffHandler handler=new ArffHandler(Files.REFORMATTEDDIR.toString()+Files.CUNEIFORMDIR.toString()+"foreigntext/Lcmc2_reformatted.txt",
                Files.REFORMATTEDDIR.toString()+Files.BOUNDARYDIR+"foreigntext/Lcmc2_reformatted.txt",
                Options.WEKA,Options.TRAININGSET,FeatureSets.CRF,dictHandler,CharTypes.CHINESE);
        handler.getEvents();
        /*Evaluation eval=new Evaluation(Files.CORPUSREFORMATTED.toString(),Files.RESULTDIR.toString()+Files.TRANSLITDIR.toString()+ ClassificationMethod.HMM.toString().toLowerCase()+"_"+FeatureSets.MAXENT.toString().toLowerCase()+"_"+Files.FIRST20NOSUF.toString()+Files.RESULT,dictHandler);
        eval.transliterationEvaluation(true,method);
        eval.segmentationEvaluation(true,method);
        eval.binaryDecisionEvaluation(true,method);
        eval.boundaryBasedEvaluation(true,method);
        eval.wordBoundaryBasedEvaluation(true,method);
        */
    }

    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = java.nio.file.Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }



    /**
     * Uses Boundary Files for setting the correct class for training.
     * @return
     */
    @Override
    public Event[] getEvents() {
        this.contextGenerator=new FeatureSetManager();
        Boolean charOrWord=this.feature.getFeatureSet().getCharOrWord();
        List<Event> elist=new ArrayList<>();
        String currentline;
        if (charOrWord) {
        try {
            currentline = this.originalFileReader.readLine();

            String b = this.boundaryReader.readLine();
            if (this.options == Options.WEKA)
                this.writeArffHeader(this.feature, trainOrTest == Options.TRAININGSET);
            //Wordbased

                while (currentline != null) {
                    System.out.println(currentline);
                    String[] boundToks = b.split(",");
                    //arrayToStr(boundToks);
                    //System.out.println("BoundToks Length: " + boundToks.length);
                    String segmented="";
                    for(int i=0;i<boundToks.length && i*chartype.getChar_length()+chartype.getChar_length()<=currentline.length();i++){
                        if(boundToks[i].equals("0")){
                            segmented+=currentline.substring(i*chartype.getChar_length(),i*chartype.getChar_length()+chartype.getChar_length());
                        }else{
                            segmented+=currentline.substring(i*chartype.getChar_length(),i*chartype.getChar_length()+chartype.getChar_length())+" ";
                        }
                    }
                    segmented+=" ";
                    for (int tok = 0; tok < currentline.length() - chartype.getChar_length(); tok += chartype.getChar_length()) {
                        StringBuffer sb = new StringBuffer(currentline);
                        //System.out.println("Length: " + sb.length());
                        for (int id = 0; id < sb.length() - 1; id += chartype.getChar_length()) {
                            String[] context =
                                    this.contextGenerator.getContext(new Tuple<>(sb, id), this.feature,segmented, dicthandler, chartype);
                            //arrayToStr(context);

                            //System.out.println("Id: " + id);
                            //System.out.println(this.feature.getFeatureSet().getClassValues().length);
                            for (String str : this.feature.getFeatureSet().getClassValues()) {
                                //System.out.println("Currentline: " + currentline);
                                //System.out.println("Str " + str);
                                //System.out.println("Id/" + chartype.getChar_length() + " " + id / chartype.getChar_length() + " Boundtoks.length() " + boundToks.length);
                                //System.out.println("BoundToks: "+boundToks[id/2]);
                                if (str.equals(boundToks[(id / chartype.getChar_length())])) {
                                    //arrayToStr(context);
                                    //elist.add(new Event(str, context));
                                    if (this.options == Options.WEKA && this.trainOrTest == Options.TRAININGSET)
                                        this.writeEventToArff(context, str);
                                    if (this.options == Options.WEKA && this.trainOrTest == Options.TESTINGSET)
                                        this.writeTestSetToArff(context);
                                    if (this.options == Options.MALLET && this.trainOrTest == Options.TRAININGSET) {
                                        this.writeEventToMallet(context, str);
                                    }
                                    if (this.options == Options.MALLET && this.trainOrTest == Options.TESTINGSET) {
                                        this.writeTestSetToMallet(context);
                                    }
                                    this.feature.getFeatureSet().setPreviousclassification(str);
                                    break;
                                }
                            }
                        }
                    }
                    currentline = this.originalFileReader.readLine();
                    b = this.boundaryReader.readLine();
                }

                if (this.options == Options.WEKA || this.options == Options.MALLET)
                    this.generatedSetWriter.close();
            }catch(IOException e){
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }else{
            try{
            currentline = this.originalFileReader.readLine();

            String b = this.boundaryReader.readLine();
            if (this.options == Options.WEKA)
                this.writeArffHeader(this.feature, trainOrTest == Options.TRAININGSET);
            //Wordbased
            int linecounter=0;
            while (currentline != null) {
                System.out.println(currentline);
                if(b==null)
                    continue;
                String[] boundToks = b.contains(",")?b.split(","):new String[]{b};
                //arrayToStr(boundToks);           ew
                //System.out.println("BoundToks Length: " + boundToks.length);
                String segmented="";
                for(int i=0;i<boundToks.length && i*chartype.getChar_length()+chartype.getChar_length()<=currentline.length();i++){
                    if(boundToks[i].equals("0")){
                        segmented+=currentline.substring(i*chartype.getChar_length(),i*chartype.getChar_length()+chartype.getChar_length());
                    }else{
                        segmented+=currentline.substring(i*chartype.getChar_length(),i*chartype.getChar_length()+chartype.getChar_length())+" ";
                    }
                }
                segmented+=" ";
                for (int tok = 0; tok < currentline.length()-chartype.getChar_length(); tok +=chartype.getChar_length()) {
                    StringBuffer sb = new StringBuffer(currentline);
                    //System.out.println("Length: " + sb.length());
                    for (int id = 0; id < sb.length() - 1; id += chartype.getChar_length()) {
                        String[] context =
                                this.contextGenerator.getContext(new Tuple<>(sb, id), this.feature,segmented, dicthandler, chartype);
                        //arrayToStr(context);
                        //System.out.println("Id: " + id);
                       // System.out.println(this.feature.getFeatureSet().getClassValues().length);
                        for (String str : this.feature.getFeatureSet().getClassValues()) {
                            System.out.println(linecounter+" "+"Currentline: " + currentline);
                            //System.out.println("Str " + str);
                            //System.out.println(id+"/" + chartype.getChar_length() + " " + id / chartype.getChar_length() + " Boundtoks.length() " + boundToks.length);
                            //arrayToStr(boundToks);
                            //System.out.println("BoundToks: "+boundToks[id/ chartype.getChar_length()]);
                            if ((id / chartype.getChar_length())<boundToks.length && str.equals(boundToks[(id / chartype.getChar_length())])) {
                                //arrayToStr(context);
                                //elist.add(new Event(str, context));
                                if (this.options == Options.WEKA && this.trainOrTest == Options.TRAININGSET)
                                    this.writeEventToArff(context, str);
                                if (this.options == Options.WEKA && this.trainOrTest == Options.TESTINGSET)
                                    this.writeTestSetToArff(context);
                                if (this.options == Options.MALLET && this.trainOrTest == Options.TRAININGSET) {
                                    this.writeEventToMallet(context, str);
                                }
                                if (this.options == Options.MALLET && this.trainOrTest == Options.TESTINGSET) {
                                    this.writeTestSetToMallet(context);
                                }
                                this.feature.getFeatureSet().setPreviousclassification(str);
                                break;
                            }
                        }
                    }
                }
                currentline = this.originalFileReader.readLine();
                b = this.boundaryReader.readLine();
                linecounter++;
            }

            if (this.options == Options.WEKA || this.options == Options.MALLET)
                this.generatedSetWriter.close();
        }catch(IOException e){
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        }
        Event[] events = new Event[elist.size()];
        elist.toArray(events);
        return events;
    }

    @Override
    public Event[] getEvents(final boolean b) {
        return this.getEvents();
    }


    /**
     * Initializes the readers and writers needed by this class according to the given filepaths.
     * @param filepath The file path of the file to generate sets for
     * @param boundarypath  the file including the correct segmentation
     * @throws IOException  on reading/writing errors
     */
    private void initReaders(final String filepath,final String boundarypath) throws IOException {
        String origfilename=filepath.substring(filepath.lastIndexOf('/')+1,filepath.lastIndexOf('.'))+"_";
        this.originalFileReader =new BufferedReader(new FileReader(new File(filepath)));
        this.boundaryReader =new BufferedReader(new FileReader(new File(boundarypath)));
        if(this.options ==Options.WEKA && this.trainOrTest==Options.TRAININGSET)
            this.generatedSetWriter =new BufferedWriter(new FileWriter(new File(Files.TRAININGDATADIR+origfilename+this.feature.toString().toLowerCase()+Files.ARFFSUFFIX)));
        if(this.options ==Options.MALLET && this.trainOrTest==Options.TRAININGSET){
            this.generatedSetWriter =new BufferedWriter(new FileWriter(new File(Files.TRAININGDATADIR+origfilename+this.feature.toString().toLowerCase()+Files.MALLETSUFFIX)));
        }
        if(this.options ==Options.WEKA && this.trainOrTest==Options.TESTINGSET)
            this.generatedSetWriter =new BufferedWriter(new FileWriter(new File(Files.TESTDATADIR+origfilename+this.feature.toString().toLowerCase()+Files.ARFFSUFFIX)));
        if(this.options ==Options.MALLET && this.trainOrTest== Options.TESTINGSET){
            this.generatedSetWriter =new BufferedWriter(new FileWriter(new File(Files.TESTDATADIR+origfilename+this.feature.toString().toLowerCase()+Files.MALLETSUFFIX)));
        }
    }

    /**
     * Writes the header for an arff file including the correct number of attributes.
     * Attributes are simply named as att(Counter)
     * @param feature the feature set to be used
     * @throws IOException
     */
    private void writeArffHeader(final FeatureSets feature,final Boolean trainOrTest) throws IOException {
        this.generatedSetWriter.write("% 1. Title: Word Boundary Detection\n");
        this.generatedSetWriter.write("% 2. Sources:\n");
        this.generatedSetWriter.write("%      (a) Creator: Automatically generated\n");
        this.generatedSetWriter.write("@RELATION " + feature.toString() + "\n");
        if(trainOrTest) {
            this.generatedSetWriter.write("@ATTRIBUTE class {");
            String[] statusvalues = feature.getFeatureSet().getClassValues();
            for (int i = 0; i < statusvalues.length; i++) {
                if (i == statusvalues.length - 1) {
                    this.generatedSetWriter.write(statusvalues[i]);
                    this.generatedSetWriter.write("}\n");
                } else {
                    this.generatedSetWriter.write(statusvalues[i]);
                    this.generatedSetWriter.write(",");
                }
            }
        }
        for(int i=0;i<feature.getFeatureSet().getAttributes();i++){
            this.generatedSetWriter.write("@ATTRIBUTE att"+i+" string\n");
        }
        this.generatedSetWriter.write("@data\n");
    }

    /**
     * Writes the given array of events to the arff output file.
     * @param event the array of events to write
     * @param value the class label
     * @throws IOException on error
     */
    private void writeEventToArff(final String[] event,final String value) throws IOException {

        this.generatedSetWriter.write(value+",");
        StringBuffer attributes=new StringBuffer();
        for(String att:event){
            attributes.append("'");
            attributes.append(att.replace("'",""));
            attributes.append("',");
        }
        this.generatedSetWriter.write(attributes.substring(0, attributes.length() - 1) + "\n");
    }

    /**
     * Writes the given array of events to the mallet output file.
     * @param event the array of events to write
     * @param value the class label
     * @throws IOException
     */
    private void writeEventToMallet(final String[] event, final String value) throws IOException {
        final StringBuilder attributes=new StringBuilder();
        for(String att:event){
            attributes.append(att.replace("'",""));
            attributes.append(" ");
        }
        this.generatedSetWriter.write(attributes.toString().trim().replaceAll(" +", " "));
        this.generatedSetWriter.write(value + " \n");
    }

    /**
     * Writes the given array of events to the mallet output file but does not include the class label.
     * @param event the array of events to write
     * @throws IOException
     */
    private void writeTestSetToArff(final String[] event) throws IOException {
        final StringBuilder attributes=new StringBuilder();
        for(String att:event){
            attributes.append("'");
            attributes.append(att.replace("'",""));
            attributes.append("',");
        }
        System.out.println("WRITE: " + attributes);
        this.generatedSetWriter.write(attributes.substring(0, attributes.length() - 1) + "\n");
    }

    /**
     * Writes the given array of events to the mallet output file but does not include the class label.
     * @param event the array of events to write
     * @throws IOException
     */
    private void writeTestSetToMallet(final String[] event) throws IOException {
        final StringBuilder attributes=new StringBuilder();
        for(String att:event){
            attributes.append(att.replace("'",""));
            attributes.append(" ");
        }
        System.out.println("WRITE: "+attributes);
        this.generatedSetWriter.write(attributes.substring(0, attributes.length() - 1) + "\n");
    }
}
