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

package de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.cuneiform;

import de.unifrankfurt.cs.acoli.akkad.dict.chars.cuneiform.CuneiChar;
import de.unifrankfurt.cs.acoli.akkad.dict.importhandler.cuneiform.AkkadianImportHandler;
import de.unifrankfurt.cs.acoli.akkad.dict.importhandler.cuneiform.CuneiSignImportHandler;
import de.unifrankfurt.cs.acoli.akkad.dict.pos.POSTagger;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Options;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Tags;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: Timo Homburg
 * Date: 26.10.13
 * Time: 14:11
 * DictHandler for the Hittitian language.
 */
public class HittiteDictHandler extends CuneiDictHandler {
    /**
     * Constructor for this class.
     * @param stopchars stopchars to consider
     */
    public HittiteDictHandler(List<String> stopchars){
        super(stopchars,CharTypes.HITTITECHAR,new POSTagger(new TreeMap<String, Color>(),CharTypes.HITTITECHAR.getLocale()));
    }

    @Override
    public void importDictFromXML(String filepath) throws ParserConfigurationException, SAXException, IOException {
        SAXParser parser=SAXParserFactory.newInstance().newSAXParser();
        java.io.InputStream in = new FileInputStream(new File(filepath));
        InputSource is = new InputSource(in);
        is.setEncoding(Tags.UTF8.toString());
        parser.parse(filepath,new AkkadianImportHandler(Options.FILLDICTIONARY,this,this.dictmap,this.translitToWordDict,this.transcriptToWordDict,CharTypes.HITTITECHAR));
    }

    @Override
    public void importMappingFromXML(String filepath) throws ParserConfigurationException, SAXException, IOException {
        SAXParser parser=SAXParserFactory.newInstance().newSAXParser();
        java.io.InputStream in = new FileInputStream(new File(filepath));
        InputSource is = new InputSource(in);
        is.setEncoding(Tags.UTF8.toString());
        parser.parse(filepath,new AkkadianImportHandler(Options.FILLMAP,this,this.dictmap,this.translitToCharMap,this.transcriptToWordDict,CharTypes.HITTITECHAR));
    }

    @Override
    public void importReverseDictFromXML(final String filepath) throws ParserConfigurationException, SAXException, IOException {
        System.setProperty("avax.xml.parsers.SAXParserFactory",
                "org.apache.xerces.parsers.SAXParser");
        SAXParser parser=SAXParserFactory.newInstance().newSAXParser();
        java.io.InputStream in = new FileInputStream(new File(filepath));
        InputSource is = new InputSource(in);
        is.setEncoding(Tags.UTF8.toString());
        AkkadianImportHandler imp=new AkkadianImportHandler(Options.REVERSEDICT,this,this.reversedictionary,this.reverseTranslitToWordDict,this.reverseTranscriptToWordDict,CharTypes.HITTITECHAR);
        parser.parse(in,imp);
        parser.reset();
    }

    @Override
    public void parseDictFile(final File file) throws IOException, ParserConfigurationException, SAXException {
        SAXParser parser=SAXParserFactory.newInstance().newSAXParser();
        java.io.InputStream in = new FileInputStream(file);
        InputSource is = new InputSource(in);
        is.setEncoding(Tags.UTF16.toString());
        parser.parse(file, new CuneiSignImportHandler(this.dictmap,this.dictionary,this.translitToCharMap,this.translitToWordDict,this.transcriptToWordDict, CharTypes.HITTITECHAR));
    }

    @Override
    public String translitWordToCunei(final CuneiChar word) {
        return null;
    }
}
