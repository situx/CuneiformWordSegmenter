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

import de.unifrankfurt.cs.acoli.akkad.dict.chars.cuneiform.CuneiChar;
import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.eval.EvalResult;
import de.unifrankfurt.cs.acoli.akkad.main.gui.util.GUIWorker;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.ClassificationMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.MethodEnum;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Files;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Tags;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class for showing translation results.
 */
public class TranslationGUI extends CompGUI {
    /**
     * Constructor for this class.
     * @param titleText  the titletext to set
     * @param generatedFile the generated file to load
     * @param originalfile  the original file to load
     * @param locale the locale to use
     * @param selectedMethods the selected methods to evaluate
     * @param result the result to consider
     */
    public TranslationGUI(String titleText,final String generatedFile, final String originalfile, final String locale,final java.util.List<MethodEnum> selectedMethods, final DictHandling dictHandler,final EvalResult evalResult){
        super(originalfile,generatedFile,selectedMethods, evalResult,dictHandler.getChartype());
        Map<String,Color> segmentationcolors=new TreeMap<String,Color>();;
        segmentationcolors.put("notranslation",Color.red);
        segmentationcolors.put("translation",Color.yellow);
        this.createLegend(segmentationcolors);
        if(selectedMethods.size()>1){
            featuresetchooser.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent actionEvent) {
                    try {
                        TranslationGUI.this.generatedFile=TranslationGUI.this.exchangeMethodName(TranslationGUI.this.generatedFile,(ClassificationMethod)featuresetchooser.getSelectedItem());
                        TranslationGUI.this.setGeneratedContents(new File(Files.RESULTDIR.toString()+Files.TRANSLATIONDIR.toString()+locale+File.separator+TranslationGUI.this.generatedFile),12,12,1);
                        TranslationGUI.this.setOriginalContents(new File(Files.RESULTDIR.toString()+Files.TRANSLITDIR.toString()+TranslationGUI.this.generatedFile),12,12,1);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        try {
            this.setOriginalContents(new File(Files.RESULTDIR.toString()+Files.TRANSLITDIR.toString()+TranslationGUI.this.generatedFile),TRANSLITFONTSIZE,TRANSLITFONTSIZE,1);
            this.setGeneratedContents(new File(Files.RESULTDIR.toString()+Files.TRANSLATIONDIR.toString()+locale+File.separator+TranslationGUI.this.generatedFile),TRANSLITFONTSIZE,TRANSLITFONTSIZE,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.switchbutton.setText(Tags.TRANSLITERATION.replaceFirst("t","T")+" Text");;
        switchbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                File orifile;
                if(switchflag1){
                    orifile=new File(Files.RESULTDIR.toString()+Files.TRANSLITDIR.toString()+TranslationGUI.this.generatedFile);
                    switchbutton.setText(Tags.TRANSLATION.replaceFirst("t","T")+" Text");
                }else{
                    orifile=new File(Files.REFORMATTEDDIR.toString()+Files.TRANSLITDIR.toString()+originalfile.substring(originalfile.lastIndexOf('/')));
                    switchbutton.setText(Tags.TRANSLITERATION.replaceFirst("t","T"));
                }
                switchflag1=!switchflag1;
                try {
                    TranslationGUI.this.setOriginalContents(orifile,TRANSLITFONTSIZE,TRANSLITFONTSIZE,1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        this.switchbutton2.setText(Tags.TRANSLATION.replaceFirst("t", "T")+" Text");
        switchbutton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                File genfile;
                if(switchflag2){
                    genfile=new File(Files.RESULTDIR.toString()+Files.TRANSLATIONDIR.toString()+locale+File.separator+TranslationGUI.this.generatedFile);
                    switchbutton2.setText(Tags.TRANSLATION.replaceFirst("t","T")+" Text");
                }else{
                    genfile=new File(Files.RESULTDIR.toString()+Files.TRANSLITDIR.toString()+TranslationGUI.this.generatedFile);
                    switchbutton2.setText(Tags.TRANSLITERATION.replaceFirst("t","T"));
                }
                switchflag2=!switchflag2;
                try {
                    TranslationGUI.this.setGeneratedContents(genfile,TRANSLITFONTSIZE,TRANSLITFONTSIZE,1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        this.diffbutton.setText(bundle.getString("getdiffs"));
        diffbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {

                TranslationGUI.this.paintResultArea(dictHandler);
            }
        });
        System.out.println("TranslationGUI Created!");
        this.setPreferredSize(new Dimension(1100, 1000));
    }

    @Override
    public void paintResultArea(final DictHandling dictHandler) {
        if(!highlightedright) {
            GUIWorker sw = new GUIWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    TranslationGUI.this.matches=0.;
                    TranslationGUI.this.all=0.;
                    if (!switchflag1 && !switchflag2) {
                        java.util.List<String> original = Arrays.asList(TranslationGUI.this.resultarea.getText().split("\n"));
                        java.util.List<String> revised = Arrays.asList(TranslationGUI.this.resultarea2.getText().split("\n"));
                        Highlighter highlighter = TranslationGUI.this.resultarea2.getHighlighter();
                        System.out.println(original);
                        System.out.println(revised);
                        int position = 0, endposition = 0;

                        for (int i = 0; i < revised.size(); i++) {
                            String[] originalwords = original.get(i).split(" \\[");
                            String[] revisedwords = revised.get(i).split(" \\[");
                            int j = 0;
                            for (int w = 0; w < revisedwords.length; ) {
                                String word = revisedwords[w].trim();
                                position += word.length();
                                if (word.equals(originalwords[j].trim())) {
                                    System.out.println("Correct word: " + word + " - " + originalwords[j]);
                                    try {
                                        endposition = TranslationGUI.this.resultarea2.getText().indexOf("]", position - word.length()) + 1;
                                        String paintword = (TranslationGUI.this.resultarea2.getText().substring(position - word.length(), endposition));
                                        String paintchar = paintword.substring(1, paintword.length() - 1);
                                        Color color = Color.red;
                                        CuneiChar c;
                                        if ((c = (CuneiChar) dictHandler.matchWordByTransliteration(paintchar)) != null && c.getDeterminative()) {
                                            color = Color.orange;
                                        }
                                        System.out.println(paintword);
                                        highlighter.addHighlight(position - word.length(), endposition, new DefaultHighlighter.DefaultHighlightPainter(color));
                                    } catch (BadLocationException e) {
                                        e.printStackTrace();
                                    }
                                    position = endposition;
                                    j++;
                                    w++;
                                    position++;
                                } else {
                                    endposition = TranslationGUI.this.resultarea2.getText().indexOf("]", position - word.length()) + 1;
                                    System.out.println(TranslationGUI.this.resultarea2.getText().substring(position - word.length(), endposition));
                                    try {
                                        highlighter.addHighlight(position - word.length(), endposition, new DefaultHighlighter.DefaultHighlightPainter(Color.yellow));
                                        TranslationGUI.this.matches++;
                                    } catch (BadLocationException e) {
                                        e.printStackTrace();
                                    }
                                    position = endposition;
                                    j++;
                                    w++;
                                    position++;
                                }
                                TranslationGUI.this.all++;
                            }
                            position++;
                        }
                    }
                    TranslationGUI.this.statistics.setText("Translated/All: "+TranslationGUI.this.matches/TranslationGUI.this.all);
                    return null;
                }

            };
            sw.execute();
            this.highlightedright=true;
        }
    }

}
