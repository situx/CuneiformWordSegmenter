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

package de.unifrankfurt.cs.acoli.akkad.main;

import de.unifrankfurt.cs.acoli.akkad.dict.corpusimport.CorpusHandlerAPI;
import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.cuneiform.CuneiDictHandler;
import de.unifrankfurt.cs.acoli.akkad.eval.Evaluation;
import de.unifrankfurt.cs.acoli.akkad.main.gui.MainGUI;
import de.unifrankfurt.cs.acoli.akkad.methods.segmentation.dict.DictMethods;
import de.unifrankfurt.cs.acoli.akkad.methods.segmentation.rule.RuleMethods;
import de.unifrankfurt.cs.acoli.akkad.methods.segmentation.stat.StatMethods;
import de.unifrankfurt.cs.acoli.akkad.methods.segmentation.stat.WekaException;
import de.unifrankfurt.cs.acoli.akkad.methods.transcription.TranscriptionMethods;
import de.unifrankfurt.cs.acoli.akkad.methods.translation.TranslationMethods;
import de.unifrankfurt.cs.acoli.akkad.util.ArffHandler;
import de.unifrankfurt.cs.acoli.akkad.util.Config;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.*;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Files;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

/**Main class of the project.
 * 
 * @author Timo Homburg
 *
 */
public class Main {
    public static ResourceBundle bundle=ResourceBundle.getBundle(Config.RESBUNDLENAME,Locale.getDefault());
    /**
     * Object containing dictionary based methods.
     */
    private final DictMethods dictMethods;
    /**Object containing rule based methods.*/
    private final RuleMethods ruleMethods;
    /**Object containing statistical methods.*/
    private final StatMethods statMethods;
    private final TranscriptionMethods transcriptionMethods;
    private final TranslationMethods translationMethods;
    public DictHandling dictHandler;
    private CorpusHandlerAPI corpusHandler;
    /**Object containing evaluation methods.*/
    private Evaluation evaluation;
    private String testFile,testFilePath;

    /**
     * Constructor for this class.
     * @throws IOException on error
     */
    public Main(final Evaluation evaluation) throws IOException {
        this.dictMethods=new DictMethods();
        this.statMethods=new StatMethods();
        this.ruleMethods=new RuleMethods();
        this.translationMethods=new TranslationMethods();
        this.transcriptionMethods=new TranscriptionMethods();
        this.evaluation=evaluation;
    }

    /**
     * Constructor for this class.
     * @throws IOException on error
     */
    public Main() throws IOException {
        this.dictMethods=new DictMethods();
        this.statMethods=new StatMethods();
        this.ruleMethods=new RuleMethods();
        this.translationMethods=new TranslationMethods();
        this.transcriptionMethods=new TranscriptionMethods();
    }

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, XMLStreamException {
        //String[] defineargs={"akkad","-train",Files.TRAININGDATADIR.toString()+"first20_maxent.mallet","-test",Files.TESTDATADIR.toString()+Files.FIRST20.toString(),"-method",MethodEnum.AVGWORDLEN.toString(),"-feature",FeatureSets.MAXENT.toString()};
        String[] defineargs={"akkad","-train",Files.TRAININGDATADIR.toString()+"corpusout.txt","-test",Files.TESTDATADIR.toString()+Files.FIRST20.toString(),
                "-method", ClassificationMethod.HIGHESTOCCURANCE.toString(),"-feature",FeatureSets.MAXENT.toString(),"-evaluation",EvaluationMethod.ALL.getShortname(),"-transliteration",TransliterationMethod.MAXPROB.getShortlabel()};
        args=defineargs;
        ArffHandler.arrayToStr(args);
        if(args.length<7){
            System.out.println("Too less parameters to run this program!");
            System.out.println("Syntax: akkad -train FILE -test FILE -method METHODNAME -feature FEATURESET -evaluate EVALUATIONMETHOD -transliteration TRANSLITERATIONMETHOD");
            return;
        }
        try {
            Main.startClassification(args);
        } catch (WekaException e) {
            e.printStackTrace();
        }
    }

