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

import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.main.Main;
import de.unifrankfurt.cs.acoli.akkad.main.gui.MainGUI;
import de.unifrankfurt.cs.acoli.akkad.methods.Methods;
import de.unifrankfurt.cs.acoli.akkad.methods.transcription.TranscriptionMethods;
import de.unifrankfurt.cs.acoli.akkad.util.Tuple;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.ClassificationMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.FeatureSets;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.TransliterationMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Files;
import org.xml.sax.SAXException;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.HMM;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SimpleLogistic;
import weka.classifiers.functions.supportVector.Kernel;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.SimpleKMeans;
import weka.core.Debug;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: Timo Homburg
 * Date: 30.11.13
 * Time: 16:37
 * Implements perceptron learning using the Weka framework.
 */
public class WekaMethods extends Methods {
    private DictHandling dictHandler;
    /**SVM*/
    private Kernel kernel;
    /**Perceptron for performing the classification.*/
    private MultilayerPerceptron perceptron;
    /**The instances to classify.*/
    private Instances testSet;
    /**The instances to classify.*/
    private Instances trainingSet;
    private StringToWordVector wordvector;

    /**
     * Constructor for this class.
     * @param instances
     * @throws Exception
     */
    public WekaMethods(final Instances instances) throws Exception {
        this.perceptron=new MultilayerPerceptron();
        this.perceptron.buildClassifier(instances);

    }

    /**
     * Constructor for this class.
     * @throws Exception
     */
    public WekaMethods(BufferedWriter cuneiwriter,BufferedWriter translitwriter,BufferedWriter transcriptwriter,DictHandling dictHandling) throws Exception {
        this.cuneiResultWriter=cuneiwriter;
        this.translitResultWriter=translitwriter;
        this.transcriptResultWriter=transcriptwriter;
        this.dictHandler=dictHandling;
        this.perceptron=new MultilayerPerceptron();

    }

    /*public static void main(String[] args) throws Exception {
        String method=args[0];
        String featureset=args[1];
        String trainingset=args[2];
        String testset=args[3];
        String charType=args[4];
        String crossValidation="";
        if(args.length>5){
            crossValidation=args[5];
        }
        System.setProperty("file.encoding", "UTF-8");
        ClassificationMethod classmethod=ClassificationMethod.valueOf(method);
        FeatureSets usedFeatureset=FeatureSets.valueOf(featureset);
        CharTypes charTypes=CharTypes.valueOf(charType);
        System.out.println("Started: "+new Date(System.currentTimeMillis()).toString());
        WekaMethods percep=new WekaMethods(new BufferedWriter(new FileWriter(new File(Files.RESULTDIR.toString()+Files.FIRST20.toString().substring(0,Files.FIRST20.toString().lastIndexOf("."))+"_"+method.getShortname().toLowerCase()
                +"_"+featureset.toString()+Files.RESULT.toString()))),new BufferedWriter(new FileWriter(new File(Files.RESULTDIR.toString()+Files.FIRST20.toString().substring(0,Files.FIRST20.toString().lastIndexOf(".")))+"_"+method.getShortname().toLowerCase()
                +"_"+featureset.toString()+Files.RESULT.toString())),null,null);
        if("".equals(crossValidation)){
            percep.classify(new File(trainingset),new File(testset),new File(trainingset),null,classmethod,usedFeatureset,0,TransliterationMethod.FIRST,charTypes);

        }else{
           percep.crossValidation(new File(trainingset),new File(testset),new File(trainingset),classmethod,usedFeatureset,0,TransliterationMethod.FIRST,charTypes,5);
        }
    }*/

    public static void main(String[] args) throws Exception {
        String method=args[0];
        String featureset=args[1];
        String trainingset=args[2];
        String testset=args[3];
        String charType=args[4];
        String crossValidation="";
        ClassificationMethod classmethod=ClassificationMethod.valueOf(method);
        FeatureSets usedFeatureset=FeatureSets.valueOf(featureset);
        CharTypes charTypes=CharTypes.valueOf(charType);
        WekaMethods methods=new WekaMethods(new BufferedWriter(new FileWriter(new File(Files.RESULTDIR.toString()+Files.FIRST20.toString().substring(0,Files.FIRST20.toString().lastIndexOf("."))+"_"+classmethod.getShortname().toLowerCase()
                +"_"+featureset.toString().toLowerCase()+Files.RESULT.toString()))),new BufferedWriter(new FileWriter(new File(Files.RESULTDIR.toString()+Files.FIRST20.toString().substring(0,Files.FIRST20.toString().lastIndexOf(".")))+"_"+classmethod.getShortname().toLowerCase()
                +"_"+featureset.toString().toLowerCase()+Files.RESULT.toString())),null,null);
        methods.saveResult(classmethod,TransliterationMethod.FIRST,usedFeatureset,"","",CharTypes.AKKADIAN);
    }

