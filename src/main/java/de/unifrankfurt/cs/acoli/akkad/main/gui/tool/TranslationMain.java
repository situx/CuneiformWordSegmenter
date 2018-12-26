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

package de.unifrankfurt.cs.acoli.akkad.main.gui.tool;

import de.unifrankfurt.cs.acoli.akkad.dict.pos.POSTagger;
import de.unifrankfurt.cs.acoli.akkad.dict.translator.Translator;
import de.unifrankfurt.cs.acoli.akkad.dict.translator.cunei.AkkadToEngTranslator;
import de.unifrankfurt.cs.acoli.akkad.main.gui.util.HighlightData;
import de.unifrankfurt.cs.acoli.akkad.main.gui.util.JToolTipArea;
import de.unifrankfurt.cs.acoli.akkad.main.gui.util.TextLineNumber;
import de.unifrankfurt.cs.acoli.akkad.main.gui.util.UTF8Bundle;
import de.unifrankfurt.cs.acoli.akkad.util.Config;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.TranslationMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Tags;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;
import java.util.List;

/**
 * Translation Screen for the POSTagger application.
 */
public class TranslationMain extends JFrame {
    /**The cuneiform font size to use.*/
    protected static Integer CUNEIFONTSIZE=13;
    /**The transliteration font size to use.*/
    protected static Integer TRANSLITFONTSIZE=12;
    /**Cuneiform text checkbox 1.*/
    private final JCheckBox cuneiformCheckbox;
    /**Cuneiform checkbox 2.*/
    private final JCheckBox cuneiformCheckbox2;
    /**Destination language.*/
    private final CharTypes destinationType;
    /**Source language.*/
    private final CharTypes originalType;
    /**The postagger to use.*/
    private final POSTagger postagger;
    /**The translator to use.*/
    private Translator translator;
    /**Case of postags to highlighted words.*/
    protected Map<String,List<Highlighter.Highlight>> caseToHighlights;
    /**The resource bundle to use.*/
    public ResourceBundle bundle=ResourceBundle.getBundle(Config.RESBUNDLENAME, Locale.getDefault(),new UTF8Bundle("UTF-8"));
    /**The show differences button.*/
    protected JButton diffbutton;
    /**Match and word counter.*/
    protected Double matches,all;
    /**Resultareas for comparing.*/
    protected JToolTipArea resultarea,resultarea2;
    /**Scrollpane for resultarea 1.*/
    protected JScrollPane scrollPane;
    /**Scrollpan for resultarea 2.*/
    protected JScrollPane scrollPane2;
    /**Statistics field.*/
    protected JTextField statistics;
    /**Word counter.*/
    int y=0;
    /**Highlighter for resultarea 1.*/
    private Highlighter highlighter;
    /**Highlighter for resultarea 2.*/
    private Highlighter highlighter2;
    /**Constructor for this class.*/
    public TranslationMain(final String title,final String originalStr,final POSTagger postagger,final CharTypes originalType,CharTypes destinationType, final TranslationMethod translationMethod){
        this.postagger=postagger;
        this.caseToHighlights=new TreeMap<>();
        this.originalType=originalType;
        this.destinationType=destinationType;
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        this.diffbutton=new JButton("Translate");
        int ysize=540;
        JPanel mainPanel=new JPanel();
        JPanel mainPanel2=new JPanel();
        JLabel original=new JLabel(bundle.getString("original"));
        JLabel generated=new JLabel(bundle.getString("generated"));
        c.fill = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = y;
        c.gridwidth=1;
        this.add(original,c);
        c.fill = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = y++;
        c.gridwidth=1;
        this.add(generated,c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = y++;
        c.gridwidth=1;
        this.add(mainPanel, c);
        c.gridx=1;
        this.add(mainPanel2,c);
        this.resultarea=new JToolTipArea(originalType,originalType.getCorpusHandlerAPI().getUtilDictHandler(),bundle)
        {
            public boolean getScrollableTracksViewportWidth()
            {
                return getUI().getPreferredSize(this).width
                        <= getParent().getSize().width;
            }
        };
        this.resultarea.setText(originalStr);
        this.scrollPane = new JScrollPane();
        this.scrollPane.setViewportView(this.resultarea);
        resultarea.setEditable(false);
        scrollPane.setPreferredSize(new Dimension(530,ysize));
        mainPanel.add(scrollPane);
        this.resultarea2=new JToolTipArea(destinationType,destinationType.getCorpusHandlerAPI().getUtilDictHandler(),bundle)
        {
            public boolean getScrollableTracksViewportWidth()
            {
                return getUI().getPreferredSize(this).width
                        <= getParent().getSize().width;
            }
        };
        TextLineNumber tln = new TextLineNumber(resultarea,new TreeMap<Integer,String>());
        scrollPane.setRowHeaderView(tln);
        ToolTipManager.sharedInstance().registerComponent(resultarea);
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
        this.scrollPane2 = new JScrollPane();
        this.scrollPane2.setViewportView(this.resultarea2);
        ToolTipManager.sharedInstance().registerComponent(resultarea2);
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
        resultarea2.setEditable(false);
        this.resultarea.setFont(new Font(this.resultarea.getFont().getName(), 0, TRANSLITFONTSIZE));
        scrollPane2.setPreferredSize(new Dimension(530, ysize));
        this.resultarea2.setFont(new Font(this.resultarea2.getFont().getName(), 0, TRANSLITFONTSIZE));
        TextLineNumber tln2 = new TextLineNumber(resultarea2,new TreeMap<Integer,String>());
        scrollPane2.setRowHeaderView( tln2 );
        scrollPane.setPreferredSize(new Dimension(530, ysize));
        mainPanel.add(scrollPane);
        mainPanel.setPreferredSize(new Dimension(530,ysize));
        scrollPane2.setPreferredSize(new Dimension(530,ysize));
        mainPanel2.add(scrollPane2);
        mainPanel2.setPreferredSize(new Dimension(530,ysize));
        JPanel charTypePanel=new JPanel();
        final JComboBox<CharTypes> chartypechooser = new JComboBox<CharTypes>(CharTypes.values());
        final JCheckBox checkbox=new JCheckBox();
        this.cuneiformCheckbox=new JCheckBox();
        this.cuneiformCheckbox.setSelected(true);
        final JLabel cuneiformLabel=new JLabel(bundle.getString("transliteration"));
        JLabel chartype = new JLabel(bundle.getString("chartype")+":");
        charTypePanel.add(cuneiformCheckbox);
        charTypePanel.add(cuneiformLabel);
        charTypePanel.add(chartype);
        charTypePanel.add(chartypechooser);
        JPanel charTypePanel2=new JPanel();
        final JComboBox<CharTypes> chartypechooser2 = new JComboBox<CharTypes>(CharTypes.values());
        final JCheckBox checkbox2=new JCheckBox();
        this.cuneiformCheckbox2=new JCheckBox();
        this.cuneiformCheckbox2.setSelected(true);
        final JLabel cuneiformLabel2=new JLabel(bundle.getString("transliteration"));
        JLabel chartype2 = new JLabel(bundle.getString("chartype")+":");
        charTypePanel2.add(cuneiformCheckbox2);
        charTypePanel2.add(cuneiformLabel2);
        charTypePanel2.add(chartype2);
        charTypePanel2.add(chartypechooser2);
        chartypechooser.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent itemEvent) {
                TranslationMain.this.translator = Translator.getTranslator((CharTypes) chartypechooser.getSelectedItem(), (CharTypes) chartypechooser2.getSelectedItem());
            }
        });
        chartypechooser.setSelectedItem(CharTypes.AKKADIAN);
        chartypechooser2.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent itemEvent) {
                TranslationMain.this.translator = Translator.getTranslator((CharTypes) chartypechooser.getSelectedItem(),(CharTypes) chartypechooser2.getSelectedItem());
            }
        });
        chartypechooser2.setSelectedItem(CharTypes.ENGLISH);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = y;
        c.gridwidth=1;
        this.add(charTypePanel,c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = y++;
        c.gridwidth=1;
        this.add(charTypePanel2,c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = y;
        c.gridwidth=2;
        this.add(diffbutton,c);
        diffbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                TranslationMain.this.paintPOSTags(resultarea,resultarea.getText(),false);
                TranslationMain.this.translate(false);
            }
        });
        this.setPreferredSize(new Dimension(1100, 1000));
        this.pack();
        this.setVisible(true);
    }
    /**Translates the given text to the target language.
    */
    private String translate(Boolean cuneiform) {
        this.all = 0.;
        String translateresult = "";
        this.matches = 0.;
        List<String> revised;
        revised = Arrays.asList(this.resultarea.getText().split("\n"));
        /*if(genOrOrig){
            revised = Arrays.asList(TranslationMain.this.genTranslit.split("\n"));
        }else{
            revised = Arrays.asList(TranslationMain.this.origTranslit.split("\n"));
        }*/
        this.highlighter2 = resultarea2.getHighlighter();
        List<HighlightData> positions=new LinkedList<>();
        String currenttrans,sentencebuffer,lastclass;
        if (!cuneiform) {
            System.out.println(revised);
            int position = 0, endposition = 0,transposition=0;
            for (String revi : revised) {
                String[] revisedwords = revi.split(" \\[");
                //for (int w = 0; w < revisedwords.length; ) {
                    /*String word = revisedwords[w].trim();
                    System.out.println("Word: " + word);
                    */

                    //List<Integer> result = this.postagger.getPosTag(revised, this.resultarea.getDictHandler());
                    translator.wordByWordPOStranslate(revi, true,position);
                    translateresult += translator.getResult();
                    positions.addAll(translator.getLength());
                //w++;
                //}
                position=positions.get(positions.size()-1).getEnd();
                translateresult+="\n";
                position++;
                transposition++;
            }
            System.out.println("Positions: "+positions);


        } else {
            System.out.println("POSTagging Cuneiform...");
            System.out.println(revised);
            System.out.println(revised);
            int position = 0, endposition = 0;
            for (String revi : revised) {
                String[] revisedwords = revi.split(" ");
                for (int w = 0; w < revisedwords.length; ) {
                    String word = revisedwords[w].trim();
                    System.out.println("Word: " + word);
                    position += word.length();
                    List <Integer> result = this.postagger.getPosTag(word, this.resultarea.getDictHandler());
                    System.out.println("GetPosTag: " + result.toString());
                    position = endposition + 1;
                    w++;
                }
                translateresult+="\n";
                position++;
            }
        }
        this.resultarea2.setText(translateresult);
        for(HighlightData pos:positions){
            try {
                highlighter2.addHighlight(pos.getStart(),pos.getEnd(),new DefaultHighlighter.DefaultHighlightPainter(this.postagger.getPoscolors().get(pos.getTag())));
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        return translateresult;
    }

    /**Paints POSTags in the source and target language.*/
    private void paintPOSTags(final JTextArea resultarea,final String translittext,final Boolean cuneiform) {
        this.all = 0.;
        this.matches = 0.;
        List<String> revised;
        revised = Arrays.asList(translittext.split("\n"));
        this.highlighter2 = resultarea.getHighlighter();
        this.resultarea2.setText("");
        /*if(genOrOrig){
            revised = Arrays.asList(TranslationMain.this.genTranslit.split("\n"));
        }else{
            revised = Arrays.asList(TranslationMain.this.origTranslit.split("\n"));
        }*/
        this.caseToHighlights.clear();
        for (String pos : this.postagger.getPoscolors().keySet()) {
            this.caseToHighlights.put(pos, new LinkedList<Highlighter.Highlight>());
        }
        this.highlighter = resultarea.getHighlighter();
        if (!cuneiform) {
            System.out.println(revised);
            int position = 0, endposition = 0;
            for (String revi : revised) {
                String[] revisedwords = revi.split(" \\[");
                for (int w = 0; w < revisedwords.length; ) {
                    String word = revisedwords[w].trim();
                    System.out.println("Word: " + word);
                    position += word.length();
                    List<Integer> result = this.postagger.getPosTag(word, this.resultarea.getDictHandler());
                    System.out.println("GetPosTag: " + result.toString());
                    Color color = Color.white;
                    if (!result.isEmpty()) {
                        color = new Color(result.get(0));
                    } else if (this.postagger.getPoscolors().containsKey(Tags.DEFAULT.toString())) {
                        color = (this.postagger.getPoscolors().get(Tags.DEFAULT.toString()));
                    }
                    try {
                        endposition = resultarea.getText().indexOf("]", position - word.length()) + 1;
                        String paintword = resultarea.getText().substring(position - word.length(), endposition);
                        System.out.println(paintword);
                        if (!result.isEmpty() && (word.length() != (result.get(2) + 2) || result.get(1) != 0)) {
                            int i = 0, endindex, startindex;
                            while (i + 2 < result.size()) {
                                endindex = endposition - word.length() + result.get(i + 2);
                                if (!resultarea.getText().substring(endindex - 1, endindex).equals("-") && !resultarea.getText().substring(endindex - 1, endindex).equals("]")) {
                                    int minusIndex = resultarea.getText().indexOf("-", endindex);
                                    int brackIndex = resultarea.getText().indexOf("]", endindex);
                                    if (minusIndex != -1 && minusIndex < brackIndex) {
                                        endindex = minusIndex + 1;
                                    } else {
                                        endindex = brackIndex + 1;
                                    }
                                } else if (resultarea.getText().substring(endindex - 1, endindex).equals("-") && endindex + 2 == endposition && !resultarea.getText().substring(endindex, endindex + 1).equals("]")) {
                                    endindex = resultarea.getText().indexOf("]", endindex) + 1;
                                }
                                startindex = position - word.length() + result.get(i + 1);
                                if (!this.resultarea.getText().substring(startindex, startindex + 1).matches("[\\[|-]")) {
                                    if (this.resultarea.getText().substring(startindex + 1, startindex + 2).matches("[\\[|-]")) {
                                        startindex++;
                                    } else if (this.resultarea.getText().substring(startindex - 1, startindex).matches("[\\[|-]")) {
                                        startindex--;
                                    }
                                }
                                this.caseToHighlights.get(this.postagger.getColorToPos().get(new Color(result.get(i)).getRGB())).add((Highlighter.Highlight) highlighter.addHighlight(startindex,
                                        endindex, new DefaultHighlighter.DefaultHighlightPainter(new Color(result.get(i)))));
                                //highlighter.addHighlight(startindex, endindex, new DefaultHighlighter.DefaultHighlightPainter(new Color(result.get(i))));
                                i += 3;
                            }
                            if (this.postagger.getPoscolors().containsKey(Tags.DEFAULT.toString())) {
                                this.caseToHighlights.get(Tags.DEFAULT.toString()).add((Highlighter.Highlight) highlighter.addHighlight(position - word.length(), endposition, new DefaultHighlighter.DefaultHighlightPainter(this.postagger.getPoscolors().get(Tags.DEFAULT.toString()))));
                            }
                        } else {
                            this.caseToHighlights.get(this.postagger.getColorToPos().get(color.getRGB())).add((Highlighter.Highlight) highlighter.addHighlight(position - word.length(), endposition/*+result.get(2)*/, new DefaultHighlighter.DefaultHighlightPainter(color)));
                        }
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                    position = endposition + 1;
                    w++;
                }

                position++;
            }

        } else {
            System.out.println("POSTagging Cuneiform...");
            System.out.println(revised);
            System.out.println(revised);
            int position = 0, endposition = 0;
            for (String revi : revised) {
                String[] revisedwords = revi.split(" ");
                for (int w = 0; w < revisedwords.length; ) {
                    String word = revisedwords[w].trim();
                    System.out.println("Word: " + word);
                    position += word.length();
                    List<Integer> result = this.postagger.getPosTag(word, this.resultarea.getDictHandler());
                    System.out.println("GetPosTag: " + result.toString());
                    Color color = Color.white;
                    if (!result.isEmpty()) {
                        color = new Color(result.get(0));
                    } else if (this.postagger.getPoscolors().containsKey(Tags.DEFAULT.toString())) {
                        color = (this.postagger.getPoscolors().get(Tags.DEFAULT.toString()));
                    }
                    try {
                        endposition = resultarea.getText().indexOf(" ", position - word.length()) + 1;
                        String paintword = resultarea.getText().substring(position - word.length(), endposition);
                        System.out.println(paintword);
                        if (!result.isEmpty() && (word.length() != (result.get(2) + 2) || result.get(1) != 0)) {
                            int i = 0, endindex, startindex;
                            while (i + 2 < result.size()) {
                                endindex = endposition - word.length() + result.get(i + 2) - 1;
                                startindex = position - word.length() + result.get(i + 1) - 1;
                                this.caseToHighlights.get(this.postagger.getColorToPos().get(new Color(result.get(i)).getRGB())).add((Highlighter.Highlight) highlighter.addHighlight(startindex,
                                        endindex, new DefaultHighlighter.DefaultHighlightPainter(new Color(result.get(i)))));
                                //highlighter.addHighlight(startindex, endindex, new DefaultHighlighter.DefaultHighlightPainter(new Color(result.get(i))));
                                i += 3;
                            }
                            if (this.postagger.getPoscolors().containsKey(Tags.DEFAULT.toString())) {
                                this.caseToHighlights.get(Tags.DEFAULT.toString()).add((Highlighter.Highlight) highlighter.addHighlight(position - word.length() + 1, endposition - 1, new DefaultHighlighter.DefaultHighlightPainter(this.postagger.getPoscolors().get(Tags.DEFAULT.toString()))));
                            }
                        } else {
                            this.caseToHighlights.get(this.postagger.getColorToPos().get(color.getRGB())).add((Highlighter.Highlight) highlighter.addHighlight(position - word.length() + 1, endposition - 1/*+result.get(2)*/, new DefaultHighlighter.DefaultHighlightPainter(color)));
                        }
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                    position = endposition + 1;
                    w++;
                }

                position++;
            }
        }
    }

}
