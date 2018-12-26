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

package de.unifrankfurt.cs.acoli.akkad.dict.corpusimport.cuneiform;

import de.unifrankfurt.cs.acoli.akkad.dict.chars.LangChar;
import de.unifrankfurt.cs.acoli.akkad.dict.chars.cuneiform.AkkadChar;
import de.unifrankfurt.cs.acoli.akkad.dict.chars.cuneiform.CuneiChar;
import de.unifrankfurt.cs.acoli.akkad.dict.corpusimport.CorpusHandlerAPI;
import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.cuneiform.AkkadDictHandler;
import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.cuneiform.CuneiDictHandler;
import de.unifrankfurt.cs.acoli.akkad.dict.pos.POSTagger;
import de.unifrankfurt.cs.acoli.akkad.dict.utils.Transliteration;
import de.unifrankfurt.cs.acoli.akkad.methods.transcription.TranscriptionMethods;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.TestMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Files;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Tags;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.*;

/**
 * Created by timo on 30.07.14.
 * CorpusHandler for cuneiform characters.
 */
public abstract class CuneiCorpusHandler extends CorpusHandlerAPI {
    protected Set<String> determinatives;
    /**The word processed before the last word.*/
    protected AkkadChar lastlastword;
    /**The last word which was processed.*/
    protected AkkadChar lastword;
    /**The POSTagger to provide.*/
    protected POSTagger posTagger;
    /**Set of sumerograms.*/
    protected Set<String> sumerograms;
    /**The dicthandler to use.*/
    protected DictHandling utilDictHandler;
    /**
     * Constructor for this class.
     * @param stopchars stopchars to consider
     */
    public CuneiCorpusHandler(List<String> stopchars){
        super(stopchars);
        this.sumerograms=new TreeSet<>();
        this.determinatives=new TreeSet<>();
    }

    @Override
    public void addTranslations(final String file, final TestMethod testMethod1) throws ParserConfigurationException, SAXException, IOException {

    }

    @Override
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

    @Override
    public String corpusToReformatted(final String text) {
        String result = "";
        Boolean notakk = false;
        if(text==null || text.isEmpty()){
            return result;
        }
        for (String line : text.split(System.lineSeparator())) {
            if (line.isEmpty()) {
                continue;
            }
            if (line.substring(0, 1).equals("#") && !line.contains(Tags.AKK.toString())) {
                continue;
            } else if (line.substring(0, 1).equals("#") && line.contains(Tags.AKK.toString())) {
                notakk = false;
            }
            if (!notakk && !line.isEmpty() && line.substring(0, 1).matches("[0-9]")) {

                line = line.substring(line.indexOf('.') + 1);
                String[] words = line.split(" ");
                for (String word : words) {

                    word = word.toLowerCase();
                    if (word.isEmpty()) {
                        continue;
                    }
                    word = word.replaceAll(" ", "");
                    if (word.equals("{ }") || word.equals("{}") || word.equals("x") || word.equals(":r:") || word.equals("...")) {
                        continue;
                    }
                    if (word.startsWith("{}")) {
                        word = word.substring(word.indexOf("}") + 1);
                    }
                    Integer isNumber = 0;
                    if (word.contains("(") && word.contains(")") && word.indexOf("(") < word.indexOf(")") && word.matches("^[0-9]\\(.*")) {
                        isNumber = Integer.valueOf(word.split("\\(")[0])-1;
                    }
                    word = word.replaceAll("[0-9]+\\{", "{");
                    word=word.replaceAll("[0-9]+\\(","(");
                    if (word.startsWith("[0-9](")) {
                        word = word.substring(word.indexOf("("), word.lastIndexOf(")"));
                    }
                    word = word.replace("x", "");
                    word = this.cleanWordString(word, true);
                    if (!word.isEmpty() && !word.equals("x") && !word.equals("r")) {
                        result += "[";
                        /*if(isNumber>1){
                            result+=isNumber+"(";
                        }*/
                        //result+=word;
                        /*if(isNumber>1){
                            result+=")";
                        }*/
                        for (int i = 0; i < (isNumber+1); i++) {
                            result += word;
                            if (i < isNumber) {
                                result += "-";
                            }
                        }
                        result += "] ";
                    }
                }
                result += System.lineSeparator();
            }

        }
        return result;
    }