    /**
     * Performs weka classification.
     * @param trainfile  Trainingfile
     * @param testfile   Testfile
     * @param sourcepath Sourcefile
     * @param method   classificationmethod
     * @param featureset featureset
     * @param trainingtime how many times to train
     * @param transliterationMethod transliterationMethod to use
     * @param charType the chartype to use
     * @throws WekaException on error
     */
    public void classify(File trainfile, File testfile,File sourcepath,final String modelfile,ClassificationMethod method,FeatureSets featureset,Integer trainingtime,TransliterationMethod transliterationMethod,CharTypes charType) throws WekaException {
        System.out.println("TestFile ARFF: "+testfile.getAbsolutePath());
        System.out.println("TrainFile ARFF: "+trainfile.getAbsolutePath());
        System.out.println("Sourcefile: " + sourcepath.getAbsolutePath());
        trainfile=new File(trainfile.getAbsolutePath());
        testfile=new File(testfile.getAbsolutePath());
        File modelexists=null;
        if(modelfile!=null){
            modelexists=new File(modelfile);
        }
        if(modelexists==null || !modelexists.exists()){
            System.out.println("ModelExists? "+Files.TRAININGDATADIR.toString()+Files.MODELDIR.toString()+trainfile.getAbsolutePath().substring(trainfile.getAbsolutePath().lastIndexOf("/")+1, trainfile.getAbsolutePath().lastIndexOf("_")) +"_"+method.getShortname().toLowerCase()+ "_" + featureset.toString().toLowerCase() +"_"+method.getFramework().toString().toLowerCase()+Files.MODELSUFFIX.toString());
            modelexists=new File(Files.TRAININGDATADIR.toString()+Files.MODELDIR.toString()+trainfile.getAbsolutePath().substring(trainfile.getAbsolutePath().lastIndexOf("/")+1, trainfile.getAbsolutePath().lastIndexOf("_")) +"_"+method.getShortname().toLowerCase()+"_" + featureset.toString().toLowerCase() +"_"+method.getFramework().toString().toLowerCase()+Files.MODELSUFFIX.toString());
        }
       System.out.println(modelexists.exists());
        try {
        if(modelexists.exists()){
            this.importData(trainfile.getAbsolutePath(),testfile.getAbsolutePath());
        }else if(testfile.exists() || trainfile.exists()){
            this.importData(trainfile.getAbsolutePath(),testfile.getAbsolutePath());
        }
        this.executeClassifier(trainfile.getAbsolutePath(),testfile.getAbsolutePath(),method,featureset,modelexists);
        System.out.println("Sourcepath: "+sourcepath);
        this.saveResult(method,transliterationMethod,featureset,sourcepath.getAbsolutePath(),sourcepath.getAbsolutePath().substring(sourcepath.getAbsolutePath().lastIndexOf('/'),sourcepath.getAbsolutePath().lastIndexOf('.')),charType);
        } catch (Exception e) {
            e.printStackTrace();
            throw new WekaException(e.getMessage());
        }
    }

    public void crossValidation(File trainfile, File testfile,File sourcepath,ClassificationMethod classmethod,FeatureSets featureset,Integer trainingtime,TransliterationMethod transliterationMethod,CharTypes charType,Integer folds) throws Exception {
        // perform cross-validation
        Evaluation eval = new Evaluation(trainingSet);
        List<Classifier> results=new LinkedList<Classifier>();
        for (int n = 0; n < folds; n++) {
            this.importData(trainfile.getAbsolutePath(),testfile.getAbsolutePath());
            Classifier tempcls=this.executeClassifier(trainfile.getAbsolutePath(),testfile.getAbsolutePath(),classmethod,featureset,null);
            // the above code is used by the StratifiedRemoveFolds filter, the
            // code below by the Explorer/Experimenter:
            // Instances train = randData.trainCV(folds, n, rand);
            results.add(tempcls);
            // build and evaluate classifier
            eval.evaluateModel(tempcls,testSet);
        }
    }