    public static void startClassification(final String[] args) throws ParserConfigurationException, XMLStreamException, SAXException, IOException, WekaException {
        String comparepath=Files.REFORMATTEDDIR+"corpus"+Files.REFORMATTED;
        EvaluationMethod evalmethod=EvaluationMethod.ALL;
        TransliterationMethod transliterationMethod= TransliterationMethod.FIRST;
        CharTypes sourcelang=CharTypes.AKKADIAN;


        //dictHandler.morfessorExport("corpusimport.morfessor");
        String trainingdata=args[2];
        String testdata=args[4];
        String modelfile="";
        FeatureSets featureSet= FeatureSets.NOFEATURE;
        //Files.TESTDATADIR.toString()+Files.FIRST20.toString()
        ArffHandler.arrayToStr(args);
        if(args.length>7){
            featureSet= FeatureSets.valueOf(args[8]);
        }
        System.out.println(Arrays.toString(args));
        if(args.length>9){
            evalmethod= EvaluationMethod.valueOf(args[10]);
        }
        if(args.length>11){
            transliterationMethod=TransliterationMethod.valueOf(args[12]);
        }
        if(args.length>13){
            sourcelang=CharTypes.valueOf(args[14]);
        }
        if(args.length>14){
            modelfile=args[15];
        }
        CorpusHandlerAPI corpusHandlerAPI=sourcelang.getCorpusHandlerAPI();
        DictHandling dictHandler=corpusHandlerAPI.generateTestTrainSets("","",0.,0.,TestMethod.FOREIGNTEXT,sourcelang);
        dictHandler.parseDictFile(new File(Files.AKKADXML.toString()));

        dictHandler.importMappingFromXML(Files.DICTDIR+"Akkadian"+Files.MAPSUFFIX);
        dictHandler.importDictFromXML(Files.DICTDIR+"Akkadian"+Files.DICTSUFFIX);
        //dictHandler.importReverseDictFromXML(Files.DICTDIR+"Akkadian"+Files.REVERSE+Files.DICTSUFFIX);
        //AkkadCorpusHandler corpushandler=new AkkadCorpusHandler();

        //corpushandler.generateCorpusDictionaryFromFile("corpusimport.txt",dictHandler,true);
        ClassificationMethod classificationMethod= ClassificationMethod.valueOf(args[6]);
        String resultfile=classificationMethod.getHasFeatureSet()?Files.RESULTDIR+Files.TRANSLITDIR.toString()+testdata.substring(testdata.lastIndexOf('/')+1,testdata.lastIndexOf('.'))
                +"_"+classificationMethod.getShortname().toLowerCase()+"_"+featureSet.toString().toLowerCase()+"_"+transliterationMethod.getShortlabel().toLowerCase()+Files.RESULT.toString():
                Files.RESULTDIR+Files.TRANSLITDIR.toString()+testdata.substring(testdata.lastIndexOf('/')+1,testdata.lastIndexOf('.'))
                        +"_"+classificationMethod.getShortname().toLowerCase()+"_"+transliterationMethod.getShortlabel().toLowerCase()+Files.RESULT.toString();
        String resultfilecunei=classificationMethod.getHasFeatureSet()?Files.RESULTDIR+Files.CUNEIFORMDIR.toString()+testdata.substring(testdata.lastIndexOf('/')+1,testdata.lastIndexOf('.'))
                +"_"+classificationMethod.getShortname().toLowerCase()+"_"+featureSet.toString().toLowerCase()+"_"+transliterationMethod.getShortlabel().toLowerCase()+Files.RESULT.toString():
                Files.RESULTDIR+Files.CUNEIFORMDIR.toString()+testdata.substring(testdata.lastIndexOf('/')+1,testdata.lastIndexOf('.'))
                        +"_"+classificationMethod.getShortname().toLowerCase()+"_"+transliterationMethod.getShortlabel().toLowerCase()+Files.RESULT.toString();

        Evaluation eval=new Evaluation(comparepath,
                resultfile,resultfilecunei,comparepath,/*"_"+featureSet.toString().toLowerCase()
                        +"_"+Files.FIRST20NOSUF.toString()+Files.RESULT,*/dictHandler);
        Main main=new Main(eval);
        main.executeMethod(testdata, trainingdata,modelfile,"", dictHandler, featureSet, classificationMethod, transliterationMethod, sourcelang, false,false,TestMethod.FOREIGNTEXT);
        main.evaluate(Files.RESULTDIR + Files.TRANSLITDIR.toString() + testdata.substring(testdata.lastIndexOf('/') + 1, testdata.lastIndexOf('.'))
                + "_" + classificationMethod.getShortname().toLowerCase()
                + "_" + transliterationMethod.getShortlabel().toLowerCase()
                + Files.RESULT, evalmethod, true, classificationMethod);
        /*dictHandler.exportToXML(Files.DICTDIR.toString()+ Tags.AKKADIAN.toString()+Files.DICTSUFFIX.toString(),
                Files.DICTDIR.toString()+ Tags.AKKADIAN.toString()+Tags.REVERSE+Files.DICTSUFFIX.toString(),
                Files.DICTDIR.toString()+Tags.AKKADIAN.toString()+Files.MAPSUFFIX.toString()); */
        //AkkadDictHandler akkad=new AkkadDictHandler(Files.DICTDIR.toString()+ Tags.AKKADIAN.toString()+Files.DICTSUFFIX.toString(),Files.DICTDIR.toString()+Tags.AKKADIAN.toString()+Files.MAPSUFFIX.toString());

    }