    @Override
    public DictHandling dictImport(final String corpus, final TestMethod testMethod, final CharTypes sourcelang) throws IOException, SAXException, ParserConfigurationException {
        return null;
    }

    @Override
    public void enrichExistingCorpus(final String filepath, final DictHandling dicthandler) throws IOException {

    }

    @Override
    public DictHandling generateCorpusDictionaryFromFile(final List<String> filepath, final String signpath, final String filename, final boolean wholecorpus, final boolean corpusstr,final TestMethod testMethod) throws IOException, ParserConfigurationException, SAXException, XMLStreamException {
        return null;
    }

    /**
     * Generates an Akkadian corpusimport out of the source file.
     * @param filepath the filepath to the source file
     * @param wholecorpus
     * @throws IOException on error
     */
    public DictHandling generateCorpusDictionaryFromFile(final List<String> filepath,final String signpath,final boolean wholecorpus,boolean fileOrStr,final TestMethod testMethod) throws IOException, ParserConfigurationException, SAXException {
        final AkkadDictHandler dicthandler=new AkkadDictHandler(this.stopchars);
        if(signpath!=null){
            dicthandler.parseDictFile(new File(signpath));
        }
        System.out.println("Filepath "+filepath);

        double matches=0,nomatches=0;
        Map<String,Integer> nomatchesmap=new TreeMap<>();
        this.randomGenerator=new Random();
        this.corpusReader =new BufferedReader(new FileReader(new File(Files.SOURCEDIR+filepath.get(0))));
        this.cuneiSegmentExport =new BufferedWriter(new FileWriter(new File(Files.TESTDATADIR.toString()+ Files.CORPUSOUT.toString())));
        this.cuneiWOSegmentExport =new BufferedWriter(new FileWriter(new File(Files.TESTDATADIR.toString()+ Files.CORPUSOUT2.toString())));
        this.reformattedTranslitWriter =new BufferedWriter(new FileWriter(new File(Files.REFORMATTEDDIR+filepath.get(0).substring(0,filepath.lastIndexOf('.'))+Files.REFORMATTED)));
        this.reformattedBoundaryWriter =new BufferedWriter(new FileWriter(new File(Files.REFORMATTEDDIR.toString()+Files.BOUNDARYDIR.toString()+filepath.get(0).substring(0,filepath.lastIndexOf('.'))+Files.BOUNDARIES.toString())));
        this.reformattedCuneiWriter =new BufferedWriter(new FileWriter(new File(Files.REFORMATTEDDIR.toString()+Files.CUNEI_SEGMENTEDDIR.toString()+filepath.get(0).substring(0, filepath.lastIndexOf('.')))));

        this.normalizedTestDataWriter=new BufferedWriter(new FileWriter(new File(Files.TESTDATA.toString())));
        String line,cuneiword="",modword="";
        StringBuffer boundaryBuffer=new StringBuffer();
        AkkadChar lastchar,lastlastchar;
        String[] words,chars;
        boolean notakk=false,nocunei,dictOrTest=true;
        System.out.println("CREATE CORPUS");
        while((line=this.corpusReader.readLine())!=null){
            ((CuneiDictHandler)dicthandler).newLine();
            lastchar=new AkkadChar(" ");
            if(line.isEmpty()){
                continue;
            }
            line=line.toLowerCase();
            if(line.substring(0,1).equals("#") && !line.contains(Tags.AKK.toString())){
                if(this.randomGenerator.nextInt(10)>7 && !wholecorpus){
                    dictOrTest=false;
                }else{
                    dictOrTest=true;
                }
                notakk=true;
            }else if(line.substring(0,1).equals("#") && line.contains(Tags.AKK.toString())){
                notakk=false;
            }
            if(!notakk && !line.isEmpty() && line.substring(0,1).matches("[0-9]")){
                line=line.substring(line.indexOf('.')+1);
                words=line.split(" ");
                for(String word:words){
                    //System.out.println("Word: "+word);
                    modword="";
                    //System.out.println("Word: "+word);
                    cuneiword="";
                    word=word.replaceAll(" ","");
                    if(word.equals("{ }") || word.equals("{}") || word.equals("x") || word.equals(":r:") || word.equals("...")){
                        continue;
                    }
                    if(word.startsWith("{}")){
                        word=word.substring(word.indexOf("}")+1);
                    }
                    word=word.replaceAll("[0-9]+\\{","{");
                    word=word.replaceAll("[0-9]+\\(","(");
                    if(word.startsWith("[0-9](")){
                        word=word.substring(word.indexOf("("),word.lastIndexOf(")"));
                    }
                    word=word.replace("x","");
                    if(!word.isEmpty()){
                        String temp=this.cleanWordString(word,true);
                        if(!temp.isEmpty() && !temp.equals("x") && !temp.equals("r")){
                            if(dictOrTest)
                                this.reformattedTranslitWriter.write("["+this.cleanWordString(word,true)+"] ");
                            else
                                this.normalizedTestDataWriter.write("["+this.cleanWordString(word,true)+"] ");
                        }
                    }
                    if(word.contains("{") && word.contains("}") ){
                        if(word.indexOf("{")>word.indexOf("}")){
                            word.replace("}","");
                            break;
                        }
                        //System.out.println(word);
                        //System.out.println(word.substring(word.indexOf("{") + 1, word.indexOf("}")));

                        LangChar chara=dicthandler.translitToChar(this.cleanWordString(word.substring(word.indexOf("{") + 1, word.indexOf("}")), true));
                        //System.out.println(chara);
                        AkkadChar akkad;
                        if(chara==null){
                            akkad=new AkkadChar(this.cleanWordString(word.substring(word.indexOf("{")+1,word.indexOf("}")),true));
                            //dicthandler.addWord(akkad);
                        }
                        else{
                            akkad=new AkkadChar(chara.getCharacter());
                        }
                        akkad.addTransliteration(new Transliteration(word.substring(word.indexOf("{") + 1, word.indexOf("}")), TranscriptionMethods.translitTotranscript(word.substring(word.indexOf("{") + 1, word.indexOf("}"))),true));
                        akkad.setDeterminative(true);
                        dicthandler.addFollowingWord(lastchar.getCharacter(),akkad.getCharacter());
                        dicthandler.addWord(akkad, CharTypes.CUNEICHAR);
                        //lastchar=akkad;
                        //System.out.println("WRITE: "+akkad.getCharacter());
                        if(!akkad.getCharacter().equals("")){
                            LangChar tempcharr;
                            if((tempcharr=dicthandler.matchChar(word.substring(word.indexOf("{") + 1, word.indexOf("}"))))!=null && !tempcharr.getTransliterationSet().isEmpty()){
                                this.cuneiSegmentExport.write(dicthandler.matchChar(word.substring(word.indexOf("{") + 1, word.indexOf("}"))).getTransliterationSet().iterator().next().toString());
                                this.cuneiWOSegmentExport.write(dicthandler.matchChar(word.substring(word.indexOf("{") + 1, word.indexOf("}"))).getTransliterationSet().iterator().next().toString()+";");
                            }
                        }else{
                            //System.out.println(word);
                        }
                        //System.out.println("Found Determiner: "+word.substring(word.indexOf("{")+1,word.indexOf("}")));
                        word=word.replace("{","-").replace("}","-");
                        //System.out.println(word);
                    }
                    chars=word.split("-|_|\\.");
                    nocunei=false;
                    for(String character:chars){
                        //System.out.println("Curchar: "+character);
                        if("-".equals(character) || "x".equals(character))
                            continue;
                        //System.out.println("CURCHAR: "+character);
                        character=cleanWordString(character,false);
                        //System.out.println("Char: "+character+"\nMatch: "+dicthandler.translitToChar(character, 0));
                        if(dicthandler.translitToChar(character)!=null) {
                           if(character.equals("disz")){
                                ((CuneiChar)dicthandler.translitToChar(character)).setIsNumberChar(true);
                            }
                                                 //System.out.println(character+" -> "+dicthandler.translitToChar(character));
                            matches++;
                            cuneiword+=dicthandler.translitToChar(character).getCharacter();
                            this.cuneiSegmentExport.write(dicthandler.translitToChar(character).getCharacter());
                            this.reformattedCuneiWriter.write(dicthandler.translitToChar(character).getCharacter());
                            this.cuneiWOSegmentExport.write(dicthandler.translitToChar(character)+";");
                            boundaryBuffer.append("0,");
                            modword+="-"+character;
                        }
                        else if(dicthandler.translitToChar(character)==null && !character.isEmpty()){
                            //System.out.println("NO MATCH: "+character);
                            nomatches++;
                            boundaryBuffer.append("0,");
                            if(nomatchesmap.get(character)==null){
                                nomatchesmap.put(character,1);
                            }else{
                                nomatchesmap.put(character,nomatchesmap.get(character)+1);
                            }
                            //System.out.println("MODWORD: "+modword);
                            nocunei=true;
                        }

                    }
                    if(boundaryBuffer.length()>1) {
                        boundaryBuffer = new StringBuffer(boundaryBuffer.substring(0, boundaryBuffer.length() - 2) + "1,");
                        this.reformattedCuneiWriter.write(" ");

                    }

                    if(!nocunei && !modword.isEmpty()){
                        AkkadChar akkad=new AkkadChar(cuneiword);
                        akkad.addTransliteration(new Transliteration(modword.substring(1),TranscriptionMethods.translitTotranscript(modword.substring(1)),true));

                        //System.out.println("WRITE: "+akkad.getCharacter());
                        dicthandler.addFollowingWord(lastchar.getCharacter(),akkad.getCharacter());
                        dicthandler.addWord(akkad,CharTypes.CUNEICHAR);
                        lastlastchar=lastchar;
                        lastchar=akkad;
                    }
                }
                this.reformattedBoundaryWriter.write(boundaryBuffer.toString());
                boundaryBuffer.delete(0,boundaryBuffer.length());
                this.cuneiSegmentExport.write(System.lineSeparator());
                this.cuneiWOSegmentExport.write(System.lineSeparator());
                this.reformattedTranslitWriter.write(System.lineSeparator());
                this.reformattedBoundaryWriter.write(System.lineSeparator());
                this.reformattedCuneiWriter.write(System.lineSeparator());
            }
        }
        this.cuneiSegmentExport.close();
        this.cuneiWOSegmentExport.close();
        this.reformattedCuneiWriter.close();
        this.normalizedTestDataWriter.close();
        this.reformattedBoundaryWriter.close();
        this.reformattedTranslitWriter.close();
        dicthandler.calculateRightLeftAccessorVariety();
        dicthandler.calculateRelativeWordOccurances(dicthandler.getAmountOfWordsInCorpus());
        dicthandler.calculateRelativeCharOccurances(dicthandler.getAmountOfWordsInCorpus());
        System.out.println("Translit To Cunei Matches: "+matches);
        System.out.println("Translit To Cunei Fails: "+nomatches);
        System.out.println("Total % "+(matches+nomatches));
        System.out.println("% "+(matches/(matches+nomatches)));
        System.out.println("No matches list: "+nomatchesmap+"\nSize: "+nomatchesmap.keySet().size());
        return dicthandler;
    }

