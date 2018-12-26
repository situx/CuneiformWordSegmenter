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
import de.unifrankfurt.cs.acoli.akkad.dict.importhandler.asian.CNImportHandler;
import de.unifrankfurt.cs.acoli.akkad.util.Tuple;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;

import java.util.LinkedList;
import java.util.List;

import static de.unifrankfurt.cs.acoli.akkad.util.ArffHandler.arrayToStr;

/**
 * Created by timo on 13.08.14.
 */
public class CharBasedFeatureSet extends FeatureSet {
    /**
     * Constructor for this class.
     *
     * @param statusValues    the class values
     * @param attributes      the number of features
     * @param name
     * @param wordOrCharBased
     */
    public CharBasedFeatureSet(final String[] statusValues, final Integer attributes, final String name,final Boolean wordOrCharBased) {
        super(statusValues, attributes, name,wordOrCharBased);
    }

    /**
     * Chinese Segmentation and New Word Detection
     * using Conditional Random Fields
     * Fuchun Peng
     * University of Massachusetts - Amherst
     * C−2: second previous character in lexicon
     * C−1: previous character in lexicon
     * C1 : next character in lexicon
     * C2 : second next character in lexicon
     * C0 C1: current and next character in lexicon
     * C−1C0 : current and previous character in lexicon
     * C−2C−1 :previous two characters in lexicon
     * C−1C0 C1 : previous, current, and next character in the lexicon
     *
     * @param tuple
     * @param dicthandler
     * @param charType
     * @return
     */
    /*protected String[] crf(final Tuple<StringBuffer, Integer> tuple, DictHandling dicthandler, CharTypes charType) {
        int charlength = charType.getChar_length();
        String currentline = tuple.getOne().toString();
        int position = tuple.getTwo();
        int counter = 0;
        List<String> result = new LinkedList<String>();
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

        return result.toArray(new String[result.size()]);
    }*/

    /**
     * Constructs a feature line for the maxentropy feature set.
     * Based on the same features as maxentprev/maxent2 except for not considering previous classifications
     * @param tuple the tuple consisting of the String and the number of features
     * @param dicthandler the dicthandler to use
     * @return An array of String containing a feature line
     */
    protected String[] maxent(final Tuple<StringBuffer,Integer> tuple,DictHandling dicthandler,CharTypes charType){
        StringBuffer currentline = tuple.getOne();
        int charlength=charType.getChar_length();
        int position = tuple.getTwo();
        //Initializing the result array
        String[] result=new String[17];
        for(int j=0;j<result.length;j++){
            result[j]="";
        }
        //Collect features
        int counter=0;
        for (int i=-2*charlength;i<=2*charlength;i+=charlength){
            if((position+i)>-1 && (position+i+2)<currentline.length()){
                result[counter++]=currentline.substring(position+i,position+i+charlength);
                if((position+i+2*charlength)<currentline.length())
                    result[counter+2*charlength]=currentline.substring(position+i,position+i+2*charlength);
            }
        }
        if (position>0) {
            if(position<currentline.length()-2*charlength){
                result[11]=currentline.substring(position-charlength,position)+" "+currentline.substring(position+charlength,position+2*charlength);
            }
        }
        Integer maxlength=0;
        //Words of length 2
        for (int i=-2*charlength;i<2*charlength;i+=charlength){
            if((position+i)>-1 && (position+i+charlength)<=(currentline.length()-charlength)){
                System.out.println("Currentline.substring("+(position+i)+","+(position+charlength)+") "+currentline.length()+"");
                System.out.println("Currentline.substring("+(position+i)+","+(position+i+charlength)+") "+currentline.length()+"");
                System.out.println("Currentline.substring(): "+currentline.substring((position+i),(position+i+charlength)));
                if(i<=0 && dicthandler.matchWord(currentline.substring(position+i,position+i+charlength))!=null){
                    result[counter++]=currentline.substring(position+i,position+i+charlength);
                    if(maxlength<result[counter-1].length()){
                        maxlength=result[counter-1].length();
                    }
                    System.out.println("Add: "+result[counter-1]);
                }
                if(i>0 && dicthandler.matchWord(currentline.substring(position,position+i+charlength))!=null){
                    result[counter++]=currentline.substring(position,position+i+charlength);
                    if(maxlength<result[counter-1].length()){
                        maxlength=result[counter-1].length();
                    }
                    System.out.println("Add: "+result[counter-1]);
                }
            }
        }
        if((position-charlength)>=0 && (position+2*charlength)<currentline.length() && dicthandler.matchWord(currentline.substring(position-charlength,position+2*charlength))!=null){
            result[counter++]=currentline.substring(position-charlength,position+2*charlength);
            if(maxlength<result[counter-1].length()){
                maxlength=result[counter-1].length();
            }
            System.out.println("Add: "+result[counter-1]);
        }
        result[counter++]=maxlength.toString();
        arrayToStr(result);
        return result;
    }

