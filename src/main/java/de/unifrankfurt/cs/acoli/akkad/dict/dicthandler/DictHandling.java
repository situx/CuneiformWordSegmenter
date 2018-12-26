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

package de.unifrankfurt.cs.acoli.akkad.dict.dicthandler;

import de.unifrankfurt.cs.acoli.akkad.dict.chars.LangChar;
import de.unifrankfurt.cs.acoli.akkad.dict.corpusimport.util.NGramStat;
import de.unifrankfurt.cs.acoli.akkad.dict.importhandler.NGramImportHandler;
import de.unifrankfurt.cs.acoli.akkad.dict.pos.POSTagger;
import de.unifrankfurt.cs.acoli.akkad.dict.utils.StopChar;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.TranslationMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.TransliterationMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.ExportMethods;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Files;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Tags;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.*;

/**
 * @author Timo Homburg
 * Abstract class for defining a dictionary or wordmap handler.
 */
public abstract class DictHandling {
    /**Amount of characters transliterations in corpus.*/
    protected Double amountOfCharTranslitsInCorpus=0.;
    /**Amount of characer in corpus.*/
    protected Double amountOfCharsInCorpus;
    /**Amount of word transliterations in corpus.*/
    protected Double amountOfWordTranslitsInCorpus=0.;
    /**Amount of words in corpusimport with duplicates.*/
    protected Double amountOfWordsInCorpus=0.;
    /**The average word length as double.*/
    protected Double averageWordLength=0.;
    /**Counts boundaries that followed boundary characters.*/
    protected Double boundariesFollowBoundaries;
    /**Counts boundaries that followed continuation characters.*/
    protected Double boundariesFollowContinuations;
    protected CharTypes chartype;
    /**Counts continuations that followed boundary characters.*/
    protected Double continuationFollowsBoundary;
    /**Counts continurations that followed continuation characters.*/
    protected Double continuationFollowsContinuation;
    /**Length of all the words in the corpusimport.*/
    protected Double lengthOfWordsInCorpus;
    /**The ngrams of this dicthandler.*/
    protected NGramStat ngrams;
    /**The postagger being used by this dicthandler.*/
    protected POSTagger postagger;
    /**Maps from transcriptions to words of the corresponding language.*/
    protected Map<String,String> reverseTranscriptToWordDict;
    /**Map from transliterations to Chars of the corresponding language.*/
    protected Map<String,String> reverseTranslitToCharMap;
    /**Maps from transliterations to words of the corresponding language.*/
    protected Map<String,String> reverseTranslitToWordDict;
    /**The list of stopchars.*/
    protected Map<String,StopChar> stopchars;
    /**Maps from transcriptions to words of the corresponding language.*/
    protected Map<String,String> transcriptToWordDict;
    /**Map from transliterations to Chars of the corresponding language.*/
    protected Map<String,String> translitToCharMap;
    /**Maps from transliterations to words of the corresponding language.*/
    protected Map<String,String> translitToWordDict;
    /**Constructor for DictHandling.*/
    public DictHandling(List<String> stopchars,CharTypes chartype,POSTagger postagger){
        this.translitToCharMap =new TreeMap<String,String>();
        this.translitToWordDict =new TreeMap<String,String>();
        this.transcriptToWordDict=new TreeMap<>();
        this.reverseTranscriptToWordDict =new TreeMap<String,String>();
        this.reverseTranslitToCharMap =new TreeMap<String,String>();
        this.reverseTranslitToWordDict=new TreeMap<>();
        this.stopchars=new TreeMap<String,StopChar>();
        for(String str:stopchars){
            StopChar stopchar=new StopChar();
            stopchar.setStopchar(str);
            this.stopchars.put(str,stopchar);
        }
        this.averageWordLength=0.;
        this.amountOfWordsInCorpus=0.;
        this.lengthOfWordsInCorpus=0.;
        this.amountOfWordTranslitsInCorpus=0.;
        this.amountOfCharTranslitsInCorpus=0.;
        this.amountOfCharsInCorpus=0.;
        this.continuationFollowsBoundary=0.;
        this.continuationFollowsContinuation=0.;
        this.boundariesFollowContinuations=0.;
        this.boundariesFollowBoundaries=0.;
        this.chartype=chartype;
        this.postagger=postagger;
        this.ngrams =new NGramStat();
    }

    /**
     * Adds a word to the dictionary/the character map.
     * @param word the word to add
     */
    public abstract void addChar(final LangChar word);