    @Override
    public void textPercentageSplit(final Double perc, final Double startline,final Boolean randomm, final String corpusfile,final  CharTypes chartype) throws IOException {
        Map<Integer,Integer> result=new TreeMap<>();
        String textresult="";
        BufferedReader reader=new BufferedReader(new FileReader(new File(corpusfile)));
        String line;
        Integer lastline=0,linec=0;
        while((line=reader.readLine())!=null){
            if(line.isEmpty()){
                linec++;
                continue;
            }
            if(line.substring(0,1).equals("#") && line.contains(chartype.getLocale())){
                if(lastline!=0){
                    result.put(linec,linec-lastline-1);
                }
                lastline=linec;
            }
            linec++;
        }
        System.out.println("Result: "+result);
        reader.close();
        Double chosenpercentage=0.;
        List<Integer> entrysetlist=new LinkedList<>(result.keySet());
        System.out.println("Entrysetlist: "+entrysetlist);
        List<Integer> chosenkeys=new LinkedList<>();
        if(randomm){
            Random random=new Random(System.currentTimeMillis());
            while(chosenpercentage<perc && chosenkeys.size()<entrysetlist.size()){
                int rand=random.nextInt(entrysetlist.size());
                if(!chosenkeys.contains(rand)){
                    chosenkeys.add(entrysetlist.get(rand));
                    chosenpercentage+=Double.valueOf(result.get(entrysetlist.get(rand)))/Double.valueOf(linec);
                    System.out.println("Chosenpercentage: "+chosenpercentage);
                }
            }
        }else{
            int rand=0;
            while(chosenpercentage<perc && chosenkeys.size()<entrysetlist.size()){
                chosenkeys.add(entrysetlist.get(rand));
                chosenpercentage+=Double.valueOf(result.get(entrysetlist.get(rand++)))/Double.valueOf(linec);
                System.out.println("Chosenpercentage: "+chosenpercentage);
            }
        }

        reader=new BufferedReader(new FileReader(new File(corpusfile)));
        int border=0;
        String trainingset="";
        linec=0;
        while((line=reader.readLine())!=null){
            if(linec>border && chosenkeys.contains(linec)){
                border=linec+result.get(linec);
            }else if(linec<border){
                textresult+=line+System.lineSeparator();
            }else{
               trainingset+=line+System.lineSeparator();
            }
            linec++;
        }
        reader.close();
        BufferedWriter writer=new BufferedWriter(new FileWriter(new File("testtttt")));
        writer.write(textresult);
        writer.close();
        writer=new BufferedWriter(new FileWriter(new File("trainnnn")));
        writer.write(trainingset);
        writer.close();
        this.trainSet=textresult;
    }

