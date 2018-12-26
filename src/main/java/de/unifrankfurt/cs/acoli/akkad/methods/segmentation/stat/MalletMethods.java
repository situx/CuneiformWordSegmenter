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

import cc.mallet.classify.*;
import cc.mallet.fst.*;
import cc.mallet.optimize.Optimizable;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.FileIterator;
import cc.mallet.pipe.iterator.LineGroupIterator;
import cc.mallet.types.*;
import de.unifrankfurt.cs.acoli.akkad.dict.chars.LangChar;
import de.unifrankfurt.cs.acoli.akkad.dict.chars.cuneiform.CuneiChar;
import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.cuneiform.AkkadDictHandler;
import de.unifrankfurt.cs.acoli.akkad.util.ArffHandler;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.ClassificationMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.FeatureSets;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.TransliterationMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Files;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Command line importhandler tool for loading a sequence of
 *  instances from a single file, with one trainingSet
 *  per line of the input file.
 *  <p>
 * Despite the name of the class, input data does not
 *  have to be comma-separated, and trainingSet data can
 *  remain sequences (rather than unordered vectors).
 *
 *  @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
 */

public class MalletMethods {
    /**Map of Mallet Pipes to use per method.*/
    private final Map<ClassificationMethod,List<Pipe>> pipes;
    /**The current alphabet to be used.*/
    private Alphabet alpha;
    /**Dicthandler for classifying good transliterations.*/
    private DictHandling dicthandler;
    /**The currently used feature set.*/
    private FeatureSets feature;
    /**A list of labelings for buffering classification results.*/
    private List<Classification> labelings;
    /**The name of the original file.*/
    private String originalFileName;
    /**OutputWriter for writing results.*/
    private StatOutputWriter result;

    /**
     * Constructor for this class.
     * @param originalFileName the name of the original file
     * @param dicthandler the dictionaryhandler for the output
     * @throws IOException
     */
    public MalletMethods(final String originalFileName,final DictHandling dicthandler) throws IOException {
        this.pipes = new TreeMap<ClassificationMethod,List<Pipe>>();
        this.dicthandler=dicthandler;
        this.originalFileName=originalFileName;
        this.alpha=new Alphabet(new String[]{"0","1"});
        /*
        alpha.entries.add("0");
        alpha.entries.add("1");*/
        this.labelings=new LinkedList<Classification>();

    }

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, XMLStreamException {
        BufferedReader reader=new BufferedReader(new FileReader(new File("trainingdata/first20_maxent.mallet")));
        BufferedWriter writerpositive=new BufferedWriter(new FileWriter(new File("testdata/corpusimport/corpusout_positive.txt")));
        BufferedWriter writernegative=new BufferedWriter(new FileWriter(new File("testdata/corpusimport/corpusout_negative.txt")));
        String temp;
        while((temp=reader.readLine())!=null){
            System.out.println(temp);
            System.out.println(temp.charAt(temp.length()-2));
            if(temp.substring(temp.length()-2,temp.length()-1).equals("1")){
               writerpositive.write(temp+"\n");
            }else{
                writernegative.write(temp+"\n");
            }
        }
        writerpositive.close();
        writernegative.close();
        reader.close();
        AkkadDictHandler dictHandler=new AkkadDictHandler(CharTypes.AKKADIAN.getStopchars());
        dictHandler.parseDictFile(new File(Files.AKKADXML.toString()));
        MalletMethods importer=new MalletMethods("beep",dictHandler);
        InstanceList instances = importer.readDirectory(new File("testdata/corpusimport/"));
        instances.save(new File("iAmAsavedInstance4U"));
        importer.trainCRF(instances,instances,"traincrf","testdata/first20_maxentt.mallet",1,TransliterationMethod.MAXPROB);
    }