    /**
     * A Dictionary-Augmented Maximum Entropy
     * Tagging Approach to Chinese Word Segmentation
     * Aaswath Raman
     * January 11, 2006
     * a) ci (i = −2, −1, 0, 1, 2)
     * b) ci ci+1 (i = −2, −1, 0, 1, 2)
     * c) c−1c1
     * d) ti (i = −2, −1)
     * e) whether the character (c0) is a punctuation mark
     * f) whether the character (c0) is a numeral
     * g) whether the character (c0) is a Latin letter
     */
    protected String[] maxent2(final Tuple<StringBuffer, Integer> tuple, DictHandling dicthandler, CharTypes charType) {
        int charlength = charType.getChar_length();
        String currentline = tuple.getOne().toString();
        int position = tuple.getTwo();
        int counter = 0;
        List<String> result = new LinkedList<String>();
        //c-2,c-1,c0,c1,c2
        for (int i = -2 * charlength; i < 3 * charlength; i += charlength) {
            if ((position + i) > -1 && (position + i + charlength) <= currentline.length()) {
                result.add(currentline.substring(position + i, position + i + charlength));
            }else{
                result.add("");
            }

        }
        //ci,ci+1
        for (int i = -2 * charlength; i < 2 * charlength; i += charlength) {
            if ((position + i) > -1 && (position + i + 2 * charlength) <= currentline.length()) {
                result.add(currentline.substring(position + i, position + i + 2 * charlength));
            }else{
                result.add("");
            }
        }
        //c-1,c1
        if ((position - charlength) > -1 && (position + 2 * charlength) <= currentline.length()) {
            result.add(currentline.substring(position - charlength, position) + currentline.substring(position + charlength, position + 2 * charlength));
        }else{
            result.add("");
        }
        //ti-1,ti-2 The tags consist of the previous ti-1 and ti-2 classifications if available
        if (!prepreviousclassification.isEmpty())
            result.add(prepreviousclassification);
        else{
            result.add("");
        }
        if (!previousclassification.isEmpty())
            result.add(previousclassification);
        else{
            result.add("");
        }
        String currentchar = currentline.substring(position, position + charlength);
        //Is PunctuationMark?
        if (charType.getStopchars().contains(currentchar)) {
            result.add("1");
        } else {
            result.add("0");
        }
        //Is Numeral?
        if (CNImportHandler.isFullWidthNumeric(currentchar)) {
            result.add("1");
        } else {
            result.add("0");
        }
        //Is Latin Character?
        if (currentchar.matches("[A-Za-z]")) {
            result.add("1");
        } else {
            result.add("0");
        }
        return result.toArray(new String[result.size()]);
    }