    /**
     * Executes a classifier.
     * @param trainfilename the filename
     * @param method the classificationmethod
     * @param featureSet the featureset to use
     * @param modelexists if a model exists already
     * @throws Exception
     */
    public Classifier executeClassifier(String trainfilename,String testfilename,ClassificationMethod method,FeatureSets featureSet,File modelexists) throws Exception {
        SimpleKMeans clust=new SimpleKMeans();
        if (modelexists.exists()) {
            MainGUI.refreshProgressBarMessage(Main.bundle.getString("classifying") + " "  + method+" "+ Main.bundle.getString("loadingClassifier"));
            System.out.println("Loading filtered classifier");
            FilteredClassifier classi = (FilteredClassifier) weka.core.SerializationHelper.read(modelexists.getAbsolutePath());
            MainGUI.refreshProgressBarMessage(Main.bundle.getString("classifying") + " " + method+" "+ Main.bundle.getString("preprocessingData"));
            System.out.println("Loaded! Loading Trainingset");
            if(method==ClassificationMethod.KMEANS){
                StringToWordVector filter = new StringToWordVector();
                filter.setInputFormat(trainingSet);
                //filter.setTokenizer(tokenizer);
                filter.setWordsToKeep(1000000);
                filter.setDoNotOperateOnPerClassBasis(true);

                //filter.setLowerCaseTokens(true);

                trainingSet=Filter.useFilter(trainingSet, filter);
                weka.filters.unsupervised.attribute.Remove filter2 = new weka.filters.unsupervised.attribute.Remove();
                //filter2.setAttributeIndices(""+1);
                filter2.setInputFormat(trainingSet);
                String[] options=new String[2];
                options[0] = "-R";                                    // "range"
                options[1] = "1";
                // first attribute
                filter2.setOptions(options);
                trainingSet=Filter.useFilter(trainingSet,filter2);
                //trainingSet.setClassIndex(0);
                testSet=Filter.useFilter(testSet, filter);
                filter2 = new weka.filters.unsupervised.attribute.Remove();
                //filter2.setAttributeIndices(""+1);
                filter2.setInputFormat(testSet);
                options=new String[2];
                options[0] = "-R";                                    // "range"
                options[1] = "1";
                // first attribute
                filter2.setOptions(options);
                testSet=Filter.useFilter(testSet,filter2);
                testSet.setClassIndex(0);
                //testSet.setClassIndex(0);

                clust.setNumClusters(2);

            }else{

                StringToWordVector filter = new StringToWordVector();
                filter.setInputFormat(trainingSet);
                //filter.setTokenizer(tokenizer);
            /*filter.setWordsToKeep(1000000);
            filter.setDoNotOperateOnPerClassBasis(true);*/
                if(featureSet!=FeatureSets.META){
                    trainingSet = Filter.useFilter(trainingSet, filter);
                    testSet = Filter.useFilter(testSet, filter);
                    testSet.setClassIndex(0);
                }

                trainingSet.setClassIndex(0);
                System.out.println("Trainingsset loaded!");
                System.out.println("Loading Testset");



                System.out.println("Testset loaded\n Now classifying...");
                MainGUI.refreshProgressBarMessage(Main.bundle.getString("classifying"));
            }


            //classi.setFilter(filter);
            //Evaluation eval = new Evaluation(this.trainingSet);
            //eval.evaluateModel(classifier, testSet);

            // create copy
            Instances labeled = new Instances(testSet);
            if(method==ClassificationMethod.KMEANS){
                for (int i = 0; i < testSet.numInstances(); i++) {
                    if(i%500==0) {
                        System.out.println("Instance " + i);
                        MainGUI.refreshProgressBarMessage(Main.bundle.getString("classifying") + " " +method +" ("+Main.bundle.getString("instance")+" "+i+" "+Main.bundle.getString("of")+" "+testSet.numInstances()+")");
                    }
                    double clsLabel = clust.clusterInstance(testSet.instance(i));
                    labeled.instance(i).setClassValue(clsLabel);
                }
            }else {


                // label instances
                for (int i = 0; i < testSet.numInstances(); i++) {
                    if (i % 500 == 0) {
                        System.out.println("Instance " + i);
                        MainGUI.refreshProgressBarMessage(Main.bundle.getString("classifying") + " " + method + " (" + Main.bundle.getString("instance") + " " + i + " " + Main.bundle.getString("of") + " " + testSet.numInstances() + ")");
                    }
                    double clsLabel = classi.classifyInstance(testSet.instance(i));
                    labeled.instance(i).setClassValue(clsLabel);
                }
                System.out.println("Testfile: " + testfilename);
            }
            String exportfile=Files.TRAININGDATADIR.toString() + testfilename.substring(testfilename.lastIndexOf("/") + 1, testfilename.lastIndexOf("_")) + "_" + method.getShortname().toLowerCase() + "_" + featureSet.toString().toLowerCase() + "_" + method.getFramework().toString().toLowerCase() + Files.XMLSUFFIX;
            exportfile=exportfile.replace("_out","");
            System.out.println("Saving labeled data to: "+exportfile);
            // save labeled data
            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(exportfile));
            writer.write(labeled.toString());
            writer.newLine();
            writer.flush();
            writer.close();
            System.out.println("Data saved!");
            return classi;
        } else {
            FilteredClassifier classifier = new FilteredClassifier();
            Classifier nonFiltered=new J48();
            clust=new SimpleKMeans();
            StringToWordVector filter;
            MainGUI.refreshProgressBarMessage(Main.bundle.getString("classifying") + " " + method+" "+ Main.bundle.getString("preprocessingData"));
            switch (method) {
                case NAIVEBAYES:
                    filter = new StringToWordVector();
                    filter.setInputFormat(trainingSet);
                    //filter.setTokenizer(tokenizer);
                    filter.setWordsToKeep(1000000);
                    filter.setDoNotOperateOnPerClassBasis(true);
                    //filter.setLowerCaseTokens(true);

                    trainingSet = Filter.useFilter(trainingSet, filter);
                    trainingSet.setClassIndex(0);
                    testSet = Filter.useFilter(testSet, filter);
                    testSet.setClassIndex(0);
                    classifier.setFilter(filter);
                    classifier.setClassifier(new NaiveBayes());
                    break;
                case IB1:
                    filter = new StringToWordVector();
                    filter.setInputFormat(trainingSet);
                    //filter.setTokenizer(tokenizer);
                    filter.setWordsToKeep(1000000);
                    filter.setDoNotOperateOnPerClassBasis(true);
                    //filter.setLowerCaseTokens(true);
                    trainingSet = Filter.useFilter(trainingSet, filter);
                    trainingSet.setClassIndex(0);
                    testSet = Filter.useFilter(testSet, filter);
                    testSet.setClassIndex(0);
                    classifier.setFilter(filter);
                    classifier.setClassifier(new IBk());
                    break;
                case KMEANS:
                    filter = new StringToWordVector();
                    filter.setInputFormat(trainingSet);
                    //filter.setTokenizer(tokenizer);
                    filter.setWordsToKeep(1000000);
                    filter.setDoNotOperateOnPerClassBasis(true);

                    //filter.setLowerCaseTokens(true);

                    trainingSet=Filter.useFilter(trainingSet, filter);
                    weka.filters.unsupervised.attribute.Remove filter2 = new weka.filters.unsupervised.attribute.Remove();
                    //filter2.setAttributeIndices(""+1);
                    filter2.setInputFormat(trainingSet);
                    String[] options=new String[2];
                    options[0] = "-R";                                    // "range"
                    options[1] = "1";
                    // first attribute
                    filter2.setOptions(options);
                    trainingSet=Filter.useFilter(trainingSet,filter2);
                    //trainingSet.setClassIndex(0);
                    testSet=Filter.useFilter(testSet, filter);
                    filter2 = new weka.filters.unsupervised.attribute.Remove();
                    //filter2.setAttributeIndices(""+1);
                    filter2.setInputFormat(testSet);
                    options=new String[2];
                    options[0] = "-R";                                    // "range"
                    options[1] = "1";
                    // first attribute
                    filter2.setOptions(options);
                    testSet=Filter.useFilter(testSet,filter2);
                    testSet.setClassIndex(0);
                    //testSet.setClassIndex(0);
                    clust=new SimpleKMeans();
                    clust.setNumClusters(2);

                    break;
                case BAYESNET:
                    filter = new StringToWordVector();
                    filter.setInputFormat(trainingSet);
                    //filter.setTokenizer(tokenizer);
                    filter.setWordsToKeep(1000000);
                    filter.setDoNotOperateOnPerClassBasis(true);
                    //filter.setLowerCaseTokens(true);
                    trainingSet = Filter.useFilter(trainingSet, filter);
                    trainingSet.setClassIndex(0);
                    testSet = Filter.useFilter(testSet, filter);
                    testSet.setClassIndex(0);
                    classifier.setFilter(filter);
                    classifier.setClassifier(new BayesNet());
                    break;
                case C45:
                    filter = new StringToWordVector();
                    filter.setInputFormat(trainingSet);
                    //filter.setTokenizer(tokenizer);
                    filter.setWordsToKeep(1000000);
                    filter.setDoNotOperateOnPerClassBasis(true);
                    //filter.setLowerCaseTokens(true);

                    trainingSet.setClassIndex(0);
                    if(featureSet!=FeatureSets.META){
                        trainingSet = Filter.useFilter(trainingSet, filter);
                        testSet = Filter.useFilter(testSet, filter);
                        classifier.setFilter(filter);

                    }else{
                        nonFiltered=new J48();
                    }
                    trainingSet.setClassIndex(0);
                    testSet.setClassIndex(0);


                    classifier.setClassifier(new J48());
                    break;
                case PERCEPTRON:
                    filter = new StringToWordVector();
                    filter.setInputFormat(trainingSet);
                    //filter.setTokenizer(tokenizer);
                    filter.setWordsToKeep(1000000);
                    filter.setDoNotOperateOnPerClassBasis(true);
                    //filter.setLowerCaseTokens(true);

                    trainingSet = Filter.useFilter(trainingSet, filter);
                    trainingSet.setClassIndex(0);

                    testSet = Filter.useFilter(testSet, filter);
                    testSet.setClassIndex(0);
                    classifier.setFilter(filter);
                    classifier.setClassifier(new MultilayerPerceptron());
                    break;
                case CRF:

                    break;
                case HMM:

                    this.trainingSet.setClassIndex(2);

                    testSet.setClassIndex(2);
                    //testSet = Filter.useFilter(testSet, filter3);
                    HMM hmm=new HMM();
                    hmm.setRandomStateInitializers(true);
                    hmm.setIterationCutoff(0.1);
                    //hmm.setNumStates(2);
                    classifier.setClassifier(hmm);

                    break;
                case SVM:
                    filter = new StringToWordVector();
                    filter.setInputFormat(trainingSet);
                    //filter.setTokenizer(tokenizer);
                    filter.setWordsToKeep(1000000);
                    filter.setDoNotOperateOnPerClassBasis(true);
                    //filter.setLowerCaseTokens(true);
                    trainingSet.setClassIndex(0);
                    if(featureSet!=FeatureSets.META){
                        trainingSet = Filter.useFilter(trainingSet, filter);
                        testSet = Filter.useFilter(testSet, filter);
                        classifier.setFilter(filter);
                        testSet.setClassIndex(0);
                    }else{
                        nonFiltered=new LibSVM();
                    }
                    trainingSet.setClassIndex(0);
                    LibSVM svm=new LibSVM();
                    /*String opts = "-S 0 -K 2 -D 3 -G 0.0 -R 0.0 -N 0.5 -M 40.0 -C 1.0  -E 0.0010 -P 0.1";
                    svm.setOptions(weka.core.Utils.splitOptions(opts));
                    svm.setKernelType(new SelectedTag(LibSVM.SVMTYPE_NU_SVC,null));*/
                    classifier.setClassifier(svm);
                    break;
                case LOGISTIC:
                    filter = new StringToWordVector();
                    filter.setInputFormat(trainingSet);
                    //filter.setTokenizer(tokenizer);
                    filter.setWordsToKeep(1000000);
                    filter.setDoNotOperateOnPerClassBasis(true);
                    //filter.setLowerCaseTokens(true);
                    trainingSet = Filter.useFilter(trainingSet, filter);
                    trainingSet.setClassIndex(0);
                    testSet = Filter.useFilter(testSet, filter);
                    testSet.setClassIndex(0);
                    classifier.setFilter(filter);
                    classifier.setClassifier(new Logistic());
                    break;

                case VOTE:
                    trainingSet.setClassIndex(0);
                    testSet.setClassIndex(0);
                    break;

                case LOGISTICREGRESSION:
                    filter = new StringToWordVector();
                    filter.setInputFormat(trainingSet);
                    //filter.setTokenizer(tokenizer);
                    filter.setWordsToKeep(1000000);
                    filter.setDoNotOperateOnPerClassBasis(true);
                    //filter.setLowerCaseTokens(true);
                    trainingSet = Filter.useFilter(trainingSet, filter);
                    trainingSet.setClassIndex(0);
                    testSet = Filter.useFilter(testSet, filter);
                    //testSet.setClassIndex(0);
                    classifier.setFilter(filter);
                    classifier.setClassifier(new SimpleLogistic());
                    break;
                default:
                    filter = new StringToWordVector();
                    filter.setInputFormat(trainingSet);
                    //filter.setTokenizer(tokenizer);
                    filter.
                            setWordsToKeep(1000000);
                    filter.setDoNotOperateOnPerClassBasis(true);
                    //filter.setLowerCaseTokens(true);
                    trainingSet = Filter.useFilter(trainingSet, filter);
                    trainingSet.setClassIndex(0);
                    testSet = Filter.useFilter(testSet, filter);
                    testSet.setClassIndex(0);
                    classifier.setFilter(filter);
                    classifier.setClassifier(new NaiveBayes());
            }
            if(method==ClassificationMethod.KMEANS){
                MainGUI.refreshProgressBarMessage(Main.bundle.getString("classifying") + " " + method+" "+ Main.bundle.getString("loadingClassifier"));
                clust.buildClusterer(trainingSet);
                ClusterEvaluation eval=new ClusterEvaluation();
                Debug.saveToFile(Files.TRAININGDATADIR.toString() + Files.MODELDIR.toString() + trainfilename.substring(trainfilename.lastIndexOf("/")+1, trainfilename.lastIndexOf("_")) +"_"+method.getShortname().toLowerCase()+ "_" + featureSet.toString().toLowerCase() + "_" + method.getFramework().toString().toLowerCase() + Files.MODELSUFFIX, classifier);
                System.out.println("SaveModelToFile: "+Files.TRAININGDATADIR.toString() + Files.MODELDIR.toString() + trainfilename.substring(trainfilename.lastIndexOf("/")+1, trainfilename.lastIndexOf("_")) +"_"+method.getShortname().toLowerCase()+"_" + featureSet.toString().toLowerCase() + "_" + method.getFramework().toString().toLowerCase() + Files.MODELSUFFIX);
                //eval.evaluateModel(classifier, testSet);

                // create copy

                Instances labeled = new Instances(testSet);

                // label instances
                for (int i = 0; i < testSet.numInstances(); i++) {
                    if(i%500==0) {
                        System.out.println("Instance " + i);
                        MainGUI.refreshProgressBarMessage(Main.bundle.getString("classifying") + " " +method +" ("+Main.bundle.getString("instance")+" "+i+" "+Main.bundle.getString("of")+" "+testSet.numInstances()+")");
                    }
                    double clsLabel = clust.clusterInstance(testSet.instance(i));
                    labeled.instance(i).setClassValue(clsLabel);
                }
                System.out.println("Saving labeled data to: "+Files.TRAININGDATADIR.toString() + testfilename.substring(testfilename.lastIndexOf("/")+1, testfilename.lastIndexOf("_")) +"_"+method.getShortname().toLowerCase()+ "_" + featureSet.toString().toLowerCase() + "_" + method.getFramework().toString().toLowerCase() + Files.XMLSUFFIX);
                // save labeled data
                BufferedWriter writer = new BufferedWriter(
                        new FileWriter(Files.TRAININGDATADIR.toString() + testfilename.substring(testfilename.lastIndexOf("/")+1, testfilename.indexOf("_")) +"_"+method.getShortname().toLowerCase()+ "_" + featureSet.toString().toLowerCase() + "_" + method.getFramework().toString().toLowerCase() + Files.XMLSUFFIX));
                writer.write(labeled.toString());
                writer.newLine();
                writer.flush();
                writer.close();
                System.out.println("Data saved!");
                return classifier;

            }else {
                System.out.println("Building Classifier...");
                MainGUI.refreshProgressBarMessage(Main.bundle.getString("classifying") + " " + method + " " + Main.bundle.getString("loadingClassifier"));
                if (method != ClassificationMethod.VOTE){

                    if(featureSet!=FeatureSets.META){
                        classifier.buildClassifier(trainingSet);
                        Debug.saveToFile(Files.TRAININGDATADIR.toString() + Files.MODELDIR.toString() + trainfilename.substring(trainfilename.lastIndexOf("/") + 1, trainfilename.lastIndexOf("_")) + "_" + method.getShortname().toLowerCase() + "_" + featureSet.toString().toLowerCase() + "_" + method.getFramework().toString().toLowerCase() + Files.MODELSUFFIX, classifier);
                    }else{
                        nonFiltered.buildClassifier(trainingSet);
                    }
                    System.out.println("SaveModelToFile: " + Files.TRAININGDATADIR.toString() + Files.MODELDIR.toString() + trainfilename.substring(trainfilename.lastIndexOf("/") + 1, trainfilename.lastIndexOf("_")) + "_" + method.getShortname().toLowerCase() + "_" + featureSet.toString().toLowerCase() + "_" + method.getFramework().toString().toLowerCase() + Files.MODELSUFFIX);
                }

                if(method==ClassificationMethod.HMM){
                    Evaluation eval = new Evaluation(this.trainingSet);
                    eval.evaluateModel(classifier, testSet);
                }
                // create copy

                Instances labeled = new Instances(testSet);
                BufferedWriter labelWriter=new BufferedWriter(new FileWriter(new File("trainingdata/labels.txt")));

                if(method!=ClassificationMethod.VOTE){
                    // label instances
                    for (int i = 0; i < testSet.numInstances(); i++) {
                        if(i%500==0) {
                            System.out.println("Instance " + i);
                            MainGUI.refreshProgressBarMessage(Main.bundle.getString("classifying") + " " + method+" ("+ method+Main.bundle.getString("instance")+" "+i+" "+Main.bundle.getString("of")+" "+testSet.numInstances()+")");
                        }
                        double clsLabel;
                        if(featureSet!=FeatureSets.META){
                            clsLabel = classifier.classifyInstance(testSet.instance(i));
                        }else {
                            clsLabel = nonFiltered.classifyInstance(testSet.instance(i));
                        }
                        //System.out.println("ClsLabel: "+clsLabel);
                        labeled.instance(i).setClassValue(clsLabel);

                        labelWriter.write(labeled.instance(i).classAttribute()+" "+labeled.instance(i).classValue()+" "+labeled.instance(i).classIndex()+System.lineSeparator());//labeled.instance(i).value(0)+System.lineSeparator());
                    }
                    labelWriter.close();
                } else{
                    java.util.Map<Integer,Tuple<Double,Double>> possMap=new TreeMap<>();
                    for(int l=1;l<testSet.instance(0).numAttributes();l++){
                        if(!possMap.containsKey(l)){
                            possMap.put(l,new Tuple<Double,Double>(0.,0.));
                        }
                        int good=0,bad=0;
                        for(int k=0;k< testSet.numInstances();k++){
                            if(testSet.instance(k).value(0)==0.){
                                bad++;
                            }else{
                                good++;
                            }
                            if(testSet.instance(k).value(l)==testSet.instance(k).value(0) && testSet.instance(k).value(0)==0.){
                                possMap.get(l).setOne(possMap.get(l).getOne()+1);
                            }else if(testSet.instance(k).value(0)==1.){
                                possMap.get(l).setTwo(possMap.get(l).getTwo()+1);
                            }
                        }
                        possMap.get(l).setOne(possMap.get(l).getOne()/bad);
                        possMap.get(l).setTwo(possMap.get(l).getTwo()/good);
                    }
                    System.out.println("PossMap: "+possMap.toString());
                    for (int i = 0; i < testSet.numInstances(); i++) {
                        if (i % 500 == 0) {
                            System.out.println("Instance " + i);
                            MainGUI.refreshProgressBarMessage(Main.bundle.getString("classifying") + " " + method + " (" + method + Main.bundle.getString("instance") + " " + i + " " + Main.bundle.getString("of") + " " + testSet.numInstances() + ")");
                        }

                        int good=0,bad=0;
                        double badprobs=1.,goodprobs=1.;
                        for(int j=1;j<testSet.instance(i).numAttributes();j++){
                            if(testSet.instance(i).value(j)==0.0){
                               badprobs+=possMap.get(j).getOne();
                               bad++;
                            }else{
                               goodprobs+=possMap.get(j).getTwo();
                               good++;
                            }
                        }
                        if(good>bad)
                            labeled.instance(i).setClassValue(1.);
                        else
                            labeled.instance(i).setClassValue(0.);

                        labelWriter.write(labeled.instance(i).classAttribute()+" "+labeled.instance(i).classValue()+" "+labeled.instance(i).classIndex()+System.lineSeparator());//labeled.instance(i).value(0)+System.lineSeparator());
                    }
                }

                String exportfile=Files.TRAININGDATADIR.toString() + testfilename.substring(testfilename.lastIndexOf("/") + 1, testfilename.lastIndexOf("_")) + "_" + method.getShortname().toLowerCase() + "_" + featureSet.toString().toLowerCase() + "_" + method.getFramework().toString().toLowerCase() + Files.XMLSUFFIX;
                exportfile=exportfile.replace("_out","");
                // save labeled data
                System.out.println("Saving labeled data to: " +exportfile);
                /*if(method!=ClassificationMethod.VOTE){
                    SparseToNonSparse sparse=new SparseToNonSparse();
                    sparse.setInputFormat(labeled);
                    labeled=Filter.useFilter(labeled, sparse);
                }*/

                // save labeled data
                BufferedWriter writer = new BufferedWriter(
                        new FileWriter(exportfile));
                writer.write(labeled.toString());
                writer.newLine();
                writer.flush();
                writer.close();
                System.out.println("Data saved!");

                return classifier;
            }

       }

