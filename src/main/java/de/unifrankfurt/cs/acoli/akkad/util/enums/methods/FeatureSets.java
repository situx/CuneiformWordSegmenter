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

import de.unifrankfurt.cs.acoli.akkad.methods.segmentation.stat.featureset.CharBasedFeatureSet;
import de.unifrankfurt.cs.acoli.akkad.methods.segmentation.stat.featureset.FeatureSet;
import de.unifrankfurt.cs.acoli.akkad.methods.segmentation.stat.featureset.WordBasedFeatureSet;

/**
 * Feature Sets for used machine learning methods.
 * User: timo
 * Date: 04.12.13
 * Time: 01:06
 */
public enum FeatureSets implements MethodEnum {
    CRF(new CharBasedFeatureSet(new String[]{"0","1"},9,"CRF",false)),
    /**Meta Feature Set for classifying many results.*/
    META(new CharBasedFeatureSet(new String[]{"0","1"},36,"META",false)),
    /**Max Entropy Feature Set.*/
    MAXENT(new CharBasedFeatureSet(new String[]{"0","1"},17,"MAXENT",false)),
    /**MaxEntPrev Feature Set.*/
    MAXENTPREV(new CharBasedFeatureSet(new String[]{"0","1"},15,"MAXENTPREV",true)),
    /**No feature set selected.*/
    PASSIVEAGGRESSIVE(new WordBasedFeatureSet(new String[]{"0","1"},8,"PASSIVEAGGRESSIVE",false)),
    /**Perceptron Featureset.*/
    PERCEPTRON(new WordBasedFeatureSet(new String[]{"0","1"},12,"PERCEPTRON",false)),
    /**No Feature Set is used.*/
    NOFEATURE(new CharBasedFeatureSet(new String[]{},12,"NOFEATURE",false)),
    //PERCEPTRONCRF(new CharBasedFeatureSet(new String[]{"0","1"},10,"PERCEPTRONCRF",false)),
    /**The unlabeled feature set.*/
    UNLABELED(new WordBasedFeatureSet(new String[]{"0","1"},108,"UNLABELED",false)),
    /**The MaxEntSighan Feature Set.*/
    MAXENTSIGHAN(new CharBasedFeatureSet(new String[]{"0","1"},20,"MAXENTSIGHAN",false));

    /**Corresponding Featureset.*/
    private FeatureSet feature;

    /**Constructor for this class.
     * @param featureSet the corresponding feature set
     */
    private FeatureSets(final FeatureSet featureSet){
         this.feature=featureSet;
    }



    /**
     * Returns the current feature set
     * @return the feature set
     */
    public FeatureSet getFeatureSet(){
        return this.feature;
    }

    @Override
    public String getShortname() {
        return null;
    }

    @Override
    public String toString() {
        return feature.toString();
    }
}
