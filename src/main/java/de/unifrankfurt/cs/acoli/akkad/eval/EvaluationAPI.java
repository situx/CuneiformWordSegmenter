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

package de.unifrankfurt.cs.acoli.akkad.eval;

import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.ClassificationMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timo
 * Date: 12.11.13
 * Time: 14:30
 * To change this template use File | Settings | File Templates.
 */
public abstract class EvaluationAPI {
    public static <T> List<List<T>> zip(List<T>... lists) {
        List<List<T>> zipped = new ArrayList<List<T>>();
        for (List<T> list : lists) {
            for (int i = 0, listSize = list.size(); i < listSize; i++) {
                List<T> list2;
                if (i >= zipped.size())
                    zipped.add(list2 = new ArrayList<T>());
                else
                    list2 = zipped.get(i);
                list2.add(list.get(i));
            }
        }
        return zipped;
    }

    /**
     * Evaluates how many decisions(boundary/no boundary) have been taken correctly.
     */
    //public void binaryDecisionEvaluation() throws IOException;

    abstract String binaryDecisionEvaluation(Boolean append, ClassificationMethod classmethod) throws IOException;

    /**
     * Evaluates the boundary positions.
     * A word boundary is seen as correct if it exists at a specified position determined by the original file.
     * @throws IOException
     */
    abstract String boundaryBasedEvaluation(Boolean append, ClassificationMethod classmethod) throws IOException;

    abstract String boundaryEditDistance(Boolean append, ClassificationMethod classmethod) throws IOException;

    abstract String boundarySimilarity(Boolean append, ClassificationMethod classmethod) throws IOException;

    /**
     * The Pk Evaluation moves through the original file and the generated file using a window of length k.
     * k is defined as half of the mean manual segmentation length.
     * Penalties are thrown if the windows edges are differing or the segmentations disagree
     * @throws IOException on error
     */
    abstract String pkEvaluation(Boolean append, ClassificationMethod classmethod) throws IOException;

    /**
     * Compares the given result with the original segmentation.
     * Finds out matches among them and produces statistics.
     * A match is a match if the amount of syllables and the word boundares are correct
     * @throws IOException on error
     */

    abstract String segmentationEvaluation(Boolean append, ClassificationMethod classmethod) throws IOException;

    /**
     * Evaluates the word boundaries of the segmented words.
     * A word boundary is only detected correctly if the beginning border and the ending border were set correctly.
     * @throws IOException
     */
   // public void wordBoundaryBasedEvaluation() throws IOException;

    abstract String transliterationEvaluation(Boolean append, ClassificationMethod classmethod) throws IOException;

    /**
     * Implements teh winPR metric.
     * @param append if the metric should be started in append mode
     * @param classmethod the method to evaluate
     * @return the evaluation String
     * @throws IOException
     */
    abstract String winPR(Boolean append, ClassificationMethod classmethod) throws IOException;

    /**
     * The Pk Evaluation moves through the original file and the generated file using a window of length k.
     * k is defined as half of the mean manual segmentation length.
     * Penalties are thrown if the windows edges are differing or the segmentations disagree
     * @throws IOException on error
     */
    abstract String windowDiff(Boolean append, ClassificationMethod classmethod) throws IOException;

    /**
     * Compares the given result with the original transliteration.
     * Finds out matches among them and produces statistics.
     * @throws IOException on error
     */
    abstract String wordBoundaryBasedEvaluation(Boolean append, ClassificationMethod classmethod) throws IOException;

}
