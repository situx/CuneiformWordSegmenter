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

package de.unifrankfurt.cs.acoli.akkad.util;

import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by timo on 2/12/15
 * Converts mallet result files to cuneiresult files with the help of the original file..
 */
public class MalletToCunei {

    public static void main(String[] args) throws IOException {
          MalletToCunei mal=new MalletToCunei();
          mal.arffToMallet("/home/timo/ownCloud/mbab/crf/corpus_train80_maxentprev.arff","/home/timo/ownCloud/mbab/crf/corpus_train800_maxentprev_crf.arff");
          //mal.retransFormToCunei("/home/timo/ownCloud/neoass/crf/res5.txt","/home/timo/ownCloud/mbab/crf/corpus_test20.atf",CharTypes.AKKADIAN,null);
    }

    /**Converts from an arff file to a mallet file.*
     *
     * @param input the input arff file
     * @param output the output mallet file
     * @throws IOException
     */
    public void arffToMallet(final String input, final String output) throws IOException {
        BufferedReader reader=new BufferedReader(new FileReader(new File(input)));
        BufferedWriter writer=new BufferedWriter(new FileWriter(new File(output)));
        String temp;
        while((temp=reader.readLine())!=null){
            temp=temp+","+temp.charAt(0);
            temp=temp.substring(2);
            temp=temp.replace(","," ");
            temp=temp.replace("'","");
            writer.write(temp+System.lineSeparator());
        }
        reader.close();
        writer.close();
    }

    /**
     * Retransforms the result of a mallet classification to the cuneiform result file.
     * @param boundarypath  the boundarypath to use
     * @param textpath the cuneiform text to use
     * @param charTypes  the language to use
     * @param dictHandling  the dicthandler to use
     * @throws IOException
     */
    public void retransFormToCunei(String boundarypath, String textpath,CharTypes charTypes,DictHandling dictHandling) throws IOException {
        BufferedReader reader=new BufferedReader(new FileReader(new File(boundarypath)));
        String temp;
        List<Boolean> seplist=new LinkedList<>();
        while((temp=reader.readLine())!=null){
               if(temp.isEmpty())
                   continue;
               if(temp.charAt(0)=='1'){
                  seplist.add(true);
               }else{
                   seplist.add(false);
               }
        }
        reader.close();
        reader=new BufferedReader(new FileReader(new File(textpath)));
        BufferedWriter writer=new BufferedWriter(new FileWriter(new File("/home/timo/ownCloud/oldakk/crf/mbab_test20_crf.txt")));
        int counter=0;
        while((temp=reader.readLine())!=null){
             temp=temp.replace(" ","");
             if(temp.isEmpty())
                 continue;
             for(int i=0;i<=temp.length()-charTypes.getChar_length();i+=charTypes.getChar_length()){
                 writer.write(temp.substring(i,i+charTypes.getChar_length()));
                 if(i<temp.length()-charTypes.getChar_length()) {
                     if (counter < seplist.size() - charTypes.getChar_length() && seplist.get(counter)) {
                         writer.write(" ");
                     }
                     counter++;
                 }
             }
            writer.write(" " + System.lineSeparator());
        }
        reader.close();
        writer.close();
    }


}
