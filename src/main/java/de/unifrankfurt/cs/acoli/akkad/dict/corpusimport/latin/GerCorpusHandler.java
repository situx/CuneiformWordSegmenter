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

package de.unifrankfurt.cs.acoli.akkad.dict.corpusimport.latin;

import de.unifrankfurt.cs.acoli.akkad.dict.corpusimport.CorpusHandlerAPI;
import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.dict.pos.POSTagger;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.TestMethod;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.List;

/**
 * Created by timo on 31.08.14.
 * German Corpus Handler.
 */
public class GerCorpusHandler extends CorpusHandlerAPI {

    /**
     * Constructor for this class.
     * @param stopchars stopchars to consider
     */
    public GerCorpusHandler(final List<String> stopchars) {
        super(stopchars);
    }

    @Override
    public void addTranslations(final String file, final TestMethod testMethod1) throws ParserConfigurationException, SAXException, IOException {

    }

    @Override
    public String cleanWordString(final String word, final boolean reformat) {
        return null;
    }

    @Override
    public String corpusToReformatted(final String text) {
        return null;
    }

    @Override
    public DictHandling dictImport(final String corpus, final TestMethod testMethod, final CharTypes sourcelang) throws IOException, SAXException, ParserConfigurationException {
        return null;
    }

    @Override
    public void enrichExistingCorpus(final String filepath, final DictHandling dicthandler) throws IOException {

    }

    @Override
    public DictHandling generateCorpusDictionaryFromFile(final List<String> filepath, final String signpath,final String filename, final boolean wholecorpus, final boolean corpusstr,final TestMethod testMethod) throws IOException, ParserConfigurationException, SAXException, XMLStreamException {
        return null;
    }

    @Override
    public POSTagger getPOSTagger(Boolean newPosTagger) {
        return null;
    }

    @Override
    public DictHandling getUtilDictHandler() {
        return null;
    }

    @Override
    public void textPercentageSplit(final Double perc, final Double startline,final Boolean random, final String corpusfile,final CharTypes charTypes) {

    }

    @Override
    public String transliterationToText(final String text, final Integer duplicator, final DictHandling dicthandler,final Boolean countmatches,final Boolean segmented) throws IOException {
        return null;
    }
}
