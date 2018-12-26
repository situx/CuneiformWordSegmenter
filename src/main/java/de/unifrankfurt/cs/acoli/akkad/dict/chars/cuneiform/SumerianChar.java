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

package de.unifrankfurt.cs.acoli.akkad.dict.chars.cuneiform;

import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;

/**
 * Created with IntelliJ IDEA.
 * User: timo
 * Date: 25.10.13
 * Time: 12:00
 * Class for modelling a sumerian cuneiform character.
 */
public class SumerianChar extends CuneiChar {

    /**Indicates if this character is a determinative.*/

    /**
     * Constructor for this class
     * @param character the cuneiform character modelled by this class.
     */
    public SumerianChar(final String character){
        super(character);
        this.character=character;
        this.charlength= CharTypes.SUMERIANCHAR.getChar_length();
    }

    @Override
    public Boolean getDeterminative() {
        return this.determinative;
    }

    @Override
    public void setDeterminative(final Boolean determinative) {
        this.determinative=determinative;
    }

    @Override
    public Boolean getLogograph() {
        return this.logograph;
    }

    @Override
    public void setLogograph(final Boolean logograph) {
        this.logograph=logograph;
    }

    @Override
    public Boolean getPhonogram() {
        return this.phonogram;
    }

    @Override
    public void setPhonogram(final Boolean phonogram) {
        this.phonogram=phonogram;
    }

    @Override
    public void setStem(final String stem) {

    }
    @Override
    public String toXML(String startelement) {
        return super.toXML(startelement);
    }
}
