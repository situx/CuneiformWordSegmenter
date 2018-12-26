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

package de.unifrankfurt.cs.acoli.akkad.util.enums.util;

/**
 * Created with IntelliJ IDEA.
 * User: timo
 * Date: 03.12.13
 * Time: 23:45
 * To change this template use File | Settings | File Templates.
 */
public enum Options {
    /**Indicates to fill the dictionary with words.*/
    FILLDICTIONARY,
    /**Indicates to fill the dictionary map.*/
    FILLMAP,
    /**Indicates to match the first transliteration on non-dict matching.*/
    FIRSTTRANSLITMATCH,
    /**Indicates to match the max occurance transliteration on non-dict matching.*/
    MAXOCCTRANSLITMATCH,
    /**Indicates to match a random transliteration on non-dict matching.*/
    RANDOMTRANSLITMATCH,
    /**Option to write a trainingset.*/
    TRAININGSET,
    /**Option to write a testingset.*/
    TESTINGSET,
    /**Write all formats available.*/
    WRITEALL,
    /**Write the arff file format.*/
    WEKA,
    /**Write the mallet file format.*/
    MALLET,
    REVERSEDICT, /**Do not write the arff format.*/
    NATIVE
}
