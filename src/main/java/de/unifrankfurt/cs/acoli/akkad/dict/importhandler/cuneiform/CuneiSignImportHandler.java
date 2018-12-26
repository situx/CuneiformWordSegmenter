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

package de.unifrankfurt.cs.acoli.akkad.dict.importhandler.cuneiform;

import de.unifrankfurt.cs.acoli.akkad.dict.chars.cuneiform.AkkadChar;
import de.unifrankfurt.cs.acoli.akkad.dict.chars.cuneiform.CuneiChar;
import de.unifrankfurt.cs.acoli.akkad.dict.chars.cuneiform.HittiteChar;
import de.unifrankfurt.cs.acoli.akkad.dict.chars.cuneiform.SumerianChar;
import de.unifrankfurt.cs.acoli.akkad.dict.utils.Transliteration;
import de.unifrankfurt.cs.acoli.akkad.methods.transcription.TranscriptionMethods;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Tags;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * ImportHandler for the Akkadian file format.
 * User: Timo Homburg
 * Date: 09.11.13
 * Time: 17:19
 */
public class CuneiSignImportHandler extends CuneiImportHandler {
    boolean sign,akkadian,logogram,lemma;
    CuneiChar tempchar;
    String tempsign="";
    /**The chartype and the current transliteration to use.*/
    private CharTypes chartype,currenttranslit;
    /**Map of words being imported.*/
    private java.util.Map<String,CuneiChar> dictionary;
    /**Map of characters being imported.*/
    private java.util.Map<String,CuneiChar> dictmap;
    /**Transliteration to word map.*/
    private java.util.Map<String,String> translitToWordDict;
    private boolean val;

    /**
     * Constructor of this class.
     * @param dictmap the dictionary map to use
     * @param dictionary the dictionary to use
     * @param translitToCharMap the transliteration to character map to use
     * @param translitToWordDict the transliteration to word map to use
     * @param chartype the chartype to parse
     */
    public CuneiSignImportHandler(final java.util.Map<String, CuneiChar> dictmap, final java.util.Map<String, CuneiChar> dictionary, final java.util.Map<String, String> translitToCharMap, final java.util.Map<String, String> translitToWordDict, final java.util.Map<String, String> transcriptToWordDict, final CharTypes chartype){
          this.dictmap=dictmap;
          this.dictionary=dictionary;
          this.translitToCuneiMap=translitToCharMap;
          this.translitToWordDict=translitToWordDict;
          this.transcriptToCuneiMap=transcriptToWordDict;
          this.chartype=chartype;
          this.currenttranslit=CharTypes.LANGCHAR;
    }

