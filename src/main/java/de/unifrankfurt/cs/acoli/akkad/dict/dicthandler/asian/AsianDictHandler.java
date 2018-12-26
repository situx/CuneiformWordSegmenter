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

package de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.asian;

import de.unifrankfurt.cs.acoli.akkad.dict.chars.LangChar;
import de.unifrankfurt.cs.acoli.akkad.dict.chars.PositionableChar;
import de.unifrankfurt.cs.acoli.akkad.dict.chars.asian.AsianChar;
import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.dict.pos.POSTagger;
import de.unifrankfurt.cs.acoli.akkad.dict.utils.Transliteration;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.TranslationMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.TransliterationMethod;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * DictHandler for asian languages.
 */
public abstract class AsianDictHandler extends DictHandling {
    /**The dictionary of words.*/
    protected Map<String,AsianChar> dictionary;
    /**The map of characters.*/
    protected Map<String,AsianChar> dictmap;
    /**The reversedictionary.*/
    protected Map<String,AsianChar> reversedictionary;

    /**
     * Constructor for this class.
     * @param stopchars stopchars to consider
     * @param charType the chartype
     * @param posTagger the postagger to use
     */
    public AsianDictHandler(final List<String> stopchars,final CharTypes charType,final POSTagger posTagger){
        super(stopchars,charType,posTagger);
    }

    @Override
    public void addChar(final LangChar character) {
        AsianChar cnChar=(AsianChar)character;
        if(!this.dictmap.containsKey(character.getCharacter())){
            this.dictmap.put(cnChar.getCharacter(),cnChar);
        }
        for(Transliteration trans:cnChar.getTransliterationSet()){
            if(!this.translitToCharMap.containsKey(trans.getTransliteration())){
                this.translitToCharMap.put(trans.getTransliteration(),cnChar.getCharacter());
            }
        }
    }

    @Override
    public abstract void addTranscriptNonCunei(final String transcription, final LangChar word);

    @Override
    public void addWord(final LangChar word2,final CharTypes charType) {
         AsianChar word=(AsianChar)word2;
         this.dictionary.put(word.getCharacter(),word);
    }

    @Override
    public void calculateRightLeftAccessorVariety() {
        for(AsianChar curchar:this.dictmap.values()){
            curchar.calculateLeftAccessorVariety();
            curchar.calculateRightAccessorVariety();
        }
        for(AsianChar curchar:this.dictionary.values()){
            curchar.calculateLeftAccessorVariety();
            curchar.calculateRightAccessorVariety();
        }
        for(AsianChar curchar:this.reversedictionary.values()){
            curchar.calculateLeftAccessorVariety();
            curchar.calculateRightAccessorVariety();
        }
    }

    @Override
    public void exportToXML(final String dictpath, final String reversedictpath, final String mappath,final String ngrampath) throws XMLStreamException, IOException {

    }

    @Override
    public Set<String> getCandidatesForChar(final String charactersequence) {
        Set<String> candidates=new TreeSet<>();
        for(String key:this.dictionary.keySet()){
            if(key.startsWith(charactersequence)){
                candidates.add(key);
            }
        }
        return candidates;
    }

    @Override
    public Map<String, ? extends LangChar> getDictMap() {
        return dictmap;
    }

    @Override
    public String getDictTranslation(final LangChar word, final TranslationMethod translationMethod, final Locale locale) {
        return super.getDictTranslation(word, translationMethod, locale);
    }

    public abstract String getDictTransliteration(final PositionableChar tempchar, final TransliterationMethod transliterationMethod);

    public Map<String, ? extends LangChar> getDictionary() {
        return dictionary;
    }

    @Override
    public void importDictFromXML(final String filepath) throws ParserConfigurationException, SAXException, IOException {

    }

    @Override
    public void importNGramsFromXML(final String s) throws ParserConfigurationException, SAXException, IOException {

    }

    @Override
    public void importReverseDictFromXML(final String filepath) throws ParserConfigurationException, SAXException, IOException {

    }

    @Override
    public boolean isFollowingWord(final LangChar word, final String following) {
        return false;
    }

    @Override
    public LangChar matchChar(final String translit) {
        return this.dictmap.get(translit);
    }

    @Override
    public LangChar matchReverseWord(final String word) {
        return this.reversedictionary.get(word);
    }

    @Override
    public LangChar matchWord(final String word) {
        return this.dictionary.get(word);
    }

    @Override
    public LangChar matchWordByTranscription(final String word,Boolean noncunei) {
        if(this.transcriptToWordDict.containsKey(word) && transcriptToWordDict.get(word)!=null && this.dictionary.containsKey(transcriptToWordDict.get(word))){
            return this.dictionary.get(transcriptToWordDict.get(word));
        }
        return null;
    }

    @Override
    public LangChar matchWordByTransliteration(final String word) {
        if(this.translitToWordDict.containsKey(word)){
            return this.dictionary.get(this.translitToWordDict.get(word));
        }
        return null;
    }

    @Override
    public void parseDictFile(final File file) throws IOException, ParserConfigurationException, SAXException {

    }

}
