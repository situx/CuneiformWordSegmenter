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

package de.unifrankfurt.cs.acoli.akkad.dict.chars.asian;

import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;
import de.unifrankfurt.cs.acoli.akkad.dict.importhandler.cuneiform.CuneiImportHandler;
import de.unifrankfurt.cs.acoli.akkad.dict.utils.*;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Tags;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: timo
 * Date: 12.10.13
 * Time: 16:05
 * Class representing a Chinese char/word.
 */
public class CNChar extends AsianChar {
    /**
     * Constructor for CNChar.
     * @param cnword the char/word to be represented
     */
    public CNChar(final String cnword){
        super(cnword);
        this.occurances=1.;
        this.relativeoccurance=0.;
        this.charlength= CharTypes.CHINESE.getChar_length();
    }


    @Override
    public Double getRelativeOccurance() {
        return this.relativeoccurance;
    }

    @Override
    public void setRelativeOccurance(final Double total) {
        this.relativeoccurance=this.occurances/total;
    }

    @Override
    public Integer length() {
        return this.character.length();
    }

    @Override
    public String toString() {
        return this.character+" "+this.transliterations+":"+this.translations;
    }

    @Override
    public String toXML(String startelement) {
        StringWriter strwriter=new StringWriter();
        XMLOutputFactory output3 = XMLOutputFactory.newInstance();
        output3.setProperty(Tags.ESCAPECHARACTERS.toString(), false);
        XMLStreamWriter writer;
        try {
            writer = new IndentingXMLStreamWriter(output3.createXMLStreamWriter(strwriter));
            writer.writeStartElement(startelement);
            //System.out.println(akkadchar.getCharacter() + " - " + akkadchar.getAkkadLogoName());
            writer.writeAttribute(Tags.LOGOGRAM, this.character);
            writer.writeAttribute(Tags.ABSOCC.toString(), CuneiImportHandler.formatDouble(this.occurances));
            writer.writeAttribute(Tags.RELOCC.toString(),CuneiImportHandler.formatDouble(this.relativeoccurance));
            writer.writeAttribute(Tags.BEGIN.toString(),this.beginoccurance.toString());
            writer.writeAttribute(Tags.MIDDLE.toString(),this.middleoccurance.toString());
            writer.writeAttribute(Tags.END.toString(),this.endoccurance.toString());
            writer.writeAttribute(Tags.SINGLE.toString(),this.singleoccurance.toString());
            writer.writeAttribute(Tags.LEFTACCVAR.toString(),this.leftaccessorvariety.toString());
            writer.writeAttribute(Tags.RIGHTACCVAR.toString(),this.rightaccessorvariety.toString());
            writer.flush();
            //System.out.println("toXML Transliterations: "+this.transliterations.toString());
            for(Transliteration akkadtrans:this.transliterations.keySet()){
                writer.writeCharacters(System.lineSeparator()+akkadtrans.toXML());
            }
            writer.flush();
            //System.out.println("toXML Translations: "+this.translations.toString());
            for(String locale:this.translations.keySet()){
                for(Translation trans:this.translations.get(locale).keySet()){
                    writer.writeCharacters(System.lineSeparator()+trans.toXML());
                }
            }
            writer.flush();
            for(POSTag pos:this.postags){
                writer.writeCharacters(System.lineSeparator()+pos.toXML());
            }
            writer.flush();
            Map<String,Preceding> precedingwords=this.precedingChars;
            for(Preceding akkadfollow:precedingChars.values()){
                writer.writeCharacters(System.lineSeparator()+akkadfollow.toXML());
            }
            Map<String,Following> followingwords=this.getFollowingWords();
            for(Following akkadfollow:followingwords.values()){
                writer.writeCharacters(System.lineSeparator()+akkadfollow.toXML());
            }
            writer.writeCharacters(System.lineSeparator()+this.character);
            writer.writeEndElement();
        } catch (XMLStreamException | FactoryConfigurationError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return strwriter.toString();
    }
}