    /**
     * Converts transliteration text to cuneiform text.
     * @param word the word/line/text to choose
     * @param duplicator duplicator
     * @param dicthandler the dicthandler to choose
     * @param countmisses if misses should be counted
     * @param segmented  if the text should be segmented
     * @return the cuneiform text as String
     * @throws IOException on error
     */
    public String transliterationToText(final String word,final Integer duplicator,final DictHandling dicthandler,final Boolean countmisses,final Boolean segmented) throws IOException {
        if(word==null || word.isEmpty()){
            return "";
        }
        String[] lines=word.split(System.lineSeparator());
        String result="",lineresult="";
        for(String line:lines){
            lineresult="";
            System.out.println("Line: "+line);
            for(String words:line.split(" ")){
            String[] chars=words.split("-|_|\\.");
            Boolean nocunei=false;
            for(String character:chars) {
                //System.out.println("Curchar: "+character);
                if ("-".equals(character) || "x".equals(character))
                    continue;
                Integer isNumber = 0;
                if (word.contains("(") && word.contains(")") && word.indexOf("(") < word.indexOf(")") && word.matches("^[0-9]\\(.*")) {
                    isNumber = Integer.valueOf(word.split("\\(")[0])-1;
                }
                //System.out.println("CURCHAR: "+character);
                character = cleanWordString(character, false);
                //System.out.println("Char: "+character+"\nMatch: "+dicthandler.translitToChar(character, 0));
                if (dicthandler.translitToChar(character) != null) {
                    System.out.println(character + " -> " + dicthandler.translitToChar(character));
                    matches++;
                    lineresult += dicthandler.translitToChar(character).getCharacter();
                    //modword+="-"+character;
                } else if(isNumber>0){
                    for(int i=0;i<isNumber;i++){
                        lineresult+=dicthandler.translitToChar(character);
                    }
                }else if (dicthandler.translitToChar(character) == null && !character.isEmpty()) {
                    //System.out.println("NO MATCH: "+character);
                    if (countmisses) {
                        nomatches++;
                        if (nomatchesmap.get(character) == null) {
                            nomatchesmap.put(character, 1);
                        } else {
                            nomatchesmap.put(character, nomatchesmap.get(character) + 1);
                        }
                    }
                    //
                    //boundaryBuffer.append("0,");

                    //System.out.println("MODWORD: "+modword);
                    nocunei = true;
                }
            }
                if(segmented){
                    lineresult+=" ";
                }
            }
            result+=lineresult.trim()+System.lineSeparator();
        }
        return result;
    }
}
