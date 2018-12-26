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
import de.unifrankfurt.cs.acoli.akkad.methods.Methods;
import de.unifrankfurt.cs.acoli.akkad.util.ArffHandler;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.ClassificationMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.MethodEnum;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.TestMethod;
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
 * Created by timo on 11.06.14.
 */
public class SegmentationGUI extends CompGUI{



    public SegmentationGUI(String titleText, final String generatedFile, final String originalfile, final java.util.List<MethodEnum> selectedMethods,final EvalResult result,final DictHandling dictHandler,final TestMethod testMethod) {
        super(originalfile,generatedFile,selectedMethods,result,dictHandler.getChartype());
        this.switchflag1=true;
        Map<String,Color> segmentationcolors=new TreeMap<String,Color>();
        segmentationcolors.put("correctsegment",Color.green);
        segmentationcolors.put("wrongsegment",Color.red);
        segmentationcolors.put("missingsegment",Color.cyan);
        segmentationcolors.put("correcttranslit",Color.green);
        segmentationcolors.put("wrongtranslit",Color.red);
        segmentationcolors.put("mediumtranslit",Color.yellow);

        this.createLegend(segmentationcolors);
        if(selectedMethods.size()>1){
            featuresetchooser.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent actionEvent) {
                    try {
                        if(switchflag2){
                            SegmentationGUI.this.setGeneratedContents(new File(Files.RESULTDIR.toString() + Files.CUNEIFORMDIR.toString() + SegmentationGUI.this.exchangeMethodName(generatedFile, (ClassificationMethod) featuresetchooser.getSelectedItem())), 13, 12, 3);

                        }else{
                            SegmentationGUI.this.setGeneratedContents(new File(Files.RESULTDIR.toString() + Files.TRANSLITDIR.toString() + SegmentationGUI.this.exchangeMethodName(generatedFile, (ClassificationMethod) featuresetchooser.getSelectedItem())), 13, 12, 1);
                        }
                        } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        try {
            this.setOriginalContents(new File(Files.REFORMATTEDDIR.toString()+Files.CUNEI_SEGMENTEDDIR.toString()+testMethod.toString().toLowerCase()+File.separator+originalfile.substring(originalfile.lastIndexOf('/'))),CUNEIFONTSIZE,TRANSLITFONTSIZE,3);
            this.setGeneratedContents(new File(Files.RESULTDIR.toString() + Files.TRANSLITDIR.toString() +generatedFile), CUNEIFONTSIZE, TRANSLITFONTSIZE, 1);
            this.origTranslit = new Methods().readWholeFile(new File(Files.REFORMATTEDDIR.toString()+Files.TRANSLITDIR.toString()+testMethod.toString().toLowerCase()+File.separator+originalfile.substring(originalfile.lastIndexOf('/'))));
            this.origCunei=new Methods().readWholeFile(new File(Files.REFORMATTEDDIR.toString()+Files.CUNEI_SEGMENTEDDIR.toString()+testMethod.toString().toLowerCase()+File.separator+originalfile.substring(originalfile.lastIndexOf('/'))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.switchbutton.setText(Tags.TRANSLITERATION.replaceFirst("t","T")+" Text");
        switchbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                File orifile;
                int spaces=1;
                if(switchflag1){
                    orifile=new File(Files.REFORMATTEDDIR.toString()+Files.TRANSLITDIR.toString()+testMethod.toString().toLowerCase()+File.separator+originalfile.substring(originalfile.lastIndexOf('/')));
                    switchbutton.setText(Tags.CUNEIFORM.toString()+" Text");
                }else{
                    spaces=3;
                    orifile=new File(Files.REFORMATTEDDIR.toString()+Files.CUNEI_SEGMENTEDDIR.toString()+testMethod.toString().toLowerCase()+File.separator+originalfile.substring(originalfile.lastIndexOf('/')));
                    switchbutton.setText(Tags.TRANSLITERATION.replaceFirst("t","T"));
                }
                switchflag1=!switchflag1;
                try {
                    SegmentationGUI.this.setOriginalContents(orifile,CUNEIFONTSIZE,TRANSLITFONTSIZE,spaces);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        this.switchbutton2.setText(Tags.CUNEIFORM.toString()+" Text");
        switchbutton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                File genfile;
                int spaces=1;
                if(switchflag2){
                    genfile=new File(Files.RESULTDIR.toString()+Files.TRANSLITDIR.toString()+generatedFile);
                    switchbutton2.setText(Tags.CUNEIFORM.toString()+" Text");
                }else{
                    spaces=3;
                    genfile=new File(Files.RESULTDIR.toString()+Files.CUNEIFORMDIR.toString()+generatedFile);
                    switchbutton2.setText(Tags.TRANSLITERATION.replaceFirst("t","T"));
                }
                switchflag2=!switchflag2;
                try {
                    SegmentationGUI.this.setGeneratedContents(genfile,CUNEIFONTSIZE,TRANSLITFONTSIZE,spaces);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        this.diffbutton.setText(bundle.getString("getdiffs"));
        diffbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                SegmentationGUI.this.paintResultArea(dictHandler);
            }
        });
        System.out.println("SegmentationGUI Created!");
        this.setPreferredSize(new Dimension(1100, 1000));
        dictHandler.calculateAvgWordLength();
    }

    @Override
    public void paintResultArea(final DictHandling dictHandler){
        if(!highlightedright) {
            GUIWorker sw = new GUIWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    boolean wordfinished = true;
                    int originalk = 0, revisedk = 0;
                    String[] originalsplitword = new String[2], revisedsplitword;
                    java.util.List<String> original;
                    java.util.List<String> revised;
                    if ((switchflag1 && !switchflag2) || (switchflag1 && switchflag2)) {
                        original = Arrays.asList(SegmentationGUI.this.origTranslit.split(System.lineSeparator()));
                    } else {
                        original = Arrays.asList(SegmentationGUI.this.resultarea.getText().split(System.lineSeparator()));
                    }
                    revised = Arrays.asList(SegmentationGUI.this.resultarea2.getText().split(System.lineSeparator()));
                    if ((!switchflag1 && !switchflag2) || (switchflag1 && !switchflag2)) {
                        Highlighter highlighter = SegmentationGUI.this.resultarea2.getHighlighter();
                        System.out.println(original);
                        System.out.println(revised);
                        int position = 0;
                        int j = 0;
                        String[] originalwords, revisedwords = null;
                        for (int i = 0; i < revised.size(); i++) {
                            if(revised.get(i).trim().isEmpty()){
                                position+=revised.get(i).length()+1;
                                continue;
                            }
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
                                        System.out.println(SegmentationGUI.this.resultarea2.getText().substring(position - word.length(), position));
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
                                            System.out.println("Paint Red: " + syll + " Interpos: " + SegmentationGUI.this.resultarea2.getText().substring(interpos - syll.length(), interpos));
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
                                        if (originalk == originalsplitword.length && revisedk < revisedsplitword.length) {
                                            originalsplitword = originalwords[++j].split("-");
                                            originalk = 0;
                                            System.out.print("New OriginalWord: ");
                                            ArffHandler.arrayToStr(originalsplitword);
                                        } else if (revisedk == revisedsplitword.length && originalk < originalsplitword.length && w < revisedwords.length - 1) {
                                            revisedsplitword = revisedwords[++w].split("-");
                                            revisedk = 0;
                                            System.out.print("New RevisedWord: ");
                                            ArffHandler.arrayToStr(revisedsplitword);
                                        } else if ((revisedk == revisedsplitword.length && originalk == originalsplitword.length) || (w >= (revisedwords.length - 1) && revisedk == revisedsplitword.length)) {
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

                    } else if ((!switchflag1 && switchflag2) || (switchflag1 && switchflag2)) {
                        original = Arrays.asList(SegmentationGUI.this.origCunei.split(System.lineSeparator()));
                        revised = Arrays.asList(SegmentationGUI.this.resultarea2.getText().split(System.lineSeparator()));
                        Highlighter highlighter = SegmentationGUI.this.resultarea2.getHighlighter();
                        System.out.println(original);
                        System.out.println(revised);
                        int position = 0;
                        for (int i = 0; i < revised.size(); i++) {
                             if(revised.get(i).isEmpty()){
                                position++;
                                continue;
                            }
                            String originalcharline=original.get(i).trim();
                            String revisedcharline=revised.get(i).trim();
                            System.out.println("Originalcharline: "+originalcharline+" RevisedCharline: "+revisedcharline);
                            for(String stopChar:dictHandler.getStopchars().keySet()){
                                originalcharline=originalcharline.replaceAll(stopChar,"");
                            }
                            String currentoriginal,currentrevised;
                            int revisedoffset=0,originaloffset=0;

                            for(int j=0;j<originalcharline.length() && j+revisedoffset+1<revisedcharline.length() && j+originaloffset+1<originalcharline.length()/*-chartype.getChar_length()*/;j+=dictHandler.getChartype().getChar_length()){
                                currentrevised=revisedcharline.substring(j+revisedoffset,j+revisedoffset+dictHandler.getChartype().getChar_length());
                                currentoriginal=originalcharline.substring(j+originaloffset,j+originaloffset+dictHandler.getChartype().getChar_length());
                                System.out.println("Currentrevised: ["+currentrevised+"] RevisedOffset: "+revisedoffset+" J:"+j);
                                System.out.println("Currentoriginal: ["+currentoriginal+"] Originaloffset: "+originaloffset+" J:"+j);
                                if(currentoriginal.substring(0,1).matches("[ ]+")){
                                    if(currentrevised.substring(0,1).matches("[ ]+")){
                                        while(j+originaloffset+1<originalcharline.length() && originalcharline.substring(j+originaloffset,j+originaloffset+1).equals(" ")){
                                            originaloffset++;
                                        }
                                        while(j+revisedoffset+1<revised.get(i).length() && revised.get(i).substring(j + revisedoffset, j + revisedoffset + 1).equals(" ")){
                                            revisedoffset++;
                                            position++;
                                        }
                                        try {
                                            highlighter.addHighlight(position-3, position, new DefaultHighlighter.DefaultHighlightPainter(Color.green));
                                        } catch (BadLocationException e) {
                                            e.printStackTrace();
                                        }
                                        position+=dictHandler.getChartype().getChar_length();
                                        //result+=("   "+currentoriginal);
                                    }else{
                                        while(j+originaloffset+1<originalcharline.length() && originalcharline.substring(j+originaloffset,j+originaloffset+1).equals(" ")){
                                            originaloffset++;
                                        }
                                        SegmentationGUI.this.resultarea2.replaceRange(" ",position, position);
                                        //SegmentationGUI.this.resultarea2.select(position, position + dictHandler.getChartype().getChar_length());
                                        //SegmentationGUI.this.resultarea2.replaceSelection(" " + SegmentationGUI.this.resultarea2.getSelectedText());
                                        try {
                                            highlighter.addHighlight(position, position + 1, new DefaultHighlighter.DefaultHighlightPainter(Color.cyan));
                                        } catch (BadLocationException e) {
                                            e.printStackTrace();
                                        }
                                        position++;
                                        j-=dictHandler.getChartype().getChar_length();
                                        //position+=dictHandler.getChartype().getChar_length();
                                    }

                                }else{
                                    if(currentrevised.substring(0,1).matches("[ ]+")){
                                        while(j+revisedoffset+1<revisedcharline.length() && revisedcharline.substring(j+revisedoffset,j+revisedoffset+1).equals(" ")){
                                            revisedoffset++;
                                            position++;
                                        }
                                        //originaloffset-=dictHandler.getChartype().getChar_length();
                                        try {
                                            highlighter.addHighlight(position-3, position, new DefaultHighlighter.DefaultHighlightPainter(Color.red));
                                        } catch (BadLocationException e) {
                                            e.printStackTrace();
                                        }
                                        j-=dictHandler.getChartype().getChar_length();
                                        //position+=dictHandler.getChartype().getChar_length();
                                    }else{
                                        position+=dictHandler.getChartype().getChar_length();
                                    }
                                }
                            }
                            /*if(revised.get(i).isEmpty()){
                                position++;
                                continue;
                            }
                            String[] originalwords = original.get(i).split(" ");
                            String[] revisedwords = revised.get(i).split("   ");
                            int j = 0;
                            for (String word : revisedwords) {
                                position += word.length();
                                if (j < originalwords.length - 1) {
                                    int sylls = (originalwords[j].length() - originalwords[j].replace("-", "").length()) + 1;
                                    System.out.println("Word.length(): "+word.length()/dictHandler.getChartype().getChar_length()+" Sylls: "+sylls+" Word: "+word+" Originalword["+j+"]: "+originalwords[j]);
                                    if (word.length() / dictHandler.getChartype().getChar_length() == sylls) {
                                        try {
                                            highlighter.addHighlight(position, position + 3, new DefaultHighlighter.DefaultHighlightPainter(Color.green));
                                        } catch (BadLocationException e) {
                                            e.printStackTrace();
                                        }
                                    } else if(word.length()<revised.get(i).length()){
                                        try {
                                            highlighter.addHighlight(position, position + 3, new DefaultHighlighter.DefaultHighlightPainter(Color.red));
                                        } catch (BadLocationException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else if (j < revisedwords.length - 1) {
                                    try {
                                        highlighter.addHighlight(position, position + 3, new DefaultHighlighter.DefaultHighlightPainter(Color.red));
                                    } catch (BadLocationException e) {
                                        e.printStackTrace();
                                    }
                                }
                                j++;
                                position += 3;
                            }*/
                            position+=4;
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
