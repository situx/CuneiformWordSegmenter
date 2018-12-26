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

package de.unifrankfurt.cs.acoli.akkad.methods.translation;

import de.unifrankfurt.cs.acoli.akkad.dict.chars.LangChar;
import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.dict.pos.util.POSDefinition;
import de.unifrankfurt.cs.acoli.akkad.methods.Methods;
import de.unifrankfurt.cs.acoli.akkad.methods.transcription.TranscriptionMethods;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.TranslationMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.TransliterationMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Files;

import java.io.*;
import java.util.List;
import java.util.Locale;

/**
 * Created by timo on 22.06.14.
 */
public class TranslationMethods extends Methods {

    private Locale fromLocale;
    private Locale toLocale;
    private BufferedWriter translationResultWriter;

    public TranslationMethods(){

    }

        private void POSTagLemmaTranslation(LangChar tempword,final String currentline,final DictHandling dicthandler,final TranslationMethod translationMethod, Locale locale) throws IOException {
            String[] words=currentline.split(" ");
            List<POSDefinition> posdeflist;
            StringBuffer result=new StringBuffer();
            for(String word:words){
                posdeflist=dicthandler.getPosTagger().getPosTag(word,dicthandler,true);
                for(POSDefinition posdef:posdeflist){
                    if(!posdef.getValue().isEmpty()){
                       result.append(posdef.getValue());
                    }
                }

                //System.out.print("Locale: "+locale.toString());
                if((tempword=dicthandler.matchWordByTransliteration(word.replace("]","").replace("[","")))!=null && tempword.getTranslationSet(locale)!=null && !tempword.getTranslationSet(locale).isEmpty()){
                    //System.out.print("Word: " + word.replace("]", "").replace("[", ""));
                    //System.out.println(" OK");
                    this.translationResultWriter.write("["+dicthandler.getDictTranslation(tempword,translationMethod,locale)+"] ");
                }else if((tempword=dicthandler.matchWordByTranscription(TranscriptionMethods.translitTotranscript(word.replace("]", "").replace("[", "")),true))!=null && tempword.getTranslationSet(locale)!=null && !tempword.getTranslationSet(locale).isEmpty()){
                    System.out.println("Non Cunei Word: "+word);
                    this.translationResultWriter.write("["+dicthandler.getDictTranslation(tempword,translationMethod,locale)+"] ");
                }else{
                    this.translationResultWriter.write(dicthandler.getNoDictTranslation(word,translationMethod,locale).trim()+" ");
                    //System.out.println(" Not OK");
                }
            }
            this.translationResultWriter.write("\n");
        }

    /**
     * Initializes parameters needed for parsing.
     * @param sourcepath the path of the sourcefile
     * @param destpath the path of the destination file
     * @param dicthandler the dicthandler to use
     * @throws java.io.IOException
     */
    public void initTranslation(final String sourcepath,final String destpath,final DictHandling dicthandler,final TranslationMethod translationMethod,final Locale locale) throws IOException {
        String currentsentence;
        LangChar tempchar=null;
        this.reader=new BufferedReader(new FileReader(new File(sourcepath)));
        File writefile=new File(Files.RESULTDIR.toString()+Files.TRANSLATIONDIR.toString()+locale.toString());
        writefile.mkdirs();
        writefile=new File(Files.RESULTDIR.toString()+Files.TRANSLATIONDIR.toString()+locale.toString()+File.separator+destpath);
        this.translationResultWriter =new BufferedWriter(new FileWriter(writefile));
        while((currentsentence=this.reader.readLine())!=null) {
            this.linecounter++;
            switch (translationMethod) {
                case LEMMAFIRST:
                case LEMMA:
                case MAXPROB:
                case LEMMARANDOM:
                default:   this.lemmaTranslation(null, currentsentence, dicthandler, translationMethod,locale);
            }
            this.translationResultWriter.write("\n");
        }
        this.translationResultWriter.close();
        this.reader.close();

    }

        private void lemmaTranslation(LangChar tempword,final String currentline,final DictHandling dicthandler,final TranslationMethod translationMethod,Locale locale) throws IOException {
              String[] words=currentline.split(" ");
              StringBuffer result=new StringBuffer();
              for(String word:words){

                  //System.out.print("Locale: "+locale.toString());
                  if((tempword=dicthandler.matchWordByTransliteration(word.replace("]","").replace("[","")))!=null && tempword.getTranslationSet(locale)!=null && !tempword.getTranslationSet(locale).isEmpty()){
                      //System.out.print("Word: " + word.replace("]", "").replace("[", ""));
                      //System.out.println(" OK");
                       this.translationResultWriter.write("["+dicthandler.getDictTranslation(tempword,translationMethod,locale)+"] ");
                  }else if((tempword=dicthandler.matchWordByTranscription(TranscriptionMethods.translitTotranscript(word.replace("]", "").replace("[", "")),true))!=null && tempword.getTranslationSet(locale)!=null && !tempword.getTranslationSet(locale).isEmpty()){
                      System.out.println("Non Cunei Word: "+word);
                      this.translationResultWriter.write("["+dicthandler.getDictTranslation(tempword,translationMethod,locale)+"] ");
                  }else{
                      this.translationResultWriter.write(dicthandler.getNoDictTranslation(word,translationMethod,locale).toString().trim()+" ");
                      //System.out.println(" Not OK");
                  }
              }


        }

    /**
     * MinMatch method.
     * @param filepath the path of the file to use
     * @param dicthandler the dicthandler to use
     * @throws IOException
     */
    public void lemmaTranslation(final String filepath,final DictHandling dicthandler,final TransliterationMethod transliterationMethod,final Locale locale) throws IOException {
        this.initTranslation(filepath, filepath.substring(filepath.lastIndexOf("/") + 1), dicthandler,TranslationMethod.LEMMA,locale);
    }


}
