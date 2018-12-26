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

package de.unifrankfurt.cs.acoli.akkad.util.enums.methods;

import de.unifrankfurt.cs.acoli.akkad.dict.corpusimport.CorpusHandlerAPI;
import de.unifrankfurt.cs.acoli.akkad.dict.corpusimport.asian.CNCorpusHandler;
import de.unifrankfurt.cs.acoli.akkad.dict.corpusimport.asian.JapaneseCorpusHandler;
import de.unifrankfurt.cs.acoli.akkad.dict.corpusimport.cuneiform.AkkadCorpusHandler;
import de.unifrankfurt.cs.acoli.akkad.dict.corpusimport.cuneiform.HittiteCorpusHandler;
import de.unifrankfurt.cs.acoli.akkad.dict.corpusimport.cuneiform.SumerianCorpusHandler;
import de.unifrankfurt.cs.acoli.akkad.dict.corpusimport.latin.EngCorpusHandler;
import de.unifrankfurt.cs.acoli.akkad.dict.corpusimport.latin.GerCorpusHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Enum containing CharTypes of the given languages.
 */
public enum CharTypes implements MethodEnum {
    /**Akkadian Char.*/
    AKKADIAN("Akkadian Char","akk",2,"-", Arrays.asList(new String[]{System.lineSeparator()}),new AkkadCorpusHandler(Arrays.asList(new String[]{System.lineSeparator()})),"[A-z0-9, -]+"),
    /**Chinese Char.*/
    CHINESE("Chinese Char", Locale.CHINESE.getLanguage(),1,"-", Arrays.asList(new String[]{"。","，","？","！"}),new CNCorpusHandler(Arrays.asList(new String[]{"。","，","？","！"})),".*"),
    /**Cuneiform Char.*/
    CUNEICHAR("Cuneiform Char",Locale.ENGLISH.toString(),2,"-", Arrays.asList(new String[]{System.lineSeparator()}),null,".*"),
    /**German Char.*/
    ENGLISH("English Char", Locale.ENGLISH.toString(),1,".", Arrays.asList(new String[]{" ",".",",","!","?",";",":","\\(","\\)","\\[","\\]","\\{","\\}",System.lineSeparator()}),new EngCorpusHandler(Arrays.asList(new String[]{" ",".",",","!","?",";",":","\\(","\\)","\\[","\\]","\\{","\\}",System.lineSeparator()})),".*"),
    /**Hittite Char.*/
    HITTITECHAR("Hittitian Char","hit",2,"-", Arrays.asList(new String[]{System.lineSeparator()}),new HittiteCorpusHandler(Arrays.asList(new String[]{System.lineSeparator()})),".*"),
    /**German Char.*/
    GERMAN("German Char",Locale.GERMAN.toString(),1,".", Arrays.asList(new String[]{" ",".",",","!","?",";",":","\\(","\\)","\\[","\\]","\\{","\\}",System.lineSeparator()}),new GerCorpusHandler(Arrays.asList(new String[]{" ",".",",","!","?",";",":","\\(","\\)","\\[","\\]","\\{","\\}",System.lineSeparator()})),".*"),
    /**Japanese Char.*/
    JAPANESE("Japanese Char",Locale.JAPANESE.toString(),1,"(?<=\\\\p{Nd})", Arrays.asList(new String[]{"。","，","？","！"}), new JapaneseCorpusHandler(Arrays.asList(new String[]{"。","，","？","！"})),".*"),
    /**Language Char.*/
    LANGCHAR("Language Char",Locale.ENGLISH.toString(),1,".", Arrays.asList(new String[0]),null,".*"),
    /**Latin Char.*/
    LATIN("Latin Char",Locale.ENGLISH.toString(),1,".", Arrays.asList(new String[]{}),null,".*"),
    /**Sumerian Char.*/
    SUMERIANCHAR("Sumerian Char","sux",2,"-", Arrays.asList(new String[]{System.lineSeparator()}),new SumerianCorpusHandler(Arrays.asList(new String[]{System.lineSeparator()})),".*"),
    /**Transliteration Char.*/
    TRANSLITCHAR("Transliteration","tra",1,"-", Arrays.asList(new String[]{System.lineSeparator()}),null,".*"),
    /**Asian Char.*/
    ASIANCHAR("Asian Char",Locale.CHINESE.toString(),1,"(?<=\\p{Nd})", Arrays.asList(new String[]{"。","，","？","！"}),null,".*"), ;
    /**The list of stop chars to consider per language.*/
    private final List<String> stopchars;
    /**The char length of the language.*/
    private Integer char_length;
    /**The DictHandler for this language.*/
    private CorpusHandlerAPI dicthandler;
    /**The regex to describe a valid transliteration.*/
    private String legalTranslitCharsRegex;
    /**String value (name of the method).*/
    private String  locale,splitcriterion;
    /**String value (name of the method).*/
    private String value;

    /**Constructor using a description parameter.*/
    private CharTypes(String value,String locale,Integer char_length,String splitcriterion,final List<String> stopchars,final CorpusHandlerAPI dicthandler,final String legalTranslitChars){
        this.value=value;
        this.locale=locale;
        this.char_length=char_length;
        this.splitcriterion=splitcriterion;
        this.stopchars=stopchars;

        this.dicthandler=dicthandler;
        this.legalTranslitCharsRegex =legalTranslitChars;
    }

    public Integer getChar_length() {
        return char_length;
    }

    public CorpusHandlerAPI getCorpusHandlerAPI() {
        return dicthandler;
    }

    public String getLegalTranslitCharsRegex() {
        return legalTranslitCharsRegex;
    }

    public void setLegalTranslitCharsRegex(final String legalTranslitCharsRegex) {
        this.legalTranslitCharsRegex = legalTranslitCharsRegex;
    }

    public String getLocale() {
        return locale;
    }

    @Override
    public String getShortname() {
        return null;
    }

    public String getSplitcriterion() {
        return splitcriterion;
    }

    public List<String> getStopchars() {
        return stopchars;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
