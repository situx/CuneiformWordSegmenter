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

// required by InputMethod

import de.unifrankfurt.cs.acoli.akkad.main.gui.ime.jquery.TreeBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputMethodEvent;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.awt.im.InputMethodHighlight;
import java.awt.im.spi.InputMethod;
import java.awt.im.spi.InputMethodContext;
import java.text.AttributedString;
import java.util.Locale;
import java.util.TreeMap;

// required by implementation
// http://jan.newmarch.name/i18n/input/input.html
//, Monash University, 2007 
public class GenericInputMethod implements InputMethod {
    private static Locale[] SUPPORTED_LOCALES = {
            GenericInputMethodDescriptor.AKKADIAN,
            GenericInputMethodDescriptor.HITTITE,
            GenericInputMethodDescriptor.SUMERIAN,
            Locale.ENGLISH
    };
    private static Window statusWindow;
    private java.util.Map<Integer,TreeBuilder> builder;
    private InputMethodContext inputMethodContext;
    private JList list;
    private Integer local=0;
    private Locale locale= GenericInputMethodDescriptor.AKKADIAN;
    private Window lookupWindow;
    private StringBuffer rawText = new StringBuffer();
    // private String convertedText;
    private boolean wordTerminated;
    private Integer words=5;

    public GenericInputMethod() {
        this.locale=GenericInputMethodDescriptor.AKKADIAN;
        this.local=0;
        this.builder=new TreeMap<>();
        builder.put(0,new TreeBuilder(this.getClass().getClassLoader().getResourceAsStream("ime/akkadian.xml")));
        builder.put(1,new TreeBuilder(this.getClass().getClassLoader().getResourceAsStream("ime/hittite.xml")));
        builder.put(2,new TreeBuilder(this.getClass().getClassLoader().getResourceAsStream("ime/sumerian.xml")));
    }

    public void activate() {
        // Activates the input method for immediate input processing.
        // do nothing
    }

    private void appendRawText(char ch) {
        rawText.append(ch);
        sendRawText();
    }

    private void commit(String text) {
        sendConvertedText(text);
        rawText.setLength(0);
    }

    private void commitRawText() {
        String converted = convert(rawText);
        this.rawText.setLength(0);
        converted=converted.substring(converted.indexOf('.')+1);
        if(converted.split("[A-Za-z0-9]").length>0)
            converted=converted.split("[A-Za-z0-9]")[0];
        commit(converted);
    }

    String [] completedWords() {
        return builder.get(local).queryToArray(rawText.toString(), words);
    }

    private String convert(StringBuffer rawText) {
        if (rawText.length() == 0) {
            return "";
        }

        InputMethodHighlight highlight;
        String convertedText = rawText.toString();

        return convertedText;
    }

    public void deactivate(boolean isTemporary) {
        // Deactivates the input method.
    }

    public void dispatchEvent(AWTEvent event) {
        // Dispatches the event to the input method.

        if (event.getID() == KeyEvent.KEY_PRESSED) {
            KeyEvent e = (KeyEvent) event;
            handleCharacter(e);
        }
        if(event.getID()==KeyEvent.KEY_TYPED || event.getID() == KeyEvent.KEY_PRESSED || event.getID() == KeyEvent.KEY_RELEASED){
            KeyEvent e = (KeyEvent) event;
            if(e.getKeyCode()!=KeyEvent.VK_BACK_SPACE){           e.consume();
            }
        }

        // Also handle selection by mouse here
    }

    public void dispose() {
        // Disposes of the input method and releases the resources used by it.
        System.out.println("English completion di-XXX");
    }

    public void endComposition() {
        // Ends any input composition that may currently be going on in this context.
        String convertedText = convert(rawText);
        if (convertedText != null) {
            commit(convertedText);
        }
    }

    public Object getControlObject() {
        // Returns a control object from this input method, or null.
        // not supported yet
        System.out.println("English completion gc-XXX");
        return null;
    }

    public Locale getLocale() {
        // Returns the current input locale.
        return locale;
    }