    public Pipe buildPipe() {
        ArrayList pipeList = new ArrayList();

        // Read data from File objects
        pipeList.add(new Input2CharSequence("UTF-8"));

        // Regular expression for what constitutes a token.
        //  This pattern includes Unicode letters, Unicode numbers,
        //   and the underscore character. Alternatives:
        //    "\\S+"   (anything not whitespace)
        //    "\\w+"    ( A-Z, a-z, 0-9, _ )
        //    "[\\p{L}\\p{N}_]+|[\\p{P}]+"   (a group of only letters and numbers OR
        //                                    a group of only punctuation marks)
        Pattern tokenPattern =
                Pattern.compile("[\\p{L}\\p{N}_]+");
        //CharSequence2CharNGrams
        // Tokenize raw strings
        pipeList.add(new CharSequence2TokenSequence(tokenPattern));

        // Normalize all tokens to all lowercase
        //pipeList.add(new TokenSequenceLowercase());

        // Remove stopwords from a standard English stoplist.
        //  options: [case sensitive] [mark deletions]
        //pipeList.add(new TokenSequenceRemoveStopwords(false, false));

        // Rather than storing tokens as strings, convert
        //  them to integers by looking them up in an alphabet.
        pipeList.add(new TokenSequence2FeatureSequence());

        // Do the same thing for the "target" field:
        //  convert a class label string to a Label object,
        //  which has an index in a Label alphabet.
        pipeList.add(new Target2Label());

        // Now convert the sequence of features to a sparse vector,
        //  mapping feature IDs to counts.
        pipeList.add(new FeatureSequence2FeatureVector());

        // Print out the features and the label
        pipeList.add(new PrintInputAndTarget());

        return new SerialPipes(pipeList);
    }

    /**
     * Classifies a testingset using information generated from the trainingset using a given method and a feature set.
     * @param trainingFilename the trainingset
     * @param testingFileName the testingset
     * @param method the evaluate method
     * @param feature the feature set
     * @param trainingtime number of times to train
     * @throws IOException on error
     */
    public void classify(final String trainingFilename, final String testingFileName,final ClassificationMethod method,final FeatureSets feature,final Integer trainingtime,final TransliterationMethod transliterationMethod) throws IOException {
        this.initPipe(method,feature);
        System.out.println("TrainingFileName: "+trainingFilename);
        System.out.println("TestingFileName: "+testingFileName);
        String testingFileAbbName=testingFileName.substring(testingFileName.indexOf('/')+1,testingFileName.indexOf('.'));
        Pipe pipe = new SerialPipes(pipes.get(method));
        PagedInstanceList trainingInstances = new PagedInstanceList(pipe,64,128);
        PagedInstanceList testingInstances = new PagedInstanceList(pipe,64,128);

        trainingInstances.addThruPipe(new LineGroupIterator(new BufferedReader(new InputStreamReader(new FileInputStream(trainingFilename))), Pattern.compile("\\s+"), true));
        testingInstances.addThruPipe(new LineGroupIterator(new BufferedReader(new InputStreamReader(new FileInputStream(testingFileName))), Pattern.compile("\\s+"), true));
        for(Instance instance:trainingInstances){
           System.out.println("Instance: "+instance.toString());
        }
        switch(method){
            case CRF:this.trainCRF(trainingInstances,testingInstances,trainingFilename,testingFileName,trainingtime,transliterationMethod);break;
            case C45:this.trainC45(trainingInstances,testingInstances,trainingFilename,testingFileName,trainingtime,transliterationMethod);break;
            case DECISIONTREE:this.trainDecisionTrees(trainingInstances,testingInstances,testingFileName,trainingtime,transliterationMethod);break;
            case HMM:this.trainHMM(trainingInstances,testingInstances,testingFileName,trainingtime,transliterationMethod);break;
            case MAXENT: this.trainMaxEnt(trainingInstances,testingInstances,testingFileName,trainingtime,transliterationMethod);break;
            case NAIVEBAYES: this.trainNaiveBayes(trainingInstances, testingInstances,testingFileName,trainingtime,transliterationMethod);break;
            //case WINNOW:this.trainWinnow(trainingInstances,testingInstances,testingFileName,trainingtime,transliterationMethod);break;
            default:
        }

    }

