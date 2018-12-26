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

package de.unifrankfurt.cs.acoli.akkad.dict.chars.cuneiform;

import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;
import de.unifrankfurt.cs.acoli.akkad.dict.chars.PositionableChar;
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
 * Class for defining a cuneiform character/Word
 * User: Timo Homburg
 * Date: 26.10.13
 * Time: 14:30
 * Created with IntelliJ IDEA.
 */
public abstract class CuneiChar extends PositionableChar {
    /**Affix of this character/word.*/
    protected String affix;
    /**UTF-8 string of the transliteration if it exists.*/
    protected String cuneiutf8translit;
    /**Indicates if this character is a determinative.*/
    protected Boolean determinative;
    /**Indicates if this character represents a number.*/
    protected Boolean isNumberChar;
    /**Indicates if this character has a logographic interpretation.*/
    protected Boolean logograph;
    /**Indicates if this character has a phonographic interpretation.*/
    protected Boolean phonogram;
    /**Stem of this word/character.*/
    protected String stem;
    /**Suffix of this word/character.*/
    protected String suffix;
    /**Indicates if this character is a sumerogram.*/
    protected Boolean sumerogram;
    /**The relative occurance of this char/word in the corpusimport.*/
    private Double relativeoccurance;

    /**
     * Constructor for this class.
     * @param character the character to add
     */
    public CuneiChar(final String character) {
        super(character);
        this.character=character;
        this.logograph = false;
        this.phonogram = false;
        this.determinative = false;
        this.sumerogram=false;
        this.cuneiutf8translit = " ";
        this.charlength= CharTypes.CUNEICHAR.getChar_length();
        this.stem="";
        this.isNumberChar=false;
    }

    /**
     * Gets the name of this character.
     * @return the name as String
     */
    public String getCharName(){
        return this.cuneiutf8translit;
    }

    /**
     * Sets the name of this character.
     * @param name the name of the character as String
     */
    public void setCharName(final String name) {
        this.cuneiutf8translit=name;
    }

    /**
     * Indicates if this character is a determinative.
     * @return true if it is false otherwise
     */
    public Boolean getDeterminative(){
        return this.determinative;
    }

    /**
     * Sets if the character represents a determinative.
     * @param determinative determinative indicator
     */
    public void setDeterminative(final Boolean determinative){
        this.determinative=determinative;
    }

    /**
     * Indicates if this character is a character representing a number.
     * @return true if it is false otherwise
     */
    public Boolean getIsNumberChar() {
        return isNumberChar;
    }

    /**
     * Sets if this character is a character representing a number.
     * @param isNumberChar the number indicator as boolean
     */
    public void setIsNumberChar(final Boolean isNumberChar) {
        this.isNumberChar = isNumberChar;
    }

    /**
     * Indicates if this character is a logograph.
     * @return true if it is false otherwise
     */
    public Boolean getLogograph(){
        return this.logograph;
    }

    /**
     * Set if the character is a logograph.
     * @param logograph logograph indicator
     */
    public void setLogograph(final Boolean logograph){
        this.logograph=logograph;
    }

    /**
     * Indicates if the character is a phonogram.
     * @return true if it is false otherwise
     */
    public Boolean getPhonogram(){
        return this.phonogram;
    }

    /**
     * Sets if the character is a phonogram.
     * @param phonogram phonogram indicator
     */
    public void setPhonogram(final Boolean phonogram){
        this.phonogram=phonogram;
    }

    @Override
    public Double getRelativeOccurance() {
        return this.relativeoccurance;
    }

    @Override
    public void setRelativeOccurance(final Double total) {
        this.relativeoccurance = this.occurances / total;
    }

    /**
     * Incidicates if this character is a sumerogram.
     * @return true if it is false otherwise
     */
    public Boolean getSumerogram() {
        return sumerogram;
    }

    /**
     * Sets if the character is a sumerogram.
     * @param sumerogram sumerogram indicator
     */
    public void setSumerogram(final Boolean sumerogram) {
        this.sumerogram = sumerogram;
    }

    @Override
    public Integer length() {
        return this.character.length();
    }

    @Override
    public void setRelativeOccuranceFromDict(final Double relocc){
        this.relativeoccurance=relocc;
    }

    /**
     * Sets the stem of this character.
     * @param stem the stem to set.
     */
    public void setStem(final String stem){
        this.stem=stem;
    }

    @Override
    public String toString() {
        return this.character;
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
        writer.writeAttribute(Tags.DETERMINATIVE.toString(),this.determinative.toString());
        writer.writeAttribute(Tags.LOGO.toString(),this.logograph.toString());
        writer.writeAttribute(Tags.PHONO.toString(),this.phonogram.toString());
        writer.writeAttribute(Tags.MEANING.toString(),this.meaning);
        writer.writeAttribute(Tags.ABSOCC.toString(), CuneiImportHandler.formatDouble(this.occurances));
        writer.writeAttribute(Tags.RELOCC.toString(),CuneiImportHandler.formatDouble(this.relativeoccurance));
        writer.writeAttribute(Tags.BEGIN.toString(),this.beginoccurance.toString());
        writer.writeAttribute(Tags.MIDDLE.toString(),this.middleoccurance.toString());
        writer.writeAttribute(Tags.END.toString(),this.endoccurance.toString());
        writer.writeAttribute(Tags.SINGLE.toString(),this.singleoccurance.toString());
        writer.writeAttribute(Tags.LEFTACCVAR.toString(),this.leftaccessorvariety.toString());
        writer.writeAttribute(Tags.RIGHTACCVAR.toString(),this.rightaccessorvariety.toString());
        writer.writeAttribute(Tags.ISNUMBERCHAR.toString(),this.isNumberChar.toString());
        writer.writeAttribute(Tags.SUMEROGRAM.toString(),this.sumerogram.toString());
        writer.writeAttribute(Tags.STEM.toString(),this.stem);
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
                writer.writeCharacters("\n"+akkadfollow.toXML());
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
        //System.out.println("ToString: "+strwriter.toString());
        return strwriter.toString();
    }
}