    /**
     * Attempts to handle a typed character.
     * @return whether the character was handled
     *
     * States that need to be looked at:
     * - user is typing characters for composition
     * - user has asked for alternative completions
     * - user is asking for text to be committed
     */
    private boolean handleCharacter(KeyEvent keyEvent) {
        System.out.println("handling char \"" + keyEvent.getKeyChar() + "\"");
        System.out.println("raw is " + rawText);
        if (lookupWindow != null) {
            if(handleLookupWindow(keyEvent)){
               return true;
            }
        }
        if(keyEvent.getKeyCode()==KeyEvent.VK_BACK_SPACE && this.rawText.length()>0) {
            //this.rawText.setLength(this.rawText.length()-1);
            //this.sendConvertedText(this.rawText.toString());
            return true;
        }
        if (Character.isLetter(keyEvent.getKeyChar()) || Character.isDigit(keyEvent.getKeyChar())) {
            //this.rawText.append(keyEvent.getKeyChar());
            appendRawText(keyEvent.getKeyChar());
            if(lookupWindow!=null){
                lookupWindow.setVisible(false);
                lookupWindow=null;
            }
            invokeLookupWindow();
            // ch handled
            return true;
        }
        //this.sendRawText();
        // ch not handled
        return false;
    }

    private Boolean handleLookupWindow(KeyEvent keyEvent) {
        System.out.println("Keycode: "+keyEvent.getKeyCode()+"=="+KeyEvent.VK_DOWN);
        if (keyEvent.getKeyChar()==' ') {
            System.out.println("Committing lookup window");
            String selection = (String) list.getSelectedValue();
            rawText.setLength(0);
            rawText.append(selection);
            commitRawText();
            wordTerminated = false;
            lookupWindow.setVisible(false);
            lookupWindow = null;
            return true;
        }else if(keyEvent.getKeyChar()=='\n'){
                this.commitRawText();
                this.wordTerminated=false;
                lookupWindow.setVisible(false);
                lookupWindow = null;
            return true;
        }else if (keyEvent.getKeyCode()==KeyEvent.VK_DOWN && list.getSelectedIndex()<list.getModel().getSize()) {
                list.setSelectedIndex(list.getSelectedIndex() + 1);
            return true;
        }else if (keyEvent.getKeyCode()==KeyEvent.VK_UP && list.getSelectedIndex()>0) {
                list.setSelectedIndex(list.getSelectedIndex() -1);
            return true;
        }else if(Character.isDigit(keyEvent.getKeyChar()) && keyEvent.getKeyChar()!='0'){
                String converted;
                Boolean finishword=true;
                for(int i=0;i<list.getModel().getSize() && finishword;i++){
                    converted=list.getModel().getElementAt(i).toString().substring(list.getModel().getElementAt(i).toString().indexOf('.')+1);
                    System.out.println("Converted: "+converted+" keyEvent.getChar()"+keyEvent.getKeyChar());
                    System.out.println("Regex: "+converted.split("[A-Za-z0-9]").length);
                    if(converted.split("[A-Za-z0-9]").length>1) {
                        converted = converted.split("[A-Za-z0-9]")[1];
                        System.out.println("Converted: "+converted+" keyEvent.getChar()"+keyEvent.getKeyChar());
                        if(converted.charAt(0)==keyEvent.getKeyChar()){
                            finishword=false;
                            i=list.getModel().getSize();
                        }
                    }
                }
                if(finishword){
                    this.rawText.setLength(0);
                    this.rawText.append(list.getModel().getElementAt(Character.getNumericValue(keyEvent.getKeyChar())-1).toString());
                    this.commitRawText();
                    this.wordTerminated=false;
                    lookupWindow.setVisible(false);
                    lookupWindow = null;
                    return true;
                }

        }
        return false;
    }

    public void hideWindows() {
        // Closes or hides all windows opened by this input method instance or its class.
    }

