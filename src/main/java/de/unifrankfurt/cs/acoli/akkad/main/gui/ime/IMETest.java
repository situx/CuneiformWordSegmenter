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

import de.unifrankfurt.cs.acoli.akkad.main.gui.util.GUIFormat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.im.InputContext;
import java.awt.im.spi.InputMethodDescriptor;
import java.util.Locale;

/**
 * Created by timo on 29.07.14.
 */
public class IMETest extends GUIFormat {


    public IMETest() {

        InputContext context = InputContext.getInstance();
        if (context.selectInputMethod(GenericInputMethodDescriptor.AKKADIAN)) {
            System.out.println("Pig latin available");
        } else {
            System.out.println("Pig latin not available");
        }

        InputMethodDescriptor pigLatinDesc = new GenericInputMethodDescriptor();
        try {
            Locale[] inputLocales = pigLatinDesc.getAvailableLocales();
            for (int n = 0; n < inputLocales.length; n++) {
                System.out.println(inputLocales[n].toString());
            }
        } catch (java.awt.AWTException e) {
            e.printStackTrace();
        }

        this.buildMenu();
        this.makeMenuBar();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setTitle("IME Test");
        this.setPreferredSize(new Dimension(800, 600));

        JTextPane text = new JTextPane();
        text.setPreferredSize(new Dimension(200,200));
        Font font = new Font("Courier", Font.PLAIN, 24);
        text.setFont(font);

        this.getContentPane().add(text, BorderLayout.NORTH);
        context = text.getInputContext();
        try {
            if (context.selectInputMethod(GenericInputMethodDescriptor.AKKADIAN)) {
                System.out.println("Pig latin set for JText");
            } else {
                System.out.println("Pig latin not set for JText");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        this.pack();
        this.setVisible(true);
    }

    private void makeMenuBar() {
        JMenuBar menuBar = this.getJMenuBar();
        JMenu menu;

        menu = new JMenu("Locale");
        menuBar.add(menu);
        JMenuItem ibusexportitem = new JMenuItem("Akkadian");
        ibusexportitem.setMnemonic(KeyEvent.VK_A);
        ibusexportitem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (!IMETest.this.getInputContext().selectInputMethod(GenericInputMethodDescriptor.AKKADIAN)) {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
        JMenuItem ibusexportitem2 = new JMenuItem("Hittite");
        ibusexportitem.setMnemonic(KeyEvent.VK_H);
        ibusexportitem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (!IMETest.this.getInputContext().selectInputMethod(GenericInputMethodDescriptor.HITTITE)) {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
        JMenuItem ibusexportitem3 = new JMenuItem("Sumerian");
        ibusexportitem.setMnemonic(KeyEvent.VK_S);
        ibusexportitem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (!IMETest.this.getInputContext().selectInputMethod(GenericInputMethodDescriptor.SUMERIAN)) {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
        menu.add(ibusexportitem);
        menu.add(ibusexportitem2);
        menu.add(ibusexportitem3);

    }
}
