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

package de.unifrankfurt.cs.acoli.akkad.methods.segmentation.rule;

import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.ClassificationMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.TransliterationMethod;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: timo
 * Date: 17.11.13
 * Time: 14:32
 * To change this template use File | Settings | File Templates.
 */
public interface RuleAPI {

    /**
     * Tries to match every character as a word. While in Chinese this is quite effective it is not guaranteed for other languages.
     * @param filepath  the path of the file to analyse
     * @param dict
     *@param transliterationMethod @throws IOException
     */
    public void charSegmentParse(final String filepath,final String destpath, final DictHandling dict, final TransliterationMethod transliterationMethod,final CharTypes chartype,final Boolean transcriptToTranslit,final Boolean corpusstr) throws IOException;

    public void initParsing(final String sourcepath,final String destpath,final DictHandling dicthandler,final ClassificationMethod method,final TransliterationMethod transliterationMethod,final CharTypes chartype,final Boolean transcriptToTranslit,final Boolean corpusstr) throws IOException;

    /**Try to match suffixes from the given words and try to find word classes in the dictionary.*/
    public void matchWordByFakePOS();

    /**
     * PrefixSuffixMatching method.
     * Constructs words by given starting,ending oder middle markers.
     * @param filepath
     * @param dicthandler
     * @throws FileNotFoundException
     */
    public void prefixSuffixMatching(final String filepath,final String destpath, final DictHandling dicthandler, final TransliterationMethod transliterationMethod, final CharTypes chartype,final Boolean transcriptToTranslit,final Boolean corpusstr) throws IOException;

    /**
     * Calculates word boundaries for every line at random.
     * @param filepath  the path of the file to analyse
     * @param dicthandler  the dicthandler to use
     * @throws IOException
     */
    public void randomSegmentParse(final String filepath,final String destpath, final DictHandling dicthandler,final TransliterationMethod transliterationMethod,final CharTypes chartype,final Boolean transcriptToTranslit,final Boolean corpusstr) throws IOException;

    /**
     * Calculates word boundaries using the tangoAlgorithm.
     *
     * @param filepath the path of the file to analyse
     * @param dicthandler the dicthandler to use
     * @param ngramsize  the size of the ngrams to use
     * @param chartype
     * @throws IOException
     */
    public void tangoAlgorithm(final String filepath,final String destpath, final DictHandling dicthandler, final int ngramsize, final TransliterationMethod transliterationMethod, final CharTypes chartype,final Boolean transcriptToTranslit,final Boolean corpusstr) throws IOException;

}