    /**
     * Evaluation method.
     */
    public String evaluate(final String filepath, final EvaluationMethod evaluation, final Boolean cuneiformOrTranslit,final ClassificationMethod classificationMethod) throws IOException {
        StringBuffer resultbuffer=new StringBuffer();
        resultbuffer.append("======================Evaluating: ");
        resultbuffer.append(filepath);
        resultbuffer.append("===========================\n");
        switch (evaluation){
            case BOUNDARYEDITDISTANCE: resultbuffer.append(this.evaluation.boundaryEditDistance(true,classificationMethod));break;
            case BOUNDARYSIMILARITY:  resultbuffer.append(this.evaluation.boundarySimilarity(true,classificationMethod));break;
            case BOUNDARYEVALUATION:  resultbuffer.append(this.evaluation.boundaryBasedEvaluation(true,classificationMethod));break;
            case BINARYEVALUATION:    resultbuffer.append(this.evaluation.binaryDecisionEvaluation(true,classificationMethod));break;
            case WORDBOUNDARYEVALUATION: resultbuffer.append(this.evaluation.wordBoundaryBasedEvaluation(true,classificationMethod));break;
            case PKEVALUATION: resultbuffer.append(this.evaluation.pkEvaluation(true,classificationMethod));break;
            case SEGMENTATIONEVALUATION:resultbuffer.append(this.evaluation.segmentationEvaluation(true,classificationMethod));break;
            case TRANSLITEVALUATION: resultbuffer.append(this.evaluation.transliterationEvaluation(true,classificationMethod));break;
            case WINDOWDIFFEVALUATION:resultbuffer.append(this.evaluation.windowDiff(true,classificationMethod));break;
            case WINPR: resultbuffer.append(this.evaluation.winPR(true,classificationMethod));break;
            case ALL:
            default: resultbuffer.append(this.evaluation.transliterationEvaluation(false,classificationMethod));
                resultbuffer.append(this.evaluation.segmentationEvaluation(true,classificationMethod));

                //resultbuffer.append(this.evaluation.binaryDecisionEvaluation(true,classificationMethod));
                resultbuffer.append(this.evaluation.boundaryBasedEvaluation(true,classificationMethod));
                resultbuffer.append(this.evaluation.wordBoundaryBasedEvaluation(true,classificationMethod));
                resultbuffer.append(this.evaluation.pkEvaluation(true,classificationMethod));
                resultbuffer.append(this.evaluation.windowDiff(true, classificationMethod));
                resultbuffer.append(this.evaluation.winPR(true, classificationMethod));
                //resultbuffer.append(this.evaluation.boundaryEditDistance(true,classificationMethod));
                resultbuffer.append(this.evaluation.boundarySimilarity(true,classificationMethod));
        }
        return resultbuffer.toString();
    }

