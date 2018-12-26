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

package de.unifrankfurt.cs.acoli.akkad.main.gui;

import de.unifrankfurt.cs.acoli.akkad.main.gui.util.GUIFormat;
import de.unifrankfurt.cs.acoli.akkad.util.Tuple;

import javax.swing.*;
import java.awt.*;

/**
 * Option view for the main program.
 */
public class OptionsGUI extends GUIFormat {
    /**
     * Constructor for this class.
     */
    public OptionsGUI(){
        this.buildMenu();
        setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setTitle(bundle.getString("options"));
        this.setSize(700,450);
        this.setLayout(new GridBagLayout());
        JPanel mainPanel=new JPanel();
        mainPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        JScrollPane framescroller=new JScrollPane(mainPanel);
        // add it to our application
        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(800,600));
        setContentPane(tabbedPane);
        System.out.println("Tabbed Pane yeah!");
        tabbedPane.addTab(bundle.getString("corpus"), framescroller);
        JList<Tuple<String,String>> corpuslist=new JList<>();
        this.setPreferredSize(new Dimension(800,600));
        pack();
    }
}
