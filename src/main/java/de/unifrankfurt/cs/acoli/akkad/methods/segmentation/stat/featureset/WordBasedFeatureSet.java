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

package de.unifrankfurt.cs.acoli.akkad.methods.segmentation.stat.featureset;

import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.dict.utils.StopChar;
import de.unifrankfurt.cs.acoli.akkad.util.Tuple;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.FeatureSets;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.TestMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Files;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by timo on 13.08.14.
 */
public class WordBasedFeatureSet extends FeatureSet {
    /**
     * Constructor for this class.
     *
     * @param statusValues    the class values
     * @param attributes      the number of features
     * @param name
     * @param wordOrCharBased
     */
    public WordBasedFeatureSet(final String[] statusValues, final Integer attributes, final String name,final Boolean wordOrCharBased) {
        super(statusValues, attributes, name,wordOrCharBased);
    }

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, XMLStreamException {
        DictHandling dictHandler = CharTypes.CHINESE.getCorpusHandlerAPI().generateTestTrainSets("", "", 0., 0., TestMethod.FOREIGNTEXT, CharTypes.CHINESE);
        dictHandler.setCharType(CharTypes.CHINESE);
        //dictHandler.parseDictFile(new File(Files.AKKADXML.toString()));
        System.out.println("CharType: "+dictHandler.getChartype().toString());
        dictHandler.importMappingFromXML(Files.DICTDIR+CharTypes.CHINESE.getLocale()+Files.MAPSUFFIX);
        dictHandler.importDictFromXML(Files.DICTDIR+CharTypes.CHINESE.getLocale()+Files.DICTSUFFIX);
        dictHandler.importReverseDictFromXML(Files.DICTDIR+CharTypes.CHINESE.getLocale()+Files.REVERSE+Files.DICTSUFFIX);
        WordBasedFeatureSet set=(WordBasedFeatureSet) FeatureSets.UNLABELED.getFeatureSet();
        //arrayToStr(set.unlabeled(new Tuple<StringBuffer, Integer>(new StringBuffer("aaaaefghi"), 3), dictHandler, CharTypes.ENGLISH));
    }

    /**
     *
     * Word-Based and Character-Based Word Segmentation Models:
     Comparison and Combination
     Weiwei Sun
     Department of Computational Linguistics, Saarland University
     German Research Center for Artificial Intelligence (DFKI)
     wsun@coli.uni-saarland.de
     * The character features includes,
     Boundary character unigram: c j , c k , ck+1 , cl
     and cl+1 ; Boundary character bigram: ck ck+1 and
     c l c l+1 .
     Inside character unigram: cs (k + 1 < s < l);
     Inside character bigram: cs cs+1 (k + 1 < s < l).
     Length of current word.
     Whether ck+1 and ck+1 are identical.
     Combination Features: ck+1 and cl ,
     The word token features includes,
     Word Unigram: previous word wi−1 and cur-
     rent word wi ; Word Bigram: wi−1 wi.
     The identity of wi, if it is a Single character
     word.
     Combination Features: wi−1 and length of wi,
     wi and length of wi−1 . ck+1 and length of wi , cl
     and length of wi.

     * @param tuple
     * @param dicthandler
     * @param charType
     * @return
     */
   /* public String[] passiveAggressive(final Tuple<StringBuffer,Integer> tuple,final String segmented,DictHandling dicthandler,CharTypes charType){
        int charlength=charType.getChar_length();
        String currentline=tuple.getOne().toString();
        int position=tuple.getTwo();
        int counter=0;
        List<String> result=new LinkedList<String>();
        //Boundary character unigram
        for(int i=-2*charlength;i<3*charlength;i+=charlength){
            if((position+i)>-1 && (position+i+charlength)<=currentline.length() && position+i!=position){
                result.add(currentline.substring(position + i, position + i + charlength));
            }else{
                result.add("");
            }

        }
        //Boundary character bigram
        for(int i=-2*charlength;i<charlength;i+=charlength){
            if((position+i)>-1 && (position+i+2*charlength)<=currentline.length()-charlength){
                result.add(currentline.substring(position + i, position + i + 2*charlength));
            }else{
                result.add("");
            }
        }
        //c-1c0c1
        if((position-charlength)>-1 && (position+2*charlength)<=currentline.length()){
            result.add(currentline.substring(position-charlength,position+2*charlength));
        }else{
            result.add("");
        }
        //Boundary word unigram (if it is a word)
        for(int i=-2*charlength;i<3*charlength;i+=charlength){
            if((position+i)>-1 && (position+i+charlength)<=currentline.length() && position+i!=position && (dicthandler.matchWord(currentline.substring(position + i, position + i + charlength))!=null)){
                result.add(currentline.substring(position + i, position + i + charlength));
            }else{
                result.add("");
            }

        }
        //Boundary word bigram (if it is a word)
        for(int i=-2*charlength;i<charlength;i+=charlength){
            if((position+i)>-1 && (position+i+2*charlength)<=currentline.length()-charlength && (dicthandler.matchWord(currentline.substring(position + i, position + i + 2*charlength))!=null)){
                result.add(currentline.substring(position + i, position + i + 2*charlength));
            }else{
                result.add("");
            }
        }


        return result.toArray(new String[result.size()]);
    }*/