    private void invokeLookupWindow() {
        // stub
        System.out.println("invoked lookup window");
        lookupWindow = inputMethodContext.createInputMethodWindow("Lookup Window", true);

        // make a list of possible completions
	/*
	String[] data = new String[] {
	    rawText.toString(),
	    rawText.toString()+rawText.toString(),
	    rawText.toString()+rawText.toString()+rawText.toString()
	};
	*/
        String [] data = completedWords();
        System.out.println("Completed Words: "+data);
        list = new JList(data);
        list.setSelectedIndex(0);

        lookupWindow.setLayout(new BorderLayout());
        lookupWindow.setAlwaysOnTop(false);
        lookupWindow.setType(Window.Type.UTILITY);
        lookupWindow.add(list, BorderLayout.CENTER);
        lookupWindow.setSize(200, 300);
        lookupWindow.setVisible(true);
    }

    public boolean isCompositionEnabled() {
        //  Determines whether this input method is enabled.
        // always enabled
        return true;
    }

    public void setCompositionEnabled(boolean enable) {
        // Enables or disables this input method for composition, depending on the value of the parameter enable.
        // not supported yet
        System.out.println("English completion sce-XXX");
        throw new UnsupportedOperationException();
    }

    public void notifyClientWindowChange(Rectangle bounds) {
        // Notifies this input method of changes in the client window location or state.
        // not supported yet
        System.out.println("English completion nw-XXX");
        throw new UnsupportedOperationException();
    }

    public void reconvert() {
        // Starts the reconversion operation.
        // not supported yet
        System.out.println("English completion re-XXX");
        throw new UnsupportedOperationException();
    }

    public void removeNotify() {
        // Notifies the input method that a client component has been removed from its containment hierarchy, or that input method support has been disabled for the component.
        // not supported yet
        System.out.println("English completion rn-XXX");
        throw new UnsupportedOperationException();
    }

    private void sendConvertedText(String convertedText) {
        InputMethodHighlight highlight;

        highlight = InputMethodHighlight.SELECTED_CONVERTED_TEXT_HIGHLIGHT;
        AttributedString as = new AttributedString(convertedText);
        as.addAttribute(TextAttribute.INPUT_METHOD_HIGHLIGHT, highlight);

        inputMethodContext.dispatchInputMethodEvent(
                InputMethodEvent.INPUT_METHOD_TEXT_CHANGED,
                as.getIterator(),
                convertedText.length(),
                TextHitInfo.leading(convertedText.length()),
                null);

    }

    private void sendRawText() {
        String text = rawText.toString();
        System.out.println("send raw text: " + text);
        InputMethodHighlight highlight;
        AttributedString as = new AttributedString(text);

        highlight = InputMethodHighlight.SELECTED_RAW_TEXT_HIGHLIGHT;
        as.addAttribute(TextAttribute.INPUT_METHOD_HIGHLIGHT, highlight);

        inputMethodContext.dispatchInputMethodEvent(
                InputMethodEvent.INPUT_METHOD_TEXT_CHANGED,
                as.getIterator(),
                0,
                null,
                null);

    }

    public void setCharacterSubsets(Character.Subset[] subsets) {
        // Sets the subsets of the Unicode character set that this input method is allowed to input.
        // not supported yet
        System.out.println("English completion sc-XXX");
        return;
        // throw new UnsupportedOperationException();
    }

    public void setInputMethodContext(InputMethodContext context) {
        // Sets the input method context, which is used to dispatch input method events to the client component and to request information from the client component.

        inputMethodContext = context;
        System.out.println("Input context set " + context);


        /*if (statusWindow == null) {
            statusWindow = context.createInputMethodWindow("Simp. Chinese Pinyin", false);
            Label label = new Label("Pig Latin locale");
            label.setBackground(Color.white);
            statusWindow.add(label);
            // updateStatusWindow(locale);
            label.setSize(200, 50);
            statusWindow.pack();
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            statusWindow.setLocation(d.width - statusWindow.getWidth(),
                                     d.height - statusWindow.getHeight());
	    } */

    }

    public boolean setLocale(Locale locale) {
        // Attempts to set the input locale.

        for (int i = 0; i < SUPPORTED_LOCALES.length; i++) {
            if (locale.equals(SUPPORTED_LOCALES[i])) {
                this.locale = locale;
                this.local=i;
		/*
		if (statusWindow != null) {
                    updateStatusWindow(locale);
                }
		*/
                System.out.println("Locale set");
                return true;
            }
        }
        System.out.println("Locale not set");
        return false;
    }


}