        //eval.crossValidateModel(classifier, trainingSet, 4, new Debug.Random(1));
    }

    /**
     * Imports data for training and testing
     * @param trainfilepath the trainfilepath
     * @param testfilepath  the testfilepath
     * @throws Exception  on error
     */
    public void importData(String trainfilepath,String testfilepath) throws Exception {
        System.out.println("Load TrainingData: "+trainfilepath);
        if(trainfilepath!=null) {
            ConverterUtils.DataSource datasource = new ConverterUtils.DataSource(trainfilepath);
            this.trainingSet = datasource.getDataSet();

            try {
                PrintWriter writer = new PrintWriter(new FileWriter(trainfilepath + "2"));
                writer.print(this.trainingSet);
                System.out.println("===== Saved dataset:  arffout.arff  =====");
                writer.close();
            } catch (IOException e) {
                System.out.println("Problem found when writing: arffout.arff");
            }
        }
        if(testfilepath!=null) {
            System.out.println("Load TestData: " + testfilepath);
            ConverterUtils.DataSource datasource2 = new ConverterUtils.DataSource(testfilepath);
            this.testSet = datasource2.getDataSet();
            int cIdx=testSet.numAttributes()-1;
            testSet.setClassIndex(cIdx);
        }
    }

    public String retransFormResultToCunei(String cuneiline,boolean[] wekaline,CharTypes charType){
        StringBuffer resultbuffer=new StringBuffer(cuneiline.length());
        System.out.print("Wekaline: ");
        for(int i=0;i<wekaline.length;i++){
            System.out.println(wekaline[i]);
        }
        int j=0;
        for(int i=0;i<=cuneiline.length()-charType.getChar_length();i+=charType.getChar_length()){
            resultbuffer.append(cuneiline.substring(i,i+charType.getChar_length()));
            if(i<cuneiline.length()-charType.getChar_length() && wekaline[j++]){
                resultbuffer.append(" ");
            }
        }
        return resultbuffer.toString();
    }

    /**
     * Saves the model generated.
     * @param fileName the export path
     * @param classifier the classifier
     */
    public void saveModel(String fileName,Classifier classifier) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
            out.writeObject(classifier);
            out.close();
            System.out.println("===== Saved model: " + fileName + " =====");
        }
        catch (IOException e) {
            System.out.println("Problem found when writing: " + fileName);
        }
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
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws XMLStreamException
     */
    public void saveResult(ClassificationMethod method,TransliterationMethod transliterationMethod,FeatureSets featureSet,String sourcefilename,String testfilename,CharTypes charType) throws IOException, SAXException, ParserConfigurationException, XMLStreamException {
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
            this.cuneiResultWriter.write(towrite + " \n");
            translit=this.assignTransliteration(towrite.split(" "), this.dictHandler, transliterationMethod);
            this.translitResultWriter.write(translit + " \n");
            this.transcriptResultWriter.write(TranscriptionMethods.translitTotranscript(translit) + " \n");
        }
        reader.close();
        reader2.close();
        this.cuneiResultWriter.close();
        this.translitResultWriter.close();
        this.transcriptResultWriter.close();
        //new Scanner(System.in).next();
    }


}