    /**
     * A Maximum Entropy Approach to Chinese Word Segmentation
     1 1,2 2
     Jin Kiat Low and Hwee Tou Ng and Wenyuan Guo1. Department of Computer Science, National University of Singapore,
     3 Science Drive 2, Singapore 117543
     2. Singapore-MIT Alliance, E4-04-10, 4 Engineering Drive 3, Singapore 117576
     {lowjinki, nght, guowy}@comp.nus.edu.sg
     (a) Cn (n = −2, −1, 0, 1, 2)
     (b) CnCn+1 (n = −2, −1, 0, 1)
     (c) C−1C1
     (d) P u(C0 )
     (e) T (C−2)T (C−1 )T (C0 )T (C1 )T (C2)
     (f) Lt0
     (g) Cnt0 (n = −1, 0, 1)
     * @return
     */
    protected String[] maxentSighan(final Tuple<StringBuffer,Integer> tuple,DictHandling dicthandler,CharTypes charType){
        int charlength=charType.getChar_length();
        String currentline=tuple.getOne().toString();
        int position=tuple.getTwo();
        String currentchar=currentline.substring(position,position+charlength);
        List<String> result=new LinkedList<String>();
        //c-2,c-1,c0,c1,c2
        for(int i=-2*charlength;i<3*charlength;i+=charlength){
            if((position+i)>-1 && (position+i+charlength)<=currentline.length()){
                result.add(currentline.substring(position + i, position + i + charlength));
            }else{
                result.add("");
            }
        }
        //ci,ci+1 without the last pair
        for(int i=-2*charlength;i<2*charlength;i+=charlength){
            if((position+i)>-1 && (position+i+2*charlength)<=currentline.length()-charlength){
                result.add(currentline.substring(position + i, position + i + 2*charlength));
            }else{
                result.add("");
            }
        }
        //c-1,c1
        if((position-charlength)>-1 && (position+2*charlength)<=currentline.length()){
            result.add(currentline.substring(position-charlength,position)+currentline.substring(position+charlength,position+2*charlength));
        }else{
            result.add("");
        }
        //Is PunctuationMark?
        if(charType.getStopchars().contains(currentchar)){
            result.add("1");
        }else{
            result.add("0");
        }
        //Character classes 0=Number 1=Date 2=LatinChar 3=Other
        for(int i=-2*charlength;i<3*charlength;i+=charlength){
            if((position+i)>-1 && (position+i+charlength)<=currentline.length()){
                currentchar=currentline.substring(position + i, position + i + charlength);
                if(CNImportHandler.isFullWidthNumeric(currentchar)){
                    result.add("0");
                }else if(currentchar.matches("[A-Za-z][Ａ-ｚ]")){
                    result.add("2");
                }else{
                    result.add("3");
                }
            }else{
                result.add("");
            }
        }
        //Features from the dictionary
        //Length of the longest found word
        Integer maxlength=0;
        String maxword="";
        for (int i=-2*charlength;i<2*charlength;i+=charlength){
            if((position+i)>-1 && (position+i+charlength)<=(currentline.length()-charlength)){
                System.out.println("Currentline.substring("+(position+i)+","+(position+charlength)+") "+currentline.length()+"");
                System.out.println("Currentline.substring("+(position+i)+","+(position+i+charlength)+") "+currentline.length()+"");
                System.out.println("Currentline.substring(): "+currentline.substring((position+i),(position+i+charlength)));
                if(i<=0 && dicthandler.matchWord(currentline.substring(position+i,position+i+charlength))!=null){
                    //result.add(currentline.substring(position+i,position+i+charlength));
                    if(maxlength<result.get(result.size()-1).length()){
                        maxlength=result.get(result.size()-1).length();
                        maxword=currentline.substring(position+i,position+i+charlength);
                    }
                    System.out.println("Add: "+result.get(result.size()-1));
                }
                if(i>0 && dicthandler.matchWord(currentline.substring(position,position+i+charlength))!=null){
                    //result.add(currentline.substring(position,position+i+charlength));
                    if(maxlength<result.get(result.size()-1).length()){
                        maxlength=result.get(result.size()-1).length();
                        maxword=currentline.substring(position+i,position+i+charlength);
                    }
                    System.out.println("Add: "+result.get(result.size()-1));
                }
            }

        }
        result.add(maxlength.toString());
        //Previous, following and identity of the character in position included in the longest found word
        Integer foundpos=currentline.indexOf(maxword);//maxword.indexOf(currentline.substring(position,position+charlength))-1;
        if(foundpos-charlength>0){
            result.add(currentline.substring(foundpos-charlength,foundpos));
        }else{
            result.add("");
        }
        if((foundpos+charlength)<=currentline.length()){
            result.add(currentline.substring(foundpos,foundpos+charlength));
        }else{
            result.add("");
        }
        if((position+2*charlength)<=currentline.length()){
            result.add(currentline.substring(foundpos+charlength,foundpos+2*charlength));
        }else{
            result.add("");
        }
        /*result.add(maxword.sub)
        if((position-charlength)>=0 && (position+2*charlength)<currentline.length() && dicthandler.matchWord(currentline.substring(position-charlength,position+2*charlength))!=null){
            result.add(currentline.substring(position-charlength,position+2*charlength));
            if(maxlength<result.get(result.size()-1).length()){
                maxlength=result.get(result.size()-1).length();
            }
            System.out.println("Add: "+result.get(result.size()-1));
        }
        result.add(maxlength.toString());
        */
        return result.toArray(new String[result.size()]);
    }

    /**
     * Feature Set Generator for CRF used in training a perceptron in Training a Perceptron with Global and Local Features
     * for Chinese Word Segmentation
     * Dong Song and Anoop Sarkar
     * School of Computing Science, Simon Fraser University
     * Burnaby, BC, Canada V5A1S6
     * Includes c0 , c-1 , c1 ,c−2 , c2, c−1 c0, c0 c1 , c−1 c1 , c−2 c−1 and c0 c2.
     * where c0 is the current character
     *
     * @param tuple
     * @param dicthandler
     * @param charType
     * @return
     */
    protected String[] perceptronCRF(final Tuple<StringBuffer, Integer> tuple, DictHandling dicthandler, CharTypes charType) {
        int charlength = charType.getChar_length();
        String currentline = tuple.getOne().toString();
        int position = tuple.getTwo();
        int counter = 0;
        List<String> result = new LinkedList<String>();
        //c-2,c-1,c0,c1,c2
        for (int i = -2 * charlength; i < 3 * charlength; i += charlength) {
            if ((position + i) > -1 && (position + i + charlength) <= currentline.length()) {
                result.add(currentline.substring(position + i, position + i + charlength));
            }else{
                result.add("");
            }
        }
        //c-1,c0
        if ((position - charlength) > -1) {
            result.add(currentline.substring(position - charlength, position + charlength));
        }else{
            result.add("");
        }
        //c0,c1
        if (position + 2 * charlength < currentline.length()) {
            result.add(currentline.substring(position, position + 2 * charlength));
        }else{
            result.add("");
        }
        //c-1,c1
        if ((position - charlength) > -1 && (position + 2 * charlength) <= currentline.length()) {
            result.add(currentline.substring(position - charlength, position) + currentline.substring(position + charlength, position + 2 * charlength));
        }else{
            result.add("");
        }
        //c-2,c-1
        if ((position - 2 * charlength) > -1) {
            result.add(currentline.substring(position - 2 * charlength, position));
        }else{
            result.add("");
        }
        //c0,c2
        if ((position + 3 * charlength) < currentline.length()) {
            result.add(currentline.substring(position, position + charlength) + currentline.substring(position + 2 * charlength, position + 3 * charlength));
        }else{
            result.add("");
        }
        return result.toArray(new String[result.size()]);
    }
}