    /**
     * Adds a following word to the current word.
     * @param word the current word
     * @param following the following word
     */
    public abstract void addFollowingWord(final String word,final String following);
    /**
     * Adds a following word to the current word.
     * @param word the current word
     * @param following the following word
     * @param preceding the preceding word
     */
    public abstract void addFollowingWord(final String word,final String following,final String preceding);

    /**
     * Adds a stopword to the list of stopwords.
     * @param stopChar the stopchar to add
     */
    public void addStopWord(final StopChar stopChar){
        if(!this.stopchars.containsKey(stopChar.getStopchar())){
            this.stopchars.put(stopChar.getStopchar(),stopChar);
        }
        stopChar.setAbsoluteOccurance(stopChar.getAbsoluteOccurance()+1);
    }

    public abstract void addTranscriptNonCunei(String transcription,LangChar word);

    /**
     * Adds a word to the dictionary/the character map.
     * @param word the word to add
     */
    public abstract void addWord(final LangChar word,final CharTypes charType);

    /**
     * Calculates the average word length of this dicthandler.
     */
    public void calculateAvgWordLength() {
        System.out.println("LengthOfWordsInCorpus: "+this.lengthOfWordsInCorpus+" AmountOfWordsInCorpus: "+this.amountOfWordsInCorpus+" "+((Math.round((this.lengthOfWordsInCorpus/this.amountOfWordsInCorpus)*100.00)/100.00>1)?Math.round((this.lengthOfWordsInCorpus/this.amountOfWordsInCorpus)*100.00)/100.00:2));
        this.averageWordLength=Math.round((this.lengthOfWordsInCorpus/this.amountOfWordsInCorpus)*100.00)/100.00>1?Math.round((this.lengthOfWordsInCorpus/this.amountOfWordsInCorpus)*100.00)/100.00:2;
        System.out.println("Mod: "+this.averageWordLength%this.getChartype().getChar_length());
        if(this.averageWordLength%this.getChartype().getChar_length()!=0){
            double calc=this.averageWordLength;
            while(calc>this.getChartype().getChar_length()){
                calc-=this.getChartype().getChar_length();
            }
            if(calc>this.getChartype().getChar_length()/2){
                System.out.println(Double.valueOf(this.averageWordLength/this.getChartype().getChar_length()).intValue()+1+" * "+this.getChartype().getChar_length());
                this.averageWordLength=Double.valueOf((Double.valueOf(this.averageWordLength/this.getChartype().getChar_length()).intValue()+1)*this.getChartype().getChar_length());
            }else{
                this.averageWordLength=Double.valueOf(Double.valueOf(this.averageWordLength/this.getChartype().getChar_length()).intValue()*this.getChartype().getChar_length());
            }

        }
        System.out.println("Final Word Length: "+this.averageWordLength);
    }



    /**
     * Calculates the occurances of a word/char relative to the corpusimport size.
     * @param charsInCorpus the amoutn of chars in the given corpusimport
     */
    public abstract void calculateRelativeCharOccurances(final Double charsInCorpus);

    /**
     * Calculates the occurances of a word/char relative to the corpusimport size.
     * @param wordsInCorpus the amount of word in the given corpusimport
     */
    public abstract void calculateRelativeWordOccurances(final Double wordsInCorpus);
    /**Calculates the left and right accessor variety.*/
    public abstract void calculateRightLeftAccessorVariety();

    /**
     * Exports the dictionary and the character map to XML.
     * @param dictpath path of the dictionary file
     * @param mappath  path of the character map file
     * @throws XMLStreamException
     * @throws IOException
     */
    public abstract void exportToXML(final String dictpath,final String reversedictpath, final String mappath,final String ngrampath)throws XMLStreamException, IOException;

    /**
     * Gets the list of alternative writings in this dicthandler.
     * @return the list
     */
    public List<String> getAlternativeWritings(){
          return null;
    }

    /**Returns the amount of words given in the corpusimport.
     * @return the amount as double
     */
    public Double getAmountOfWordsInCorpus(){
        return this.amountOfWordsInCorpus;
    }

    /**
     * Sets the amount of words in this corpus.
     * @param amountOfWordsInCorpus the amount as Double
     */
    public void setAmountOfWordsInCorpus(final Double amountOfWordsInCorpus) {
        this.amountOfWordsInCorpus = amountOfWordsInCorpus;
    }

    /**
     * Returns the average word length of the given corpusimport.
     * @return the average word length as int
     */
    public Double getAvgWordLength(){
        return this.averageWordLength;
    }

    /**
     * Sets the average word length of this dicthandler.
     * @param avgWordLength the average word length to set
     */
    public void setAvgWordLength(final Double avgWordLength) {
        this.averageWordLength = avgWordLength;
    }

