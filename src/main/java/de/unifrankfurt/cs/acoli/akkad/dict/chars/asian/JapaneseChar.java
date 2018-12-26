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

package de.unifrankfurt.cs.acoli.akkad.dict.chars.asian;

import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;

/**
 * Created by timo on 17.06.14.
 * Class representing a Japanese word/char.
 */
public class JapaneseChar extends AsianChar {
    public Boolean hiragana,katakana,kanji,romaji;

    public JapaneseChar(String character){
        super(character);this.charlength= CharTypes.JAPANESE.getChar_length();
    }

    /**
     * Indicates if this character is a hiragana character.
     * @return true if it is false otherwise
     */
    public Boolean getHiragana() {
        return hiragana;
    }

    /**
     * Sets if this character is a hiragana character.
     * @param hiragana hiragana indicator
     */
    public void setHiragana(final Boolean hiragana) {
        this.hiragana = hiragana;
    }

    /**
     * Indicates if this character is a kanji character.
     * @return true if it is false otherwise
     */
    public Boolean getKanji() {
        return kanji;
    }

    /**
     * Sets if this chracter is a kanji character.
     * @param kanji kanji indicator
     */
    public void setKanji(final Boolean kanji) {
        this.kanji = kanji;
    }

    /**
     * Indicates if this character is a katakana character.
     * @return true if it is false otherwise
     */
    public Boolean getKatakana() {
        return katakana;
    }

    /**
     * Sets if this character is a katakana character.
     * @param katakana katakana indicator
     */
    public void setKatakana(final Boolean katakana) {
        this.katakana = katakana;
    }

    /**
     * Indicates if this character is a romaji character.
     * @return true if it is false otherwise
     */
    public Boolean getRomaji() {
        return romaji;
    }

    /**
     * Sets if this character is a romaji character.
     * @param romaji romaji indicator
     */
    public void setRomaji(final Boolean romaji) {
        this.romaji = romaji;
    }
}
