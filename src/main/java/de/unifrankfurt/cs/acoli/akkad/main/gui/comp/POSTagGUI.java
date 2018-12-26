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
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.ClassificationMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.MethodEnum;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.TestMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Files;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Tags;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by timo on 11.07.14.
 */
public class POSTagGUI extends CompGUI {
    /**
     * Constructor for this class.
     * @param generatedFile the generated file
     * @param originalfile the original file
     * @param selectedMethods the methods to evaluate
     * @param dictHandler the dicthandler to use
     * @param evalResult the evaluation result to use
     * @param testMethod  the testmethod to use
     */
    public POSTagGUI(final String generatedFile, final String originalfile, final List<MethodEnum> selectedMethods,final DictHandling dictHandler, final EvalResult evalResult,final TestMethod testMethod) {
        super(originalfile, generatedFile, selectedMethods, evalResult,dictHandler.getChartype());
        try {
            this.setOriginalContents(new File(Files.REFORMATTEDDIR.toString()+Files.TRANSLITDIR.toString()+testMethod.toString().toLowerCase()+File.separator+originalfile.substring(originalfile.lastIndexOf('/'))),CUNEIFONTSIZE,TRANSLITFONTSIZE,1);
            if(generatedFile!=null)
                this.setGeneratedContents(new File(Files.RESULTDIR.toString() + Files.TRANSLITDIR.toString()+generatedFile),CUNEIFONTSIZE,TRANSLITFONTSIZE,1);
            } catch (IOException e) {
            e.printStackTrace();
        }
        POSTagGUI.this.createLegend(dictHandler.getPosTagger().getPoscolors());
        if(selectedMethods.size()>1){
            featuresetchooser.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent actionEvent) {
                    try {
                        POSTagGUI.this.generatedFile=POSTagGUI.this.exchangeMethodName(POSTagGUI.this.generatedFile,(ClassificationMethod)featuresetchooser.getSelectedItem());
                        POSTagGUI.this.genTranslit =new Methods().readWholeFile(new File(Files.RESULTDIR.toString()+Files.TRANSLITDIR.toString()+POSTagGUI.this.generatedFile));
                        if(switchflag2){
                            POSTagGUI.this.setGeneratedContents(new File(Files.RESULTDIR.toString()+Files.TRANSLITDIR.toString()+POSTagGUI.this.generatedFile),12,12,1);

                        }else{
                            POSTagGUI.this.setGeneratedContents(new File(Files.RESULTDIR.toString()+Files.CUNEIFORMDIR.toString()+POSTagGUI.this.generatedFile),12,12,1);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        this.resultarea.setPostagger(dictHandler.getPosTagger());
        this.resultarea.setDictHandler(dictHandler);
        this.resultarea2.setPostagger(dictHandler.getPosTagger());
        this.resultarea2.setDictHandler(dictHandler);
        ToolTipManager.sharedInstance().registerComponent(resultarea);
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
        ToolTipManager.sharedInstance().registerComponent(resultarea2);
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
        this.genTranslit =this.resultarea2.getText();
        this.origTranslit =this.resultarea.getText();
        this.switchflag1=true;
        this.switchflag2=true;
        this.switchbutton.setText(Tags.TRANSLITERATION.replaceFirst("t","T")+" Text");
        switchbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                File orifile;
                int spaces=1;
                if(switchflag1){
                    spaces=3;
                    orifile=new File(Files.REFORMATTEDDIR.toString()+Files.CUNEI_SEGMENTEDDIR+testMethod.toString().toLowerCase()+File.separator+originalfile.substring(originalfile.lastIndexOf('/')));
                    switchbutton.setText(Tags.TRANSLITERATION.replaceFirst("t","T")+" Text");
                }else{
                    orifile=new File(Files.REFORMATTEDDIR.toString()+Files.TRANSLITDIR.toString()+testMethod.toString().toLowerCase()+File.separator+originalfile.substring(originalfile.lastIndexOf('/')));
                    switchbutton.setText(Tags.CUNEIFORM+" Text");
                }
                switchflag1=!switchflag1;
                try {
                    POSTagGUI.this.setOriginalContents(orifile,TRANSLITFONTSIZE,CUNEIFONTSIZE,spaces);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        this.switchbutton2.setText(Tags.CUNEIFORM+" Text");
        switchbutton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                File genfile;
                int spaces;
                if(switchflag2){
                    spaces=3;
                    genfile=new File(Files.RESULTDIR.toString()+Files.CUNEIFORMDIR.toString()+POSTagGUI.this.generatedFile);
                    switchbutton2.setText(Tags.TRANSLITERATION.replaceFirst("t","T")+" Text");
                }else{
                    spaces=1;
                    genfile=new File(Files.RESULTDIR.toString()+Files.TRANSLITDIR.toString()+POSTagGUI.this.generatedFile);
                    switchbutton2.setText(Tags.CUNEIFORM+" Text");
                }
                switchflag2=!switchflag2;
                try {
                    POSTagGUI.this.setGeneratedContents(genfile,TRANSLITFONTSIZE,CUNEIFONTSIZE,spaces);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        this.diffbutton.setText(bundle.getString("getdiffs"));
        diffbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                POSTagGUI.this.paintResultArea(dictHandler);
            }
        });
        System.out.println("PosTagGUI Created!");
        this.setPreferredSize(new Dimension(1100, 1000));
    }

    /**
     * Method for painting the postags on designated words.
     * @param resultarea the resultarea to paint in
     * @param translittext the transliteration text to use as a basis
     * @param genOrOrig indicates if the generated or original text is painted
     * @param cuneiform indicates if cuneiform text is used
     * @param dictHandler the dicthandler to use
     */
    private void paintPOSTags(final JTextArea resultarea,final String translittext,final Boolean genOrOrig,final Boolean cuneiform,final DictHandling dictHandler){
        this.all=0.;
        this.matches=0.;
        List<String> revised;
        if(genOrOrig){
            revised = Arrays.asList(POSTagGUI.this.genTranslit.split("\n"));
        }else{
            revised = Arrays.asList(POSTagGUI.this.origTranslit.split("\n"));
        }
        Highlighter highlighter = resultarea.getHighlighter();
        if (!cuneiform) {
            System.out.println(revised);
            int position = 0, endposition = 0;
            for (String revi:revised) {
                String[] revisedwords = revi.split(" \\[");
                for (int w = 0; w < revisedwords.length; ) {
                    String word = revisedwords[w].trim();
                    System.out.println("Word: "+word);
                    position += word.length();
                    List<Integer> result=dictHandler.getPosTagger().getPosTag(word,dictHandler);
                    System.out.println("GetPosTag: "+result.toString());
                    Color color=Color.white;
                    if(!result.isEmpty()){
                        color=new Color(result.get(0));
                    }else if(dictHandler.getPosTagger().getPoscolors().containsKey(Tags.DEFAULT.toString())){
                        color=(dictHandler.getPosTagger().getPoscolors().get(Tags.DEFAULT.toString()));
                    }
                    try {
                        endposition = resultarea.getText().indexOf("]", position - word.length())+1;
                        String paintword = resultarea.getText().substring(position - word.length(), endposition);
                        System.out.println(paintword);
                        if(!result.isEmpty() && (word.length()!=(result.get(2)+2) || result.get(1)!=0)){
                            int i=0,endindex,startindex;
                            while(i+2<result.size()){
                                endindex=endposition-word.length()+result.get(i+2);
                                if(!resultarea.getText().substring(endindex-1,endindex).equals("-")) {
                                    int minusIndex = resultarea.getText().indexOf("-", endindex);
                                    int brackIndex = resultarea.getText().indexOf("]", endindex);
                                    if (minusIndex != -1 && minusIndex < brackIndex) {
                                        endindex = minusIndex+1;
                                    } else {
                                        endindex = brackIndex+1;
                                    }
                                }else if(resultarea.getText().substring(endindex-1,endindex).equals("-") && endindex+2==endposition && !resultarea.getText().substring(endindex,endindex+1).equals("]")){
                                    endindex=resultarea.getText().indexOf("]", endindex)+1;
                                }
                                startindex=position - word.length()+result.get(i+1);
                                highlighter.addHighlight(startindex, endindex, new DefaultHighlighter.DefaultHighlightPainter(new Color(result.get(i))));
                                i+=3;
                            }
                            if(dictHandler.getPosTagger().getPoscolors().containsKey(Tags.DEFAULT.toString())){
                                highlighter.addHighlight(position-word.length(), endposition, new DefaultHighlighter.DefaultHighlightPainter(dictHandler.getPosTagger().getPoscolors().get(Tags.DEFAULT.toString())));
                            }
                        }else{
                            highlighter.addHighlight(position - word.length(), endposition/*+result.get(2)*/, new DefaultHighlighter.DefaultHighlightPainter(color));
                        }
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                    position = endposition+1;
                    w++;
                }

                position++;
            }

        }else{
            System.out.println("POSTagging Cuneiform...");
            System.out.println(revised);
            int position = 0, cuneiposition=0,endposition;
            for (String revi:revised) {
                String[] revisedwords = revi.split(" \\[");
                for (int w = 0; w < revisedwords.length; ) {
                    String word = revisedwords[w].trim();
                    System.out.println("Word: "+word);
                    position += word.length();
                    int length=dictHandler.getChartype().getChar_length();
                    if(word.contains("-")){
                        length=word.split("-").length*dictHandler.getChartype().getChar_length();
                    }
                    this.all+=length/dictHandler.getChartype().getChar_length();
                    cuneiposition+=length;
                    System.out.println("Length: "+length);
                    List<Integer> result=dictHandler.getPosTagger().getPosTag(word,dictHandler);
                    System.out.println("GetPosTag: "+result.toString());
                    Color color=Color.white;
                    if(!result.isEmpty()){
                        color=new Color(result.get(0));
                    }else if(dictHandler.getPosTagger().getPoscolors().containsKey(Tags.DEFAULT.toString())){
                        color=dictHandler.getPosTagger().getPoscolors().get(Tags.DEFAULT.toString());
                    }
                    System.out.println("Determinative: " + word);
                    endposition = translittext.indexOf(" ", position - word.length()) + 1;
                    String topaint=resultarea.getText().substring(cuneiposition - length, cuneiposition);
                    if(topaint.contains(" ")){
                        topaint=topaint.replace(" ","");
                        cuneiposition=cuneiposition-length+topaint.length();
                        length=topaint.length();
                    }
                    try {
                        if(!result.isEmpty() && word.length()!=result.get(2)){
                            int i=0,sylllength=0,start=0;
                            int[]range=new int[2];
                            range[0]=-1;
                            range[1]=-1;
                            while(i+2<result.size()){
                                if(range[0]==-1 || result.get(i+1)<range[0]){
                                    range[0]=result.get(i+1);
                                }
                                if(range[1]==-1 || result.get(i+2)<range[1]){
                                    range[1]=result.get(i+2);
                                }
                                sylllength=word.substring(result.get(i+1),result.get(i+2)).split("-").length*dictHandler.getChartype().getChar_length();
                                start=word.substring(0,result.get(i+1)).split("-").length*dictHandler.getChartype().getChar_length()-dictHandler.getChartype().getChar_length();
                                System.out.println("Sylllength: "+sylllength);
                                System.out.println("Start: "+start);
                                System.out.println("Word: "+word+" Substring("+result.get(i+1)+","+result.get(i+2)+"): "+word.substring(result.get(i+1),result.get(i+2)));
                                System.out.println("Highlight("+((cuneiposition - length)+start)+","+((cuneiposition-length)+start+sylllength)+")");
                                highlighter.addHighlight((cuneiposition - length)+start,
                                        (cuneiposition-length)+start+sylllength, new DefaultHighlighter.DefaultHighlightPainter(new Color(result.get(i))));
                                i+=3;
                            }
                            this.matches+=word.substring(range[0],range[1]).split("-").length;
                            if(dictHandler.getPosTagger().getPoscolors().containsKey(Tags.DEFAULT.toString())){
                                highlighter.addHighlight(cuneiposition - length, cuneiposition, new DefaultHighlighter.DefaultHighlightPainter(dictHandler.getPosTagger().getPoscolors().get(Tags.DEFAULT.toString())));
                            }
                        }else{
                            highlighter.addHighlight(cuneiposition - length, cuneiposition, new DefaultHighlighter.DefaultHighlightPainter(color));
                            this.matches+=length/dictHandler.getChartype().getChar_length();
                        }
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                    position = endposition+1;
                    w++;
                    System.out.println("Cuneiposition: "+cuneiposition+" +3="+(cuneiposition+3));
                    cuneiposition+=3;
                }
                position++;
                cuneiposition++;
            }
        }
        this.statistics.setText(" Postagged/All: "+this.matches/this.all);
        System.out.println("PostaggedSylls: "+this.matches+" All Sylls: "+this.all+" Postagged/All: "+this.matches/this.all);

    }

    @Override
    public void paintResultArea(final DictHandling dictHandler) {
        if(!highlightedleft) {
            GUIWorker sw = new GUIWorker() {
                @Override
                protected Object doInBackground() throws Exception {

                    if (POSTagGUI.this.switchflag1) {
                        POSTagGUI.this.paintPOSTags(POSTagGUI.this.resultarea, POSTagGUI.this.origTranslit, false, false, dictHandler);
                    } else {
                        POSTagGUI.this.paintPOSTags(POSTagGUI.this.resultarea, POSTagGUI.this.origTranslit, false, true, dictHandler);
                    }
                    POSTagGUI.this.highlightedleft = true;
                    return null;
                }
            };
            sw.execute();
        }
        if(!highlightedright) {
            GUIWorker sw2 = new GUIWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    if (POSTagGUI.this.switchflag2) {
                        POSTagGUI.this.paintPOSTags(POSTagGUI.this.resultarea2, POSTagGUI.this.genTranslit, true, false, dictHandler);
                    } else {
                        POSTagGUI.this.paintPOSTags(POSTagGUI.this.resultarea2, POSTagGUI.this.genTranslit, true, true, dictHandler);
                    }
                    POSTagGUI.this.highlightedright = true;
                    return null;
                }
            };
            sw2.execute();
        }
    }
}