    @Override
    public void characters(final char[] ch, final int start, final int length)
            throws SAXException {
        if(this.val && this.sign){
            String temp=new String(ch,start,length);
            if(this.currenttranslit==this.chartype && !this.logogram){
                String temptranslit=temp.replace("!", "").replace("#", "").replace("?","").toLowerCase();
                String asciitranslit=this.reformatToASCIITranscription(temptranslit);
                this.tempchar.addTransliteration(new Transliteration(temptranslit, TranscriptionMethods.translitTotranscript(this.reformatToASCIITranscription(temptranslit))));
                if(!asciitranslit.equals(temptranslit)){
                    this.tempchar.addTransliteration(new Transliteration(asciitranslit,TranscriptionMethods.translitTotranscript(asciitranslit)));
                }

                //this.tempchar.addTrans(new Transliteration(this.reformatToASCIITranscription(temp),TranscriptionMethods.translitTotranscript(this.reformatToASCIITranscription(temp))));
                //System.out.println("PUSH :"+temp.replace("!","").replace("#","").toLowerCase()+" - "+this.tempsign);
                this.tempchar.setPhonogram(true);
                this.translitToCuneiMap.put(temptranslit, this.tempsign);
                this.translitToCuneiMap.put(asciitranslit, this.tempsign);
                this.transcriptToCuneiMap.put(asciitranslit, this.tempsign);
            } else if(this.currenttranslit==this.chartype && this.lemma) {
                this.tempchar.setCharName(temp);
            }
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName)
            throws SAXException {
        switch(qName){
            case Tags.SIGN: this.sign=false;break;
            case Tags.VAL: this.val=false;this.currenttranslit=CharTypes.LANGCHAR;break;
            case Tags.LOGOGRAM: this.logogram=false;this.currenttranslit=CharTypes.LANGCHAR;break;
            case Tags.LEMMA: this.lemma=false;
            default: break;
        }
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName,
                             final Attributes attributes) throws SAXException {
       switch(qName){
            case Tags.SIGN:
                if(attributes.getValue(Tags.SIGN)!=null){
                    this.sign=true;
                    //System.out.println(attributes.getValue(Tags.SIGN).toString().substring(attributes.getValue(Tags.SIGN).toString().length()-2));

                    this.tempsign=attributes.getValue(Tags.SIGN).toString().substring(attributes.getValue(Tags.SIGN).toString().length()-2);
                    //System.out.println(i+++" "+attributes.getValue(Tags.SIGN).toString().substring(attributes.getValue(Tags.SIGN).toString().length()-2)+" - "+attributes.getValue("signName"));
                    if(this.dictmap.get(tempsign)!=null){
                        this.tempchar=this.dictmap.get(tempsign);
                        //TODO: FIXME: Find out why 𒉾 is so dominant after rescanning
                    }else{
                        //if((this.tempchar=AkkadDictHandler.this.dictmap.get(tempsign))==null){
                        switch (chartype){
                            case AKKADIAN: this.tempchar=new AkkadChar(this.tempsign);break;
                            case HITTITECHAR: this.tempchar=new HittiteChar(this.tempsign);break;
                            case SUMERIANCHAR: this.tempchar=new SumerianChar(this.tempsign);break;
                            default:
                        }

                        if(this.tempchar.getCharacter().length()>2){
                            this.dictionary.put(this.tempsign,this.tempchar);
                        }else{
                            this.dictmap.put(this.tempsign, this.tempchar);
                        }
                        //}
                        if(attributes.getValue(Tags.SIGNNAME.toString())!=null){
                            this.tempchar.setCharName(attributes.getValue(Tags.SIGNNAME.toString()));
                        }

                        if(attributes.getValue(Tags.LOGO.toString())!=null){
                            this.tempchar.setLogograph(true);
                        }
                        if(attributes.getValue(Tags.DETERMINATIVE.toString())!=null)
                            this.tempchar.setDeterminative(Boolean.valueOf(attributes.getValue(Tags.DETERMINATIVE.toString())));
                    }

                }break;
            case Tags.VAL:
                if(attributes.getValue(Tags.LANGUAGE.toString())!=null && this.sign){
                this.val=true;
                if(Tags.AKKADIAN.toString().equals(attributes.getValue(Tags.LANGUAGE.toString()))|| Tags.SUMERIAN.toString().equals(attributes.getValue(Tags.LANGUAGE.toString())) && chartype==CharTypes.AKKADIAN){
                    this.currenttranslit=CharTypes.AKKADIAN;
                } else if(Tags.SUMERIAN.toString().equals(attributes.getValue(Tags.LANGUAGE.toString()))){
                    this.currenttranslit=CharTypes.SUMERIANCHAR;
                } else if(Tags.HITTITE.toString().equals(attributes.getValue(Tags.LANGUAGE.toString()))){
                    this.currenttranslit=CharTypes.HITTITECHAR;
            }else{
                    this.currenttranslit=CharTypes.LANGCHAR;
                }
                }break;
            case Tags.LOGOGRAM: if(this.sign){
                this.logogram=true;
                if(attributes.getValue(Tags.LANGUAGE.toString())==null){
                    this.logogram=false;break;
                }
                if(Tags.AKKADIAN.toString().equals(attributes.getValue(Tags.LANGUAGE.toString()))){
                    this.currenttranslit=CharTypes.AKKADIAN;
                } else if(Tags.SUMERIAN.toString().equals(attributes.getValue(Tags.LANGUAGE.toString()))){
                    this.currenttranslit=CharTypes.SUMERIANCHAR;
                } else if(Tags.HITTITE.toString().equals(attributes.getValue(Tags.LANGUAGE.toString()))){
                    this.currenttranslit=CharTypes.HITTITECHAR;
                }else{
                    this.currenttranslit=CharTypes.LANGCHAR;
                }}break;
            case Tags.LEMMA: if(this.logogram && this.sign){
                this.lemma=true;
            }
            default: break;
        }
    }


}