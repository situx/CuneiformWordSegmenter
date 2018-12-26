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
import de.unifrankfurt.cs.acoli.akkad.main.gui.util.JToolTipArea;
import de.unifrankfurt.cs.acoli.akkad.main.gui.util.TextLineNumber;
import de.unifrankfurt.cs.acoli.akkad.main.gui.util.UTF8Bundle;
import de.unifrankfurt.cs.acoli.akkad.util.Config;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.ClassificationMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.MethodEnum;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * ComparingGUI for viewing results..
 */
public abstract class CompGUI extends JPanel {

    /**Size of cuneiform fonts.*/
    protected static Integer CUNEIFONTSIZE=16;
    /**Size of transliteration fonts.*/
    protected static Integer TRANSLITFONTSIZE=12;
    /**Resource bundle to use.*/
    public ResourceBundle bundle=ResourceBundle.getBundle(Config.RESBUNDLENAME, Locale.getDefault(),new UTF8Bundle("UTF-8"));
    /**Button for the difference view.*/
    protected JButton diffbutton;
    /**Dropdown menu for the classification methods.*/
    protected JComboBox<ClassificationMethod> featuresetchooser;
    /**Generated and original file paths.*/
    protected String generatedFile="",originalfile="";
    /**Counts matches and all words.*/
    protected Double matches,all;
    /**Temporary Strings to save transliteration and original texts.*/
    protected String origTranslit ="", genTranslit ="",origCunei="",genCunei="";
    /**Tooltip areas to visualize the result.*/
    protected JToolTipArea resultarea,resultarea2;
    /**Scrollpane for Tooltip area one.*/
    protected JScrollPane scrollPane;
    /**Scrollpane for Tooltip area two.*/
    protected JScrollPane scrollPane2;
    protected Boolean selectedMethodsG1;
    protected JTextField statistics;
    /**Switcher button for transliteration to cuneiform.*/
    protected JButton switchbutton;
    /**Switcher button for transliteration to cuneiform.*/
    protected JButton switchbutton2;
    /**Boolean flags as switching indicators.*/
    protected boolean switchflag1,switchflag2,highlightedleft=false,highlightedright=false;
    int y=0;

