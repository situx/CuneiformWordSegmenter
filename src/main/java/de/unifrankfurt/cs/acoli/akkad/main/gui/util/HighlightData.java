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

package de.unifrankfurt.cs.acoli.akkad.main.gui.util;

import de.unifrankfurt.cs.acoli.akkad.dict.utils.POSTag;
import de.unifrankfurt.cs.acoli.akkad.util.enums.pos.POSTags;

/**
 * Class for storing data to be highlighted.
 */
public class HighlightData{
    /**Start position of the highlight.*/
    private Integer start;
    /**End position of the highlight.*/
    private Integer end;
    /**POSTag of the highlight.*/
    private POSTags posTag;
    /**Word to be highlighted.*/
    private String word;
    /**Constructor for this class.*/
    public HighlightData(final Integer start, final Integer end, final String colorrgb,final POSTags postag,final String word) {
        this.start = start;
        this.end = end;
        this.tag = colorrgb;
        this.posTag=postag;
        this.word=word;
    }
    /**POSTag as String.*/
    private String tag;

    public Integer getStart() {
        return start;
    }

    public void setStart(final Integer start) {
        this.start = start;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(final Integer end) {
        this.end = end;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(final String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return start+" - "+end+" - "+tag;
    }
}
