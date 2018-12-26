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

package de.unifrankfurt.cs.acoli.akkad.dict.pos.util;

import java.util.regex.Pattern;

/**
 * Created by timo on 11.10.14.
 * POSTagging Groups to further analyse an already POSTagged word.
 */
public class GroupDefinition {
    /**The description of the group.*/
    private String description;
    /**An equals condition.*/
    private String equals;
    /**The case of the group.*/
    private String groupCase;
    /**The regular expression to detect an attribute.*/
    private Pattern regex;
    /**A group value.*/
    private String value;

    /**
     * Constructor for this class.
     * @param regex the group regex
     * @param equals the equals string
     * @param description the group description
     * @param groupCase the group case
     * @param value the group value
     */
    public GroupDefinition(final String regex, final String equals, final String description,final String groupCase,final String value) {
        if(regex!=null)
            this.regex = Pattern.compile(regex);
        else
            this.regex=Pattern.compile(".*");
        if(equals!=null)
            this.equals = equals;
        else
            this.equals="";
        this.description = description;
        this.groupCase=groupCase;
        this.value=value;

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getEquals() {
        return equals;
    }

    public void setEquals(final String equals) {
        this.equals = equals;
    }

    public String getGroupCase() {
        return groupCase;
    }

    public void setGroupCase(final String groupCase) {
        this.groupCase = groupCase;
    }

    public Pattern getRegex() {

        return regex;
    }

    public void setRegex(final String regex) {
        this.regex = Pattern.compile(regex);
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.description;
    }
}
