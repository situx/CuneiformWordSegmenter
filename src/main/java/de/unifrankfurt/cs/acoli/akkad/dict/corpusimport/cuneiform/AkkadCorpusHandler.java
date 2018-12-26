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

package de.unifrankfurt.cs.acoli.akkad.dict.corpusimport.cuneiform;

import de.unifrankfurt.cs.acoli.akkad.dict.corpusimport.importformat.ATFImporter;
import de.unifrankfurt.cs.acoli.akkad.dict.corpusimport.importformat.FileFormatImporter;
import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.cuneiform.AkkadDictHandler;
import de.unifrankfurt.cs.acoli.akkad.dict.importhandler.cuneiform.TranslationImportHandler2;
import de.unifrankfurt.cs.acoli.akkad.dict.pos.POSTagger;
import de.unifrankfurt.cs.acoli.akkad.dict.pos.cuneiform.AkkadPOSTagger;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.TestMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Files;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Tags;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Timo Homburg
 * Date: 06.11.13
 * Time: 14:21
 * CorpusHandler for the akkadian language processing a corpusimport.
 */
public class AkkadCorpusHandler extends CuneiCorpusHandler {

    /**
     * Constructor for this class.
     */
    public AkkadCorpusHandler(List<String> stopChars) {
        super(stopChars);
    }

    @Override
    public void addTranslations(final String file, final TestMethod testMethod1) throws ParserConfigurationException, SAXException, IOException {
        TranslationImportHandler2 importHandler = new TranslationImportHandler2(this.dictHandlers.get(testMethod1).get(0));
        javax.xml.parsers.SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        java.io.InputStream in = new FileInputStream(new File(file));
        InputSource is = new InputSource(in);
        is.setEncoding(Tags.UTF8.toString());
        //ImportHandler imp=new ImportHandler(Options.FILLDICTIONARY,this.dictionary,this.translitToWordDict, CharTypes.AKKADIAN);
        parser.parse(in, importHandler);
    }

    @Override
    public DictHandling dictImport(final String corpus, final TestMethod testMethod, final CharTypes sourcelang) throws IOException, SAXException, ParserConfigurationException {
        final AkkadDictHandler dictHandler = new AkkadDictHandler(this.stopchars);
        dictHandler.importMappingFromXML(Files.DICTDIR + sourcelang.getLocale() + Files.MAPSUFFIX);
        dictHandler.importDictFromXML(Files.DICTDIR + sourcelang.getLocale() + Files.DICTSUFFIX);
        dictHandler.importReverseDictFromXML(Files.DICTDIR + sourcelang.getLocale() + Files.REVERSE + Files.DICTSUFFIX);
        dictHandler.importNGramsFromXML(Files.DICTDIR + sourcelang.getLocale() + Files.NGRAMSUFFIX);
        return dictHandler;
    }

    @Override
    public void enrichExistingCorpus(final String filepath, final DictHandling dicthandler) throws IOException {

    }

