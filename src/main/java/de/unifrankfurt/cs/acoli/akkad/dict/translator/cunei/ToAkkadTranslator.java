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

package de.unifrankfurt.cs.acoli.akkad.dict.translator.cunei;

import de.unifrankfurt.cs.acoli.akkad.dict.translator.Translator;
import de.unifrankfurt.cs.acoli.akkad.methods.translation.TranslationMethods;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.TranslationMethod;

/**
 * Class to initialize translators from Akkadian to another language.
 */
public class ToAkkadTranslator extends Translator {
    /**Translates a given text to a given target language using a given translation method.*/
    public String translate(CharTypes to,String translationText,TranslationMethod translationMethod){
        String result="";
        switch (to){
              case ENGLISH: result=this.akkadToEnglish(translationText,translationMethod);
                  break;
              default:
          }
        return result;
    }
    /**Gets a translator from to a given target language.*/
    public Translator getTranslator(CharTypes to){
        String result="";
        switch (to){
            case ENGLISH: return new AkkadToEngTranslator(CharTypes.AKKADIAN);
            case GERMAN: return new AkkadToGerTranslator(CharTypes.AKKADIAN);
            default:
                return this;
        }
    }

    public String akkadToEnglish(String translationText,TranslationMethod translationMethod){
            switch (translationMethod){
                default:
                case LEMMA: //return new AkkadToEngTranslator().wordByWordPOStranslateToEnglish(translationText,true);
            }
        return "";
    }

    @Override
    public void wordByWordPOStranslate(final String translationText, final Boolean pinyin, final Integer initialPos) {

    }
}