    /**
     * Executes a given method using given parameters.
     * @param testdata the testdata to use
     * @param trainingdata the trainingdata to use
     * @param dictHandler the dicthandler to use
     * @param featureSet the feature set to use
     * @param method the method to use
     * @throws IOException on error
     */
    public void executeMethod(final String testdata,final String trainingdata,final String destpath,final String modelfile,final DictHandling dictHandler,final FeatureSets featureSet,final ClassificationMethod method,final TransliterationMethod transliterationMethod,final CharTypes chartype,final Boolean transcriptToTranslit,final Boolean corpusstr,final TestMethod testMethod) throws IOException, WekaException {
        switch(method){
            case AVGWORDLEN: this.ruleMethods.matchByAvgWordLength(testdata,destpath,dictHandler,transliterationMethod,chartype,transcriptToTranslit,corpusstr);
                break;
            case BIGRAM: this.statMethods.bigramSegmenting(testdata,dictHandler,transliterationMethod,chartype,testMethod);
                break;
            case BIGRAMHMM: this.statMethods.bigramHMMMatching(testdata,dictHandler,transliterationMethod,chartype,testMethod);
                break;
            case BREAKPOINT:this.dictMethods.breakPointMatching(testdata,destpath,dictHandler,transliterationMethod,chartype,corpusstr);
                break;
            case C45: this.statMethods.c45Segmenting(testdata,trainingdata,modelfile,dictHandler,featureSet,transliterationMethod,chartype,testMethod);
                break;
            case CHARSEGMENTPARSE: this.ruleMethods.charSegmentParse(testdata,destpath,dictHandler,transliterationMethod,chartype,transcriptToTranslit,corpusstr);
                break;
            case CRF:  this.statMethods.conditionalRandomFields(testdata,trainingdata,modelfile,dictHandler,featureSet,transliterationMethod,chartype,testMethod);
                break;
            case DECISIONTREE:
                break;
            case HIGHESTOCCURANCE: this.statMethods.highestOccuranceSegmenting(testdata,dictHandler,transliterationMethod);
                break;
            case IB1:  this.statMethods.knn(testdata,trainingdata,modelfile,dictHandler,featureSet,transliterationMethod,chartype,testMethod);
                break;
            case HMM:  this.statMethods.hmmSegmenting(testdata,trainingdata,modelfile,dictHandler,featureSet,transliterationMethod,chartype,testMethod);
                break;
            case KMEANS:  this.statMethods.kmeans(testdata,trainingdata,modelfile,dictHandler,featureSet,transliterationMethod,chartype,testMethod);
                break;
            case LCUMATCHING:  this.dictMethods.lcuMatching(testdata,destpath, dictHandler, transliterationMethod, chartype,corpusstr);
                break;
            case LOGISTICREGRESSION:
            case LOGISTIC:  this.statMethods.logisticRegression(testdata,trainingdata,modelfile,dictHandler,featureSet,transliterationMethod,chartype,testMethod);
                break;
            case MAXENT: this.statMethods.maxEntropyMatching(testdata,trainingdata,modelfile,dictHandler,featureSet,transliterationMethod,chartype,testMethod);
                break;
            case MAXMATCH: this.dictMethods.maxMatch(testdata,destpath,dictHandler,true,transliterationMethod,chartype,corpusstr);
                break;
            case MAXMATCH2: this.dictMethods.maxMatch(testdata,destpath,dictHandler,false,transliterationMethod,chartype,corpusstr);
                break;
            case MAXMATCHCOMBINED: this.dictMethods.maxMatchCombined(testdata,destpath,dictHandler,transliterationMethod,chartype,corpusstr);
                break;
            case MINWCMATCH:this.dictMethods.minWCMatching(testdata,destpath,dictHandler,transliterationMethod,chartype,corpusstr);
                break;
            case MINWCMATCH2:this.dictMethods.minWCMatching2(testdata,destpath,dictHandler,transliterationMethod,chartype,corpusstr);
                break;
            case MAXPROB:  this.statMethods.maxProbSegmenting(testdata,dictHandler,transliterationMethod,chartype,testMethod);
                break;
            case MORFESSOR:
                try {
                    this.dictMethods.morfessorSegmenting(trainingdata,testdata,((CuneiDictHandler)dictHandler),false,transliterationMethod,chartype);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case NAIVEBAYES: this.statMethods.naiveBayesSegmenting(testdata,trainingdata,modelfile,dictHandler,featureSet,transliterationMethod,chartype,testMethod);
                break;
            //case NAIVEBAYESSIMPLE: this.statMethods.naiveBayesSegmenting(testdata,trainingdata,dictHandler,featureSet,transliterationMethod,chartype,testMethod);
            //    break;
            case PERCEPTRON:this.statMethods.perceptron(testdata,trainingdata,modelfile,dictHandler,featureSet,transliterationMethod,chartype,testMethod);
                break;
            case PREFSUFF:  this.ruleMethods.prefixSuffixMatching(testdata,destpath,dictHandler,transliterationMethod,chartype,transcriptToTranslit,corpusstr);
                break;
            case RANDOMSEGMENTPARSE:  this.ruleMethods.randomSegmentParse(testdata,destpath,dictHandler,transliterationMethod,chartype,transcriptToTranslit,corpusstr);
                break;
            case SVM:  this.statMethods.svmSegmenting(testdata,trainingdata,modelfile,dictHandler,featureSet,transliterationMethod,chartype,testMethod);
                break;
            case VOTE:  this.statMethods.voteSegmenting(testdata,trainingdata,modelfile,dictHandler,featureSet,transliterationMethod,chartype,testMethod);
                break;
            case TANGO: this.ruleMethods.tangoAlgorithm(testdata,destpath,dictHandler,3,transliterationMethod, chartype,transcriptToTranslit,corpusstr);
                break;
            //case WINNOW: this.statMethods.winnowSegmenting(testdata,trainingdata,dictHandler,featureSet,transliterationMethod,chartype,testMethod);
            //    break;
            default: System.out.println("No method recognized!");
                return;
        }
    }

    public String getTestFile() {
        return testFile;
    }

    public void setEvaluation(final Evaluation evaluation){
        this.evaluation=evaluation;
    }

    public String startClassification(final String trainingdata,final String testdata,final String modelfile,final ClassificationMethod classificationMethod,
                                      final FeatureSets featureSet,final EvaluationMethod evalmethod,final TransliterationMethod transliterationMethod,
                                      final TranslationMethod translationMethod,final CharTypes targetlang,
                                      final CharTypes sourcelang,final TestMethod testmethod,final String corpusfile,
                                      final Boolean reusedicthandler,final Double startposition,final Double foldOrPerc,final Boolean onlyEval,final Boolean directArff)
            throws ParserConfigurationException,ArithmeticException, XMLStreamException, SAXException, IOException, WekaException {
        String comparepath,comparepathcunei;
        MainGUI.progress=10;
        MainGUI.refreshProgressBar();
        this.corpusHandler=sourcelang.getCorpusHandlerAPI();
        if(!reusedicthandler) {
            MainGUI.progress = 10;
            MainGUI.refreshProgressBar();
            MainGUI.refreshProgressBarMessage(bundle.getString("creatingdict"));
            this.dictHandler=this.corpusHandler.generateTestTrainSets(corpusfile,Files.AKKADXML.toString(),foldOrPerc,startposition,testmethod,sourcelang);
            this.corpusHandler.addTranslations("newwords.xml",testmethod);
            System.out.println("CharType: "+dictHandler.getChartype().toString());


        }
        MainGUI.progress=20;
        MainGUI.refreshProgressBar();
        //dictHandler.morfessorExport("corpusimport.morfessor");
        String resultfile="",resultfilecunei="";


        Evaluation eval;
        MainGUI.progress=30;
        MainGUI.refreshProgressBar();
        MainGUI.refreshProgressBarMessage(bundle.getString("classifying")+" "+classificationMethod);

        String result="";
        Main main;
            switch (testmethod) {
                case CROSSVALIDATION:
                    DictHandling dict;

                    int i = 0;
                    for (i = 0; i < corpusHandler.getCrossfoldLength(); i++) {
                        resultfile = classificationMethod.getHasFeatureSet() ? Files.RESULTDIR + Files.TRANSLITDIR.toString() + corpusfile.substring(corpusfile.lastIndexOf('/') + 1, corpusfile.lastIndexOf(".")) + i + "_" + testmethod.toString().toLowerCase() + "" + corpusHandler.getCrossfoldLength() +
                                "_" + classificationMethod.getShortname().toLowerCase() + "_" + featureSet.toString().toLowerCase() + "_" + transliterationMethod.getShortlabel().toLowerCase() + Files.RESULT.toString() :
                                Files.RESULTDIR + Files.TRANSLITDIR.toString() + corpusfile.substring(corpusfile.lastIndexOf('/') + 1, corpusfile.lastIndexOf(".")) + i + "_" + testmethod.toString().toLowerCase()
                                        + "_" + classificationMethod.getShortname().toLowerCase() + "_" + transliterationMethod.getShortlabel().toLowerCase() + Files.RESULT.toString();
                        resultfilecunei = classificationMethod.getHasFeatureSet() ? Files.RESULTDIR + Files.CUNEIFORMDIR.toString() + corpusfile.substring(corpusfile.lastIndexOf('/') + 1, corpusfile.lastIndexOf(".")) + i + "" + corpusHandler.getCrossfoldLength() + "_" + testmethod.toString().toLowerCase()
                                + "_" + classificationMethod.getShortname().toLowerCase() + "_" + featureSet.toString().toLowerCase() + "_" + transliterationMethod.getShortlabel().toLowerCase() + Files.RESULT.toString() :
                                Files.RESULTDIR + Files.CUNEIFORMDIR.toString() + corpusfile.substring(corpusfile.lastIndexOf('/') + 1, corpusfile.lastIndexOf(".")) + i + "_" + testmethod.toString().toLowerCase()
                                        + "_" + classificationMethod.getShortname().toLowerCase() + "_" + transliterationMethod.getShortlabel().toLowerCase() + Files.RESULT.toString();
                        comparepathcunei = Files.REFORMATTEDDIR + Files.CUNEI_SEGMENTEDDIR.toString() + testmethod.toString().toLowerCase() + File.separator + corpusfile.substring(corpusfile.lastIndexOf('/') + 1);
                        comparepath = Files.REFORMATTEDDIR + Files.TRANSLITDIR.toString() + testmethod.toString().toLowerCase() + File.separator + corpusfile.substring(corpusfile.lastIndexOf('/') + 1);

                        eval = new Evaluation(comparepath,
                                resultfile.substring(0, resultfile.lastIndexOf(".")) + resultfile.substring(resultfile.lastIndexOf(".")), comparepathcunei, resultfilecunei.substring(0, resultfilecunei.lastIndexOf(".")) + resultfilecunei.substring(resultfilecunei.lastIndexOf(".")), dictHandler);
                        main = new Main(eval);
                        if (!onlyEval) {
                        dict = corpusHandler.getCrossValidation(i);

                        main.executeMethod(corpusHandler.getCrossFold(i), trainingdata, corpusfile.substring(corpusfile.lastIndexOf('/') + 1, corpusfile.lastIndexOf(".")) + i + "_" + testmethod.toString().toLowerCase(), modelfile, dict, featureSet, classificationMethod, transliterationMethod, sourcelang, false, true, testmethod);
                        main.translate(new Locale(targetlang.getLocale()), resultfile.substring(0, resultfile.lastIndexOf(".")) + resultfile.substring(resultfile.lastIndexOf(".")), corpusHandler.getCrossValidation(i), translationMethod);
                        main.transcript(resultfile.substring(0, resultfile.lastIndexOf(".")) + resultfile.substring(resultfile.lastIndexOf(".")), resultfile.substring(0, resultfile.lastIndexOf(".")) + resultfile.substring(resultfile.lastIndexOf(".")), corpusHandler.getCrossValidation(i), TranscriptionMethod.TRANSCRIPTTOTRANSLIT, transliterationMethod, classificationMethod);
                        }
                        MainGUI.progress=50;
                        MainGUI.refreshProgressBar();
                        MainGUI.refreshProgressBarMessage(bundle.getString("evaluating") + " " + classificationMethod);
                        result = main.evaluate(resultfile.substring(0, resultfile.lastIndexOf(".")) + i + "_" + resultfile.substring(resultfile.lastIndexOf(".")), evalmethod, true, classificationMethod);
                         }
                    this.testFile = corpusHandler.getTestSetPath();
                    break;
                case PERCENTAGE:
                case RANDOMSAMPLE:
                    resultfile = classificationMethod.getHasFeatureSet() ? Files.RESULTDIR + Files.TRANSLITDIR.toString() + corpusfile.substring(corpusfile.lastIndexOf('/') + 1, corpusfile.lastIndexOf(".")) + "_" + testmethod.toString().toLowerCase()
                            + "_" + classificationMethod.getShortname().toLowerCase() + "_" + featureSet.toString().toLowerCase() + "_" + transliterationMethod.getShortlabel().toLowerCase() + Files.RESULT.toString() :
                            Files.RESULTDIR + Files.TRANSLITDIR.toString() + corpusfile.substring(corpusfile.lastIndexOf('/') + 1, corpusfile.lastIndexOf(".")) + "_" + testmethod.toString().toLowerCase()
                                    + "_" + classificationMethod.getShortname().toLowerCase() + "_" + transliterationMethod.getShortlabel().toLowerCase() + Files.RESULT.toString();
                    resultfilecunei = classificationMethod.getHasFeatureSet() ? Files.RESULTDIR + Files.CUNEIFORMDIR.toString() + corpusfile.substring(corpusfile.lastIndexOf('/') + 1, corpusfile.lastIndexOf(".")) + "_" + testmethod.toString().toLowerCase()
                            + "_" + classificationMethod.getShortname().toLowerCase() + "_" + featureSet.toString().toLowerCase() + "_" + transliterationMethod.getShortlabel().toLowerCase() + Files.RESULT.toString() :
                            Files.RESULTDIR + Files.CUNEIFORMDIR.toString() + corpusfile.substring(corpusfile.lastIndexOf('/') + 1, corpusfile.lastIndexOf(".")) + "_" + testmethod.toString().toLowerCase()
                                    + "_" + classificationMethod.getShortname().toLowerCase() + "_" + transliterationMethod.getShortlabel().toLowerCase() + Files.RESULT.toString();
                    comparepathcunei = Files.REFORMATTEDDIR + Files.CUNEI_SEGMENTEDDIR.toString() + testmethod.toString().toLowerCase() + File.separator + corpusfile.substring(corpusfile.lastIndexOf('/') + 1);
                    comparepath = Files.REFORMATTEDDIR + Files.TRANSLITDIR.toString() + testmethod.toString().toLowerCase() + File.separator + corpusfile.substring(corpusfile.lastIndexOf('/') + 1);
                    eval = new Evaluation(comparepath,
                            resultfile, comparepathcunei, resultfilecunei, dictHandler);
                    main = new Main(eval);
                    if(!onlyEval) {
                        main.executeMethod(corpusHandler.getTestSet(), trainingdata, corpusfile.substring(0, corpusfile.lastIndexOf(".")) + "_" + testmethod.toString().toLowerCase(), modelfile, dictHandler, featureSet, classificationMethod, transliterationMethod, sourcelang, false, true, testmethod);
                        main.translate(new Locale(targetlang.getLocale()), resultfile, dictHandler, translationMethod);
                        main.transcript(resultfile, resultfile, dictHandler, TranscriptionMethod.TRANSCRIPTTOTRANSLIT, transliterationMethod, classificationMethod);
                    }
                    MainGUI.progress=50;
                    MainGUI.refreshProgressBar();
                    MainGUI.refreshProgressBarMessage(bundle.getString("evaluating") + " " + classificationMethod);
                    result = main.evaluate(resultfile, evalmethod, true, classificationMethod);
                    this.testFile = corpusHandler.getTestSetPath();
                    break;
                case FOREIGNTEXT:
                default:
                    resultfile = classificationMethod.getHasFeatureSet() ? Files.RESULTDIR + Files.TRANSLITDIR.toString() + testdata.substring(testdata.lastIndexOf('/') + 1, testdata.lastIndexOf('.'))
                            + "_" + classificationMethod.getShortname().toLowerCase() + "_" + featureSet.toString().toLowerCase() + "_" + transliterationMethod.getShortlabel().toLowerCase() + Files.RESULT.toString() :
                            Files.RESULTDIR + Files.TRANSLITDIR.toString() + testdata.substring(testdata.lastIndexOf('/') + 1, testdata.lastIndexOf('.'))
                                    + "_" + classificationMethod.getShortname().toLowerCase() + "_" + transliterationMethod.getShortlabel().toLowerCase() + Files.RESULT.toString();
                    resultfilecunei = classificationMethod.getHasFeatureSet() ? Files.RESULTDIR + Files.CUNEIFORMDIR.toString() + testdata.substring(testdata.lastIndexOf('/') + 1, testdata.lastIndexOf('.'))
                            + "_" + classificationMethod.getShortname().toLowerCase() + "_" + featureSet.toString().toLowerCase() + "_" + transliterationMethod.getShortlabel().toLowerCase() + Files.RESULT.toString() :
                            Files.RESULTDIR + Files.CUNEIFORMDIR.toString() + testdata.substring(testdata.lastIndexOf('/') + 1, testdata.lastIndexOf('.'))
                                    + "_" + classificationMethod.getShortname().toLowerCase() + "_" + transliterationMethod.getShortlabel().toLowerCase() + Files.RESULT.toString();
                    comparepathcunei = Files.REFORMATTEDDIR + Files.CUNEI_SEGMENTEDDIR.toString() + testmethod.toString().toLowerCase() + File.separator + testdata.substring(testdata.lastIndexOf('/') + 1);
                    comparepath = Files.REFORMATTEDDIR + Files.TRANSLITDIR.toString() + testmethod.toString().toLowerCase() + File.separator + testdata.substring(testdata.lastIndexOf('/') + 1);
                    eval = new Evaluation(comparepath,
                            resultfile, comparepathcunei, resultfilecunei, dictHandler);
                    main = new Main(eval);
                    if(!onlyEval){
                        main.executeMethod(testdata, trainingdata, resultfile, modelfile, dictHandler, featureSet, classificationMethod, transliterationMethod, sourcelang, false, false, testmethod);
                     main.translate(new Locale(targetlang.getLocale()), resultfile, dictHandler, translationMethod);
                        main.transcript(resultfile, resultfile, dictHandler, TranscriptionMethod.TRANSCRIPTTOTRANSLIT, transliterationMethod, classificationMethod);
                    }
                   MainGUI.progress=50;
                    MainGUI.refreshProgressBar();
                    MainGUI.refreshProgressBarMessage(bundle.getString("evaluating") + " " + classificationMethod);
                    result = main.evaluate(resultfile, evalmethod, true, classificationMethod);
                    this.testFile = comparepath;
            }

        System.out.println("Resultfile: "+resultfile);

        MainGUI.progress=70;
        MainGUI.refreshProgressBar();
        MainGUI.refreshProgressBarMessage(bundle.getString("calcstats")+" "+classificationMethod);
        //dictHandler.exportToXML(Files.DICTDIR.toString()+ Tags.OLDAKKADIAN.toString()+Files.DICTSUFFIX.toString(),
        //        Files.DICTDIR.toString()+ Tags.OLDAKKADIAN.toString()+Tags.REVERSE+Files.DICTSUFFIX.toString(),
        //       Files.DICTDIR.toString()+Tags.OLDAKKADIAN.toString()+Files.MAPSUFFIX.toString());
        //dictHandler.exportToXML(Files.DICTDIR.toString()+ Tags.AKKADIAN.toString()+Files.DICTSUFFIX.toString(),
        //        Files.DICTDIR.toString()+ Tags.AKKADIAN.toString()+Tags.REVERSE+Files.DICTSUFFIX.toString(),
        //        Files.DICTDIR.toString()+Tags.AKKADIAN.toString()+Files.MAPSUFFIX.toString(),Files.DICTDIR.toString()+Tags.AKKADIAN.toString()+Files.NGRAMSUFFIX.toString());
        return resultfile;
        //AkkadDictHandler akkad=new AkkadDictHandler(Files.DICTDIR.toString()+ Tags.AKKADIAN.toString()+Files.DICTSUFFIX.toString(),Files.DICTDIR.toString()+Tags.AKKADIAN.toString()+Files.MAPSUFFIX.toString());

    }

    public void transcript(final String fromTextFile,final String toTextFile,final DictHandling dictHandler,final TranscriptionMethod transcriptionMethod,final TransliterationMethod  transliterationMethod,final ClassificationMethod classificationMethod) throws IOException {
        switch(transcriptionMethod){
            case TRANSCRIPTTOTRANSLIT:this.transcriptionMethods.transcriptToTranslit(fromTextFile,dictHandler,transcriptionMethod,transliterationMethod,classificationMethod);break;
            default:
        }
    }

    public void translate(final Locale to,final String fromTextFile,final DictHandling dictHandler,final TranslationMethod translationMethod) throws IOException {
         switch(translationMethod){
             case LEMMAFIRST:this.translationMethods.lemmaTranslation(fromTextFile,dictHandler,TransliterationMethod.FIRST,to); break;
             case MAXPROB: this.translationMethods.lemmaTranslation(fromTextFile,dictHandler,TransliterationMethod.FIRST,to);break;
             case LEMMARANDOM:this.translationMethods.lemmaTranslation(fromTextFile,dictHandler,TransliterationMethod.FIRST,to);break;
             case LEMMA:this.translationMethods.lemmaTranslation(fromTextFile,dictHandler,TransliterationMethod.FIRST,to);break;
             default:
         }
    }
}
