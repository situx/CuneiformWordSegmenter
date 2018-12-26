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

package de.unifrankfurt.cs.acoli.akkad.dict.importhandler.asian;

import de.unifrankfurt.cs.acoli.akkad.dict.chars.LangChar;
import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.dict.utils.Transliteration;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.TestMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Files;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by timo on 10.08.14.
 * ImportHandler for CDDict formatted files.
 */
public class CEDictImportHandler {


    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, XMLStreamException {
        DictHandling dictHandler = CharTypes.CHINESE.getCorpusHandlerAPI().generateTestTrainSets("","",0.,0., TestMethod.FOREIGNTEXT,CharTypes.CHINESE);
        dictHandler.setCharType(CharTypes.CHINESE);
        //dictHandler.parseDictFile(new File(Files.AKKADXML.toString()));
        System.out.println("CharType: "+dictHandler.getChartype().toString());
        dictHandler.importMappingFromXML(Files.DICTDIR+CharTypes.CHINESE.getLocale()+Files.MAPSUFFIX);
        dictHandler.importDictFromXML(Files.DICTDIR+CharTypes.CHINESE.getLocale()+Files.DICTSUFFIX);
        //dictHandler.importReverseDictFromXML(Files.DICTDIR+CharTypes.CHINESE.getLocale()+Files.REVERSE+Files.DICTSUFFIX);
        CEDictImportHandler importHandler=new CEDictImportHandler();
        importHandler.parseCNCeDict(Files.DICTDIR + "cedict_ts.u8", Locale.ENGLISH, dictHandler);
        dictHandler.exportToXML("source/cn1.txt","source/cn2.txt","source/cn3.txt","source/cn4.txt");
    }

    /**
     * Parses a CEDIct formatted dictionary.
     * @param filepath the filepath for this dictionary
     * @param locale the locale to parse
     * @param dictHandler the dicthandler to use
     * @throws IOException
     */
    public void parseCNCeDict(final String filepath,final Locale locale,final DictHandling dictHandler) throws IOException {
         BufferedReader reader=new BufferedReader(new FileReader(new File(filepath)));
         String temp;
         while((temp=reader.readLine())!=null){
             if(temp.startsWith("#")){
                 continue;
             }
             //System.out.println(temp);
             String[] parts=temp.split(" ");
             String pinyin="";
             int i=2;
             boolean unfinished=true;
             while(unfinished){
                 if(!parts[i].contains("]")){
                     pinyin+=(parts[i++]+"-");
                 }else{
                     pinyin+=parts[i];
                     unfinished=false;
                 }
             }
             i++;
             LangChar word;
             if((word=dictHandler.matchWord(parts[0]))!=null || (word=dictHandler.matchWord(parts[1]))!=null || (word=dictHandler.matchChar(parts[0]))!=null || (word=dictHandler.matchWord(parts[1]))!=null){
                 System.out.println("First Check: "+word);
                 System.out.println(word.getTransliterationSet()+" - "+pinyin);
                 pinyin=pinyin.replaceAll("\\[", "").replaceAll("\\]","").toLowerCase();
                 pinyin=pinyin.trim();
                 if(word.getTransliterationSet().contains(new Transliteration(pinyin,pinyin))){
                     System.out.println("Second Check: "+word);
                     for(String trans:parts[i].substring(1,parts[i].length()).split("/")){
                         word.addTranslation(trans,locale);
                     }
                 }
             }
         }
        reader.close();
    }
}