    /**
     * Initialises the mallet processing pipe if necessary.
     * @param method the method to use
     * @param feature the feature set to use
     */
    public void initPipe(final ClassificationMethod method,final FeatureSets feature){
        this.labelings.clear();
        this.feature=feature;
        if(!this.pipes.containsKey(method)){
           switch(method){
               case CRF:
               case C45:
               case DECISIONTREE:
                   this.pipes.put(method,new LinkedList<Pipe>());
                   this.pipes.get(method).add(new SimpleTagger.SimpleTaggerSentence2FeatureVectorSequence());
                   //this.pipes.put(method,new FeatureVe)
                   //this.pipes.get(method).add(new SimpleTaggerSentence2TokenSequence());
                   //this.pipes.get(method).add(new SimpleTaggerSentence2TokenSequence(false));
                   //this.pipes.get(method).add(new TokenSequence2FeatureVectorSequence());
                   this.pipes.get(method).add(new PrintInputAndTarget());
                   break;
               case HMM:this.pipes.put(method,new LinkedList<Pipe>());
                        //this.pipes.get(method).add(new SimpleTaggerSentence2TokenSequence());
                        this.pipes.get(method).add(new TokenSequence2FeatureSequence());
                        this.pipes.get(method).add(new PrintInputAndTarget());
                    break;
               case MAXENT:
                   this.pipes.put(method,new LinkedList<Pipe>());
                   //this.pipes.get(method).add(new SimpleTaggerSentence2TokenSequence());
                   this.pipes.get(method).add(new TokenSequence2FeatureSequence());
                   this.pipes.get(method).add(new FeatureSequence2FeatureVector());
                   this.pipes.get(method).add(new Target2Label());
                   this.pipes.get(method).add(new PrintInputAndTarget());
               break;
               case NAIVEBAYES:
                   this.pipes.put(method,new LinkedList<Pipe>());
                   //this.pipes.get(method).add(new SimpleTaggerSentence2TokenSequence());
                   this.pipes.get(method).add(new TokenSequence2FeatureSequence());
                   this.pipes.get(method).add(new FeatureSequence2FeatureVector());
                   this.pipes.get(method).add(new Target2Label());
                   this.pipes.get(method).add(new PrintInputAndTarget());
               break;
               /*case WINNOW:
                   this.pipes.put(method,new LinkedList<Pipe>());
                   //this.pipes.get(method).add(new SimpleTaggerSentence2TokenSequence());
                   this.pipes.get(method).add(new TokenSequence2FeatureSequence());
                   this.pipes.get(method).add(new FeatureSequence2FeatureVector());
                   this.pipes.get(method).add(new Target2Label());
                   this.pipes.get(method).add(new PrintInputAndTarget());
               break;*/
               default:
           }
        }

    }

    public InstanceList readDirectories(File[] directories) {

        // Construct a file iterator, starting with the
        //  specified directories, and recursing through subdirectories.
        // The second argument specifies a FileFilter to use to select
        //  files within a directory.
        // The third argument is a Pattern that is applied to the
        //   filename to produce a class label. In this case, I've
        //   asked it to use the last directory name in the path.
        FileIterator iterator =
                new FileIterator(directories,
                        null,
                        FileIterator.LAST_DIRECTORY);
        Pipe pipe=this.buildPipe();
        // Construct a new trainingSet list, passing it the pipe
        //  we want to use to process instances.
        InstanceList instances = new InstanceList(pipe);

        // Now process each trainingSet provided by the iterator.
        instances.addThruPipe(iterator);

        return instances;
    }

    public InstanceList readDirectory(File directory) {
        return readDirectories(new File[] {directory});
    }