    /**
     * Chinese Segmentation with a Word-Based Perceptron Algorithm
     Yue Zhang and Stephen Clark
     Oxford University Computing Laboratory
     Wolfson Building, Parks Road
     Oxford OX1 3QD, UK
     {yue.zhang,stephen.clark}@comlab.ox.ac.uk
     word w
     word bigram w1 w2
     single-character word w
     a word starting with character c and having
     length l
     a word ending with character c and having
     length l
     space-separated characters c1 and c2
     character bigram c1 c2 in any word
     the first and last characters c1 and c2 of any
     word
     word w immediately before character c
     character c immediately before word w
     the starting characters c1 and c2 of two con-
     secutive words
     the ending characters c1 and c2 of two con-
     secutive words
     a word of length l and the previous word w
     a word of length l and the next word w
     * @return
     */
    /*public String[] perceptron(final Tuple<StringBuffer, Integer> tuple, final String segmented, DictHandling dicthandler, CharTypes charType){
        int charlength = charType.getChar_length();
        String currentline = tuple.getOne().toString();
        int position = tuple.getTwo();
        int counter = 0;
        List<String> result = new LinkedList<String>();
        System.out.println("Tuple: "+tuple);
        String positionchar=currentline.substring(position,position+charlength);
        //Word surrounding the current char
        String currentword=segmented.substring(segmented.indexOf(positionchar,position-charlength),segmented.indexOf(' ',segmented.indexOf(positionchar,position-charlength)));
        result.add(currentword);
        //Single character word?
        result.add(currentword.length()==charlength?"1":"0");
        //Word starting with c of length l
        for(String word:dicthandler.getCandidatesForChar(currentword.substring(0,charlength))){
            if(word.length()/charlength){

            }
        }
        //Word ending with c of length l

        //c1 c2
        if(currentword.length()>=charlength*3)
            result.add(currentword.substring(charlength,charlength*2)+" "+currentword.substring(charlength*2,charlength*3));
        else{
            result.add("");
        }
        //c-2,c-1,c1,c2
        for (int i = -2 * charlength; i < 3 * charlength; i += charlength) {
            if ((position + i) > -1 && (position + i + charlength) <= currentline.length() && position + i != position) {
                result.add(currentline.substring(position + i, position + i + charlength));
            }else{
                result.add("");
            }

        }
        //ci,ci+1 without the last pair
        for (int i = -2 * charlength; i < charlength; i += charlength) {
            if ((position + i) > -1 && (position + i + 2 * charlength) <= currentline.length() - charlength) {
                result.add(currentline.substring(position + i, position + i + 2 * charlength));
            }else{
                result.add("");
            }
        }
        //c-1c0c1
        if ((position - charlength) > -1 && (position + 2 * charlength) <= currentline.length()) {
            result.add(currentline.substring(position - charlength, position + 2 * charlength));
        }else{
            result.add("");
        }
        String conse
        //Starting characters c1 and c2 of two consecutive words
        for(String cand:dicthandler.getCandidatesForChar(currentword.substring(charlength,charlength*2))){
            if(dicthandler.matchWord(cand)!=null){
               for(Following fol:dicthandler.matchWord(cand).getFollowingWords()){
                   if(fol.getFollowingstr().startsWith(currentword.substring(charlength*2,charlength*3))){
                       result.add()
                   }
               }
            } dicthandler.matchWord(cand).getFollowingWords()
        }
        result
        //Word w immediately before c
        if(segmented.substring())
        return result.toArray(new String[result.size()]);
    } */

