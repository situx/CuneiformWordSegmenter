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

/**
 * Created with IntelliJ IDEA.
 * User: timo
 * Date: 05.12.13
 * Time: 12:15
 * To change this template use File | Settings | File Templates.
 */
public abstract class FeatureSet {
    /**Number of attributes of the feature vector.*/
    protected final Integer attributes;
    /**Values of the classes to classify.*/
    protected final String[] classValues;
    protected String name;
    /**The previous classification of the previously classified vector.*/
    protected String prepreviousclassification;
    /**The classification of the previously classified vector.*/
    protected String previousclassification;
    protected Boolean wordOrCharBased;

    /**
     * Constructor for this class.
     * @param statusValues the class values
     * @param attributes the number of features
     */
    public FeatureSet(String[] statusValues,Integer attributes,String name,Boolean wordOrCharBased){
         this.classValues =statusValues;
         this.attributes=attributes;
         this.prepreviousclassification="";
         this.previousclassification="";
        this.wordOrCharBased=wordOrCharBased;
        this.name=name;
    }

    /**
     * Gets the amount of attributes of this feature set.
     * @return the amount as int
     */
    public Integer getAttributes(){
        return this.attributes;
    }

    public Boolean getCharOrWord() {
        return wordOrCharBased;
    }

    /**
     * Gets the class values of this feature set
     * @return the array of class values.
     */
    public String[] getClassValues(){
        return this.classValues;
    }

    /**
     * Stores previous classificaitions in case needed by the processing step.
     * @param previous the classification to store
     */
    public void setPreviousclassification(final String previous){
           this.prepreviousclassification=previousclassification;
           this.previousclassification=previous;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
