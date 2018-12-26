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
 * Enum for used testingmethods.
 */
public enum TestMethod {
    /**The crossvalidation method.*/
    CROSSVALIDATION("crossvalidation"),
    /**Method for choosing a foreign text.*/
    FOREIGNTEXT("foreigntext"),
    /**Method for a percentage split.*/
    PERCENTAGE("percentage"),
    /**Method for a percentage split on a text basis.*/
    TEXTPERCENTAGE("TextPercentage"),
    /**Method for a random text percentage split.*/
    RANDOMTEXTPERCENTAGE("RandomTextPercentage"),
    /**Random Sample out of the corpus.*/
    RANDOMSAMPLE("randomsample"),
    /**No method chosen.*/
    NONE("None");
    /**Description of the testing methods.*/
    private String description;

    /**
     * Constructor for this class.
     * @param description the description of this testingmethod
     */
    private TestMethod(String description){
        this.description=description;
    }
}