    /**
     * Processes a result labeling generated by some methods to a result file.
     * @param originalFileName the name of the original file
     * @param testingFileAbbName the name of the testing file
     * @param labeling the labeling
     * @param method the classification method
     * @throws IOException on error
     */
    private void resultLabelingToFile(final String originalFileName,final String testingFileAbbName,Labeling labeling,ClassificationMethod method,TransliterationMethod transliterationMethod) throws IOException {
        System.out.println("RESULTLABELINGTOFILE===================================================");
        BufferedReader reader=new BufferedReader(new FileReader(new File(originalFileName)));
        BufferedWriter writer=new BufferedWriter(new FileWriter(new File(Files.RESULTDIR+method.toString().toLowerCase()+"_"+this.feature.toString().toLowerCase()+"_"+testingFileAbbName+Files.RESULT)));
        String currentline,currentword="";
        Integer classificationcounter=0;
        LangChar tempword;
        String[] labelarray=labeling.getBestLabel().toString().split(":");
        List<String> labellist=new LinkedList<String>();
        ArffHandler.arrayToStr(labelarray);
        for(String label:labelarray){
            System.out.println("Label: "+label);
            System.out.println("Label2: "+label.replaceAll("[0-9]","").replaceAll("\\(","").replaceAll("\\)","").replaceAll(" ","").replaceAll(",","").replaceAll("\n",";"));
            String templabel=label.replaceAll("\\(","").replaceAll("\\)","").replaceAll("\n"," ").replaceAll(" ","").replaceAll(",", "").substring(0,1);
            System.out.println("Templabel: "+templabel);
            if(!templabel.isEmpty())
                labellist.add(templabel);
        }
        while((currentline=reader.readLine())!=null){
            if(currentline.isEmpty()){
                continue;
            }
            for(int i=0;i<currentline.length()-2;i+=2){
                currentword+=currentline.substring(i,i+2);
                System.out.println("Label: "+labellist.get(classificationcounter)+"\nLabelEnd");
                if(labellist.get(classificationcounter++).toString().equals(this.feature.getFeatureSet().getClassValues()[1])){
                    tempword=this.dicthandler.matchWord(currentword);
                    if(tempword!=null)
                        writer.write("["+((CuneiChar)tempword).getFirstSingleTransliteration()+"] ");
                    else{
                        writer.write(dicthandler.getNoDictTransliteration(currentword, transliterationMethod));
                    }
                    currentword="";
                }
            }
            if(!currentword.isEmpty()){
                tempword=this.dicthandler.matchWord(currentword);
                if(tempword!=null)
                    writer.write("["+((CuneiChar)tempword).getFirstSingleTransliteration()+"] ");
                else{
                    writer.write(this.dicthandler.getNoDictTransliteration(currentword, transliterationMethod));
                }
            }
            writer.write("\n");
        }
        writer.close();
    }

    /**
     * Processes a result generated by some methods to a result file.
     * @param originalFileName the name of the original file
     * @param testingFileAbbName the name of the testing file
     * @param method the classification method
     * @throws IOException on error
     */
    private void resultToFile(final String originalFileName,final String testingFileAbbName,final ClassificationMethod method,final TransliterationMethod transliterationMethod) throws IOException {
        System.out.println("RESULTTOFILE===================================================");
        BufferedReader reader=new BufferedReader(new FileReader(new File(originalFileName)));
        BufferedWriter writer=new BufferedWriter(new FileWriter(new File(Files.RESULTDIR+method.toString().toLowerCase()+"_"+this.feature.toString().toLowerCase()+"_"+testingFileAbbName+Files.RESULT.toString())));
        String currentline,currentword="";
        Integer classificationcounter=0;
        LangChar tempword;
        while((currentline=reader.readLine())!=null){
            if(currentline.isEmpty()){
                continue;
            }
            for(int i=0;i<currentline.length()-2;i+=2){
                currentword+=currentline.substring(i,i+2);
                if(this.result.getOutput(classificationcounter++)){
                    tempword=this.dicthandler.matchWord(currentword);
                    if(tempword!=null)
                        writer.write("["+((CuneiChar)tempword).getFirstSingleTransliteration()+"] ");
                    else{
                        writer.write(dicthandler.getNoDictTransliteration(currentword, transliterationMethod));
                    }
                    currentword="";
                }
            }
            if(!currentword.isEmpty()){
                tempword=this.dicthandler.matchWord(currentword);
                if(tempword!=null)
                    writer.write("["+((CuneiChar)tempword).getFirstSingleTransliteration()+"] ");
                else{
                    writer.write(dicthandler.getNoDictTransliteration(currentword, transliterationMethod));
                }
            }
            writer.write("\n");
        }
        writer.close();
    }

