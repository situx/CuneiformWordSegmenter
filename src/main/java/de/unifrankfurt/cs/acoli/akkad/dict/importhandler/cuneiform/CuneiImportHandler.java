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

import de.unifrankfurt.cs.acoli.akkad.dict.chars.cuneiform.CuneiChar;
import de.unifrankfurt.cs.acoli.akkad.dict.importhandler.ImportHandler;

import java.util.Map;

/**
 * Created by timo on 24.06.14.
 */
public class CuneiImportHandler extends ImportHandler {

    protected Map<String,CuneiChar> resultmap;
    protected Map<String,String> transcriptToCuneiMap;
    protected Map<String,String> translitToCuneiMap;




    /**
     * Reformats a string to the ATF format.
     * @param transcription the String to reformat
     * @return the reformatted String
     */
    @Override
    public String reformatToASCIITranscription(final String transcription) {
        String result=transcription;
        int i=0,length=0;
        if(transcription.isEmpty()){
            return "";
        }
        result=transcription.replace("!", "").replace("#","").replaceAll("\\*","");
        result=result.replaceAll("š","sz").replaceAll("Š","SZ").replaceAll("ṣ","s,").replaceAll("Ṣ","S,")
                .replaceAll("ḫ","h").replaceAll("Ḫ","H").replaceAll("ĝ","g").replaceAll("ṭ","t,").replaceAll("Ṭ","T,");
        result=result.replaceAll("â","a").replaceAll("ā","a").replaceAll("á","a2").replaceAll("à","a3")
                .replaceAll("ê","e").replaceAll("ē","e").replaceAll("é","e2").replaceAll("è","e3")
                .replaceAll("î","i").replaceAll("ī","i").replaceAll("í","i2").replaceAll("ì","i3")
                .replaceAll("û","u").replaceAll("ū", "u").replaceAll("ú","u2").replaceAll("ù","u3");
        result=result.replaceAll("₀", "0").replaceAll("₁","1").replaceAll("₂","2").replaceAll("₃","3")
                .replaceAll("₄","4").replaceAll("₅","5").replaceAll("₆","6").replaceAll("₇","7").replaceAll("₈","8").replaceAll("₉","9");
        length=result.length();
        while(!(length<2) && Character.isDigit(result.toCharArray()[length-1])){
            length-=1;
        }
        for(i=0;i<length;i++){
            if(Character.isDigit(result.charAt(i))){
                result.replace(""+result.charAt(i),"");
                result+=result.charAt(i);
            }
        }
        return result.toLowerCase();
    }


    @Override
    public String reformatToUnicodeTranscription(final String transcription) {
        String result=transcription;
        int i=0,length=0;
        result=transcription.replace("!","").replace("#","");
        result=result.replaceAll("sz","š").replaceAll("SZ","Š").replaceAll("s,","ṣ").replaceAll("S,","Ṣ").
                replaceAll("h","ḫ").replaceAll("H","Ḫ").replaceAll("ĝ","g").replaceAll("t,", "ṭ").replaceAll("T,", "Ṭ");
        result=result.replaceAll("a:","ā").replaceAll("a2","á").replaceAll("a3","à")
                .replaceAll("e:","ē").replaceAll("e2","é").replaceAll("e3","è")
                .replaceAll("i:", "ī").replaceAll("i2,", "í").replaceAll("i3", "ì")
                .replaceAll("u:", "ū").replaceAll("u2", "ú").replaceAll("u3,","ù");
        result=result.replaceAll("0", "₀").replaceAll("1", "₁").replaceAll("2", "₂").replaceAll("3", "₃")
                .replaceAll("4", "₄").replaceAll("5", "₅").replaceAll("6", "₆").replaceAll("7", "₇").replaceAll("8", "₈").replaceAll("9","₉");
        length=result.length();
        while(Character.isDigit(result.toCharArray()[length-1])){
            length-=1;
        }
        for(i=0;i<length;i++){
            if(Character.isDigit(result.charAt(i))){
                result.replace(""+result.charAt(i),"");
                result+=result.charAt(i);
            }
        }
        return result.toLowerCase();
    }


}
