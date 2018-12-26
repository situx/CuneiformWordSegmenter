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

import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;
import de.unifrankfurt.cs.acoli.akkad.dict.chars.LangChar;
import de.unifrankfurt.cs.acoli.akkad.dict.chars.cuneiform.AkkadChar;
import de.unifrankfurt.cs.acoli.akkad.dict.chars.cuneiform.CuneiChar;
import de.unifrankfurt.cs.acoli.akkad.dict.chars.cuneiform.HittiteChar;
import de.unifrankfurt.cs.acoli.akkad.dict.chars.cuneiform.SumerianChar;
import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.dict.pos.POSTagger;
import de.unifrankfurt.cs.acoli.akkad.dict.utils.StopChar;
import de.unifrankfurt.cs.acoli.akkad.dict.utils.Transliteration;
import de.unifrankfurt.cs.acoli.akkad.methods.transcription.TranscriptionMethods;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.TranslationMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.TransliterationMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Tags;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: timo
 * Date: 26.10.13
 * Time: 14:36
 * DictHandler fo cuneiform dictionaries.
 */
public abstract class CuneiDictHandler extends DictHandling {
    public Map<String,CuneiChar> dictionary;
    protected Map<String,CuneiChar> dictmap;
    protected Map<String,CuneiChar> reversedictionary;
    /**Map for words with transcript without cuneiform match so far.*/
    protected Map<String,CuneiChar> transcriptToNonCunei;
    /**Saves the last processed word or character.*/
    CuneiChar lastword=null,lastlastword=null;
    /**
     * Constructor for this abstract class.
     */
    public CuneiDictHandler(List<String> stopchars,CharTypes  charType,POSTagger posTagger){
        super(stopchars,charType,posTagger);
        this.dictionary=new TreeMap<String,CuneiChar>();
        this.reversedictionary=new TreeMap<>();
        this.dictmap=new TreeMap<>();
        this.transcriptToNonCunei=new TreeMap<>();
        this.amountOfWordsInCorpus=0.;
    }

    @Override
    public void addChar(final LangChar character) {
        CuneiChar cnChar=(CuneiChar)character;
        if(!this.dictmap.containsKey(character.getCharacter())){
            this.dictmap.put(cnChar.getCharacter(),cnChar);
        }
        for(Transliteration trans:cnChar.getTransliterationSet()){
            if(!this.translitToCharMap.containsKey(trans.getTransliteration())){
                this.translitToCharMap.put(trans.getTransliteration(),cnChar.getCharacter());
            }
        }
        this.amountOfCharsInCorpus++;
    }

    /**
     * Adds a word or char following the current word or char.
     * @param word the current word or char
     * @param following the following word or char
     */
    public void addFollowingChar(final String word,final String following){
        if(!" ".equals(word) && this.dictmap.get(word)!=null)
            this.dictmap.get(word).addFollowingWord(following);
    }

    /**
     * Adds a word or char following the current word or char.
     * @param word the current word or char
     * @param following the following word or char
     */
    public void addFollowingWord(final String word,final String following){
        if(!" ".equals(word) && this.dictionary.get(word)!=null)
            this.dictionary.get(word).addFollowingWord(following);
    }

    /**
     * Adds a word or char following the current word or char.
     * @param word the current word or char
     * @param following the following word or char
     */
    public void addFollowingWord(final String word,final String following,final String preceding){
        if(!" ".equals(word) && this.dictionary.get(word)!=null)
            this.dictionary.get(word).addFollowingWord(following,preceding);
    }

    @Override
    public void addTranscriptNonCunei(final String transcription, final LangChar word) {
        this.transcriptToNonCunei.put(transcription,(CuneiChar)word);
    }

