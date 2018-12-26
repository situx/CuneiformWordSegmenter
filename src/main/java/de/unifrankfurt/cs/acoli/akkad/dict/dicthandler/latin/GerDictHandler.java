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

package de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.latin;

import de.unifrankfurt.cs.acoli.akkad.dict.chars.LangChar;
import de.unifrankfurt.cs.acoli.akkad.dict.chars.latin.GerChar;
import de.unifrankfurt.cs.acoli.akkad.dict.pos.POSTagger;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by timo on 17.06.14.
 */
public class GerDictHandler extends LatinDictHandler {
    /**The dictionary.*/
    protected Map<String,GerChar> dictionary;
    /**The signlist to use.*7
    protected Map<String,GerChar> dictmap;

    /**
     * Constructor for this
     * @param stopchars
     */
    public GerDictHandler(List<String> stopchars){
        super(stopchars,CharTypes.GERMAN,new POSTagger(new TreeMap<String, Color>(),CharTypes.GERMAN.getLocale()));
    }

    @Override
    public void addFollowingWord(final String word, final String following) {

    }

    @Override
    public void importMappingFromXML(final String filepath) throws ParserConfigurationException, SAXException, IOException {

    }
    @Override
    public LangChar matchChar(final String word,CharTypes chartype){
        if(chartype==CharTypes.TRANSLITCHAR){
            return this.translitToChar(word);
        }else{
            return this.matchChar(word);
        }
    }

    /**
     * Translates a transliteration char to its cuneiform dependant.
     * @param translit the transliteration
     * @return The cuneiform character as String
     */
    @Override
    public LangChar translitToChar(final String translit){
        return this.matchChar(this.translitToCharMap.get(translit));
    }
}
