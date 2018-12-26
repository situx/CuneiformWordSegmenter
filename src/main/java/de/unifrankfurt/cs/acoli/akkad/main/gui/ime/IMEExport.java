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

import de.unifrankfurt.cs.acoli.akkad.dict.corpusimport.CorpusHandlerAPI;
import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.main.gui.util.GUIFormat;
import de.unifrankfurt.cs.acoli.akkad.main.gui.util.TextLineNumber;
import de.unifrankfurt.cs.acoli.akkad.methods.Methods;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.TestMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.ExportMethods;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Files;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Created by timo on 05.07.14.
 */
public class IMEExport extends GUIFormat {

    public IMEExport(){
        this.buildMenu();
        JMenu about = new JMenu(bundle.getString("imetest"));
        about.setMnemonic(KeyEvent.VK_I);
        JMenuItem optionitem = new JMenuItem(bundle.getString("imetest"));
        optionitem.setMnemonic(KeyEvent.VK_O);
        optionitem.setToolTipText(bundle.getString("imetest"));
        optionitem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                IMETest optionsGUI = new IMETest();
            }
        });
        about.add(optionitem);
        this.getJMenuBar().add(about);
        setLocationRelativeTo(null);
        setTitle(bundle.getString("imexport"));
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel mainPanel=new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        final JScrollPane scrollPane;
        final JEditorPane resultarea=new JEditorPane();
        resultarea.setEditable(true);
        resultarea.setSize(new Dimension(530, 520));
        resultarea.setPreferredSize(new Dimension(530, 520));
        resultarea.setEnabled(false);
        TextLineNumber tln = new TextLineNumber(resultarea,new TreeMap<Integer, String>());
        scrollPane = new JScrollPane(resultarea);
        scrollPane.setRowHeaderView( tln );
        scrollPane.setPreferredSize(new Dimension(530,520));
        scrollPane.setSize(new Dimension(530, 520));
        GridBagConstraints c = new GridBagConstraints();
        JScrollPane framescroller=new JScrollPane(mainPanel);
        JLabel language = new JLabel(bundle.getString("corpus")+":");
        final JComboBox<String> corpuschooser = new JComboBox<String>(new File("source/").list());
        final JPanel langPanel = new JPanel();
        langPanel.add(language);
        langPanel.add(corpuschooser);
        JLabel language2 = new JLabel(bundle.getString("imexport")+":");
        final JComboBox<ExportMethods> imechooser = new JComboBox<ExportMethods>(ExportMethods.values());
        imechooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                try {
                    resultarea.setText("");
                    if(((ExportMethods)imechooser.getSelectedItem()).getHasoptions()){
                        BufferedReader reader=new BufferedReader(new FileReader(new File(Files.IME_DIR.toString()+imechooser.getSelectedItem().toString().toLowerCase()+File.separator+imechooser.getSelectedItem().toString().toLowerCase()+Files.HEADER.toString()+((ExportMethods)imechooser.getSelectedItem()).fileformat)));
                        resultarea.read(reader,null);
                        TextLineNumber tln = new TextLineNumber(resultarea,new TreeMap<Integer, String>());
                        scrollPane.setRowHeaderView(tln);
                        resultarea.setEnabled(true);
                    }else{
                        TextLineNumber tln = new TextLineNumber(resultarea,new TreeMap<Integer, String>());
                        scrollPane.setRowHeaderView(tln);
                        resultarea.setEnabled(false);
                    }

                } catch (IOException e) {
                    JOptionPane.showMessageDialog(IMEExport.this,
                            e.getMessage(), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        final JPanel langPanel2 = new JPanel();
        langPanel2.add(language2);
        langPanel2.add(imechooser);
        JLabel language3 = new JLabel(bundle.getString("language")+":");
        final JComboBox<CharTypes> chartypechooser = new JComboBox<CharTypes>(CharTypes.values());
        final JPanel langPanel3 = new JPanel();
        langPanel3.add(language3);
        langPanel3.add(chartypechooser);


        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth=3;
        mainPanel.add(scrollPane,c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth=3;
        JButton button=new JButton(this.bundle.getString("imexport"));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                SimpleDateFormat format=new SimpleDateFormat("yyyymmdd");
                String exportfile;
                CorpusHandlerAPI corpushandler=((CharTypes)chartypechooser.getSelectedItem()).getCorpusHandlerAPI();
                ExportMethods export=((ExportMethods)imechooser.getSelectedItem());
                try {
                    DictHandling dicthandler=corpushandler.generateTestTrainSets(corpuschooser.getSelectedItem().toString(),"akkad.xml",0.,0., TestMethod.FOREIGNTEXT,((CharTypes)chartypechooser.getSelectedItem()));
                    String header="";
                    if(export.getHasoptions()){
                        header=resultarea.getText();
                        header=header.replaceAll("UUID( )*=( )*(.)*$", "UUID = " + UUID.randomUUID()).replaceAll("SERIAL_NUMBER( )*=( )*(.)*$", "SERIAL_NUMBER = " + format.format(new Date(System.currentTimeMillis())));
                    }else{
                        File file=new File(Files.IME_DIR.toString()+imechooser.getSelectedItem().toString().toLowerCase()+File.separator+imechooser.getSelectedItem().toString().toLowerCase()+Files.HEADER.toString()+export.fileformat);
                        if(file.exists()){
                            header=new Methods().readWholeFile(file);
                        }
                    }
                    exportfile=dicthandler.toIME(export, dicthandler.getDictMap(), dicthandler.getDictionary(), header, corpuschooser.getSelectedItem().toString().substring(0, corpuschooser.getSelectedItem().toString().lastIndexOf('.')).toLowerCase() + "_" + chartypechooser.getSelectedItem().toString().toLowerCase());
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(IMEExport.this,
                            e.getMessage(), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
                    return;
                }catch (ParserConfigurationException e) {
                    JOptionPane.showMessageDialog(IMEExport.this,
                            e.getMessage(), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
                    return;
                }catch (SAXException e) {
                    JOptionPane.showMessageDialog(IMEExport.this,
                            e.getMessage(), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
                    return;
                } catch (XMLStreamException e) {
                    JOptionPane.showMessageDialog(IMEExport.this,
                            e.getMessage(), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
                    return;
                }
                JOptionPane.showMessageDialog(IMEExport.this,
                        bundle.getString("exportfinished")+": "+exportfile, bundle.getString("exportfinished"), JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        });
        mainPanel.add(button, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth=1;
        mainPanel.add(langPanel,c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth=1;
        mainPanel.add(langPanel2,c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 2;
        c.gridy = 1;
        c.gridwidth=1;
        mainPanel.add(langPanel3,c);
        mainPanel.setPreferredSize(new Dimension(800, 600));
        this.setContentPane(mainPanel);
        pack();
    }
}
