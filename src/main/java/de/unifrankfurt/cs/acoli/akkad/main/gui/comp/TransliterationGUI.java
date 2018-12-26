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

package de.unifrankfurt.cs.acoli.akkad.main.gui.comp;

import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.eval.EvalResult;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.MethodEnum;

import java.util.List;

/**
 * Class for evaluating transliteration results.
 */
public class TransliterationGUI extends CompGUI {
    /**
     * Constructor for this class.
     * @param generatedFile the generated file to load
     * @param originalfile  the original file to load
     * @param selectedMethods the selected methods to evaluate
     */
    public TransliterationGUI(final String originalfile, final String generatedFile, final List<MethodEnum> selectedMethods, final EvalResult evalResult) {
        super(originalfile, generatedFile, selectedMethods, evalResult,null);
    }

    @Override
    public void paintResultArea(final DictHandling dictHandler) {

    }
}
