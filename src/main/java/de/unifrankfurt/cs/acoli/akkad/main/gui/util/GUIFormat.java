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

import de.unifrankfurt.cs.acoli.akkad.main.gui.OptionsGUI;
import de.unifrankfurt.cs.acoli.akkad.main.gui.ime.IMEExport;
import de.unifrankfurt.cs.acoli.akkad.main.gui.tool.EvaluationMain;
import de.unifrankfurt.cs.acoli.akkad.main.gui.tool.POSTagMain;
import de.unifrankfurt.cs.acoli.akkad.util.Config;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Base class for all GUI windows for formatting.
 */
public class GUIFormat extends JFrame{
    /**The resource bundle to use.*/
    public ResourceBundle bundle=ResourceBundle.getBundle(Config.RESBUNDLENAME,Locale.getDefault(),new UTF8Bundle("UTF-8"));
    /**The menubar to use.*/
    private JMenuBar menuBar;
    /**Builds the menubar and everything GUI related.*/
    public void buildMenu(){
        this.menuBar = new JMenuBar();
        this.setIconImage(new ImageIcon("img/akkadian.png").getImage());
        ImageIcon icon = new ImageIcon("exit.png");

        JMenu file = new JMenu(bundle.getString("file"));
        file.setMnemonic(KeyEvent.VK_F);

        JMenu singlemethods = new JMenu(bundle.getString("singlemethods"));
        singlemethods.setMnemonic(KeyEvent.VK_S);

        JMenu about = new JMenu(bundle.getString("help"));
        about.setMnemonic(KeyEvent.VK_A);

        JMenuItem optionitem = new JMenuItem(bundle.getString("options"), icon);
        optionitem.setMnemonic(KeyEvent.VK_O);
        optionitem.setToolTipText(bundle.getString("options"));
        optionitem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                OptionsGUI optionsGUI = new OptionsGUI();
                optionsGUI.setVisible(true);
            }
        });

        JMenuItem postagger = new JMenuItem(bundle.getString("postagger"), icon);
        postagger.setMnemonic(KeyEvent.VK_O);
        postagger.setToolTipText(bundle.getString("postagger"));
        postagger.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                POSTagMain postagger=new POSTagMain();
                postagger.setVisible(true);
            }
        });

        JMenuItem evaluation = new JMenuItem(bundle.getString("evaluation"), icon);
        evaluation.setMnemonic(KeyEvent.VK_O);
        evaluation.setToolTipText(bundle.getString("evaluation"));
        evaluation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                EvaluationMain evalmain=new EvaluationMain();
                evalmain.setVisible(true);
            }
        });

        JMenuItem ibusexportitem = new JMenuItem(bundle.getString("imexport"), icon);
        ibusexportitem.setMnemonic(KeyEvent.VK_O);
        ibusexportitem.setToolTipText(bundle.getString("imexport"));
        ibusexportitem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                IMEExport ibusexport=new IMEExport();
                ibusexport.setVisible(true);
            }
        });


        JMenuItem exitItem = new JMenuItem(bundle.getString("exit"), icon);
        exitItem.setMnemonic(KeyEvent.VK_E);
        exitItem.setToolTipText(bundle.getString("exit"));
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });


        JMenuItem aboutItem = new JMenuItem(bundle.getString("about"), icon);
        aboutItem.setMnemonic(KeyEvent.VK_A);
        aboutItem.setToolTipText(bundle.getString("about"));
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JOptionPane.showMessageDialog(GUIFormat.this,
                        "Copyright by Timo Homburg\n",bundle.getString("about"),JOptionPane.INFORMATION_MESSAGE);
            }
        });

        about.add(aboutItem);
        singlemethods.add(postagger);
        singlemethods.add(evaluation);
        file.add(optionitem);
        file.add(ibusexportitem);
        file.add(exitItem);

        menuBar.add(file);
        menuBar.add(singlemethods);
        menuBar.add(about);

        setJMenuBar(menuBar);

        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

}