    /**
     * Constructor for CompGUI.
     * @param originalfile the original text
     * @param generatedFile the generated text
     * @param selectedMethods the selected methods to compare
     * @param evalResult the evaluation result
     */
    public CompGUI(final String originalfile, final String generatedFile, final List<MethodEnum> selectedMethods, final EvalResult evalResult,CharTypes charTypes){
        this.setLayout(new GridBagLayout());
        this.selectedMethodsG1=selectedMethods.size()>1;
        GridBagConstraints c = new GridBagConstraints();
        this.generatedFile=generatedFile;
        this.originalfile=originalfile;
        this.switchbutton=new JButton();
        this.switchbutton2=new JButton();
        this.diffbutton=new JButton();
        int ysize=540;
        JPanel mainPanel=new JPanel();
        JPanel mainPanel2=new JPanel();
        this.statistics=new JTextField();
        this.statistics.setEnabled(false);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = y;
        c.gridwidth=1;
        this.add(statistics,c);
        if(selectedMethods.size()>1){
            JPanel featurePanel=new JPanel();
            JLabel featureset = new JLabel(bundle.getString("algorithm")+":");
            Set<MethodEnum> evalset=new TreeSet<>(selectedMethods);
            featuresetchooser = new JComboBox<ClassificationMethod>(evalset.toArray(new ClassificationMethod[evalset.size()]));

            ysize=530;
            featurePanel.add(featureset);
            featurePanel.add(featuresetchooser);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 0.5;
            c.gridx = 0;
            c.gridy = y++;
            c.gridwidth=1;
            this.add(featurePanel,c);
        }else{
            y++;
        }
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
        c.gridy = y;
        c.gridwidth=1;
        this.add(mainPanel,c);
        c.gridx=1;
        this.add(mainPanel2,c);
        this.resultarea=new JToolTipArea(charTypes,null,bundle)
        {
            public boolean getScrollableTracksViewportWidth()
            {
                return getUI().getPreferredSize(this).width
                        <= getParent().getSize().width;
            }
        };
        this.scrollPane = new JScrollPane();
        this.scrollPane.setViewportView(this.resultarea);
        resultarea.setEditable(false);
        scrollPane.setPreferredSize(new Dimension(530,ysize));
        mainPanel.add(scrollPane);
        this.resultarea2=new JToolTipArea(charTypes,null,bundle)
        {
            public boolean getScrollableTracksViewportWidth()
            {
                return getUI().getPreferredSize(this).width
                        <= getParent().getSize().width;
            }
        };
        TextLineNumber tln = new TextLineNumber(resultarea,new TreeMap<Integer,String>());
        scrollPane.setRowHeaderView( tln );
        this.scrollPane2 = new JScrollPane();
        this.scrollPane2.setViewportView(this.resultarea2);
        resultarea2.setEditable(false);
        this.resultarea.setFont(new Font(this.resultarea.getFont().getName(), 0, TRANSLITFONTSIZE));
        scrollPane2.setPreferredSize(new Dimension(530,ysize));
        this.resultarea2.setFont(new Font(this.resultarea2.getFont().getName(), 0, TRANSLITFONTSIZE));
        TextLineNumber tln2 = new TextLineNumber(resultarea2,new TreeMap<Integer,String>());
        scrollPane2.setRowHeaderView( tln2 );
        scrollPane.setPreferredSize(new Dimension(530,ysize));
        mainPanel.add(scrollPane);
        mainPanel.setPreferredSize(new Dimension(530,ysize));
        scrollPane2.setPreferredSize(new Dimension(530,ysize));
        mainPanel2.add(scrollPane2);
        mainPanel2.setPreferredSize(new Dimension(530,ysize));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = ++y;
        c.gridwidth=1;
        this.add(switchbutton,c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = y++;
        c.gridwidth=1;
        this.add(switchbutton2,c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = y;
        c.anchor = GridBagConstraints.PAGE_END;
        c.gridwidth=2;
        this.add(diffbutton,c);
    }

    /**
     * Attemps to find missing segments in the given text.
     * @param originalstr the original text
     * @param revisedstr the revised text
     * @param chartype the language to use
     * @return the Strign containing missing elements
     */
    public static String findMissingSegments(final String originalstr,final String revisedstr,final CharTypes chartype){
        boolean wordfinished = true;
        int originalk = 0, revisedk = 0;
        String result="";
        String[] originalsplitword = new String[2], revisedsplitword;
        java.util.List<String> original;
        java.util.List<String> revised;
        original = Arrays.asList(originalstr.split("\n"));
        revised = Arrays.asList(revisedstr.split("\n"));
        int position = 0;
        for (int i = 0; i < revised.size(); i+=chartype.getChar_length()) {
            String charline=original.get(i).replaceAll(" ","");
            String prev,fol="",currentoriginal,currentrevised;
            int revisedoffset=0,originaloffset=0;

            for(int j=0;j<charline.length()/*-chartype.getChar_length()*/;j+=chartype.getChar_length()){
                   prev=charline.substring(j,j+chartype.getChar_length());
                   //fol=charline.substring(j+chartype.getChar_length(),j+chartype.getChar_length()*2);
                   currentrevised=revised.get(i).substring(j+revisedoffset,j+revisedoffset+chartype.getChar_length());
                   currentoriginal=original.get(i).substring(j+originaloffset,j+originaloffset+chartype.getChar_length());
                   System.out.println("Currentrevised: "+currentrevised);
                   System.out.println("Currentoriginal: "+currentoriginal);
                   System.out.println("Prev: "+prev);
                   System.out.println("Fol: "+fol);
                   if(currentoriginal.matches("[ ]+")){
                       if(currentrevised.matches("[ ]+")){
                           while(original.get(i).substring(j+originaloffset,j+originaloffset+1).equals(" ")){
                               originaloffset++;
                           }
                           while(revised.get(i).substring(j+revisedoffset,j+revisedoffset+1).equals(" ")){
                               revisedoffset++;
                           }
                           result+=("   "+currentoriginal);
                       }else{
                           while(original.get(i).substring(j+originaloffset,j+originaloffset+1).equals(" ")){
                               originaloffset++;
                           }
                           //txt.replaceRange( replace, nextPosn, nextPosn + find.length() );
                           result+=" "+currentrevised;
                       }

                   }else{
                       if(currentrevised.matches("[ ]+")){
                           while(revised.get(i).substring(j+revisedoffset,j+revisedoffset+1).equals(" ")){
                               revisedoffset++;
                           }
                           result+="   "+currentoriginal;
                       }else{
                           result+=currentoriginal;
                       }
                   }
            }
        }
        System.out.println("Result: "+result);
        return result;
    }

    public static void main(String[] args){
          String cunei= "𒇲   𒐊𒐼𒊍𒍣";
          String cunei2="𒇲𒐊   𒐼   𒊍   𒍣";

          CompGUI.findMissingSegments(cunei,cunei2,CharTypes.AKKADIAN);
    }

    /**
     * Creates a legend for the user.
     * @param legenddata the legenddata needed to create the legend
     */
    public void createLegend(java.util.Map<String,Color> legenddata){
        JPanel legendpanel=new JPanel();
        legendpanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        int y=2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 2;
        c.gridy = y++;
        c.gridwidth=1;
        this.add(legendpanel,c);

        for(String key:legenddata.keySet()){
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 0.5;
            c.gridx = 0;
            c.gridy = y++;
            c.gridwidth=1;
            JLabel nounoradj=new JLabel(bundle.getString(key));
            Border border = BorderFactory.createLineBorder(Color.BLACK, 1);

            // set the border of this component
            nounoradj.setBorder(border);
            nounoradj.setOpaque(true);
            nounoradj.setBackground(legenddata.get(key));
            legendpanel.add(nounoradj,c);
        }
    }

    /**
     * Reformates the given method name.
     * @param filename the name of the file to consider
     * @param method the method to choose
     * @return  the modified name
     */
    public String exchangeMethodName(String filename,ClassificationMethod method){
        return filename.substring(0,filename.indexOf('_')+1)+method.toString().toLowerCase()+filename.substring(filename.indexOf('_',filename.indexOf('_')+1));
    }

    /**
     * Generic method for painting the result area.
     * @param dictHandler the dicthandler to use
     */
    public abstract void paintResultArea(DictHandling dictHandler);

    /**
     * Reformats the generated contents area to display new text.
     * @param file the file to load
     * @param font1 the first font to use
     * @param font2 the second font to use
     * @param spaces the amount of spaces to set
     * @throws IOException
     */
    public void setGeneratedContents(File file,Integer font1,Integer font2,Integer spaces) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        this.resultarea2.setText("");
        this.resultarea2.read(br, null);
        br.close();
        String space="";
        for(int i=0;i<spaces;i++){
            space+=" ";
        }
        this.resultarea2.setText(this.resultarea2.getText().replaceAll(" ",space));
        if(switchflag2){

            this.resultarea2.setFont(new Font(this.resultarea2.getFont().getName(), 0, font1));
        }else{
            this.resultarea2.setFont(new Font(this.resultarea2.getFont().getName(), 0, font2));
        }
        TextLineNumber tln = new TextLineNumber(resultarea2,new TreeMap<Integer,String>());
        scrollPane2.setRowHeaderView(tln);
        this.highlightedright=false;
    }
    /**
     * Reformats the original contents area to display new text.
     * @param file the file to load
     * @param font1 the first font to use
     * @param font2 the second font to use
     * @param spaces the amount of spaces to set
     * @throws IOException
     */
    public void setOriginalContents(File file,Integer font1,Integer font2,Integer spaces) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        this.resultarea.setText("");
        this.resultarea.read(br, null);
        br.close();
        String space="";
        for(int i=0;i<spaces;i++){
            space+=" ";
        }
        this.resultarea.setText(this.resultarea.getText().replaceAll(" ",space));
        if(switchflag1){
            this.resultarea.setFont(new Font(this.resultarea.getFont().getName(), 0, font1));
        }else{
            this.resultarea.setFont(new Font(this.resultarea.getFont().getName(), 0, font2));
        }
        TextLineNumber tln = new TextLineNumber(resultarea,new TreeMap<Integer,String>());
        scrollPane.setRowHeaderView( tln );
        this.highlightedleft=false;
    }

}
