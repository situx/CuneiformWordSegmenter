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

package de.unifrankfurt.cs.acoli.akkad.dict.corpusimport.importformat;

import de.unifrankfurt.cs.acoli.akkad.dict.chars.LangChar;
import de.unifrankfurt.cs.acoli.akkad.dict.chars.cuneiform.AkkadChar;
import de.unifrankfurt.cs.acoli.akkad.dict.chars.cuneiform.CuneiChar;
import de.unifrankfurt.cs.acoli.akkad.dict.chars.cuneiform.HittiteChar;
import de.unifrankfurt.cs.acoli.akkad.dict.chars.cuneiform.SumerianChar;
import de.unifrankfurt.cs.acoli.akkad.dict.corpusimport.cuneiform.CuneiCorpusHandler;
import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.cuneiform.AkkadDictHandler;
import de.unifrankfurt.cs.acoli.akkad.dict.pos.POSTagger;
import de.unifrankfurt.cs.acoli.akkad.dict.utils.Following;
import de.unifrankfurt.cs.acoli.akkad.dict.utils.Transliteration;
import de.unifrankfurt.cs.acoli.akkad.methods.transcription.TranscriptionMethods;
import de.unifrankfurt.cs.acoli.akkad.util.ArffHandler;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Tags;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by timo on 08.09.14.
 * Importer for CDLI ATF.
 */
public class ATFImporter extends CuneiCorpusHandler implements FileFormatImporter {

    /**
     * Constructor for this class.
     * @param stopchars language specific stopchars
     * @param corpusReader the reader for the atf file
     * @param reformattedTranslitWriter writer for transliterations
     * @param reformattedBoundaryWriter writer for boundaries
     * @param reformattedCuneiWriter writer for cuneiform segmented text
     * @param reformattedUsegCuneiWriter writer for cuneiform unsegmented text
     */
    public ATFImporter(final List<String> stopchars,final BufferedReader corpusReader,
                       final BufferedWriter reformattedTranslitWriter,final BufferedWriter reformattedBoundaryWriter,
                       final BufferedWriter reformattedCuneiWriter, final BufferedWriter reformattedUsegCuneiWriter
                        ,final Map<String,Integer> nomatchesmap,Set<String> sumerograms,Set<String> determinatives) {
        super(stopchars);
        this.corpusReader=corpusReader;
        this.reformattedTranslitWriter=reformattedTranslitWriter;
        this.reformattedUSegCuneiWriter=reformattedUsegCuneiWriter;
        this.reformattedCuneiWriter=reformattedCuneiWriter;
        this.reformattedBoundaryWriter=reformattedBoundaryWriter;
        this.nomatchesmap=nomatchesmap;
        this.sumerograms=sumerograms;
        this.determinatives=determinatives;
    }

    /**
     * Cleans the word string from unnneeded annotations.
     * @param word the word to be cleaned
     * @param reformat if special reformatted words are needed
     * @return the cleaned word string
     */
    public String cleanWordString(String word, final boolean reformat) {
        word = word.replace("<", "").replace(">", "").replace("[", "").replace("]", "").replace("|", "");
        word = word.replace("_", "").replace(";", "").replace("+", "").replace("?", "").replace("!", "").replace("...", "");
        word = word.replace("#", "").replace("/", "").replace("'", "").replace("\"", "").replace("“", "").replace("*", "").replace("@", "").replace("$", "").replace("%", "");
        if (!reformat)
            return word.replace("-", "").replace("{", "").replace("}", "").replace("(", "").replace(")", "");
        word = word.replace("}", "-").replace("{", "-");
        word = word.replace(")", "-").replace("(", "-");
        word = word.replace(".", "-");
        word = word.replace("--", "-");
        if (word.indexOf("-") == 0) {
            word = word.substring(1);
        }
        if (!word.isEmpty() && word.lastIndexOf("-") == word.length() - 1) {
            word = word.substring(0, word.length() - 1);
        }
        return word.toLowerCase();
    }