    /**
     * Runs the c45 classifier for training.
     * @param trainingInstances the training instances
     * @param testingInstances the testing instances
     * @param testingFileName the name of the testing set file
     * @param trainingtime the name of the training set file
     * @throws IOException on error
     */
    public void trainC45(InstanceList trainingInstances,InstanceList testingInstances,String trainingFileName, String testingFileName,Integer trainingtime,TransliterationMethod transliterationMethod) throws IOException {
        C45 c45=new C45(new SerialPipes(this.pipes.get(ClassificationMethod.C45)),null);
        C45Trainer c45Trainer=new C45Trainer(DecisionTreeTrainer.DEFAULT_MAX_DEPTH);
        c45Trainer.train(trainingInstances);
        c45Trainer.getClassifier().classify(testingInstances);
        List<Classification> laber=c45.classify(testingInstances);
        this.resultLabelingToFile(this.originalFileName,trainingFileName,laber.get(0).getLabeling(), ClassificationMethod.C45,transliterationMethod);
    }

    /**
     * Runs the crf classifier for training.
     * @param trainingInstances
     * @param testingInstances
     * @param trainingFileName
     * @param testingFileName
     * @param trainingtime
     * @throws IOException
     */
    public void trainCRF(InstanceList trainingInstances,InstanceList testingInstances,String trainingFileName, String testingFileName, final Integer trainingtime,final TransliterationMethod transliterationMethod) throws IOException {
        String testingFileAbbName=testingFileName.substring(testingFileName.indexOf('/')+1,testingFileName.indexOf('.'));
        CRF crf = new CRF(trainingInstances.getAlphabet(),trainingInstances.getTargetAlphabet());
        crf.addFullyConnectedStatesForLabels();

        //crf.setWeightsDimensionAsIn(trainingInstances, false);
        //crf.addStatesForThreeQuarterLabelsConnectedAsIn(trainingInstances);
        //crf.addStartState();
        //crf.addFullyConnectedStatesForLabels();
        // initialize model's weights
        //crf.setWeightsDimensionAsIn(trainingInstances, false);

        //  CRFOptimizableBy* objects (terms in the objective function)
        // objective 1: label likelihood objective
        CRFOptimizableByLabelLikelihood optLabel =
                new CRFOptimizableByLabelLikelihood(crf, trainingInstances);
       /* CRFOptimizableByBatchLabelLikelihood batchOptLabel =
                new CRFOptimizableByBatchLabelLikelihood(crf, trainingInstances, 4);
        ThreadedOptimizable optLabel = new ThreadedOptimizable(
                batchOptLabel, trainingInstances, crf.getParameters().getNumFactors(),
                new CRFCacheStaleIndicator(crf)); */
        // CRF trainer
        Optimizable.ByGradientValue[] opts =
                new Optimizable.ByGradientValue[]{optLabel};
        /*CRFTrainerByThreadedLikelihood trainer =
                new CRFTrainerByThreadedLikelihood(crf, numThreads);
        trainer.train(trainingData);
        trainer.shutdown();;/*
        // by default, use L-BFGS as the optimizer
        CRFTrainerByValueGradients crfTrainer =
                new CRFTrainerByValueGradients(crf, opts);
        crf.setWeightsDimensionAsIn(trainingInstances, false);
        CRFOptimizableByLabelLikelihood optLabel =
                new CRFOptimizableByLabelLikelihood(crf, trainingInstances);
        Optimizable.ByGradientValue[] opts =
                new Optimizable.ByGradientValue[]{optLabel};*/
        /*CRFTrainerByLabelLikelihood trainer =
                new CRFTrainerByLabelLikelihood(crf);*/
        //trainer.setGaussianPriorVariance(10.0);
        //CRFTrainerByValueGradients trainer =
                //new CRFTrainerByValueGradients(crf, opts);
        CRFTrainerByStochasticGradient trainer=
                new CRFTrainerByStochasticGradient(crf,1.);
        //Optimizable.ByGradientValue[] opts = new Optimizable.ByGradientValue[]{optLabel};

        CRFTrainerByValueGradients crfTrainer = new CRFTrainerByValueGradients(crf, opts);
        TransducerTrainer tt = crfTrainer;


        String[] labels = trainingInstances.getTargetAlphabet().toString().split("\n");

        TransducerEvaluator evaluator = new TokenAccuracyEvaluator(
                new InstanceList[]{trainingInstances, testingInstances},
                new String[]{"train", "test"});
       /* String[] labels = new String[]{"1", "0"};

        TransducerEvaluator evaluator = new MultiSegmentationEvaluator(
                new InstanceList[]{trainingInstances, testingInstances},
                new String[]{"train", "test"}, labels, labels) {
            @Override
            public boolean precondition(TransducerTrainer tt) {
                System.out.println("IterationTransduce: " + tt.getIteration());
                return tt.getIteration() % 5 == 0;
            }
        };*/
        this.result = new StatOutputWriter(
                "/home/timo/workspace2/Master/results/"+ ClassificationMethod.CRF.toString()+"_"+testingFileAbbName, // output file prefix
                new InstanceList[] { trainingInstances, testingInstances },
                new String[] { "0", "1" }) {
            @Override
            public boolean precondition (TransducerTrainer tt) {
                System.out.println("IterationOut: " + tt.getIteration());
                return tt.getIteration()==trainingtime;
            }
        };
        CRFWriter crfWriter = new CRFWriter("/home/timo/workspace2/Master/trainingdata/ner_crf.model") {
            @Override
            public boolean precondition(TransducerTrainer tt) {
                System.out.println("IterationCRF: "+tt.getIteration());
                return tt.getIteration() % Integer.MAX_VALUE == 10;
            }
        };
        trainer.addEvaluator(new PerClassAccuracyEvaluator(trainingInstances, "train"));
        //trainer.addEvaluator(new TokenAccuracyEvaluator(testingInstances, "test"));
        trainer.addEvaluator(evaluator);
        //trainer.addEvaluator(this.result);
        System.out.println(trainingInstances.getAlphabet()+" - "+trainingInstances.getTargetAlphabet());
        //trainer.addEvaluator(crfWriter);
        trainer.addEvaluator(this.result);
        // all setup done, train until convergence
        //trainer.setMaxResets(0);
        trainer.train(trainingInstances, 25);
        // evaluate
        //evaluator.evaluateInstanceList(tt, testingInstances, "testing");
        //                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  trainer.setMaxResets(0);
        //trainer.train(trainingInstances,5);
        //optLabel.shutdown();
        evaluator.evaluate(trainer);
        System.out.println("Output(1): " + this.result);
        //this.resultToFile(this.originalFileName,testingFileAbbName,MethodEnum.CRF);

    }

