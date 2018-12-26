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
import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.dict.utils.Following;
import de.unifrankfurt.cs.acoli.akkad.dict.utils.Translation;
import de.unifrankfurt.cs.acoli.akkad.dict.utils.Transliteration;
import de.unifrankfurt.cs.acoli.akkad.methods.transcription.TranscriptionMethods;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Options;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Tags;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Locale;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Timo Homburg
 * Date: 06.11.13
 * Time: 17:59
 * ImportHandler for the cunei dictionaries and map format.
 */
public class AkkadianImportHandler extends CuneiImportHandler {
    public Double amountOfWordsInCorpus=0.;
    public Double lengthOfWordsInCorpus=0.;
    private String charcollector ="";
    /**The CharType to use.*/
    private CharTypes chartype;
    private String followingcollector="",languagecollector="",precedingcollector="";
    /**Option to fill a dictionary or a map.*/
    private Options mapOrDict;
    private CuneiChar newChar;
    private double occurancetemp;
    private Double precedingtemp,precedingtemp2;
    private Following tempfollowing;
    private Translation temptranslat;
    private Transliteration temptranslit;
    private boolean translation;
    private boolean transliteration,following,mapentry,preceding;

    /**
     * Constructor for this class.
     * @param mapOrDict indicates if we are parsing a mapping or dictionary file
     * @param resultmap the map to put the resulting chars in
     * @param translitToCuneiMap the map to put the resulting transliterations in
     * @param chartype the language char type to use
     */
    public AkkadianImportHandler(final Options mapOrDict, final DictHandling dictHandler,final Map<String, CuneiChar> resultmap, final Map<String, String> translitToCuneiMap, final Map<String, String> transcriptToCuneiMap, CharTypes chartype){
          this.resultmap=resultmap;
          this.mapOrDict=mapOrDict;
          this.translitToCuneiMap=translitToCuneiMap;
          this.transcriptToCuneiMap=transcriptToCuneiMap;
          this.chartype=chartype;
          this.dictHandler=dictHandler;
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        //System.out.println(new String(ch,start,length));
        if(this.transliteration){
            this.temptranslit.setTransliterationString((this.temptranslit.getTransliteration()+new String(ch, start, length)).replace("\n", "").trim());
            this.temptranslit.setTranscription(TranscriptionMethods.translitTotranscript(this.temptranslit.getTransliteration()));
        }else if(this.following){
            this.followingcollector+=new String(ch,start,length);
        }else if(this.preceding){
            this.precedingcollector+=new String(ch,start,length);
        } else if(this.translation){
            this.languagecollector+=new String(ch,start,length);
        }else if(this.mapentry){
            this.charcollector +=new String(ch,start,length);
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        switch(qName){
            case Tags.TRANSLITERATION: this.transliteration=false;this.newChar.addTransliteration(this.temptranslit);break;
            case Tags.TRANSLATION: this.translation=false;this.newChar.addTranslation(this.languagecollector.replace("\n", "").trim(),this.temptranslat.getLocale());this.languagecollector="";break;
            case Tags.PRECEDING:if(this.following){
                this.tempfollowing.addPreceding(this.precedingcollector.replace("\n", "").trim(),this.precedingtemp,this.precedingtemp2);
            }else{
                this.newChar.addPrecedingWord(this.precedingcollector.replace("\n","").trim());
            }
                this.precedingcollector=""; this.preceding=false;break;
            case Tags.FOLLOWING: this.following=false;
                this.tempfollowing.setFollowingstr(followingcollector.replace("\n", "").trim());
                this.newChar.addFollowingWord(this.tempfollowing);
                followingcollector="";this.following=false;break;
            case Tags.MAPENTRY:
            case Tags.DICTENTRY: this.mapentry=false;this.newChar.setCharacter(this.charcollector.replace("\n", "").trim());
                this.lengthOfWordsInCorpus+=this.newChar.length();
                for(Transliteration translit:this.newChar.getTransliterationSet()){
                    this.translitToCuneiMap.put(translit.toString(),this.newChar.getCharacter());
                    this.transcriptToCuneiMap.put(TranscriptionMethods.translitTotranscript(translit.toString()),this.newChar.getCharacter());
                }
            this.resultmap.put(this.newChar.getCharacter(), this.newChar);
            //System.out.println(this.newChar);
            break;
            default:
        }
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        switch(qName) {
            case Tags.DICTENTRIES:
                if(attributes.getValue(Tags.NUMBEROFWORDS)!=null)
                    this.dictHandler.setAmountOfWordsInCorpus(Double.valueOf(attributes.getValue(Tags.NUMBEROFWORDS)));
                if(attributes.getValue(Tags.AVGWORDLENGTH)!=null)
                    this.dictHandler.setAvgWordLength(Double.valueOf(attributes.getValue(Tags.AVGWORDLENGTH)));
                break;
            case Tags.MAPENTRY:
            case Tags.DICTENTRY:
                this.mapentry = true;
                this.charcollector = "";
                switch (chartype) {
                    case AKKADIAN:
                        this.newChar = new AkkadChar("");
                        break;
                    case HITTITECHAR:
                        this.newChar = new HittiteChar("");
                        break;
                    case SUMERIANCHAR:
                        this.newChar = new SumerianChar("");
                        break;
                    default:
                        this.newChar = new AkkadChar("");
                }
                this.newChar.setPhonogram(Boolean.valueOf(attributes.getValue(Tags.PHONO.toString())));
                this.newChar.setDeterminative(Boolean.valueOf(attributes.getValue(Tags.DETERMINATIVE.toString())));
                this.newChar.setLogograph(Boolean.valueOf(attributes.getValue(Tags.LOGO.toString())));
                this.newChar.setLeftaccessorvariety(Double.valueOf(attributes.getValue(Tags.LEFTACCVAR.toString())));
                this.newChar.setRightaccessorvariety(Double.valueOf(attributes.getValue(Tags.RIGHTACCVAR.toString())));
                if (this.mapOrDict == Options.FILLMAP) {
                    this.occurancetemp = Double.valueOf(attributes.getValue(Tags.SINGLE.toString()));
                    if (occurancetemp > 0) {
                        newChar.setSingleOccurance(occurancetemp);
                        newChar.setSingleCharacter(true);
                    } else {
                        newChar.setEndingCharacter(false);
                    };
                    this.occurancetemp = Double.valueOf(attributes.getValue(Tags.BEGIN.toString()));
                    if (occurancetemp > 0) {
                        newChar.setBeginOccurance(occurancetemp);
                        newChar.setBeginningCharacter(true);
                    } else {
                        newChar.setBeginningCharacter(false);
                    }
                    this.occurancetemp = Double.valueOf(attributes.getValue(Tags.MIDDLE.toString()));
                    if (occurancetemp > 0) {
                        newChar.setMiddleOccurance(occurancetemp);
                        newChar.setMiddleCharacter(true);
                    } else {
                        newChar.setMiddleCharacter(false);
                    }
                    this.occurancetemp = Double.valueOf(attributes.getValue(Tags.END.toString()));
                    if (occurancetemp > 0) {
                        newChar.setEndOccurance(occurancetemp);
                        newChar.setEndingCharacter(true);
                    } else {
                        newChar.setEndingCharacter(false);
                    }

                }
                if(!attributes.getValue(Tags.ABSOCC.toString()).isEmpty())
                this.newChar.setAbsOccurance(Double.valueOf(attributes.getValue(Tags.ABSOCC.toString())));
                //System.out.println("RelativeOccurance: "+attributes.getValue(Tags.RELOCC.toString()));
                if(!attributes.getValue(Tags.RELOCC.toString()).isEmpty())
                this.newChar.setRelativeOccuranceFromDict(Double.valueOf(attributes.getValue(Tags.RELOCC.toString())));
                //System.out.println("RelativeOccurance: "+this.newChar.getRelativeOccurance());
                break;
            case Tags.TRANSLITERATION:
                this.temptranslit = new Transliteration("", "");
                this.temptranslit.setAbsoluteOccurance(Double.valueOf(attributes.getValue(Tags.ABSOCC.toString())));
                //System.out.println("RelativeOccurance: "+attributes.getValue(Tags.RELOCC.toString()));
                this.temptranslit.setRelativeOccuranceFromDict(Double.valueOf(attributes.getValue(Tags.RELOCC.toString())));
                //System.out.println("RelativeOccurance: "+this.temptranslit.getRelativeOccurance());
                if (this.mapOrDict == Options.FILLMAP) {
                    Double temp;
                    temp = Double.valueOf(attributes.getValue(Tags.BEGIN.toString()));
                    this.temptranslit.setBeginTransliteration(Boolean.valueOf(attributes.getValue(Tags.BEGIN.toString())), temp.intValue());
                    temp = Double.valueOf(attributes.getValue(Tags.MIDDLE.toString()));
                    this.temptranslit.setMiddleTransliteration(Boolean.valueOf(attributes.getValue(Tags.MIDDLE.toString())), temp.intValue());
                    temp = Double.valueOf(attributes.getValue(Tags.END.toString()));
                    this.temptranslit.setEndTransliteration(Boolean.valueOf(attributes.getValue(Tags.END.toString())), temp.intValue());
                    temp = Double.valueOf(attributes.getValue(Tags.SINGLE.toString()));
                    this.temptranslit.setSingleTransliteration(Boolean.valueOf(attributes.getValue(Tags.SINGLE.toString())), temp.intValue());
                }
                this.temptranslit.setIsWord(Boolean.valueOf(attributes.getValue(Tags.ISWORD.toString())));
                this.temptranslit.setTranscription(attributes.getValue(Tags.TRANSCRIPTION.toString()));

                this.transliteration=true;break;
            case Tags.TRANSLATION:
                this.temptranslat=new Translation("", new Locale(attributes.getValue(Tags.LOCALE.toString())));
                this.translation=true;break;
            case Tags.FOLLOWING: this.following=true;
                this.tempfollowing=new Following();
                this.tempfollowing.setFollowing((attributes.getValue(Tags.ABSOCC.toString())!=null?Double.valueOf(attributes.getValue(Tags.ABSOCC.toString())):0.0),(attributes.getValue(Tags.ABSBD.toString())!=null?Double.valueOf(attributes.getValue(Tags.ABSBD.toString())):0.0));
                break;
            case Tags.PRECEDING: this.preceding=true;
                if(attributes.getValue(Tags.ABSOCC.toString())!=null){
                    this.precedingtemp=Double.valueOf(attributes.getValue(Tags.ABSOCC.toString()));
                }
                if(attributes.getValue(Tags.ABSBD.toString())!=null)
                    this.precedingtemp2=Double.valueOf(attributes.getValue(Tags.ABSBD.toString()));
                break;
            default:

        }

    }


}
