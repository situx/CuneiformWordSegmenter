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

package de.unifrankfurt.cs.acoli.akkad.methods;

import de.unifrankfurt.cs.acoli.akkad.dict.chars.LangChar;
import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.dict.utils.Transliteration;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.TestMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.TransliterationMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Files;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Base class for containing method utilities.
 * @author Timo Homburg
 * Date: 17.11.13
 * Time: 13:42
 */
public class Methods {
    /**Wirter for the cuneiform segmentation result.*/
    protected BufferedWriter cuneiResultWriter;
    /**Provides a line counter and a charcounter for debugging purposes.*/
    protected Integer linecounter,charcounter;
    /**The reader for the input file.*/
    protected BufferedReader reader;
    /**Temporary String for saving values.*/
    protected String tempstr;
    /**The text to process.*/
    protected String text;
    /**Writer for the transliteration result.*/
    protected BufferedWriter transcriptResultWriter;
    /**Writer for the transliteration result.*/
    protected BufferedWriter translitResultWriter;
    /*List of wordboundaries as Integers*/
    protected List<Integer> wordboundaries;
    /*List for saving the segmented words*/
    protected List<String> words;

    /**Constructor for this class.*/
    public Methods(){
        super();
        this.words=new LinkedList<>();
        this.wordboundaries=new LinkedList<>();
        this.linecounter=0;
        this.charcounter=0;
    }

    /**
     * Assigns a transliteration to a given segmented line.
     * @param cuneiwords The line segmentation as String array
     * @param dicthandler the dicthandler needed for transliterating
     * @param transliterationMethod the transliteration method to choose
     * @return the segmented, transliterated String
     */
    public String assignTransliteration(String[] cuneiwords, DictHandling dicthandler, TransliterationMethod transliterationMethod){
        LangChar tempword;
        String result="";
        for(int i=0;i<cuneiwords.length;){
            System.out.println("Stringarray["+i+"]: "+cuneiwords[i]);
            if(cuneiwords[i].equals(" ") || cuneiwords[i].isEmpty()){
                i++;
                continue;
            }
            else if(i==cuneiwords.length-1){
                tempword=dicthandler.matchChar(cuneiwords[cuneiwords.length-1]);
                if(tempword!=null) {
                    System.out.println("Result+= "+"[" + dicthandler.getDictTransliteration (tempword,transliterationMethod) + "] ");
                    result+="[" + dicthandler.getDictTransliteration (tempword,transliterationMethod) + "] ";
                }
                else{
                    System.out.println("Result+= "+dicthandler.getNoDictTransliteration(cuneiwords[i], transliterationMethod));
                    result+=dicthandler.getNoDictTransliteration(cuneiwords[i], transliterationMethod)+" ";
                }
                i++;
                continue;
            }
            tempword=dicthandler.matchWord(cuneiwords[i]);
            if(tempword!=null) {
                System.out.println("Dict");
                System.out.println("Result+= "+"[" + dicthandler.getDictTransliteration (tempword,transliterationMethod) + "] ");
                result+="[" + dicthandler.getDictTransliteration (tempword,transliterationMethod) + "] ";
            }
            else{
                System.out.println("NoDict");
                System.out.println("Result+= "+dicthandler.getNoDictTransliteration(cuneiwords[i], transliterationMethod));
                result+=dicthandler.getNoDictTransliteration(cuneiwords[i], transliterationMethod)+" ";
            }
            i++;
            /*if(cuneiwords[i].length()/2==0){
                i++;
            }else{
                i+=cuneiwords[i].length()/2;
            }*/
        }
        if(( !result.isEmpty() || result.equals(" ")) && result.substring(result.length()-1).equals("-")){
            result=result.substring(0,result.length()-1)+"] ";
        }
        return result.replaceAll(" +", " ");
    }

    /**
     * Transforms a given fiel to the boundary representation.
     * @param filepath the path of the file to transform
     * @param chartype the language in which the file is written
     * @param testMethod the test method to use
     * @throws IOException
     */
    protected void fileToBoundaries(String filepath,CharTypes chartype,TestMethod testMethod) throws IOException {
        int charlength=chartype.getChar_length();
        BufferedReader reader=new BufferedReader(new FileReader(new File(filepath)));
        BufferedWriter writer=new BufferedWriter(new FileWriter(new File(Files.REFORMATTEDDIR.toString()+Files.BOUNDARYDIR.toString()+testMethod.toString().toLowerCase()+File.separator+filepath.substring(filepath.lastIndexOf('/')+1))));
        String temp;
        String[] sylls;
        while((temp=reader.readLine())!=null){
            for(String word:temp.split(" ")){
                sylls=word.split("-");
                for(int i=0;i<sylls.length;i++){
                    if(i==sylls.length-1){
                        writer.write("1,");
                    }else{
                        writer.write("0,");
                    }
                }
            }
            writer.write("\n");
        }
        reader.close();
        writer.close();
    }

    /**
     * Matches the most appropriate transliteration according to the position of the character in the word.
     * @param position the position to search for
     * @param currentchar the char to search for
     * @return the corresponding transliteration
     */
    public Transliteration getTransliterationByPosition(final int position, final LangChar currentchar){
        if(currentchar.getTransliterationSet().isEmpty()){
            return new Transliteration("","");
        }
        for(Transliteration translit:currentchar.getTransliterationSet()){
            switch(position){
                case 0: if(translit.isBeginTransliteration()){
                    return translit;
                }break;
                case 1: if(translit.isMiddleTransliteration()){
                    return translit;
                }break;
                case 2: if(translit.isEndTransliteration()){
                    return translit;
                }break;
                case 3: if(translit.isSingleTransliteration()){
                    return translit;
                }break;

                default:
            }
        }
        return currentchar.getTransliterationSet().iterator().next();
    }

    /**
     * Reads the whole file and puts it in a String object.
     * Needed for String reverse scanning purposes.
     * @param file the file to read
     * @throws IOException on error
     */
    public String readWholeFile(final File file) throws IOException {
        this.reader=new BufferedReader(new FileReader(file));
        this.text="";
        String temp;
        while((temp=this.reader.readLine())!=null){
            this.text+=temp+"\n";
        }
        return this.text;
    }

    /**
     * Reads the whole file and puts it in a String object.
     * Needed for String reverse scanning purposes.
     * @param file the file to read
     * @throws IOException on error
     */
    public String reverseWholeFile(final File file) throws IOException {
        this.reader=new BufferedReader(new FileReader(file));
        this.text="";
        String temp;
        StringBuffer buffer=new StringBuffer();
        while((temp=this.reader.readLine())!=null){
            buffer.append(temp);
            this.text+=buffer.reverse()+"\n";
            buffer.delete(0,temp.length());
        }
        return this.text;
    }

    /**
     * Searches an already generated candidate set for a subset.
     * @param character the character to search for
     * @param candidates the candidates to get
     * @return the set of candidates
     */
    protected Set<String> searchCandidateSubSet(final String character,final Set<String> candidates){
        final Set<String> newcandidates=new TreeSet<>();
        for(String key:candidates){
            if(key.startsWith(character)){
                newcandidates.add(key);
            }
        }
        return newcandidates;
    }
}