    /**
     * Gets the occurances of boundaries following boundaries.
     * @return  the occurance as Double
     */
    public Double getBoundariesFollowBoundaries() {
        return boundariesFollowBoundaries;
    }
    /**
     * Gets the occurances of boundaries following continuations.
     * @return  the occurance as Double
     */
    public Double getBoundariesFollowContinuations() {
        return boundariesFollowContinuations;
    }

    /**
     * Gets a set of possible transliterations for a character or sequence of characters.
     * @param charactersequence the sequence of characters to match
     * @return The set of possible transliterations
     */
    public abstract Set<String> getCandidatesForChar(final String charactersequence);

    /**
     * Gets the chartype.
     * @return the chartype
     */
    public CharTypes getChartype() {
        return chartype;
    }
    /**
     * Gets the occurances of continuations following boundaries.
     * @return  the occurance as Double
     */
    public Double getContinuationFollowsBoundary() {
        return continuationFollowsBoundary;
    }
    /**
     * Gets the occurances of continuations following continuations.
     * @return  the occurance as Double
     */
    public Double getContinuationFollowsContinuation() {
        return continuationFollowsContinuation;
    }

    /**
     * Gets the dictmap.
     * @return the dictmap
     */
    public abstract Map<String,? extends LangChar> getDictMap();

    /**
     * Gets the dictionary translation for a given word.
     * @param word the word
     * @param translationMethod the translationmethod
     * @param locale the locale
     * @return the translation as String
     */
    public String getDictTranslation(final LangChar word, final TranslationMethod translationMethod, final Locale locale){
        if(word==null){
            return null;
        }
        switch (translationMethod){
            case LEMMA:
            case LEMMAFIRST: return word.getFirstTranslation(locale);
            case MAXPROB:  return word.getMaxProbTranslation(locale);
            case LEMMARANDOM: return null;
            default:  return null;
        }
    }

    public abstract String getDictTransliteration(final LangChar tempword, final TransliterationMethod transliterationMethod);

    public abstract Map<String,? extends LangChar> getDictionary();

    /**
     * Gets a set of possible transliterations for a character or sequence of characters.
     * @param charactersequence the sequence of characters to match
     * @return The set of possible transliterations including their frequency
     */
    public abstract Map<Double,Set<String>> getFreqCandidatesForChar(final String charactersequence);

    /**
     * Gets the current ngram statistics of the corpus.
     * @return the ngramstats
     */
    public NGramStat getNGramStats() {
        return ngrams;
    }

    public abstract String getNoDictTranslation(final String word, final TranslationMethod translationMethod, final Locale locale);

    /**Matches a given String which cannot be found in the dictionary.
     * @param word  the word to match
     * @param transliterationMethod indicates which TransliterationMethod to choose
     * @return the transliteration as string
     */
    public abstract String getNoDictTransliteration(final String word, final TransliterationMethod transliterationMethod);

    public POSTagger getPosTagger(){
        return this.postagger;
    }

    public Map<String,StopChar> getStopchars() {
        return stopchars;
    }

    public Map<String, String> getTranscriptToWordDict() {
        return transcriptToWordDict;
    }

    public Map<String, String> getTranslitToCharMap() {
        return translitToCharMap;
    }

    public Map<String, String> getTranslitToWordDict() {
        return translitToWordDict;
    }

    /**
     * Imports a given dictionary from XML.
     * @param filepath the path to the dictionary
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public abstract void importDictFromXML(final String filepath)throws ParserConfigurationException, SAXException, IOException;

    public abstract void importMappingFromXML(final String filepath)throws ParserConfigurationException, SAXException, IOException;

    public void importNGramsFromXML(final String filepath) throws IOException, SAXException, ParserConfigurationException {
        System.setProperty("avax.xml.parsers.SAXParserFactory",
                "org.apache.xerces.parsers.SAXParser");
        SAXParser parser= SAXParserFactory.newInstance().newSAXParser();
        java.io.InputStream in = new FileInputStream(new File(filepath));
        InputSource is = new InputSource(in);
        is.setEncoding(Tags.UTF8.toString());
        NGramImportHandler imp=new NGramImportHandler(this.ngrams,this.chartype);
        parser.parse(in,imp);
        parser.reset();
    }

    /**
     * Imports a given dictionary from XML.
     * @param filepath the path to the dictionary
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public abstract void importReverseDictFromXML(final String filepath)throws ParserConfigurationException, SAXException, IOException;

    public void incLengthOfWordsInCorpus(Integer length){
          this.lengthOfWordsInCorpus+=length;
    }

    /**
     * Checks if a given word/char is a following word..
     * @return true if it is a following word, false otherwise
     */
    public abstract boolean isFollowingWord(final LangChar word,final String following);

