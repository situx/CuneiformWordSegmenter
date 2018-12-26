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

import de.unifrankfurt.cs.acoli.akkad.dict.chars.PositionableChar;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;

/**
 * Created by timo on 17.06.14.
 * Abstract class to represent an Asian character.
 */
public abstract class AsianChar extends PositionableChar {

    /**The relative occurance of this char/word in the corpusimport.*/
    protected Double relativeoccurance;

    /**
     * Constructor for this class
     * @param character the character to add
     */
    public AsianChar(final String character) {
        super(character);
        this.charlength= CharTypes.ASIANCHAR.getChar_length();
    }

    @Override
    public Integer getCharlength() {
        return this.charlength;
    }

    @Override
    public Double getRelativeOccurance() {
        return this.relativeoccurance;
    }

    @Override
    public void setRelativeOccurance(final Double occurance) {

    }

    @Override
    public void setRelativeOccuranceFromDict(final Double relocc){
        this.relativeoccurance=relocc;
    }

    @Override
    public String toXML(final String startelement) {
        return super.toXML(startelement);
    }
}
