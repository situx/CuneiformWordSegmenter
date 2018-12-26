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

package de.unifrankfurt.cs.acoli.akkad.dict.importhandler;

import de.unifrankfurt.cs.acoli.akkad.dict.corpusimport.util.NGramStat;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Tags;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Created by timo on 31.08.14.
 */
public class NGramImportHandler extends ImportHandler {
    /**The chartype to use.*/
    private final CharTypes charType;
    /**Ngramstats to savve.*/
    private final NGramStat ngramstat;
    /**Temp frequency.*/
    private Double freq;
    /**Temp length of the ngram.*/
    private Integer length;
    /**Temp ngram.*/
    private String ngram;
    /**Ngram boolean for xml navigation purposes.*/
    private Boolean ngramb=false;

    /**
     * Constructor for this class.
     * @param ngramstat  the ngramstat to fill
     * @param charTypes the chartype to use
     */
    public NGramImportHandler(final NGramStat ngramstat,final CharTypes charTypes){
         this.ngramstat=ngramstat;
         this.charType=charTypes;
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
         if(ngramb){
             this.ngram+=new String(ch,start,length);
         }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        switch (qName){
            case Tags.NGRAM: this.ngramstat.addNGram(charType,ngram,length,freq);
                ngram="";
                ngramb=false;
            break;
            default:
        }
    }

    @Override
    public String reformatToASCIITranscription(final String transcription) {
        return transcription;
    }

    @Override
    public String reformatToUnicodeTranscription(final String transcription) {
        return transcription;
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        switch(qName){
            case Tags.NGRAM: this.length=Integer.valueOf(attributes.getValue(Tags.LENGTH));
                this.freq= Double.valueOf(attributes.getValue(Tags.FREQ));
                ngramb=true;
                break;
            default:
        }
    }
}