    /**
     * Enhancing Chinese Word Segmentation Using Unlabeled Data
     Weiwei Sun†‡ and Jia Xu‡
     †
     Department of Computational Linguistics, Saarland University
     ‡
     German Research Center for Artificial Intelligence (DFKI)
     D-66123, Saarbr ̈ ucken, Germany
     wsun@coli.uni-saarland.de, Jia.Xu@dfki.de
     • Character uni-grams: cs (i − 3 < s < i + 3)
     • Character bi-grams: cs cs+1 (i−3 < s < i+3)
     • Whether cs and cs+1 are identical, for i − 2 <
     s < i + 2.
     • Whether cs and cs+2 are identical, for i − 4 <
     s < i + 2.
     The word type features are listed as follows.
     • The identity of the string c[s:i] (i − 6 < s < i),
     if it matches a word from the list of uni-gram
     words;
     • The identity of the string c[i:e] (i < e < i + 6),
     if it matches a word; multiple features could be
     generated.
     • The identity of the bi-gram c[s:i−1]c[i:e] (i − 6 <
     s, e < i + 6), if it matches a word bi-gram from
     the list of uni-gram words.
     • The identity of the bi-gram c[s:i] c[i+1:e] (i − 6 <
     s, e < i + 6), if it matches a word bi-gram;
     multiple features could be generated.
     Mutual test
     Right/Left Accessor Variety
     Right/Left Punctuation vari

     */
    public String[] unlabeled(final Tuple<StringBuffer, Integer> tuple,final String segmented, DictHandling dicthandler, CharTypes charType){
        int charlength = charType.getChar_length();
        String currentline = tuple.getOne().toString();
        int position = tuple.getTwo();
        int counter = 0;
        List<String> result = new LinkedList<String>();
        System.out.println("Extract Unigrams:");
        //c-3,c-2,c-1,c0,c1,c2,c3
        for (int i = -3 * charlength; i <= 3 * charlength; i += charlength) {
            if ((position + i) > -1 && (position + i + charlength) <= currentline.length()) {
                //System.out.print(currentline.substring(position + i, position + i + charlength)+",");
                result.add(currentline.substring(position + i, position + i + charlength));
            }else{
                result.add("");
            }
        }
        System.out.println("Extract Bigrams:");
        //All bigrams ci,ci+1  in range
        for (int i = -3 * charlength; i < charlength*3; i += charlength) {
            if ((position + i) > -1 && (position + i + 2 * charlength) <= currentline.length() - charlength) {
                //System.out.print(currentline.substring(position + i, position + i + 2*charlength)+",");
                result.add(currentline.substring(position + i, position + i + 2 * charlength));
            }else{
                result.add("");
            }
        }
        System.out.println("Check if occured unigrams are the same");
        //Check if occured bigrams c_s and c_s+1 are the same
        String tempbigram=""/*=currentline.substring(position + -3*charlength, position + -3*charlength + 2 * charlength)*/,currentbigram;
        for (int i = -3 * charlength; i < charlength*3; i += charlength) {
            if ((position + i) > -1 && (position + i + charlength) <= currentline.length() - charlength) {
                currentbigram=currentline.substring(position + i, position + i + charlength);
                //System.out.println("Compare: "+tempbigram+" "+currentbigram+" - "+tempbigram.equals(currentbigram));
                if(tempbigram.equals(currentbigram)){
                    result.add("1");
                }else{
                    result.add("0");
                }
                tempbigram=currentbigram;
            }else{
                result.add("");
            }
        }
        System.out.println("Check if occured bigrams are the same");
        //Check if occured bigrams c_s and c_s+2 are the same
        tempbigram="";//currentline.substring(position + -3*charlength, position + -3*charlength + 2 * charlength);
        for (int i = -4 * charlength; i < charlength*2; i += charlength*2) {
            if ((position + i) > -1 && (position + i + 2 * charlength) <= currentline.length() - charlength) {
                currentbigram=currentline.substring(position + i, position + i + 2 * charlength);
                //System.out.println("Compare: "+tempbigram+" "+currentbigram+" - "+tempbigram.equals(currentbigram));
                if(tempbigram.equals(currentbigram)){
                    result.add("1");
                }else{
                    result.add("0");
                }
                tempbigram=currentbigram;
            }else{
                result.add("");
            }
        }
        System.out.println("Match unigram strings to identity");
        //Check if c-6 - c matches a unigram word
        for (int i = -6 * charlength; i < position; i += charlength) {
            if ((position + i) > -1 && (position + i + charlength) <= currentline.length() - charlength) {
                currentbigram=currentline.substring(position + i, position + i + charlength);
                //System.out.println("Match: "+currentbigram+" - "+dicthandler.matchWord(currentbigram));
                if(dicthandler.matchWord(currentbigram)!=null){
                    result.add(dicthandler.matchWord(currentbigram).getCharacter());
                }else{
                    result.add("");
                }
            }else{
                result.add("");
            }
        }
        System.out.println("Match word strings to identity");
        for (int i = 0; i < position+charlength*6; i += charlength) {
            for(int j=i+charlength;j<position+charlength*6;j+=charlength){
                if ((position + i) > -1 && (position + j) <= currentline.length() - charlength) {
                    currentbigram=currentline.substring(position + i, position+j);
                    //System.out.println("Match: "+currentbigram+" - "+dicthandler.matchWord(currentbigram));
                    if(dicthandler.matchWord(currentbigram)!=null){
                        result.add(dicthandler.matchWord(currentbigram).getCharacter());
                    }else{
                        result.add("");
                    }
                }else{
                    result.add("");
                }
            }
        }
        //Check if pairs of unigrams are matched
        for (int i = -6 * charlength; i < position+6*charlength; i += charlength) {
            if ((position + i) > -1 && (position + i + charlength) <= currentline.length() - charlength) {
                currentbigram=currentline.substring(position + i, position + i + charlength);
                //System.out.println("Match: "+currentbigram+" - "+dicthandler.matchWord(currentbigram));
                if(dicthandler.matchWord(tempbigram)!=null && dicthandler.matchWord(currentbigram)!=null){
                    result.add(dicthandler.matchWord(tempbigram).getCharacter()+dicthandler.matchWord(currentbigram).getCharacter());
                }else{
                    result.add("");
                }
                tempbigram=currentbigram;
            }else{
                result.add("");
            }
        }
        //Check if c-6 - c matches a bigram word
        for (int i = -6 * charlength; i < position+6*charlength; i += charlength) {
            if ((position + i) > -1 && (position + i + charlength) <= currentline.length() - charlength) {
                currentbigram=currentline.substring(position + i, position + i + charlength);
                //System.out.println("Match: "+currentbigram+" - "+dicthandler.matchWord(currentbigram));
                if(dicthandler.matchWord(currentbigram)!=null){
                    result.add(dicthandler.matchWord(currentbigram).getCharacter());
                }else{
                    result.add("");
                }
            }else{
                result.add("");
            }
        }
        System.out.println("Mutual information");
        //Mutual information
        tempbigram="";//currentline.substring(position + -3*charlength, position + -3*charlength + 2 * charlength);
        for (int i = -3 * charlength; i < charlength*3; i += charlength) {
            if ((position + i) > -1 && (position + i + 2 * charlength) <= currentline.length() - charlength) {
                currentbigram=currentline.substring(position + i, position + i + 2 * charlength);
                //System.out.println("Match: "+currentbigram+" - "+dicthandler.matchWord(currentbigram));
                if(dicthandler.matchWord(tempbigram)!=null && dicthandler.matchWord(currentbigram)!=null){
                    result.add(Math.log((dicthandler.matchWord(tempbigram).getFollowingWords()
                            .containsKey(currentbigram)?dicthandler.matchWord(tempbigram).getFollowingWords().get(currentbigram).getFollowing().getOne():0.)/(dicthandler.matchWord(tempbigram).getOccurances()+dicthandler.matchWord(currentbigram).getOccurances()))+"");
                }else{
                    result.add("0");
                }
                tempbigram=currentbigram;
            }else{
                result.add("");
            }
        }
        System.out.println("Accessor Variety Length 2");
        //Accessor Variety Length 2
        for (int i = -2 * charlength; i < charlength*2; i += charlength) {
            if ((position + i) > -1 && (position + i + 2 * charlength) <= currentline.length() - charlength) {
                currentbigram=currentline.substring(position + i, position + i + 2 * charlength);
                //System.out.println("Match: "+currentbigram+" - "+dicthandler.matchWord(currentbigram));
                if(dicthandler.matchWord(currentbigram)!=null){
                    if(i>0){
                        result.add(dicthandler.matchWord(currentbigram).getLeftaccessorvariety().toString());
                    }else{
                        result.add(dicthandler.matchWord(currentbigram).getRightaccessorvariety().toString());
                    }
                }else{
                    result.add("");
                }
            }else{
                result.add("");
            }
        }
        System.out.println("Accessor Variety Length 3");
        //Accessor Variety Length 3
        for (int i = -3 * charlength; i < charlength*3; i += charlength) {
            if ((position + i) > -1 && (position + i + 3 * charlength) <= currentline.length() - charlength) {
                currentbigram=currentline.substring(position + i, position + i + 3 * charlength);
                //System.out.println("Match: "+currentbigram+" - "+dicthandler.matchWord(currentbigram));
                if(dicthandler.matchWord(currentbigram)!=null){
                    if(i>0){
                        result.add(dicthandler.matchWord(currentbigram).getLeftaccessorvariety().toString());
                    }else{
                        result.add(dicthandler.matchWord(currentbigram).getRightaccessorvariety().toString());
                    }
                }else{
                    result.add("");
                }
            }else{
                result.add("");
            }
        }
        System.out.println("Accessor Variety Length 4");
        for (int i = -4 * charlength; i < charlength*4; i += charlength) {
            if ((position + i) > -1 && (position + i + 4 * charlength) <= currentline.length() - charlength) {
                currentbigram=currentline.substring(position + i, position + i + 4 * charlength);
                //System.out.println("Match: "+currentbigram+" - "+dicthandler.matchWord(currentbigram));
                if(dicthandler.matchWord(currentbigram)!=null){
                    if(i>0){
                        result.add(dicthandler.matchWord(currentbigram).getLeftaccessorvariety().toString());
                    }else{
                        result.add(dicthandler.matchWord(currentbigram).getRightaccessorvariety().toString());
                    }
                }else{
                    result.add("");
                }
            }else{
                result.add("");
            }
        }
        System.out.println("Punctuation Variety Length 2");
        //Accessor Variety Length 2
        for (int i = -2 * charlength; i < charlength*2; i += charlength) {
            if ((position + i) > -1 && (position + i + 2 * charlength) <= currentline.length() - charlength) {
                currentbigram=currentline.substring(position + i, position + i + 2 * charlength);
                //System.out.println("Match: "+currentbigram+" - "+dicthandler.matchStopChar(currentbigram));
                for(StopChar stop:dicthandler.getStopchars().values()){
                    if(stop.getPreceding().containsKey(currentbigram)){

                    }
                }
                if(dicthandler.matchStopChar(currentbigram)!=null){
                    if(i>0){
                        result.add(dicthandler.matchStopChar(currentbigram).getRightaccessorvariety().toString());
                    }else{
                        result.add(dicthandler.matchStopChar(currentbigram).getLeftaccessorvariety().toString());
                    }
                }else{
                    result.add("");
                }
            }else{
                result.add("");
            }
        }
        System.out.println("Punctuation Variety Length 3");
        //Accessor Variety Length 3
        for (int i = -3 * charlength; i < charlength*3; i += charlength) {
            if ((position + i) > -1 && (position + i + 3 * charlength) <= currentline.length() - charlength) {
                currentbigram=currentline.substring(position + i, position + i + 3 * charlength);
                //System.out.println("Match: "+currentbigram+" - "+dicthandler.matchStopChar(currentbigram));
                if(dicthandler.matchStopChar(currentbigram)!=null){
                    if(i>0){
                        result.add(dicthandler.matchStopChar(currentbigram).getRightaccessorvariety().toString());
                    }else{
                        result.add(dicthandler.matchStopChar(currentbigram).getLeftaccessorvariety().toString());
                    }
                }else{
                    result.add("");
                }
            }else{
                result.add("");
            }
        }
        System.out.println("Punctuation Variety Length 4");
        for (int i = -4 * charlength; i < charlength*4; i += charlength) {
            if ((position + i) > -1 && (position + i + 4 * charlength) <= currentline.length() - charlength) {
                currentbigram=currentline.substring(position + i, position + i + 4 * charlength);
                //System.out.println("Match: "+currentbigram+" - "+dicthandler.matchStopChar(currentbigram));
                if(dicthandler.matchStopChar(currentbigram)!=null){
                    if(i>0){
                        result.add(dicthandler.matchStopChar(currentbigram).getRightaccessorvariety().toString());
                    }else{
                        result.add(dicthandler.matchStopChar(currentbigram).getLeftaccessorvariety().toString());
                    }
                }else{
                    result.add("");
                }
            }else{
                result.add("");
            }
        }
        return result.toArray(new String[result.size()]);
    }
}