    /**
     * Generates an Akkadian corpusimport out of the source file.
     *
     * @param filepath    the filepath to the source file
     * @param wholecorpus
     * @throws IOException on error
     */
    @Override
    public DictHandling generateCorpusDictionaryFromFile(final List<String> filepath, final String signpath, final String filename, final boolean wholecorpus, final boolean corpusstr,final TestMethod testMethod) throws IOException, ParserConfigurationException, SAXException {
        final AkkadDictHandler dicthandler = new AkkadDictHandler(this.stopchars);
        if (signpath != null) {
            dicthandler.parseDictFile(new File(signpath));
        }
        dicthandler.setCharType(CharTypes.AKKADIAN);
        if (!corpusstr)
            System.out.println("Filepath " + filepath);
        matches = 0.;
        nomatches = 0.;
        nomatchesmap = new TreeMap<>();
        this.determinatives = new TreeSet<>();
        this.sumerograms = new TreeSet<>();
        this.randomGenerator = new Random();
        //this.cuneiSegmentExport = new BufferedWriter(new FileWriter(new File(Files.TESTDATADIR.toString() + Files.CORPUSOUT.toString())));
        //this.cuneiWOSegmentExport = new BufferedWriter(new FileWriter(new File(Files.TESTDATADIR.toString() + Files.CORPUSOUT2.toString())));
        this.reformattedTranslitWriter = new BufferedWriter(new FileWriter(new File(Files.REFORMATTEDDIR.toString()+Files.TRANSLITDIR.toString()+ testMethod.toString().toLowerCase()+File.separator+ filename)));
        this.reformattedBoundaryWriter = new BufferedWriter(new FileWriter(new File(Files.REFORMATTEDDIR.toString() + Files.BOUNDARYDIR.toString()+testMethod.toString().toLowerCase()+File.separator+ filename)));
        this.reformattedCuneiWriter = new BufferedWriter(new FileWriter(new File(Files.REFORMATTEDDIR.toString() + Files.CUNEI_SEGMENTEDDIR.toString() +testMethod.toString().toLowerCase()+File.separator+ filename)));
        this.reformattedUSegCuneiWriter = new BufferedWriter(new FileWriter(new File(Files.REFORMATTEDDIR.toString() + Files.CUNEIFORMDIR.toString() +testMethod.toString().toLowerCase()+File.separator+ filename)));
        this.normalizedTestDataWriter = new BufferedWriter(new FileWriter(new File(Files.TESTDATA.toString())));
        String fileext;
        for (String str : filepath) {
            if (corpusstr) {
                this.corpusReader = new BufferedReader(new StringReader(str));
                fileext=filename.substring(filename.lastIndexOf("."));
                this.importCorpus(dicthandler, fileext).importFromFormat(dicthandler.getChartype(),dicthandler);
            } else {
                File file = new File(Files.SOURCEDIR + str);
                if (file.exists() && file.isDirectory()) {
                    for (File fil : file.listFiles()) {
                        this.corpusReader = new BufferedReader(new FileReader(fil));
                        fileext=str.substring(str.lastIndexOf("."));
                        this.importCorpus(dicthandler, fileext).importFromFormat(dicthandler.getChartype(),dicthandler);
                    }
                } else if (file.exists() && !file.isDirectory()) {
                    this.corpusReader = new BufferedReader(new FileReader(file));
                    fileext=str.substring(str.lastIndexOf("."));
                    this.importCorpus(dicthandler, fileext).importFromFormat(dicthandler.getChartype(),dicthandler);
                }

            }
        }
        //this.cuneiSegmentExport.close();
        //this.cuneiWOSegmentExport.close();
        this.reformattedCuneiWriter.close();
        this.normalizedTestDataWriter.close();
        this.reformattedBoundaryWriter.close();
        this.reformattedTranslitWriter.close();
        this.reformattedUSegCuneiWriter.close();
        dicthandler.calculateRightLeftAccessorVariety();
        dicthandler.calculateRelativeWordOccurances(dicthandler.getAmountOfWordsInCorpus());
        dicthandler.calculateRelativeCharOccurances(dicthandler.getAmountOfWordsInCorpus());
        dicthandler.calculateAvgWordLength();
        System.out.println("Translit To Cunei Matches: " + matches);
        System.out.println("Translit To Cunei Fails: " + nomatches);
        System.out.println("Total % " + (matches + nomatches));
        System.out.println("% " + (matches / (matches + nomatches)));
        System.out.println("No matches list: " + nomatchesmap + "\nSize: " + nomatchesmap.keySet().size());
        System.out.println("Sumerograms: " + sumerograms + "\nSize: " + sumerograms.size());
        String sumeroregex="^.*(";
        for(String sumero:sumerograms) {
            sumeroregex+=sumero+"|";
        }
        sumeroregex=sumeroregex.substring(0,sumeroregex.length()-1);
        sumeroregex+=").*$";
        System.out.println("Sumeroregex: "+sumeroregex);

        System.out.println("Sumeroregex2: "+sumeroregex.toLowerCase());
        String sumeroregex3="^.*(";
        for(String sumero:sumerograms) {
            sumeroregex3+=dicthandler.getTranslitToCharMap().get(sumero.toLowerCase())+"|";
        }
        sumeroregex3=sumeroregex3.substring(0,sumeroregex3.length()-1);
        sumeroregex3+=").*$";
        System.out.println("Sumeroregex3: "+sumeroregex3);
        System.out.println("Determinatives: " + determinatives + "\nSize: " + determinatives.size());
        String detregex="^.*(";
        for(String sumero:determinatives) {
            detregex+=sumero+"|";
        }
        detregex=detregex.substring(0,detregex.length()-1);
        detregex+=").*$";
        System.out.println("Detregex: "+detregex);
        System.out.println("Detregex2: "+detregex.toLowerCase());
        String detregex3="^.*(";
        for(String sumero:determinatives) {
            detregex3+=dicthandler.getTranslitToCharMap().get(sumero.toLowerCase())+"|";
        }
        detregex3=detregex3.substring(0,detregex3.length()-1);
        detregex3+=").*$";
        System.out.println("Detregex3: "+detregex3);
            return dicthandler;
    }

    @Override
    public POSTagger getPOSTagger(Boolean newPosTagger) {
        if(this.posTagger==null || newPosTagger){
            this.posTagger=new AkkadPOSTagger();
        }
        return this.posTagger;
    }

    @Override
    public DictHandling getUtilDictHandler() {
        if(this.utilDictHandler==null){
            this.utilDictHandler=new AkkadDictHandler(new LinkedList<String>());
            try {
                this.utilDictHandler.parseDictFile(new File("akkad.xml"));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
        }
        return this.utilDictHandler;
    }

    /**
     * According to the file extension chooses the correct importer.
     * @param dicthandler the dicthandler to choose
     * @param fileext the fileextension
     * @return the file format importer to choose
     * @throws IOException on error
     */
    public FileFormatImporter importCorpus(DictHandling dicthandler,String fileext) throws IOException {
           switch (fileext.toLowerCase()){
               case ".atf": return new ATFImporter(this.stopchars,this.corpusReader,this.reformattedTranslitWriter,this.reformattedBoundaryWriter,this.reformattedCuneiWriter,this.reformattedUSegCuneiWriter,nomatchesmap,this.sumerograms,this.determinatives);
               default:  return new ATFImporter(this.stopchars,this.corpusReader,this.reformattedTranslitWriter,this.reformattedBoundaryWriter,this.reformattedCuneiWriter,this.reformattedUSegCuneiWriter,nomatchesmap,this.sumerograms,this.determinatives);
           }
    }
}