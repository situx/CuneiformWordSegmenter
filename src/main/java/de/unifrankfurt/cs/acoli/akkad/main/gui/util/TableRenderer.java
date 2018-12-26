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

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.regex.Pattern;

/**
 * Table renderer dealing with appropriate coloring of columns and cells.
 */
public class TableRenderer extends DefaultTableCellRenderer {
    /**Pattern for cells to be colored.*/
    private static final Pattern DOUBLE_PATTERN = Pattern.compile(
            "[\\x00-\\x20]*[+-]?(NaN|Infinity|((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)" +
                    "([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|" +
                    "(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))" +
                    "[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*");
    /**The current default background color used for description cells.*/
    Color backgroundColor = getBackground();

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
        TableModel model = (TableModel) table.getModel();
        if(model.getValueAt(row, column)==null){
            c.setBackground(backgroundColor);
        }else if (DOUBLE_PATTERN.matcher(model.getValueAt(row, column).toString()).matches() && Double.valueOf(model.getValueAt(row,column).toString())>=75.) {
            c.setBackground(Color.green);
        } else if (DOUBLE_PATTERN.matcher(model.getValueAt(row, column).toString()).matches() && Double.valueOf(model.getValueAt(row,column).toString())<75. && Double.valueOf(model.getValueAt(row,column).toString())>=50.) {
            c.setBackground(Color.yellow);
        }else if (DOUBLE_PATTERN.matcher(model.getValueAt(row, column).toString()).matches() && Double.valueOf(model.getValueAt(row,column).toString())<50. && Double.valueOf(model.getValueAt(row,column).toString())>=25.) {
                c.setBackground(Color.orange);
        } else if (DOUBLE_PATTERN.matcher(model.getValueAt(row, column).toString()).matches() && Double.valueOf(model.getValueAt(row,column).toString())<25. && Double.valueOf(model.getValueAt(row,column).toString())>=0.) {
            c.setBackground(Color.red);
        }else{
            c.setBackground(backgroundColor);
        }
        return c;
    }


}
