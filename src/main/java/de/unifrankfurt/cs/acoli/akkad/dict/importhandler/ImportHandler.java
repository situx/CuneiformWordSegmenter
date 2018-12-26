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

package de.unifrankfurt.cs.acoli.akkad.dict.importhandler;

import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import org.xml.sax.ext.DefaultHandler2;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * ImportHandler for dictionary files.
 */
public abstract class ImportHandler extends DefaultHandler2 {
    /**The dicthandler to import to.*/
    public DictHandling dictHandler;

    /**Formats a double value to the corresponding trailing digits.
     *
     * @param d the double value to format
     * @return
     */
    public static String formatDouble(Double d){
        if(d==null){
            return "";
        }
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        if(d==Double.POSITIVE_INFINITY){
            return d.toString();
        }
        return new DecimalFormat("#0.000000",otherSymbols).format(d);
    }

    /**
     * Formats unicode ATF to ASCII atf.
     * @param transcription the string to format
     * @return the ascii atf
     */
    public abstract String reformatToASCIITranscription(String transcription);
    /**
     * Formats SCII ATF to Unicode atf.
     * @param transcription the string to format
     * @return the unicode atf
     */
    public abstract String reformatToUnicodeTranscription(String transcription);
}
