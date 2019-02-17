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

package de.unifrankfurt.cs.acoli.akkad.dict.utils;

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;
import de.unifrankfurt.cs.acoli.akkad.dict.importhandler.cuneiform.CuneiImportHandler;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Tags;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.util.TreeMap;

/**
 * Class for modelling a Transliteration.
 * User: Timo Homburg
 * Date: 13.11.13
 * Time: 17:21
 */
public class Transliteration implements Comparable<Transliteration>{
    /**The absolute occurance of this transliteration.*/
    private Double absoluteOccurance=0.;
    /**Indicates  if this transliteration is used at the beginning of a word.*/
    private Double beginTransliteration;
    /**Indicates if this transliteration is used at the end of a word.*/
    private Double endTransliteration;
    /**Indicates if this transliteration is the transliteration of a whole word.*/
    private Boolean isWord;
    /**Indicates if this transliteration is used in the middle of a word.*/
    private Double middleTransliteration;
    /**The position map of this transliteration.*/
    private java.util.Map<Integer,Occurance> position;
    private Double relativeOccurance=0.;
    /**Indicates if this transliteration is a single transliteration.*/
    private Double singleTransliteration;
    /**The transliteration as string.*/
    private String transcription;
    /**The transliteration as string.*/
    private String transliteration;

    /**
     * Constructor for a char transliteration.
     * @param transliteration
     */
    public Transliteration(final String transliteration,final String transcription){
        this(transliteration,transcription,false);
    }

    /**
     * Constructor for transliteration.
     * @param transliteration the transliteration to create
     * @param isWord indicates if the transliteration is assigned to a word
     */
    public Transliteration(final String transliteration,final String transcription,final Boolean isWord){
        this.transliteration=transliteration;
        this.beginTransliteration =0.;
        this.endTransliteration =0.;

        this.middleTransliteration =0.;
        this.singleTransliteration =0.;
        this.isWord=isWord;
        this.position=new TreeMap<Integer,Occurance>();
        this.transcription=transcription;
    }

    @Override
    public int compareTo(final Transliteration o) {
            return this.transliteration.compareTo(o.transliteration);
    }

    @Override
    public boolean equals(final Object obj) {
        if(obj instanceof Transliteration)
            return this.transliteration.equals(((Transliteration)obj).transliteration);
        return false;
    }

    /**
     * Gets the absolute occurance of this transliteration.
     * @return the absolute occurance as Double
     */
    public Double getAbsOcc() {
        return this.absoluteOccurance;
    }
    /**
     * Gets the absolute occurance of this transliteration.
     * @return the absolute occurance as Double
     */
    public Double getAbsoluteOccurance() {
        return absoluteOccurance;
    }

    public void setAbsoluteOccurance(final Double absoluteOccurance) {
        this.absoluteOccurance = absoluteOccurance;
    }

    public Double getBeginTransliteration() {
        return beginTransliteration;
    }

    public Double getEndTransliteration() {
        return endTransliteration;
    }

    public Boolean getIsWord() {
        return isWord;
    }

    public void setIsWord(final Boolean isWord) {
        this.isWord = isWord;
    }

    public Double getMiddleTransliteration() {
        return middleTransliteration;
    }

    /**
     * Gets the occurance amount of this char at a given position.
     * @param position the position as Integer
     * @return the occurance object at the given position
     */
    public Occurance getOccuranceAtPosition(final Integer position) {
        return this.position.get(position);
    }

    public Double getRelOcc() {
        return relativeOccurance;
    }

    public Double getRelativeOccurance() {
        return relativeOccurance;
    }

    public void setRelativeOccurance(final Double globalTranslitOccurance) {
        this.relativeOccurance = this.absoluteOccurance/globalTranslitOccurance;
    }

    public Double getSingleTransliteration() {
        return singleTransliteration;
    }

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(final String transcription) {
        this.transcription = transcription;
    }

    public String getTransliteration() {
        return transliteration;
    }

    /**
     * Gets the transliteration String for this transliteration.
     * @return the transliteration String
     */
    public String getTransliterationString() {
        return transliteration;
    }

