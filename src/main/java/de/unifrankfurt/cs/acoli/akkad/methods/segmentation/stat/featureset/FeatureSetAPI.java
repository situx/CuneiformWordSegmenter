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

package de.unifrankfurt.cs.acoli.akkad.methods.segmentation.stat.featureset;

import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.util.Tuple;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.FeatureSets;

/**
 * API for descriptions of feature sets.
 * User: Timo Homburg
 * Date: 05.12.13
 * Time: 16:28
 */
public interface FeatureSetAPI {


    /**
     * Gets the context of the current
     * @param tagset the tagset to use
     * @param featureSet the feature set to use
     * @param dicthandler the dicthandler to use
     * @return the result as String[]
     */
    public String[] getContext(Tuple<StringBuffer, Integer> tagset, FeatureSets featureSet,String segmented, DictHandling dicthandler, CharTypes chartype);
}