    /**
     * Gets the char in a char type need for processing
     * @param charTypes the current chartype
     * @return the needed char
     */
    public LangChar getChar(final CharTypes charTypes){
         switch (charTypes){
             case AKKADIAN: return new AkkadChar("");
             case SUMERIANCHAR: return new SumerianChar("");
             case HITTITECHAR: return new HittiteChar("");
             default: return new AkkadChar("");
         }
    }

    @Override
    public POSTagger getPOSTagger(Boolean newPosTagger) {
        return null;
    }

    @Override
    public DictHandling getUtilDictHandler() {
        return null;
    }

    /**
     * Importere for akkadian.
     * @param dicthandler the dicthandler to usw
     * @param charTypes the chartype to use
     * @throws IOException on error
     */
    public void importAkkadian(DictHandling dicthandler,CharTypes charTypes) throws IOException {
        String line, cuneiword = "", modword = "", cuneiline = "";
        StringBuffer boundaryBuffer = new StringBuffer();
        AkkadChar lastchar = null, lastlastchar = null;
        String[] words, chars;
        boolean notakk = false, nocunei, sumerogram = false, logogram = false;
        System.out.println("CREATE CORPUS");
        while ((line = this.corpusReader.readLine()) != null) {
            ((AkkadDictHandler) (dicthandler)).newLine();
            if (line.isEmpty()) {
                continue;
            }
            if (line.substring(0, 1).equals("#") && !line.contains(Tags.AKK.toString())) {
                notakk = true;
            } else if (line.substring(0, 1).equals("#") && line.contains(Tags.AKK.toString())) {
                notakk = false;
            }
            if (!notakk && !line.isEmpty() && line.substring(0, 1).matches("[0-9]")) {

                line = line.substring(line.indexOf('.') + 1);
                words = line.split(" ");
                boolean opened=false;
                for (String word : words) {
                    logogram=false;
                    word = word.toLowerCase();
                    if (word.isEmpty()) {
                        continue;
                    }
                    word = word.replaceAll(" ", "");
                    if (word.equals("{ }") || word.equals("{}") || word.equals("x") || word.equals(":r:") || word.equals("...")) {
                        continue;
                    }
                    //System.out.println("Word: "+word);
                    modword = "";
                    //System.out.println("Word: "+word);
                    cuneiword = "";

                    if (word.startsWith("{}")) {
                        word = word.substring(word.indexOf("}") + 1);
                    }
                    Integer isNumber = 0;
                    boolean determinative = false;

                    /*if (word.contains("(") && word.contains(")") && word.indexOf("(") < word.indexOf(")") && word.matches("^[0-9]\\(.*")) {
                        isNumber = Integer.valueOf(word.split("\\(")[0]) - 1;
                        System.out.println("IsNumber: " + isNumber);
                    }*/
                    if (word.contains("{") && word.contains("}") && word.indexOf("{") > word.indexOf("}")) {
                        determinative = true;
                    }
                    if (word.contains("_")) {
                        logogram = true;
                    }
                    word = word.replaceAll("[0-9]+\\{", "{");
                    //word=word.replaceAll("[0-9]+\\(","(");
                    /*if (word.startsWith("[0-9](")) {
                        word = word.substring(word.indexOf("("), word.lastIndexOf(")"));
                    }*/
                    String numberchar="";
                    word = word.replace("x", "");
                    if (!word.isEmpty()) {
                        String cleaned = this.cleanWordString(word, true);
                        /*if (!cleaned.isEmpty() && !cleaned.equals("x") && !cleaned.equals("r")) {
                            this.reformattedTranslitWriter.write("[");
                            /*if (isNumber > 1) {
                                this.reformattedTranslitWriter.write("(");
                            }*/
                            numberchar=cleaned;
                          /*  if(isNumber>1){
                            for (int i = 0; i < (isNumber + 1); i++) {

                                if (i < isNumber) {
                                    this.reformattedBoundaryWriter.write("0,");
                                    this.reformattedTranslitWriter.write(cleaned+"-");
                                }
                            }
                            }
                            this.reformattedTranslitWriter.write(cleaned);
                            /*if (isNumber > 1) {
                                this.reformattedTranslitWriter.write(cleaned+")");
                                //this.reformattedBoundaryWriter.write("1,");
                            }*/
                          //  this.reformattedTranslitWriter.write("] ");
                        //}
                    }
                    AkkadChar akkad;
                    if (word.contains("{") && word.contains("}")) {
                        if (word.indexOf("{") > word.indexOf("}")) {
                            word = word.replace("}", "");
                            break;
                        }
                        //System.out.println("Word: "+word);
                        //System.out.println(word.substring(word.indexOf("{") + 1, word.indexOf("}")));

                        LangChar chara = dicthandler.translitToChar(this.cleanWordString(word.substring(word.indexOf("{") + 1, word.indexOf("}")), true));
                        //System.out.println(chara);
                        if (chara == null) {
                            akkad = new AkkadChar(this.cleanWordString(word.substring(word.indexOf("{") + 1, word.indexOf("}")), true));
                            //dicthandler.addChar(akkad);
                        } else {
                            akkad = new AkkadChar(chara.getCharacter());
                        }
                        if (StringUtils.isAllUpperCase(word.substring(word.indexOf("{") + 1, word.indexOf("}")))) {
                           // System.out.println("Sumerogram: " + word.substring(word.indexOf("{") + 1, word.indexOf("}")));
                            sumerogram = true;
                        } else {
                            //System.out.println("No Sumerogram: "+word.substring(word.indexOf("{") + 1, word.indexOf("}")));
                            sumerogram = false;
                        }
                        akkad.addTransliteration(new Transliteration(word.substring(word.indexOf("{") + 1, word.indexOf("}")).toLowerCase(), TranscriptionMethods.translitTotranscript(word.substring(word.indexOf("{") + 1, word.indexOf("}"))).toLowerCase(), true));
                        this.determinatives.add(this.cleanWordString(word.substring(word.indexOf("{") + 1, word.indexOf("}")),false).toUpperCase());
                        akkad.setDeterminative(determinative);
                        akkad.setIsNumberChar(isNumber > 0);
                        /*if(logogram){
                            this.sumerograms.add(word.substring(word.indexOf("{") + 1, word.indexOf("}")).toUpperCase());
                        }*/
                        akkad.setSumerogram(sumerogram);
                        akkad.setLogograph(logogram);
                        dicthandler.addWord(akkad, CharTypes.AKKADIAN);
                        if (lastlastword != null && lastword != null) {
                            dicthandler.addFollowingWord(lastword.getCharacter(), akkad.getCharacter(), lastlastword.getCharacter());
                        }
                        if (lastword != null) {
                            akkad.addPrecedingWord(lastword.getCharacter());
                        }
                        lastlastword = lastword;
                        lastword = akkad;
                        //System.out.println("WRITE: "+akkad.getCharacter());
                        if (!akkad.getCharacter().equals("")) {
                            LangChar tempcharr;
                            if ((tempcharr = dicthandler.matchChar(word.substring(word.indexOf("{") + 1, word.indexOf("}")))) != null && !tempcharr.getTransliterationSet().isEmpty()) {
                                //this.cuneiSegmentExport.write(dicthandler.matchChar(word.substring(word.indexOf("{") + 1, word.indexOf("}"))).getTransliterationSet().iterator().next().toString());
                                //this.cuneiWOSegmentExport.write(dicthandler.matchChar(word.substring(word.indexOf("{") + 1, word.indexOf("}"))).getTransliterationSet().iterator().next().toString() + ";");
                                boundaryBuffer.append("0,");
                                //if(logogram){
                                    this.reformattedTranslitWriter.write(word.substring(word.indexOf("{") + 1, word.indexOf("}")).toUpperCase());
                                //}else{
                                //    this.reformattedTranslitWriter.write(word.substring(word.indexOf("{") + 1, word.indexOf("}")));
                                //}

                                this.reformattedCuneiWriter.write(dicthandler.matchChar(word.substring(word.indexOf("{") + 1, word.indexOf("}"))).getTransliterationSet().iterator().next().toString());
                                this.reformattedUSegCuneiWriter.write(dicthandler.matchChar(word.substring(word.indexOf("{") + 1, word.indexOf("}"))).getTransliterationSet().iterator().next().toString());
                            }
                        } else {
                            //System.out.println(word);
                        }
                        String newword="",temp="";
                        boolean bracket=false;
                        System.out.println("Determinative Word "+word);
                        for(int i=0;i<word.length();i++) {
                           temp=word.substring(i,i+1);
                           if(temp.equals("{")){
                               bracket=true;
                           }else if(temp.equals("}")){
                               bracket=false;
                           }
                               if(bracket){
                                   newword+=temp.toUpperCase();
                               }else{
                                   newword+=temp;
                               }


                        }
                        System.out.println("Newword: "+newword);
                        /*if(word.endsWith("-")){
                            word=newword.substring(0,newword.length());
                        }else{
                            word=newword;
                        }*/

                        //System.out.println("Found Determiner: "+word.substring(word.indexOf("{")+1,word.indexOf("}")));
                        word = newword.replace("{", "-").replace("}", "-");

                        //System.out.println(word);
                    }
                    //System.out.println("Origintext: "+this.cleanWordString(word,true));
                    //System.out.println("CharsToBoundaries: "+this.transliterationToBoundaries(this.cleanWordString(word,true),CharTypes.AKKADIAN));
                    //this.reformattedBoundaryWriter.write(this.transliterationToBoundaries(this.cleanWordString(word,true),CharTypes.AKKADIAN));
                    //System.out.println("TransliterationToText: "+this.transliterationToText(this.cleanWordString(word,true),isNumber,dicthandler));

                    //word=word.replaceAll("^(-)*","");
                    word=word.replace("[...]","");
                    word=word.replace("...]","");
                    word=word.replace("[...]","");
                    word=word.replace("...","");
                    word=word.replace("]","");
                    word=word.replace("[","");
                    word=word.replace("#","");
                    while(word.startsWith("-")) {
                        word=word.substring(1,word.length());
                    }
                    chars = word.split("-|_|\\.");
                    List<Boolean> isLogogram = new LinkedList<>();
                    int j = 0;
                    boolean justopened=false;
                    for (int i = 0; i < word.length(); i++) {
                        if ((word.substring(i, i + 1).equals("-") || word.substring(i, i + 1).equals(".") ||word.substring(i, i + 1).equals(" ")) && ((opened) || ((i+1)<word.length() && word.substring(i+1, i + 2).equals("_")))) {
                            isLogogram.add(true);
                            justopened=false;
                            System.out.println("First Case: "+word.substring(i, i + 1));
                        }else if ((word.substring(i, i + 1).equals("-") || word.substring(i, i + 1).equals(".") || word.substring(i, i + 1).equals(" ")) && !opened) {
                            isLogogram.add(false);
                            justopened=false;
                            System.out.println("Second Case: "+word.substring(i, i + 1));
                        }  else if (word.substring(i, i + 1).equals("_") && opened ) {
                            //System.out.println("Logogram: "+word.substring(i));
                            if(i==0)
                                isLogogram.add(false);
                            opened=false;
                            System.out.println("Third Case: "+word.substring(i, i + 1));
                        } else if (word.substring(i, i + 1).equals("_") && !opened) {
                            //System.out.println("Logogram: "+word.substring(i));
                           // if(i==0)
                            //    isLogogram.add(true);
                            opened=true;
                            justopened=true;
                            System.out.println("Fourth Case: "+word.substring(i, i + 1));
                        }else if(!word.substring(i, i + 1).equals("_") && !word.substring(i, i + 1).equals("-") && !word.substring(i, i + 1).equals(".") && ((justopened && opened)||(opened && i==0))){
                            isLogogram.add(true);
                            justopened=false;
                            System.out.println("Fifth Case: "+word.substring(i, i + 1));
                        }else if(i==0 && !word.substring(0, 1).equals("_") && !word.substring(0, 1).equals("-") && !word.substring(i, i + 1).equals(".") && !justopened && !opened ){
                            isLogogram.add(false);
                            System.out.println("Sixth Case: "+word.substring(i, i + 1));
                        }
                    }
                    if(!word.contains("-")){
                        isLogogram.add(false);
                    }
                    //System.out.println("Word: "+word);
                   for(Boolean b:isLogogram){
                       System.out.print(b.toString());
                   }
                    System.out.print("\n");
                    ArffHandler.arrayToStr(chars);

                    nocunei = false;
                    int i = 0;
                    String writetranslit="[";
                    for (String character : chars) {
                        //System.out.println("Curchar: "+character);
                        if ("-".equals(character) || "x".equals(character)  || character.isEmpty()){
                            continue;
                        }

                        //System.out.println("CURCHAR: "+character);
                        isNumber=0;
                        if (character.contains("(") && character.contains(")") && character.indexOf("(") < character.indexOf(")") && character.matches("^[0-9]\\(.*")) {
                            isNumber = Integer.valueOf(character.split("\\(")[0]) - 1;
                            System.out.println("IsNumber: " + isNumber);
                            character=character.substring(1,character.length());
                        }
                        character = cleanWordString(character, false);
                        //System.out.println("Char: "+character+"\nMatch: "+dicthandler.translitToChar(character, 0));
                        if (dicthandler.translitToChar(character.toLowerCase()) != null) {
                            if (character.equals("disz") || character.equals("u")) {
                                ((CuneiChar) dicthandler.translitToChar(character.toLowerCase())).setIsNumberChar(true);
                            }
                            //System.out.println(character+" -> "+dicthandler.translitToChar(character));
                            matches++;
                            cuneiword += dicthandler.translitToChar(character.toLowerCase()).getCharacter();
                            //this.cuneiSegmentExport.write(dicthandler.translitToChar(character).getCharacter());

                            if(isNumber>0){
                                for(int k=0;k<=isNumber;k++){
                                    boundaryBuffer.append("0,");
                                    if (isLogogram.get(i)) {
                                        writetranslit+=character.toUpperCase()+"-";
                                        this.sumerograms.add(character.toUpperCase());
                                        //System.out.println("Logogram: " + character);
                                        ((CuneiChar) dicthandler.translitToChar(character.toLowerCase())).setLogograph(true);
                                    }else{
                                        writetranslit+=character+"-";
                                    }

                                    this.reformattedCuneiWriter.write(dicthandler.translitToChar(character.toLowerCase()).getCharacter());
                                    this.reformattedUSegCuneiWriter.write(dicthandler.translitToChar(character.toLowerCase()).getCharacter());
                                }
                            }else{
                                    boundaryBuffer.append("0,");
                                if (i<isLogogram.size() && isLogogram.get(i)) {
                                    writetranslit+=character.toUpperCase()+"-";
                                    this.sumerograms.add(character.toUpperCase());
                                    //System.out.println("Logogram: " + character);
                                    ((CuneiChar) dicthandler.translitToChar(character.toLowerCase())).setLogograph(true);
                                }else{
                                    writetranslit+=character+"-";
                                }
                                   this.reformattedCuneiWriter.write(dicthandler.translitToChar(character.toLowerCase()).getCharacter());
                                   this.reformattedUSegCuneiWriter.write(dicthandler.translitToChar(character.toLowerCase()).getCharacter());
                            }

                            //this.cuneiWOSegmentExport.write(dicthandler.translitToChar(character) + ";");
                            modword += "-" + character;


                        } else if (dicthandler.translitToChar(character) == null && !character.isEmpty()) {
                            //System.out.println("NO MATCH: "+character);
                            nomatches++;
                            boundaryBuffer.append("0,");
                            if (nomatchesmap.get(character) == null) {
                                nomatchesmap.put(character, 1);
                            } else {
                                nomatchesmap.put(character, nomatchesmap.get(character) + 1);
                            }
                            //System.out.println("MODWORD: "+modword);
                            nocunei = true;
                        }
                        i++;

                    }
                    if (boundaryBuffer.length() > 1) {
                        boundaryBuffer = new StringBuffer(boundaryBuffer.substring(0, boundaryBuffer.length() - 2) + "1,");
                        //System.out.println("BoundaryBuffer: "+boundaryBuffer.toString());
                        this.reformattedCuneiWriter.write(" ");
                        this.reformattedTranslitWriter.write(writetranslit.equals("[")?"":writetranslit.substring(0,writetranslit.length()-1)+"] ");
                    }
                    if (!nocunei && !modword.isEmpty()) {
                        akkad = new AkkadChar(cuneiword);
                        if (StringUtils.isAllUpperCase(modword.substring(1))) {
                            //System.out.println("Sumerogram: "+modword.substring(1));
                            sumerogram = true;
                        } else {
                            //System.out.println("No Sumerogram: "+modword.substring(1));
                            sumerogram = false;
                        }
                        akkad.setIsNumberChar(isNumber > 0);
                        akkad.setDeterminative(determinative);
                        /*if(i<isLogogram.size() && isLogogram.get(i)){
                            this.sumerograms.add(modword.substring(1).toUpperCase());
                        }*/
                        akkad.setSumerogram(sumerogram);
                        akkad.setLogograph(logogram);
                        akkad.addTransliteration(new Transliteration(modword.substring(1).toLowerCase(), TranscriptionMethods.translitTotranscript(modword.substring(1).toLowerCase()), true));
                        //System.out.println("WRITE: "+akkad.getCharacter());
                        Following following = new Following();
                        following.setIsStopChar(false);
                        if (lastlastword != null && lastword != null) {
                            dicthandler.addFollowingWord(lastword.getCharacter(), akkad.getCharacter(), lastlastword.getCharacter());
                        }
                        if (lastword != null) {
                            akkad.addPrecedingWord(lastword.getCharacter(), true);
                        }
                        dicthandler.addWord(akkad, CharTypes.AKKADIAN);
                        lastlastword = lastword;
                        lastword = akkad;
                    }
                }
                this.reformattedBoundaryWriter.write(boundaryBuffer.toString());
                //this.cuneiSegmentExport.write(System.lineSeparator());
                //this.cuneiWOSegmentExport.write(System.lineSeparator());
                if(!line.isEmpty() || !line.matches("^[ ]*$")){
                    this.reformattedTranslitWriter.write(System.lineSeparator());
                    this.reformattedTranslitWriter.flush();
                    this.reformattedBoundaryWriter.write(System.lineSeparator());
                    this.reformattedCuneiWriter.write(System.lineSeparator());
                    this.reformattedUSegCuneiWriter.write(System.lineSeparator());
                }
                boundaryBuffer.delete(0, boundaryBuffer.length());
                opened=false;
            }
            cuneiline = this.transliterationToText(line, 0, dicthandler, false, false);
            dicthandler.getNGramStats().generateNGramsFromLine(CharTypes.AKKADIAN, cuneiline, cuneiline.length());
        }
   }

    @Override
    public void importFromFormat(final CharTypes charType, final DictHandling dictHandler) throws IOException {
        switch(charType){
            case AKKADIAN:
            case SUMERIANCHAR:
            case HITTITECHAR:
                this.importAkkadian(dictHandler,charType);
                break;
            default:
        }
    }
}