    /**
     * Runs the decision tree classifier for training.
     * @param trainingInstances the training instances
     * @param testingInstances the testing instances
     * @param trainingtime the name of the training set file
     * @throws IOException on error
     */
    public void trainDecisionTrees(InstanceList trainingInstances,InstanceList testingInstances,String trainingFileName,Integer trainingtime,final TransliterationMethod transliterationMethod) throws IOException {
       DecisionTree tree=new DecisionTree(new SerialPipes(this.pipes.get(ClassificationMethod.DECISIONTREE)),null);
       DecisionTreeTrainer treeTrainer=new DecisionTreeTrainer(DecisionTreeTrainer.DEFAULT_MAX_DEPTH);
       treeTrainer.train(trainingInstances);
       List<Classification> labels=treeTrainer.getClassifier().classify(testingInstances);
       this.resultLabelingToFile(this.originalFileName, trainingFileName, labels.get(0).getLabeling(), ClassificationMethod.C45, transliterationMethod);
    }

    /**
     * Trains the hmm classifier.
     * @param trainingInstances the training instances
     * @param testingInstances the testing instances
     * @param testingFileName the name of the testing set file
     * @param trainingtime the name of the training set file
     * @throws IOException on error
     */
    public void trainHMM(InstanceList trainingInstances,InstanceList testingInstances, String testingFileName,final Integer trainingtime,final TransliterationMethod transliterationMethod) throws IOException {
        String testingFileAbbName=testingFileName.substring(testingFileName.indexOf('/')+1,testingFileName.indexOf('.'));
       HMM hmm=new HMM(new SerialPipes(this.pipes.get(ClassificationMethod.HMM)),null);
        hmm.addFullyConnectedStatesForLabels();
        //hmm.addStatesForLabelsConnectedAsIn(trainingInstances);
       HMMTrainerByLikelihood hmmtrainer=new HMMTrainerByLikelihood(hmm);
        TransducerEvaluator trainingEvaluator =
                new PerClassAccuracyEvaluator(trainingInstances, "train");
        TransducerEvaluator testingEvaluator =
                new PerClassAccuracyEvaluator(testingInstances, "test");
       this.result = new StatOutputWriter(
                "/home/timo/workspace2/Master/results/"+ ClassificationMethod.HMM.toString()+"_"+testingFileAbbName, // output file prefix
                new InstanceList[] { trainingInstances, testingInstances },
                new String[] { "train", "test" }) {
            @Override
            public boolean precondition (TransducerTrainer tt) {
                System.out.println("Viterbiii");
                return tt.getIteration()==trainingtime;
            }
        };
        hmmtrainer.addEvaluator(this.result);
        hmmtrainer.train(trainingInstances);
        this.resultToFile(this.originalFileName, testingFileAbbName, ClassificationMethod.HMM, transliterationMethod);
    }