    @Override
    public void addWord(final LangChar word,CharTypes charType){
        CuneiChar word2=(CuneiChar)word;
        final Integer charlength=word2.getCharlength();
        AkkadChar reverseword=new AkkadChar(new StringBuffer(word.getCharacter()).reverse().toString());
        reverseword.setDeterminative(word2.getDeterminative());
        reverseword.setLogograph(word2.getLogograph());
        reverseword.setPhonogram(word2.getPhonogram());
        reverseword.setSumerogram(word2.getSumerogram());
        reverseword.setIsNumberChar(word2.getIsNumberChar());
        reverseword.setCharacter(new StringBuffer(word2.getCharacter()).reverse().toString());
        for(Transliteration trans:word2.getTransliterationSet()){
            String reversed=this.reverseTransliteration(trans.getTransliteration(), CharTypes.CUNEICHAR.getSplitcriterion());
            reverseword.addTransliteration(new Transliteration(reversed, TranscriptionMethods.translitTotranscript(reversed)));
            this.reverseTranslitToWordDict.put(reversed,reverseword.getCharacter());
            this.reverseTranscriptToWordDict.put(TranscriptionMethods.translitTotranscript(reversed),reverseword.getCharacter());
        }
        this.reversedictionary.put(reverseword.getCharacter(),reverseword);
        CuneiChar templangchar=null,oldtemplangchar=null;
        CuneiChar tempchar;
        String[] tempcharsplit;
        this.amountOfWordsInCorpus++;
        this.lengthOfWordsInCorpus+=word2.length();
        this.amountOfWordTranslitsInCorpus++;
        if(this.dictionary.containsKey(word.getCharacter())){
            this.dictionary.get(word.getCharacter()).addOccurance();
            //System.out.println("Occurance of "+word2.getCharacter()+": "+this.dictionary.get(word2.getCharacter()).getOccurances());
            String cuneiword=word.getCharacter();
            if(cuneiword.length()>charlength){
                tempchar= (CuneiChar)translitToChar(cuneiword.substring(0, charlength));
                if(tempchar!=null){
                    templangchar=tempchar;
                    templangchar.addBeginOccurance();
                    if(this.lastword!=null) {
                        if(lastlastword!=null){
                            this.lastword.addFollowingWord(templangchar.getCharacter(),lastlastword.getCharacter(), false,2);
                        }else{
                            this.lastword.addFollowingWord(templangchar.getCharacter(), false);
                        }
                        templangchar.addPrecedingWord(this.lastword.getCharacter());
                    }
                    this.continuationFollowsBoundary++;
                }
                lastlastword=lastword;
                this.lastword=templangchar;
                for(int i=charlength;i<cuneiword.length()-charlength;i+=charlength){

                    tempchar= (CuneiChar)translitToChar(cuneiword.substring(i, i + charlength));

                    if(tempchar!=null){
                        templangchar=(AkkadChar)tempchar;
                        templangchar.addMiddleOccurance(i/charlength);
                        if(this.lastword!=null) {
                            if(lastlastword!=null){
                                this.lastword.addFollowingWord(templangchar.getCharacter(),lastlastword.getCharacter(), false,3);
                            }else{
                                this.lastword.addFollowingWord(templangchar.getCharacter(), false);
                            }
                            templangchar.addPrecedingWord(this.lastword.getCharacter());
                        }
                        this.continuationFollowsContinuation++;
                    }
                    lastlastword=lastword;
                    this.lastword=templangchar;
                }
                tempchar= (CuneiChar)translitToChar(cuneiword.substring(cuneiword.length() - charlength));

                if(tempchar!=null){
                    templangchar=tempchar;
                    templangchar.addEndOccurance(templangchar.length()-charlength);
                    if(this.lastword!=null) {
                        if(lastlastword!=null){
                            this.lastword.addFollowingWord(templangchar.getCharacter(),lastlastword.getCharacter(), true,1);
                        }else{
                            this.lastword.addFollowingWord(templangchar.getCharacter(), true);
                        }
                        templangchar.addPrecedingWord(this.lastword.getCharacter());
                    }
                    lastlastword=lastword;
                    this.lastword=templangchar;
                    this.boundariesFollowContinuations++;
                }
            }else if(cuneiword.length()==charlength){
                tempchar= (CuneiChar)translitToChar(cuneiword.substring(0, charlength));

                if(tempchar!=null){
                    templangchar=tempchar;
                    templangchar.addSingleOccurance();
                    if(this.lastword!=null) {
                        if(lastlastword!=null){
                            this.lastword.addFollowingWord(templangchar.getCharacter(),lastlastword.getCharacter(), true,0);
                        }else{
                            this.lastword.addFollowingWord(templangchar.getCharacter(), true);
                        }
                        templangchar.addPrecedingWord(this.lastword.getCharacter());
                    }
                    this.boundariesFollowBoundaries++;
                }
            }
        }else{
            this.dictionary.put(word.getCharacter(), word2);
            this.dictionary.put(reverseword.getCharacter(),reverseword);
            //System.out.println("ADD WORD: " + word2.getCharacter());
            //System.out.println("ADD REVERSEWORD: " + reverseword);
            for(Transliteration trans:word2.getTransliterationSet()){
                this.translitToWordDict.put(trans.getTransliteration(), word2.getCharacter());
                this.transcriptToWordDict.put(TranscriptionMethods.translitTotranscript(trans.getTransliteration()), word2.getCharacter());
            }

            tempcharsplit=word.getTransliterationSet().iterator().next().toString().split("-");
            tempchar=(CuneiChar)word;
            if(tempchar.length().equals(charlength)){
                if(!this.dictmap.containsKey(tempchar.getCharacter())){
                    this.addChar(word2);
                }
                tempchar.setSingleCharacter(true);
                for(Transliteration translit:tempchar.getTransliterationSet()){
                    if(tempchar.getTransliterationSet().contains(translit)){
                        for(Transliteration trans:tempchar.getTransliterationSet()){
                            if(trans.toString().equals(translit.getTransliterationString())){
                                trans.setSingleTransliteration(true,0);
                                tempchar.addSingleOccurance();
                                if(lastword!=null){
                                    if(lastlastword!=null){
                                        this.lastword.addFollowingWord(tempchar.getCharacter(),lastlastword.getCharacter(), false,0);
                                    }else{
                                        this.lastword.addFollowingWord(tempchar.getCharacter(), false);
                                    }
                                    tempchar.addPrecedingWord(this.lastword.getCharacter());
                                }
                                this.continuationFollowsBoundary++;
                            }
                        }
                        translit.setSingleTransliteration(true,0);
                        this.dictmap.get(tempchar.getCharacter()).addSingleOccurance();
                        this.boundariesFollowBoundaries++;
                    }
                /*for(Transliteration translit:word2.getTransliterationSet()){
                    if(tempchar.getTransliterationSet().contains(translit)){
                        CuneiChar testchar=(CuneiChar)this.dictmap.get(tempchar.getCharacter());
                        for(Transliteration trans:testchar.getTransliterationSet()){
                            if(trans.toString().equals(translit.getTransliterationString())){
                               trans.setSingleTransliteration(true,0);
                               this.dictmap.get(tempchar.getCharacter()).addSingleOccurance();
                               this.boundariesFollowBoundaries++;
                            }
                        }
                        testchar.addTransliteration(translit);
                    }*/
                }
            }else if(tempchar.length()>charlength){
                if(!this.dictmap.containsKey(tempchar.getCharacter().substring(0,charlength)) || this.dictmap.get(tempchar.getCharacter().substring(0,charlength)).getTransliterationSet().isEmpty()){
                    CuneiChar addchar=this.createCorrectCharType(tempchar.getCharacter().substring(0,charlength),charType);
                    String[] split=word2.getTransliterationSet().iterator().next().getTransliteration().split(CharTypes.AKKADIAN.getSplitcriterion());

                    /*System.out.print("Split: ");
                    ArffHandler.arrayToStr(split);*/
                    if(split.length>1)
                        addchar.addTransliteration(new Transliteration(split[0],split[0]));
                    addchar.setIsNumberChar(word2.getIsNumberChar());
                    this.addChar(addchar);
                    templangchar=addchar;
                }else{
                    templangchar=this.dictmap.get(tempchar.getCharacter().substring(0,charlength));
                }
                //templangchar=(AkkadChar)this.dictmap.get(tempchar.getCharacter().substring(0,charlength));
                if(templangchar!=null){
                    templangchar.setBeginningCharacter(true);
                    for(Transliteration translit:templangchar.getTransliterationSet()){
                        if(templangchar.getTransliterationSet().contains(translit)){
                            for(Transliteration trans:templangchar.getTransliterationSet()){
                                if(trans.toString().equals(translit.getTransliterationString())){
                                    trans.setBeginTransliteration(true,0);
                                    templangchar.addBeginOccurance();
                                    if(lastword!=null){
                                        if(lastlastword!=null){
                                            this.lastword.addFollowingWord(templangchar.getCharacter(),lastlastword.getCharacter(), false,2);
                                        }else{
                                            this.lastword.addFollowingWord(templangchar.getCharacter(), false);
                                        }
                                        templangchar.addPrecedingWord(this.lastword.getCharacter());
                                    }
                                    this.continuationFollowsBoundary++;
                                }
                            }
                            templangchar.addTransliteration(translit);
                            lastlastword=lastword;
                            this.lastword=templangchar;
                        }
                    }

                }
                for(int i=charlength;i<tempchar.length()-charlength;i+=charlength){
                    if(!this.dictmap.containsKey(tempchar.getCharacter().substring(i,i+charlength)) || this.dictmap.get(tempchar.getCharacter().substring(i,i+charlength)).getTransliterationSet().isEmpty()){
                        CuneiChar addchar=this.createCorrectCharType(tempchar.getCharacter().substring(i,i+charlength),charType);
                        String[] split=word2.getTransliterationSet().iterator().next().getTransliteration().split(CharTypes.AKKADIAN.getSplitcriterion());
                        /*System.out.print("Split: ");
                        ArffHandler.arrayToStr(split);*/
                        if(split.length>1)
                            addchar.addTransliteration(new Transliteration(split[(i/charlength)-1],split[(i/charlength)-1]));
                        this.addChar(addchar);
                        //System.out.println("Add: "+tempchar.substring(i,i+charlength));

                    }
                    templangchar=this.dictmap.get(tempchar.getCharacter().substring(i,i+charlength));
                    if(templangchar!=null){
                        templangchar.setMiddleCharacter(true);
                        for(Transliteration translit:templangchar.getTransliterationSet()){
                            if(templangchar.getTransliterationSet().contains(translit)){
                                for(Transliteration trans:templangchar.getTransliterationSet()){
                                    if(trans.toString().equals(translit.getTransliterationString())){
                                        trans.setMiddleTransliteration(true,i);
                                        templangchar.addMiddleOccurance(i);
                                        this.continuationFollowsContinuation++;
                                        if(lastword!=null) {
                                            if(lastlastword!=null){
                                                this.lastword.addFollowingWord(templangchar.getCharacter(),lastlastword.getCharacter(), false,3);
                                            }else{
                                                this.lastword.addFollowingWord(templangchar.getCharacter(), false);
                                            }
                                            templangchar.addPrecedingWord(this.lastword.getCharacter());
                                        }
                                    }
                                }
                                templangchar.addTransliteration(translit);
                                lastlastword=lastword;
                                this.lastword=templangchar;
                            }
                        }
                    }

                }
                if(!this.dictmap.containsKey(tempchar.getCharacter().substring(tempchar.length()-charlength)) || this.dictmap.get(tempchar.getCharacter().substring(tempchar.length()-charlength)).getTransliterationSet().isEmpty()){
                    CuneiChar addchar=this.createCorrectCharType(tempchar.getCharacter().substring(tempchar.length()-charlength),charType);
                    String[] split=word2.getTransliterationSet().iterator().next().getTransliteration().split(CharTypes.AKKADIAN.getSplitcriterion());
                    /*System.out.print("Split: ");
                    ArffHandler.arrayToStr(split);*/
                    if(split.length>1)
                        addchar.addTransliteration(new Transliteration(split[split.length-1],split[split.length-1]));
                    this.addChar(addchar);
                }
                templangchar=this.dictmap.get(tempchar.getCharacter().substring(tempchar.length()-charlength));
                if(templangchar!=null){
                    templangchar.setEndingCharacter(true);
                    for(Transliteration translit:templangchar.getTransliterationSet()){
                        if(templangchar.getTransliterationSet().contains(translit)){
                            for(Transliteration trans:templangchar.getTransliterationSet()){
                                if(trans.toString().equals(translit.getTransliterationString())){
                                    trans.setEndTransliteration(true,tempchar.length()-charlength);
                                    templangchar.addEndOccurance(tempchar.length()-charlength);
                                    this.boundariesFollowContinuations++;
                                    if(lastword!=null) {
                                        if(lastlastword!=null){
                                            this.lastword.addFollowingWord(templangchar.getCharacter(),lastlastword.getCharacter(), true,1);
                                        }else{
                                            this.lastword.addFollowingWord(templangchar.getCharacter(), true);
                                        }
                                        templangchar.addPrecedingWord(this.lastword.getCharacter());
                                    }
                                }
                            }
                            templangchar.addTransliteration(translit);
                            lastlastword=lastword;
                            this.lastword=templangchar;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void calculateRelativeCharOccurances(final Double charsInCorpus){
        for(CuneiChar cunei:this.dictmap.values()){
            cunei.setRelativeOccurance(charsInCorpus);
            for(Transliteration translit:cunei.getTransliterationSet()){
                translit.setRelativeOccurance(this.amountOfCharTranslitsInCorpus);
            }
        }
    }

    @Override
    public void calculateRelativeWordOccurances(final Double wordsInCorpus) {
        for(CuneiChar cunei:this.dictionary.values()){
            cunei.setRelativeOccurance(wordsInCorpus);
            for(Transliteration translit:cunei.getTransliterationSet()){
                translit.setRelativeOccurance(this.amountOfWordTranslitsInCorpus);
            }
        }
    }

    @Override
    public void calculateRightLeftAccessorVariety() {
        for(CuneiChar curchar:this.dictmap.values()){
            curchar.calculateLeftAccessorVariety();
            curchar.calculateRightAccessorVariety();
        }
        for(CuneiChar curchar:this.dictionary.values()){
            curchar.calculateLeftAccessorVariety();
            curchar.calculateRightAccessorVariety();
        }
        for(CuneiChar curchar:this.reversedictionary.values()){
            curchar.calculateLeftAccessorVariety();
            curchar.calculateRightAccessorVariety();
        }
        for(StopChar curchar:this.stopchars.values()){
            curchar.calculateLeftPunctuationVariety(CharTypes.CUNEICHAR);
            curchar.calculateRightPunctuationVariety(CharTypes.CUNEICHAR);
        }
    }

    protected Boolean checkForNumberChar(final String cuneiform){
        String currentchar="";
        for(int i=0;i<cuneiform.length()-chartype.getChar_length();i+=chartype.getChar_length()){
            //System.out.println("Currentchar: "+currentchar);
           // System.out.println("Currentchar.equals(substring)?: "+currentchar+" - "+cuneiform.substring(i,i+chartype.getChar_length())+" - "+currentchar.equals(cuneiform.substring(i,i+chartype.getChar_length())));
            if(currentchar.isEmpty()){
                currentchar=cuneiform.substring(i,i+chartype.getChar_length());
                //System.out.println("Currentchar in dict?: "+this.dictmap.get(currentchar)+" IsNumberChar? "+this.dictmap.get(currentchar).getIsNumberChar());
                if((this.dictmap.get(currentchar)==null || !this.dictmap.get(currentchar).getIsNumberChar()) && (this.dictionary.get(currentchar)==null || !this.dictionary.get(currentchar).getIsNumberChar())){
                    return false;
                }
            }else if(!currentchar.equals(cuneiform.substring(i,i+chartype.getChar_length()))){
                return false;
            }
        }
        return true;
    }

    public CuneiChar createCorrectCharType(final String character,CharTypes chartype){
        CuneiChar result;
        switch (chartype){
            case AKKADIAN:result=new AkkadChar(character);break;
            case HITTITECHAR: result=new HittiteChar(character);break;
            case SUMERIANCHAR:result=new SumerianChar(character);break;
            default: result=new AkkadChar(character);break;
        }
        return result;
    }

    @Override
    public void exportToXML(final String dictpath,final String reversedictpath, final String mappath,final String ngrampath)throws XMLStreamException, IOException {
        XMLOutputFactory output = XMLOutputFactory.newInstance();
        output.setProperty(Tags.ESCAPECHARACTERS.toString(), false);
        OutputStreamWriter outwriter=new OutputStreamWriter(new FileOutputStream(dictpath), Tags.UTF8.toString());
        XMLStreamWriter writer = new IndentingXMLStreamWriter(output.createXMLStreamWriter(outwriter));
        writer.writeStartDocument(Tags.UTF8.toString(),Tags.XMLVERSION.toString());
        //writer.writeCharacters("\n");
        writer.writeStartElement(Tags.DICTENTRIES.toString());
        writer.writeAttribute(Tags.NUMBEROFWORDS,this.amountOfWordsInCorpus.toString());
        writer.writeAttribute(Tags.NUMBEROFCHARS,this.amountOfCharTranslitsInCorpus.toString());
        writer.writeAttribute(Tags.NUMBEROFWORDTRANSLITS,this.amountOfWordTranslitsInCorpus.toString());
        writer.writeAttribute(Tags.NUMBEROFCHARTRANSLITS,this.amountOfCharTranslitsInCorpus.toString());
        writer.writeAttribute(Tags.AVGWORDLENGTH,this.averageWordLength.toString());
        writer.writeCharacters("\n");
        for(CuneiChar akkadchar:this.dictionary.values()){
            writer.writeCharacters(akkadchar.toXML(Tags.DICTENTRY)+System.lineSeparator());
        }
        writer.writeStartElement(Tags.STOPCHARS);
        for(StopChar stopchar:this.stopchars.values()){
            writer.writeCharacters(stopchar.toXML());
        }
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.close();
        XMLOutputFactory output3 = XMLOutputFactory.newInstance();
        output3.setProperty(Tags.ESCAPECHARACTERS.toString(), false);
        OutputStreamWriter outwriter3=new OutputStreamWriter(new FileOutputStream(reversedictpath), Tags.UTF8.toString());
        XMLStreamWriter writer3 = new IndentingXMLStreamWriter(output.createXMLStreamWriter(outwriter3));
        writer3.writeStartDocument(Tags.UTF8.toString(), Tags.XMLVERSION.toString());
        writer3.writeStartElement(Tags.DICTENTRIES.toString());
        System.out.println("Words: "+this.amountOfWordsInCorpus.toString());
        writer3.writeAttribute(Tags.NUMBEROFWORDS, this.amountOfWordsInCorpus.toString());
        writer3.writeAttribute(Tags.NUMBEROFCHARS,this.amountOfCharTranslitsInCorpus.toString());
        writer3.writeAttribute(Tags.NUMBEROFWORDTRANSLITS,this.amountOfWordTranslitsInCorpus.toString());
        writer3.writeAttribute(Tags.NUMBEROFCHARTRANSLITS,this.amountOfCharTranslitsInCorpus.toString());
        writer3.writeAttribute(Tags.AVGWORDLENGTH,this.averageWordLength.toString());
        writer3.writeCharacters(System.lineSeparator());
        for(CuneiChar akkadchar:this.reversedictionary.values()){
            writer3.writeCharacters(akkadchar.toXML(Tags.DICTENTRY)+System.lineSeparator());
        }
        writer3.writeStartElement(Tags.STOPCHARS.toString());
        for(StopChar stopchar:this.stopchars.values()){
            writer3.writeCharacters(stopchar.toXML());
        }
        writer3.writeEndElement();
        writer3.writeEndElement();
        writer3.writeEndDocument();
        writer3.close();
        XMLOutputFactory output2 = XMLOutputFactory.newInstance();
        output2.setProperty(Tags.ESCAPECHARACTERS.toString(), false);
        OutputStreamWriter outwriter2=new OutputStreamWriter(new FileOutputStream(mappath), Tags.UTF8.toString());
        XMLStreamWriter writer2 = new IndentingXMLStreamWriter(output.createXMLStreamWriter(outwriter2));
        writer2.writeStartDocument(Tags.UTF8.toString(),Tags.XMLVERSION.toString());
        writer2.writeCharacters(System.lineSeparator());
        writer2.writeStartElement(Tags.MAPENTRIES.toString());
        writer2.writeAttribute(Tags.NUMBEROFWORDS,this.amountOfWordsInCorpus.toString());
        writer2.writeAttribute(Tags.NUMBEROFCHARS,this.amountOfCharTranslitsInCorpus.toString());
        writer2.writeAttribute(Tags.NUMBEROFWORDTRANSLITS,this.amountOfWordTranslitsInCorpus.toString());
        writer2.writeAttribute(Tags.NUMBEROFCHARTRANSLITS,this.amountOfCharTranslitsInCorpus.toString());
        writer2.writeCharacters(System.lineSeparator());
        for(CuneiChar akkadchar:this.dictmap.values()){
            writer2.writeCharacters(akkadchar.toXML(Tags.MAPENTRY)+System.lineSeparator());
        }
        writer2.writeStartElement(Tags.STOPCHARS);
        for(StopChar stopchar:this.stopchars.values()){
            writer2.writeCharacters(stopchar.toXML());
        }
        writer2.writeEndElement();
        writer2.writeEndElement();
        writer2.writeEndDocument();
        writer2.close();
        XMLOutputFactory output4 = XMLOutputFactory.newInstance();
        output4.setProperty(Tags.ESCAPECHARACTERS.toString(), false);
        OutputStreamWriter outwriter4=new OutputStreamWriter(new FileOutputStream(ngrampath), Tags.UTF8.toString());
        XMLStreamWriter writer4 = new IndentingXMLStreamWriter(output.createXMLStreamWriter(outwriter4));
        writer4.writeStartDocument(Tags.UTF8.toString(),Tags.XMLVERSION.toString());
        writer4.writeCharacters(System.lineSeparator());
        writer4.writeStartElement(Tags.NGRAMS.toString());
        writer4.writeCharacters(System.lineSeparator());
        writer4.writeCharacters(this.ngrams.toXML());
        writer4.writeEndElement();
        writer4.writeEndDocument();
        writer4.close();
    }

    @Override
    public List<String> getAlternativeWritings() {
        return null;
    }

    /**
     * Gets the boundary-follows-boundary score.
     * @return the score as double
     */
    @Override
    public Double getBoundariesFollowBoundaries(){
        return this.boundariesFollowBoundaries;
    }

    /**
     * Gets the boundary-follows-continuation score.
     * @return the score as double
     */
    @Override
    public Double getBoundariesFollowContinuations(){
        return this.boundariesFollowContinuations;
    }

    @Override
    public Set<String> getCandidatesForChar(final String character){
        Set<String> candidates=new TreeSet<>();
        for(String key:this.dictionary.keySet()){
            if(key.startsWith(character)){
                candidates.add(key);
            }
        }
        return candidates;
    }

    /**
     * Gets the continuation-follows-boundary score.
     * @return the score as double
     */
    public Double getContinuationFollowsBoundary(){
        return this.continuationFollowsBoundary;
    }

    /**
     * Gets the continuation-follows-continuation score.
     * @return the score as double
     */
    public Double getContinuationFollowsContinuation(){
        return this.continuationFollowsContinuation;
    }

    public Map<String, ? extends LangChar> getDictMap() {
        return dictmap;
    }

    /**
     * Matches a given string with the dictionary
     * @param word2 the word to match
     * @param transliterationMethod the transliteration method to use
     * @return
     */
    @Override
    public String getDictTransliteration(final LangChar word2,final TransliterationMethod transliterationMethod){
        CuneiChar word=(CuneiChar)word2;
        if(word==null){
            return null;
        }
        switch (transliterationMethod){
            case FIRST:return word.getTransliterationSet().isEmpty()?"":word.getTransliterationSet().iterator().next().getTransliteration();
            case MAXPROB: return word.getMostProbableWordTransliteration().getTransliteration();
            case RANDOM:return word.getRandomWordTransliteration().getTransliteration();
            default: return null;
        }
}

    /**
     * Gets the current dictionary to be used.
     * @return the dictionary
     */
    public Map<String, ? extends LangChar> getDictionary() {
        return dictionary;
    }

    @Override
    public Map<Double, Set<String>> getFreqCandidatesForChar(final String character) {
        Map<Double,Set<String>> candidates=new TreeMap<>();
        for(String key:this.dictionary.keySet()){
            if(key.startsWith(character)){
                if(candidates.get(this.dictionary.get(key).getOccurances())==null){
                    candidates.put(this.dictionary.get(key).getOccurances(),new TreeSet<String>());
                }
                candidates.get(this.dictionary.get(key).getOccurances()).add(key);
            }
        }
        return candidates;
    }

    @Override
    public String getNoDictTranslation(final String word, final TranslationMethod translationMethod, final Locale locale) {
        return word;
    }

    /**
     * Generates a transliteration that cannot be found in the dictionary by using probabilistic models.
     * @param word  the word to match
     * @param transliterationMethod the transliteration method to choose
     * @return the transliteration as String
     */
    @Override
    public String getNoDictTransliteration(final String word,final TransliterationMethod transliterationMethod){
          int charlength=this.chartype.getChar_length();
          System.out.println("NoDict Word: "+word);
          String curchar,result="[";
          if(word.length()<charlength){
              return " ";
          }else if(word.length()==charlength){
              if(dictmap.containsKey(word) && !dictmap.get(word).getTransliterationSet().isEmpty()) {
                  switch (transliterationMethod) {
                      case MAXPROB:
                          return "[" + this.dictmap.get(word).getMostProbableBeginTransliteration(0).getTransliteration() + "]";
                      case RANDOM:
                      case FIRST:
                      default:
                          return "[" + this.dictmap.get(word).getFirstSingleTransliteration().getTransliteration() + "]";
                  }
              }else{
                  return "[]";
              }
          }
          switch(transliterationMethod){
              case MAXPROB:
                  for(int i=0;i<word.length()-1;i+=charlength){
                      curchar=word.substring(i,i+charlength);
                      if(i==0 && this.dictmap.get(curchar)!=null){
                          result+=this.dictmap.get(curchar).getMostProbableBeginTransliteration(i)+"-";
                      }else if(i<word.length()-charlength && this.dictmap.get(curchar)!=null){
                          result+=this.dictmap.get(curchar).getMostProbableMiddleTransliteration(i)+"-";
                      }else if(i==word.length()-charlength && this.dictmap.get(curchar)!=null){
                          result+=this.dictmap.get(curchar).getMostProbableEndTransliteration(i)+"] ";
                      }
                  }
                  break;
              case RANDOM:for(int i=0;i<word.length()-1;i+=charlength){
                  curchar=word.substring(i,i+charlength);
                  if(i==0 && this.dictmap.get(curchar)!=null){
                      result+=this.dictmap.get(curchar).getRandomWordTransliteration()+"-";
                  }else if(i<word.length()-charlength && this.dictmap.get(curchar)!=null){
                      result+=this.dictmap.get(curchar).getRandomWordTransliteration()+"-";
                  }else if(i==word.length()-charlength && this.dictmap.get(curchar)!=null){
                      result+=this.dictmap.get(curchar).getRandomWordTransliteration()+"] ";
                  }
              }break;
              case FIRST:
              default:for(int i=0;i<word.length()-1;i+=charlength){
                  curchar=word.substring(i,i+charlength);
                  if(i==0 && this.dictmap.get(curchar)!=null){
                      result+=this.dictmap.get(curchar).getFirstBeginningTransliteration()+"-";
                  }else if(i<word.length()-charlength && this.dictmap.get(curchar)!=null){
                      result+=this.dictmap.get(curchar).getFirstMiddleTransliteration()+"-";
                  }else if(i==word.length()-charlength && this.dictmap.get(curchar)!=null){
                      result+=this.dictmap.get(curchar).getFirstEndTransliteration()+"] ";
                  }
              }
          }
          return result;
    }

    /**
     * Gets reverse candidates for the given char.
     * @param character the character to investigate
     * @return the set of candidates
     */
    public Set<String> getReverseCandidatesForChar(final String character){
        //System.out.println(this.reversedictionary.toString());
        Set<String> candidates=new TreeSet<>();
        for(String key:this.reversedictionary.keySet()){
            if(key.endsWith(character)){
                candidates.add(key);
            }
        }
        return candidates;
    }

    @Override
    public abstract void importMappingFromXML(final String filepath)throws ParserConfigurationException, SAXException, IOException;

    @Override
    public void importNGramsFromXML(final String s) throws ParserConfigurationException, SAXException, IOException {

    }

    @Override
    public boolean isFollowingWord(final LangChar word, final String following){
        return this.dictmap.get(word.getCharacter()).getFollowingWords().containsKey(following);
    }

    @Override
    public LangChar matchChar(final String cuneiform){
        if(this.dictmap.containsKey(cuneiform)){
            return this.dictmap.get(cuneiform);
        }
        return null;
    }
    @Override
    public LangChar matchChar(final String word,CharTypes chartype){
        if(chartype==CharTypes.TRANSLITCHAR){
            return this.translitToChar(word);
        }else{
            return this.matchChar(word);
        }
    }

    @Override
    public LangChar matchReverseWord(final String word){
        if(this.reversedictionary.get(word)!=null){
            return this.reversedictionary.get(word);
        }
        return null;
    }

    @Override
    public LangChar matchWord(final String cuneiform){
        if(this.dictionary.get(cuneiform)!=null){
            return this.dictionary.get(cuneiform);
        }
        //System.out.println("MatchWordNumberChar: "+cuneiform+" - "+checkForNumberChar(cuneiform));
        if((cuneiform.length()/chartype.getChar_length()>1) && checkForNumberChar(cuneiform)){
            AkkadChar akkad=new AkkadChar(cuneiform);
            akkad.addTransliteration(new Transliteration((akkad.length()/chartype.getChar_length())+"("+dictmap.get(cuneiform.substring(0,chartype.getChar_length())).getTransliterationSet().iterator().next().getTransliteration()+")",(akkad.length()/chartype.getChar_length())+"("+cuneiform+")"));
            this.dictionary.put(cuneiform,akkad);
            this.translitToWordDict.put((akkad.length()/chartype.getChar_length())+"("+cuneiform+")",cuneiform);
            return new AkkadChar(cuneiform);
        }
        return null;
    }
    @Override
    public LangChar matchWordByTranscription(final String word,Boolean noncunei){
        if(this.transcriptToWordDict.containsKey(word)){
            return this.dictionary.get(this.transcriptToWordDict.get(word));
        }
        if(noncunei && this.transcriptToNonCunei.containsKey(word)){
            return this.transcriptToNonCunei.get(word);
        }
        return null;
    }
    @Override
    public LangChar matchWordByTransliteration(final String word){
        if(this.translitToWordDict.containsKey(word)){
            return this.dictionary.get(this.translitToWordDict.get(word));
        }
        return null;
    }

    public void morfessorExport(final String filepath) throws IOException {
        System.out.println("Morfessor Export: "+filepath);
        BufferedWriter morfessorwriter=new BufferedWriter(new FileWriter(new File(filepath)));
        for(String key:this.dictionary.keySet()){
            morfessorwriter.write(this.dictionary.get(key).getOccurances().intValue()+" "+this.dictionary.get(key).getCharacter()+"\n");
        }
        morfessorwriter.close();
    }

    /**Resets the last processed word when beginning a new line.*/
    public void newLine(){
        this.lastword=null;
    }

    /**
     * Translates a transliteration char to its cuneiform dependant.
     * @param translit the transliteration
     * @return The cuneiform character as String
     */
    @Override
    public LangChar translitToChar(final String translit){
        if(!this.translitToCharMap.containsKey(translit)){
            return null;
        }
        return this.dictmap.get(this.translitToCharMap.get(translit));
        /*System.out.println("Translit: "+translit+" "+this.translitToCharMap.toString());
        System.out.println("Dictmap: "+this.dictmap.toString());
        return this.dictmap.containsKey(this.translitToCharMap.get(translit))?:null;*/
    }

    public abstract String translitWordToCunei(CuneiChar word);

}
