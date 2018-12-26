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

package de.unifrankfurt.cs.acoli.akkad.main.gui.ime;

import java.awt.*;
import java.awt.im.spi.InputMethod;
import java.awt.im.spi.InputMethodDescriptor;
import java.util.Locale;

public class GenericInputMethodDescriptor implements InputMethodDescriptor {
    public static final Locale HITTITE = new Locale("hit", "", "x-hit-latin");
    public static final Locale SUMERIAN = new Locale("sum", "", "x-sum-latin");
    public static Locale AKKADIAN = new Locale("akk", "", "x-akk-latin");

    public GenericInputMethodDescriptor() {
        System.setProperty("java.awt.im.style",
                "below-the-spot");
    }

    /**
     * @see java.awt.im.spi.InputMethodDescriptor#
     */
    public InputMethod createInputMethod() throws Exception {
        System.out.println("Creating Cuneiform input method");
        return new GenericInputMethod();
    }

    /**
     * @see java.awt.im.spi.InputMethodDescriptor#getAvailableLocales
     */
    public Locale[] getAvailableLocales() {
        Locale[] locales = {
                AKKADIAN,HITTITE,SUMERIAN
        };
        return locales;
    }

    /**
     * @see java.awt.im.spi.InputMethodDescriptor#getInputMethodDisplayName
     */
    public synchronized String getInputMethodDisplayName(Locale inputLocale, Locale displayLanguage) {
        String localeName = null;
        if (inputLocale == AKKADIAN) {
            localeName = "Akkadian";
        }else if (inputLocale == HITTITE) {
            localeName = "Hittite";
        } else if (inputLocale == SUMERIAN) {
            localeName = "Sumerian";
        } else if (localeName != null) {

            return "Cuneiform - " + localeName;
        } else {
            return "Cuneiform";
        }
        return null;
    }

    /**
     * @see java.awt.im.spi.InputMethodDescriptor#getInputMethodIcon
     */
    public Image getInputMethodIcon(Locale inputLocale) {
        return null;
    }

    /**
     * @see java.awt.im.spi.InputMethodDescriptor#hasDynamicLocaleList
     */
    public boolean hasDynamicLocaleList() {
        return false;
    }
}