    /**
     * Runs the maximum entropy mc classifier for training.
     * @param trainingInstances the training instances
     * @param testingInstances the testing instances
     * @param testingFileName the name of the testing set file
     * @param trainingtime the name of the training set file
     * @throws IOException on error
     */
    /*public void trainMCMaxEnt(InstanceList trainingInstances,InstanceList testingInstances,String testingFileName,Integer trainingtime,final TransliterationMethod transliterationMethod) throws IOException {
        String testingFileAbbName=testingFileName.substring(testingFileName.indexOf('/')+1,testingFileName.indexOf('.'));
        MCMaxEnt maxent=new MCMaxEnt(new SerialPipes(this.pipes.get(ClassificationMethod.MAXMCENT)),null);
        MCMaxEntTrainer trainer=new MCMaxEntTrainer(maxent);
        trainer.train(trainingInstances);
        this.result = new StatOutputWriter(
                "/home/timo/workspace2/Master/results/"+ ClassificationMethod.MAXMCENT.toString()+"_"+testingFileAbbName, // output file prefix
                new InstanceList[] { trainingInstances, testingInstances },
                new String[] { "train", "test" }) {
            @Override
            public boolean precondition (TransducerTrainer tt) {
                System.out.println("Viterbiii");
                return true;
            }
        };
        List<Classification> classification=trainer.getClassifier().classify(testingInstances);
        this.resultLabelingToFile(this.originalFileName,testingFileAbbName,classification.get(0).getLabeling(), ClassificationMethod.MAXMCENT,transliterationMethod);
        System.out.println(classification.get(0).getLabeling().labelAtLocation(0));

    } */