    /**Matches a given char with the map of chars.
     * @param translit the char to match
     * @return the LangChar which matches the given transliteration
     */
    public abstract LangChar matchChar(final String translit);

    /**
     * Matches a character or word in the dictionary.
     * @param word the word to match
     * @param chartype the chartype to use
     * @return the dictionary LangChar
     */
    public abstract LangChar matchChar(final String word,CharTypes chartype);

    /**
     * Matches the ngram occurance of a current ngram from the dictionary.
     * @param word the ngram to match
     * @return the occurance
     */
    public Double matchNGramOccurance(String word){
        return this.ngrams.getNGramOccurance(word);
    }

    /**
     * Matches a reverse word saved in the reversed dictionary.
     * @param word the word to match
     * @return the word from the dictionary
     */
    public abstract LangChar matchReverseWord(final String word);

    public StopChar matchStopChar(String stopchar){
        if(this.stopchars.containsKey(stopchar)){
            return this.stopchars.get(stopchar);
        }
        return null;
    }

    /**Matches a given String with the dictionary.
     * @param word  the word to match
     * @return the LangChar which matches the given word
     */
    public abstract LangChar matchWord(final String word);

    /**
     * Matches a given word from the dictionary by its transcription.
     * @param word the word to match
     * @param noncunei indicates if the word is given in cuneiform
     * @return the char from the dictionary
     */
    public abstract LangChar matchWordByTranscription(String word,Boolean noncunei);

    /**
     * Matches a word by its transliteration.
     * @param word the transliteration to match
     * @return the char from the dictionary
     */
    public abstract LangChar matchWordByTransliteration(String word);

    /**
     * Parses the file containing a non-standardized dictionary.
     * @param file the dictionary file
     * @throws IOException on IO error
     * @throws ParserConfigurationException on Parsing error
     * @throws SAXException on SAXException
     */
    public abstract void parseDictFile(final File file) throws IOException, ParserConfigurationException, SAXException;

    /**
     * Gets the reverse transliteration of a transliteration.
     * @param translit the transliteration to reverse
     * @param splitcriterion a splitcriterion to as a syllable separator
     * @return the reverse transliteration as String
     */
    public String reverseTransliteration(final String translit,final String splitcriterion){
        String result="";
        if(!translit.contains("-")){
            return translit;
        }
        //first create a list from String array
        List<String> list = Arrays.asList(translit.split(splitcriterion));

        //next, reverse the list using Collections.reverse method
        Collections.reverse(list);
        for(String str:list){
            result+=str+"-";
        }
        return result.substring(0,result.length()-1);
    }

    /**
     * Sets the current chartype of the dictionary.
     * @param charType the chartype to set
     */
    public void setCharType(final CharTypes charType) {
        this.chartype = charType;
    }

    /**
     * Prepares an export to different IME formats.
     * @param export the exportmap for exporting
     * @param dictmap  the charmap for exporting
     * @param dictionary the dictionary for exporting
     * @param header the header of the export file
     * @param exportfile the file to export to
     * @return
     * @throws IOException
     */
    public String toIME(final ExportMethods export,Map<String,? extends LangChar> dictmap,Map<String,? extends LangChar> dictionary,String header,String exportfile) throws IOException {
        BufferedWriter writer=new BufferedWriter(new FileWriter(new File(Files.IME_DIR.toString()+exportfile+"_"+export.methodname.toLowerCase()+export.fileformat)));
        BufferedReader footerreader;
        String temp;
        writer.write(header);
        for(LangChar ch:dictmap.values()){
            writer.write(ch.toIME(export));
        }
        for(LangChar ch:dictionary.values()){
            writer.write(ch.toIME(export));
        }
        File file=new File(Files.IME_DIR.toString() + export.methodname.toLowerCase() + File.separator + export.methodname.toLowerCase() + Files.FOOTER.toString() + export.fileformat);
        if(file.exists()){
            footerreader = new BufferedReader(new FileReader(file));
            while ((temp = footerreader.readLine()) != null) {
                writer.write(temp + "\n");
            }
            footerreader.close();
        }
        writer.close();
        return Files.IME_DIR.toString()+exportfile+"_"+export.methodname.toLowerCase()+export.fileformat;
    }

    /**
     * Translates a transliteration char to its cuneiform dependant.
     * @param translit the transliteration
     * @return The cuneiform character as String
     */
    public abstract LangChar translitToChar(final String translit);
}
