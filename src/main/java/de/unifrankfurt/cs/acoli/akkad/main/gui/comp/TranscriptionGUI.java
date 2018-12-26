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
import de.unifrankfurt.cs.acoli.akkad.main.gui.util.GUIWorker;
import de.unifrankfurt.cs.acoli.akkad.util.ArffHandler;
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
 * Class for evaluation transcriptions.
 */
public class TranscriptionGUI extends CompGUI {
    /**
     * Constructor for this class.
     * @param titleText  the titletext to set
     * @param generatedFile the generated file to load
     * @param originalfile  the original file to load
     * @param locale the locale to use
     * @param selectedMethods the selected methods to evaluate
     * @param result the result to consider
     */
    public TranscriptionGUI(String titleText, final String generatedFile, final String originalfile, final String locale, final java.util.List<MethodEnum> selectedMethods,final EvalResult result){
        super(originalfile,generatedFile,selectedMethods,result,null);
        Map<String,Color> segmentationcolors=new TreeMap<String,Color>();
        segmentationcolors.put("correcttranslit",Color.green);
        segmentationcolors.put("wrongtranslit",Color.red);
        segmentationcolors.put("mediumtranslit",Color.yellow);
        this.createLegend(segmentationcolors);
        if(selectedMethods.size()>1){
            featuresetchooser.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent actionEvent) {
                    try {
                        TranscriptionGUI.this.generatedFile=TranscriptionGUI.this.exchangeMethodName(TranscriptionGUI.this.generatedFile,(ClassificationMethod)featuresetchooser.getSelectedItem());

                        TranscriptionGUI.this.setGeneratedContents(new File(Files.RESULTDIR.toString()+Files.SYLLDIR.toString()+TranscriptionGUI.this.generatedFile),TRANSLITFONTSIZE,TRANSLITFONTSIZE,1);
                        TranscriptionGUI.this.setOriginalContents(new File(Files.RESULTDIR.toString()+Files.TRANSLITDIR.toString()+TranscriptionGUI.this.generatedFile),TRANSLITFONTSIZE,TRANSLITFONTSIZE,1);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        try {
            this.setOriginalContents(new File(Files.RESULTDIR.toString()+Files.TRANSLITDIR.toString()+TranscriptionGUI.this.generatedFile),TRANSLITFONTSIZE,TRANSLITFONTSIZE,1);
            this.setGeneratedContents(new File(Files.RESULTDIR.toString() + Files.SYLLDIR.toString()+this.generatedFile),TRANSLITFONTSIZE,TRANSLITFONTSIZE,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.switchbutton.setText(Tags.TRANSLITERATION.replaceFirst("t","T")+" Text");
        switchbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                File orifile;
                if(switchflag1){
                    orifile=new File(Files.RESULTDIR.toString()+Files.TRANSLITDIR.toString()+TranscriptionGUI.this.generatedFile);
                    switchbutton.setText(Tags.TRANSCRIPTION.toString().replaceFirst("t", "T")+" Text");
                }else{
                    orifile=new File(Files.RESULTDIR.toString()+Files.TRANSCRIPTDIR.toString()+TranscriptionGUI.this.generatedFile);
                    switchbutton.setText(Tags.TRANSLITERATION.replaceFirst("t","T"));
                }
                switchflag1=!switchflag1;
                try {
                    TranscriptionGUI.this.setOriginalContents(orifile,12,12,1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        this.switchbutton2.setText(Tags.TRANSCRIPTION.toString()+" Text");
        switchbutton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                File genfile;
                if(switchflag2){
                    genfile=new File(Files.RESULTDIR.toString()+Files.SYLLDIR.toString()+TranscriptionGUI.this.generatedFile);
                    switchbutton2.setText(Tags.TRANSCRIPTION.toString().replaceFirst("t", "T")+" Text");
                }else{
                    genfile=new File(Files.RESULTDIR.toString()+Files.TRANSCRIPTDIR.toString()+TranscriptionGUI.this.generatedFile);
                    switchbutton2.setText(Tags.TRANSLITERATION.replaceFirst("t","T"));
                }
                switchflag2=!switchflag2;
                try {
                    TranscriptionGUI.this.setGeneratedContents(genfile,12,12,1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        this.diffbutton.setText(bundle.getString("getdiffs"));
        diffbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                TranscriptionGUI.this.paintResultArea(null);
            }
        });
        System.out.println("TransliterationGUI Created!");
        this.setPreferredSize(new Dimension(1100, 1000));
    }


    @Override
    public void paintResultArea(DictHandling dictHandler) {
        if(!highlightedright) {
            GUIWorker sw = new GUIWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    //System.out.println("Cuneiform: "+cuneiform.toString()+" Cuneiform2: "+cuneiform2.toString());
                    boolean wordfinished = true;
                    int originalk = 0, revisedk = 0;
                    String[] originalsplitword = new String[2], revisedsplitword;
                    if (!switchflag1 && !switchflag2) {
                        java.util.List<String> original = Arrays.asList(TranscriptionGUI.this.resultarea.getText().split("\n"));
                        java.util.List<String> revised = Arrays.asList(TranscriptionGUI.this.resultarea2.getText().split("\n"));
                        Highlighter highlighter = TranscriptionGUI.this.resultarea2.getHighlighter();
                        System.out.println(original);
                        System.out.println(revised);
                        int position = 0;
                        int j = 0;
                        String[] originalwords, revisedwords = null;
                        for (int i = 0; i < revised.size(); i++) {
                            if (revisedwords != null && j > revisedwords.length) {
                                try {
                                    highlighter.addHighlight(position, position + (revisedwords.length - j), new DefaultHighlighter.DefaultHighlightPainter(Color.red));
                                } catch (BadLocationException e) {
                                    e.printStackTrace();
                                }
                            }
                            originalwords = original.get(i).split(" ");
                            revisedwords = revised.get(i).split(" ");
                            j = 0;
                            revisedk = 0;
                            originalk = 0;
                            for (int w = 0; w < revisedwords.length; ) {
                                //System.out.println("w: "+w+" w-length:"+revisedwords.length+" j: "+j+" j-length: "+originalwords.length);
                                String word = revisedwords[w];
                                if (word.isEmpty()) {
                                    w++;
                                    j++;
                                    continue;
                                }
                                position += word.length();
                                System.out.println("Word.length() " + word.length());
                                if ((originalwords.length - 1) < j) {
                                    System.out.println("Missed Word: " + word + " - " + originalwords[j]);
                                    try {
                                        highlighter.addHighlight(position - word.length(), position, new DefaultHighlighter.DefaultHighlightPainter(Color.red));
                                    } catch (BadLocationException e) {
                                        e.printStackTrace();
                                    }
                                    j++;
                                    w++;
                                    revisedk = 0;
                                    originalk = 0;
                                    position++;
                                } else if (word.equals(originalwords[j])) {
                                    System.out.println("Correct word: " + word + " - " + originalwords[j]);
                                    try {
                                        System.out.println(TranscriptionGUI.this.resultarea2.getText().substring(position - word.length(), position));
                                        highlighter.addHighlight(position - word.length(), position, new DefaultHighlighter.DefaultHighlightPainter(Color.green));
                                    } catch (BadLocationException e) {
                                        e.printStackTrace();
                                    }
                                    j++;
                                    w++;
                                    revisedk = 0;
                                    originalk = 0;
                                    position++;
                                } else {
                                    revisedsplitword = word.split("-");
                                    System.out.print("Revisedword: ");
                                    ArffHandler.arrayToStr(revisedsplitword);
                                    originalsplitword = originalwords[j].split("-");
                                    System.out.print("Originalword: ");
                                    ArffHandler.arrayToStr(originalsplitword);
                                    int interpos = position - word.length();
                                    if (wordfinished)
                                        originalk = 0;
                                    wordfinished = false;
                                    for (String syll : revisedsplitword) {
                                        System.out.println("originalk: " + originalk + " revisedk: " + revisedk);
                                        ArffHandler.arrayToStr(originalsplitword);
                                        //System.out.println("Gimme da K yoa one moa time!: "+originalk+" strlen: "+originalsplitword.length);
                                        interpos += syll.length() + 1;
                                        if (originalk < originalsplitword.length) {
                                            System.out.println("OriginalSyll: " + syll + " syll.length " + syll.length());
                                            System.out.println("Syll: " + syll.replaceAll("\\[", "").replaceAll("]", "") + " Originalsplitword: " + originalsplitword[originalk].replaceAll("\\[", "").replaceAll("]", ""));
                                        }
                                        if (originalk > (originalsplitword.length - 1)) {
                                            System.out.println("Paint Red: " + syll + " Interpos: " + TranscriptionGUI.this.resultarea2.getText().substring(interpos - syll.length(), interpos));
                                            if (revisedk == revisedsplitword.length - 1) {
                                                try {
                                                    highlighter.addHighlight(interpos - syll.length() - 1, interpos - 1, new DefaultHighlighter.DefaultHighlightPainter(Color.red));
                                                } catch (BadLocationException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                try {
                                                    highlighter.addHighlight(interpos - syll.length() - 1, interpos, new DefaultHighlighter.DefaultHighlightPainter(Color.red));
                                                } catch (BadLocationException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                        } else if (syll.replaceAll("\\[", "").replaceAll("]", "").equals(originalsplitword[originalk].replaceAll("\\[", "").replaceAll("]", ""))) {
                                            System.out.println("Paint Yellow: " + syll + " Interpos: " + resultarea2.getText().substring(interpos - syll.length(), interpos + 1));
                                            if (revisedk == revisedsplitword.length - 1) {
                                                try {
                                                    highlighter.addHighlight(interpos - syll.length() - 1, interpos - 1, new DefaultHighlighter.DefaultHighlightPainter(Color.yellow));
                                                } catch (BadLocationException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                try {
                                                    highlighter.addHighlight(interpos - syll.length() - 1, interpos, new DefaultHighlighter.DefaultHighlightPainter(Color.yellow));
                                                } catch (BadLocationException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        } else {
                                            System.out.println("Paint Red: " + syll + " Interpos: " + resultarea2.getText().substring(interpos - syll.length(), interpos + 1));
                                            if (revisedk == revisedsplitword.length - 1) {
                                                try {
                                                    highlighter.addHighlight(interpos - syll.length() - 1, interpos - 1, new DefaultHighlighter.DefaultHighlightPainter(Color.red));
                                                } catch (BadLocationException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                try {
                                                    highlighter.addHighlight(interpos - syll.length() - 1, interpos, new DefaultHighlighter.DefaultHighlightPainter(Color.red));
                                                } catch (BadLocationException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                        originalk++;
                                        revisedk++;
                                        System.out.println("Gimme da K yoa!: " + originalk + " strlen: " + originalsplitword.length);
                                        if ((originalk == originalsplitword.length && revisedk < revisedsplitword.length) || (revisedk == revisedsplitword.length && originalk < originalsplitword.length && w < revisedwords.length - 1)
                                                || ((revisedk == revisedsplitword.length && originalk == originalsplitword.length) || (w >= (revisedwords.length - 1) && revisedk == revisedsplitword.length))) {
                                            System.out.println("j++ k++");
                                            j++;
                                            w++;
                                            originalk = 0;
                                            revisedk = 0;
                                            break;
                                        }
                                    }
                                    position++;
                                }
                            }
                            position++;
                        }

                    }
                    return null;
                }
            };
            sw.execute();
            this.highlightedright=true;
        }
    }
}
