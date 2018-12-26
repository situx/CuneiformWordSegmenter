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

import de.unifrankfurt.cs.acoli.akkad.dict.corpusimport.CorpusHandlerAPI;
import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.dict.pos.POSTagger;
import de.unifrankfurt.cs.acoli.akkad.dict.pos.util.POSDefinition;
import de.unifrankfurt.cs.acoli.akkad.methods.Methods;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.TransliterationMethod;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * JTextArea extended with a tooltip and coloring extension.
 */
public class JToolTipArea extends JTextArea implements MouseListener {
    /**The resource bundle to use.*/
    private final ResourceBundle bundle;
    /**The chartype to use.*/
    private CharTypes charType;
    /**The corpus handler to use.*/
    private CorpusHandlerAPI corpusHandler;
    /**The dicthandler to use.*/
    private DictHandling dictHandler;
    /**The postagger to use.*/
    private POSTagger postagger;
    /**Constructor for this class.*/
    public JToolTipArea(CharTypes charType,DictHandling dictHandler,ResourceBundle bundle){
        if(charType!=null){
            this.postagger=charType.getCorpusHandlerAPI().getPOSTagger(true);
            this.corpusHandler=charType.getCorpusHandlerAPI();
        }

        this.dictHandler=dictHandler;

        this.bundle=bundle;
        this.charType=charType;
    }

    public DictHandling getDictHandler() {
        return dictHandler;
    }

    public void setDictHandler(final DictHandling dictHandler) {
        this.dictHandler = dictHandler;
    }

    public POSTagger getPostagger() {
        return postagger;
    }

    public void setPostagger(final CharTypes charType) {
        this.postagger=charType.getCorpusHandlerAPI().getPOSTagger(true);
        this.corpusHandler=charType.getCorpusHandlerAPI();
        this.dictHandler=charType.getCorpusHandlerAPI().getUtilDictHandler();
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        Integer pos=this.viewToModel(event.getPoint());
        List<POSDefinition> posdefs;
        String currentword="";
        String result="<html>";
        try {
            int end=Utilities.getWordEnd(this, pos),start=Utilities.getWordStart(this, pos);
            if(start!=end){
                while(!this.getText().substring(end-1, end).equals("]") && !this.getText().substring(end-1, end).equals(" ") && !this.getText().substring(end-1, end).equals(System.lineSeparator())){
                    end=Utilities.getWordEnd(this, end);
                }
                while(!this.getText().substring(start, start+1).matches("(\\[|\\])") && !this.getText().substring(start, start+1).equals(" ") && !this.getText().substring(end-1, end).equals(System.lineSeparator())){
                    start=Utilities.getWordStart(this, start-1);
                }
            }
            currentword=this.getText().substring(start, end);
            System.out.println("Currentword: "+currentword);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        posdefs=this.postagger.getPosTagDefs(currentword,dictHandler);
        if(posdefs==null || posdefs.isEmpty()){
            if (!currentword.isEmpty() && !currentword.matches("[ ]+|\\[|\\]")) {
                result += currentword.endsWith("-") ? currentword.substring(0, currentword.length() - 1) : currentword;
                String temp;
                if (currentword.matches(charType.getLegalTranslitCharsRegex())) {
                            try {
                                temp=corpusHandler.transliterationToText(currentword.toLowerCase(), 0, dictHandler, false, true);
                                if(temp.isEmpty() || temp.matches("[ ]+")){
                                    return null;
                                }
                                result += " ("+temp + ")";
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            temp=new Methods().assignTransliteration(currentword.split(" "), dictHandler, TransliterationMethod.MAXPROB);
                            if(temp.isEmpty() || temp.matches("[ ]+")){
                                return null;
                            }
                            result += temp+ " (generated)";
                        }
            }else{
               return null;
            }

        }else{
            if(currentword.isEmpty() || currentword.matches("[ ]+|\\[|\\]")){
                return null;
            }
            Collections.reverse(posdefs);
            int i=0;
            for(POSDefinition posdef:posdefs){
                if(i>0)
                    result+="--------------------<br>";
                result+=posdef.toHTMLString(bundle,charType)+"<br>";
                i++;
            }
        }
        return result+"</html>";
    }

    @Override
    public void mouseClicked(final MouseEvent event) {

        }

    @Override
    public void mouseEntered(final MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(final MouseEvent mouseEvent) {

    }

    @Override
    public void mousePressed(final MouseEvent event) {
        System.out.println("MouseClickedEvent");
        Integer pos = this.viewToModel(event.getPoint());
        List<POSDefinition> posdefs;
        String currentword = "";
        String result = "<html>";
        try {
            int end = Utilities.getWordEnd(this, pos), start = Utilities.getWordStart(this, pos);
            if (start != end) {
                while (!this.getText().substring(end - 1, end).equals("]") && !this.getText().substring(end - 1, end).equals(" ") && !this.getText().substring(end - 1, end).equals(System.lineSeparator())) {
                    end = Utilities.getWordEnd(this, end);
                }
                while (!this.getText().substring(start, start + 1).matches("(\\[|\\])") && !this.getText().substring(start, start + 1).equals(" ") && !this.getText().substring(end - 1, end).equals(System.lineSeparator())) {
                    start = Utilities.getWordStart(this, start - 1);
                }
            }
            currentword = this.getText().substring(start, end);
            System.out.println("Currentword: " + currentword);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        posdefs = this.postagger.getPosTagDefs(currentword, dictHandler);
        if (posdefs == null || posdefs.isEmpty()) {
            if (!currentword.isEmpty() && !currentword.matches("[ ]+|\\[|\\]")) {
                result += currentword.endsWith("-") ? currentword.substring(0, currentword.length() - 1) : currentword;
                String temp;
                if (currentword.matches(charType.getLegalTranslitCharsRegex())) {
                    try {
                        temp = corpusHandler.transliterationToText(currentword.toLowerCase(), 0, dictHandler, false, true);
                        ClipboardHandler handler=new ClipboardHandler();
                        handler.setClipboardContents(temp);
                        System.out.println("Clipboard contains:" + handler.getClipboardContents());
                        JOptionPane.showInputDialog("Get Cuneiform here",temp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    temp = new Methods().assignTransliteration(currentword.split(" "), dictHandler, TransliterationMethod.MAXPROB);
                    ClipboardHandler handler=new ClipboardHandler();
                    handler.setClipboardContents(temp);
                    System.out.println("Clipboard contains:" + handler.getClipboardContents());
                    JOptionPane.showInputDialog("Get Cuneiform here",temp);
                }
            }
        }
    }

    @Override
    public void mouseReleased(final MouseEvent mouseEvent) {

    }

    public void setPostagger(final POSTagger postagger) {
        this.postagger=postagger;
    }

}