    /**
     * Sets the transliteration String for this transliteration.
     * @param transliteration the transliteration String to set
     */
    public void setTransliterationString(final String transliteration) {
        this.transliteration = transliteration;
    }

    public Boolean isBeginTransliteration() {
        return beginTransliteration >0;
    }

    public Boolean isEndTransliteration() {
        return endTransliteration >0;
    }

    public Boolean isMiddleTransliteration() {
        return middleTransliteration >0;
    }

    /**
     * Returns if this transliteration is a single transliteration.
     * @return true if it is false otherwise
     */
    public Boolean isSingleTransliteration() {
        return singleTransliteration >0;
    }

    /**
     * Sets a transliteration to be a beginning transliteration.
     * @param beginTransliteration indicates if it is a beginning transliteration
     * @param position adds a position to the current occurance
     */
    public void setBeginTransliteration(Boolean beginTransliteration,Integer position) {
        if(!this.position.containsKey(position)){
            this.position.put(position,new Occurance(0,position));
        }else{
            this.position.get(position).addTransliterationOccurance();
        }

        this.beginTransliteration++;
    }

    public void setEndTransliteration(Boolean endTransliteration,Integer position) {
        if(!this.position.containsKey(position)){
            this.position.put(position,new Occurance(2,position));
        }else{
            this.position.get(position).addTransliterationOccurance();
        }
        this.endTransliteration++;
    }

    public void setMiddleTransliteration(Boolean middleTransliteration,Integer position) {
        if(!this.position.containsKey(position)){
            this.position.put(position,new Occurance(1,position));
        }else{
            this.position.get(position).addTransliterationOccurance();
        }
        this.middleTransliteration++;
    }

    public void setRelativeOccuranceFromDict(final Double relocc){
        this.relativeOccurance=relocc;
    }

    public void setSingleTransliteration(Boolean singleTransliteration,Integer position) {
        if(!this.position.containsKey(position)){
            this.position.put(position,new Occurance(3,position));
        }else{
            this.position.get(position).addTransliterationOccurance();
        }
        this.singleTransliteration++;
    }

    @Override
    public String toString(){
        return this.transliteration;
    }

    public String toXML(){
        StringWriter strwriter=new StringWriter();
        XMLOutputFactory output3 = XMLOutputFactory.newInstance();
        output3.setProperty(Tags.ESCAPECHARACTERS.toString(), false);
        XMLStreamWriter writer;
        try {
            writer = new IndentingXMLStreamWriter(output3.createXMLStreamWriter(strwriter));
        writer.writeStartElement(Tags.TRANSLITERATION);
        writer.writeAttribute(Tags.BEGIN.toString(), this.beginTransliteration.toString());
        writer.writeAttribute(Tags.MIDDLE.toString(), this.middleTransliteration.toString());
        writer.writeAttribute(Tags.END.toString(), this.endTransliteration.toString());
        writer.writeAttribute(Tags.SINGLE.toString(), this.singleTransliteration.toString());
        writer.writeAttribute(Tags.ISWORD.toString(),this.isWord.toString());
        writer.writeAttribute(Tags.ABSOCC.toString(), CuneiImportHandler.formatDouble(this.absoluteOccurance));
        writer.writeAttribute(Tags.RELOCC.toString(),CuneiImportHandler.formatDouble(this.relativeOccurance));
        writer.writeAttribute(Tags.TRANSCRIPTION.toString(),this.transcription);
        writer.flush();
        //writer2.writeAttribute(Tags.POSITION,akkadtrans.getPosition().iterator().next().toString());
        for(Integer position:this.position.keySet()){
            writer.writeStartElement(Tags.POSITION.toString());
            writer.writeAttribute(Tags.POSITION.toString(), position.toString());
            writer.writeAttribute(Tags.ABSOCC.toString(),this.position.get(position).toString());
            writer.writeEndElement();
        }
        writer.writeCharacters(this.transliteration);
        writer.writeEndElement();
        } catch (XMLStreamException | FactoryConfigurationError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return strwriter.toString();
    }
}
