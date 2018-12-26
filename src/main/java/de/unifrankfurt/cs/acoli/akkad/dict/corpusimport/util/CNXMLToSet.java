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

package de.unifrankfurt.cs.acoli.akkad.dict.corpusimport.util;

import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Tags;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;

/**
 * Created by timo on 14.09.14.
 */
public class CNXMLToSet extends DefaultHandler2 {

    private String result="";

    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        this.result+=new String(ch,start,length);
    }

    public String convert(final String file) throws ParserConfigurationException, SAXException, IOException {
        this.result="";
        BufferedWriter writer=new BufferedWriter(new FileWriter(new File("beeeep")));
        writer.write(file);
        writer.close();
        System.setProperty("avax.xml.parsers.SAXParserFactory",
                "org.apache.xerces.parsers.SAXParser");
        SAXParserFactory.newInstance().newSAXParser().parse(new InputSource(new StringReader(file)),this);
        return this.result;
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if(qName.equals(Tags.S)){
            result+=System.lineSeparator();
        }
        if(qName.equals(Tags.C)){
            result+=System.lineSeparator();
        }
    }

    public String getResult() {
        return result;
    }
}