    /**
     * Runs the maximum entropy classifier for training.
     * @param trainingInstances the training instances
     * @param testingInstances the testing instances
     * @param testingFileName the name of the testing set file
     * @param trainingtime the name of the training set file
     * @throws IOException on error
     */
    public void trainMaxEnt(final InstanceList trainingInstances,final InstanceList testingInstances,final String testingFileName,final Integer trainingtime,final TransliterationMethod transliterationMethod) throws IOException {
        String testingFileAbbName=testingFileName.substring(testingFileName.indexOf('/')+1,testingFileName.indexOf('.'));
        MaxEntTrainer trainer=new MaxEntTrainer();
        Classifier classifier=trainer.train(trainingInstances);
        this.result = new StatOutputWriter(
                "/home/timo/workspace2/Master/results/"+ ClassificationMethod.MAXENT.toString()+"_"+testingFileAbbName, // output file prefix
                new InstanceList[] { trainingInstances, testingInstances },
                new String[] { "train", "test" }) {
            @Override
            public boolean precondition (TransducerTrainer tt) {
                System.out.println("Viterbiii");
                return true;
            }
        };
        List<Classification> classification=trainer.getClassifier().classify(testingInstances);
        this.resultLabelingToFile(this.originalFileName,testingFileAbbName,classification.get(0).getLabeling(), ClassificationMethod.MAXENT,transliterationMethod);
    }

    /**
     * Runs the naive bayes classifier for training.
     * @param trainingInstances the training instances
     * @param testingInstances the testing instances
     * @param testingFileName the name of the testing set file
     * @param trainingtime the name of the training set file
     * @throws IOException on error
     */
    public void trainNaiveBayes(final InstanceList trainingInstances, final InstanceList testingInstances,final String testingFileName,Integer trainingtime,final TransliterationMethod transliterationMethod) throws IOException {
        String testingFileAbbName=testingFileName.substring(testingFileName.indexOf('/')+1,testingFileName.indexOf('.'));
        NaiveBayesTrainer trainer=new NaiveBayesTrainer();
        Classifier classifier=trainer.train(trainingInstances);
        this.result = new StatOutputWriter(
                "/home/timo/workspace2/Master/results/"+ ClassificationMethod.NAIVEBAYES.toString()+"_"+testingFileAbbName, // output file prefix
                new InstanceList[] { trainingInstances, testingInstances },
                new String[] { "train", "test" }) {
            @Override
            public boolean precondition (TransducerTrainer tt) {
                System.out.println("Viterbiii");
                return true;
            }
        };
        this.labelings=trainer.getClassifier().classify(testingInstances);
        System.out.println("Labelings: ");
        for(Classification classi:this.labelings){
            System.out.println(classi.getLabeling());
        }
        this.resultLabelingToFile(originalFileName,testingFileAbbName,this.labelings.get(0).getLabeling(), ClassificationMethod.NAIVEBAYES,transliterationMethod);
    }

    /**
     * Runs the winnow classifier for training.
     * @param trainingInstances
     * @param testingInstances
     * @param testingFileName
     * @param trainingtimes
     */
    public void trainWinnow(InstanceList trainingInstances,InstanceList testingInstances,String testingFileName,Integer trainingtimes,final TransliterationMethod transliterationMethod){
        String testingFileAbbName=testingFileName.substring(testingFileName.indexOf('/')+1,testingFileName.indexOf('.'));
        //Winnow winnow=new Winnow(new SerialPipes(this.pipes.get(MethodEnum.MAXENT)),null);
    }

}
