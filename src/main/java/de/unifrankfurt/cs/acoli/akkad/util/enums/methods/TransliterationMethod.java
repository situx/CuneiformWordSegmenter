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

/**
 * Enum for transliteration methods.
 */
public enum TransliterationMethod implements MethodEnum {
    /**First Transliteration  method.*/
    FIRST("First Transliteration","first"),
    /**MaxProb Transliteration Method.*/
    MAXPROB("MaxProb Transliteration","MAXPROB"),
    /**Random transliteration method.*/
    RANDOM("Random Transliteration","random");
    /**Label and shortlabel of the method.*/
    String label,shortlabel;

    private TransliterationMethod(){


    }

    private TransliterationMethod(String label,String shortlabel){
         this.label=label;
         this.shortlabel=shortlabel;
    }

    public String getLabel() {
        return label;
    }

    public String getShortlabel() {
        return shortlabel;
    }

    @Override
    public String getShortname() {
        return null;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
