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

package de.unifrankfurt.cs.acoli.akkad.dict.translator;

import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.dict.pos.POSTagger;
import de.unifrankfurt.cs.acoli.akkad.dict.pos.util.POSDefinition;
import de.unifrankfurt.cs.acoli.akkad.dict.translator.cunei.ToAkkadTranslator;
import de.unifrankfurt.cs.acoli.akkad.main.gui.util.HighlightData;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.TranslationMethod;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Translator class for creating translators.
 */
public abstract class Translator {
    /**The list of highlight data to translate.*/
    protected List<HighlightData> length;
    /**The POSTagger to use.*/
    protected POSTagger posTagger;
    /**The DictHandler to use.*/
    protected DictHandling dictHandler;
    /**The currentposition in the text.*/
    protected Integer currentpos;
    /**The last translation observed.*/
    protected POSDefinition lasttranslation;
    /**The last String written to the output.*/
    protected String lastWritten;
    /**The last result found.*/
    protected String result;
    /**Translates a String from one language to the other using a given TranslationMethod.*/
    public static String translateTo(CharTypes from,CharTypes to,String toTranslate,TranslationMethod translationMethod){
        String result;
        switch (from){
            case AKKADIAN: result=new ToAkkadTranslator().translate(to, toTranslate, translationMethod);
                break;
            default: result="";
        }
        return result;
    }

    public List<HighlightData> getLength() {
        return length;
    }

    public String getResult() {
        return result;
    }

    /**Gets an appropriate translator class for the corresponding language.*/
    public static Translator getTranslator(CharTypes from,CharTypes to){
        String result;
        switch (from){
            case AKKADIAN: return new ToAkkadTranslator().getTranslator(to);
            default: return new ToAkkadTranslator().getTranslator(to);
        }
    }
    /**Stub for a word by word translation algorithm.*/
    public abstract void wordByWordPOStranslate(String translationText,Boolean pinyin,Integer initialPos);


    /**Constant separator for translating names.*/
    public static String separateConsonants(final String word){
        Boolean wasConsonant=false,isConsonant=false;
        String wasConsonantStr="",isConsonantStr="";
        String result="";
        for(int i=0;i<word.length();i++){
            isConsonant=isConsonant(word.substring(i, i + 1));
            if(isConsonant){
                isConsonantStr=word.substring(i,i+1);
            }else {
                isConsonantStr="";
            }
            if(isConsonant && wasConsonant && wasConsonantStr.equals(isConsonantStr)){
                result+=" "+word.substring(i,i+1).toUpperCase();
            }else if(isConsonant){
                result+=word.substring(i,i+1);
                wasConsonantStr=word.substring(i,i+1);
                wasConsonant=true;
            }else{
                result+=word.substring(i,i+1);
                wasConsonantStr="";
                wasConsonant=false;
            }
        }
        return result;
    }
    /**Checks if a given String is a vowel.*/
    public static boolean isVowel(String c){
        String vowels = "aeiouAEIOU";
        return vowels.contains(c+"");
    }
    /**Checks if a given String is an uppercaseOrNumber.*/
    public static boolean isAllUpperCaseOrNumber(String c){
        Pattern upperregex=Pattern.compile("[A-Z0-9Š]+");
        return upperregex.matcher(c).matches();
    }
    /**Checks if a given String is all Uppercase or Number or Delimiter.*/
    public static boolean isAllUpperCaseOrNumberOrDelim(String c){
        Pattern upperregex=Pattern.compile("[A-ZŠ0-9 -]+");
        return upperregex.matcher(c).matches();
    }
    /**Checks if a given String is a consonant.*/
    public static boolean isConsonant(String c){
        String cons = "bcdfghjklmnpqrstvwxyzBCDFGHJKLMNPQRSTVWXYZ";
        return cons.contains(c+"");
    }
}
