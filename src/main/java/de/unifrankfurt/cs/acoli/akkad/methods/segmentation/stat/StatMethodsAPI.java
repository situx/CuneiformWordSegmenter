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

package de.unifrankfurt.cs.acoli.akkad.methods.segmentation.stat;

import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.*;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Timo Homburg
 * Date: 17.11.13
 * Time: 13:54
 * To change this template use File | Settings | File Templates.
 */
public interface StatMethodsAPI {
    /**
     * Implements the c45 segmenting algorithm.
     * @param sourcepath the path of the testingdata to use
     * @param trainpath the path of the trainingdata to use
     * @param dicthandler the dicthandler to use
     * @param featureSet the featureset to use
     * @throws IOException on error
     */
    public void c45Segmenting(final String sourcepath,final String trainpath,final String modelfile,final DictHandling dicthandler,final FeatureSets featureSet,final TransliterationMethod transliterationMethod,final CharTypes chartype,final TestMethod testMethod) throws IOException, WekaException;
    /**
     * Implements the crf segmenting algorithm.
     * @param sourcepath the path of the testingdata to use
     * @param trainpath the path of the trainingdata to use
     * @param dicthandler the dicthandler to use
     * @param featureSet the featureset to use
     * @throws IOException on error
     */
    public void conditionalRandomFields(final String sourcepath,final String trainpath,final String modelfile,final DictHandling dicthandler,final FeatureSets featureSet,final TransliterationMethod transliterationMethod,final CharTypes chartype,final TestMethod testMethod) throws IOException, WekaException;

    /**
     * Initialises the parsing using statistical methods.
     * @param sourcepath the path of the testingdata to use
     * @param destpath the result path to use
     * @param trainpath the path of the trainingdata to use
     * @param dicthandler the dicthandler to use
     * @param method the statistical method to use
     * @param featureset the featureset to use
     * @throws IOException on error
     */
    public void initParsing(final String sourcepath,final String destpath,final String trainpath,final String modelfile,final DictHandling dicthandler,final ClassificationMethod method,final FeatureSets featureset,final TransliterationMethod transliterationMethod,final CharTypes chartype,final TestMethod testMethod) throws Exception;
    /**
     * Implements the maxEntropyMatching algorithm.
     * @param sourcepath the path of the testingdata to use
     * @param trainpath the path of the trainingdata to use
     * @param dicthandler the dicthandler to use
     * @param featureSet the featureset to use
     * @throws IOException on error
     */
    public void maxEntropyMatching(final String sourcepath,final String trainpath,final String modelfile,final DictHandling dicthandler,final FeatureSets featureSet,final TransliterationMethod transliterationMethod,final CharTypes chartype,final TestMethod testMethod) throws IOException, WekaException;

    /**
     * Dynamic programming algorithm to create a sequence of words for a sentence with maximum probability.
     * @param sourcepath the file to analyse
     * @param dicthandler the dicthandler to use
     * @param transliterationMethod
     * @throws IOException
     */
    public void maxProbSegmenting(final String sourcepath, final DictHandling dicthandler, final TransliterationMethod transliterationMethod,final CharTypes chartype,final TestMethod testMethod) throws IOException, WekaException;

    /**
     * Implements the naive bayes segmenting algorithm.
     * @param sourcepath the path of the testingdata to use
     * @param trainpath the path of the trainingdata to use
     * @param dicthandler the dicthandler to use
     * @param featureSet the featureset to use
     * @throws IOException on error
     */
    public void naiveBayesSegmenting(final String sourcepath,final String trainpath,final String modelfile,final DictHandling dicthandler,final FeatureSets featureSet,final TransliterationMethod transliterationMethod,final CharTypes chartype,final TestMethod testMethod) throws IOException, WekaException;

    /**
     * Implements the svmsegmenting algorithm.
     * @param sourcepath the path of the testingdata to use
     * @param trainpath the path of the trainingdata to use
     * @param dicthandler the dicthandler to use
     * @param featureSet the featureset to use
     * @throws IOException on error
     */
    public void svmSegmenting(final String sourcepath,final String trainpath,final String modelfile,final DictHandling dicthandler,final FeatureSets featureSet,final TransliterationMethod transliterationMethod,final CharTypes chartype,final TestMethod testMethod) throws IOException, WekaException;

    /**
     * Implements the winnow segmenting algorithm.
     * @param sourcepath the path of the testingdata to use
     * @param trainpath the path of the trainingdata to use
     * @param dicthandler the dicthandler to use
     * @param featureSet the featureset to use
     * @throws IOException on error
     */
    public void winnowSegmenting(final String sourcepath,final String trainpath,final DictHandling dicthandler,final FeatureSets featureSet,final TransliterationMethod transliterationMethod,final CharTypes chartype,final TestMethod testMethod) throws IOException, WekaException;
}
