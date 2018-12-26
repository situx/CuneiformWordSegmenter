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
import de.unifrankfurt.cs.acoli.akkad.dict.pos.cuneiform.AkkadPOSTagger;
import de.unifrankfurt.cs.acoli.akkad.main.gui.util.*;
import de.unifrankfurt.cs.acoli.akkad.util.Config;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.ClassificationMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.TranslationMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Tags;
import org.abego.treelayout.TreeForTreeLayout;
import org.abego.treelayout.TreeLayout;
import org.abego.treelayout.util.DefaultConfiguration;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Main class for the external POSTagger application.
 */
public class POSTagMain extends JFrame {
    /**The cuneiform font size.*/
    protected static Integer CUNEIFONTSIZE=13;
    /**The transliteration font size.*/
    protected static Integer TRANSLITFONTSIZE=12;
    /**Checkbox to switch to cuneiform mode.*/
    private final JCheckBox cuneiformCheckbox;
    /**Button to show a tree of the current sentence.*/
    private final JButton treebutton;
    /**The resource bundle to use.*/
    public ResourceBundle bundle=ResourceBundle.getBundle(Config.RESBUNDLENAME, Locale.getDefault(),new UTF8Bundle("UTF-8"));
    /**Maps from POSTag case to the given highlights in order to modify them.*/
    protected Map<String,List<Highlighter.Highlight>> caseToHighlights;
    /**Necessary buttons for exporting and classifying.*/
    protected JButton diffbutton,exportResult;
    /**Shows the given classification methods.*/
    protected JComboBox<ClassificationMethod> featuresetchooser;
    /**Matches the given words and all words.*/
    protected Double matches,all;
    /**The postagger to use.*/
    protected POSTagger postagger;
    /**The resultarea to use.*/
    protected JToolTipArea resultarea;
    /**The scrollpane of the resultarea.*/
    protected JScrollPane scrollPane;
    /**The statistics to display.*/
    protected JTextField statistics;
    /**Boolean flags for view switching.*/
    protected boolean switchflag1,switchflag2,highlighted;
    /**Maps from the count of the beginning word of a highlight to the highlight.*/
    protected Map<Integer,HighlightData> wordCountToHighlightData;
    /**Counter variable for words.*/
    int y=0;
    /**Highlighter for coloring the text.*/
    private Highlighter highlighter;
    /**The panel containing the legend.*/
    private JPanel legendpanel;

    /**
     * Constructor for this class.
     */
    public POSTagMain(){
        //this.buildMenu();
        this.caseToHighlights=new TreeMap<>();
        this.wordCountToHighlightData=new TreeMap<>();
        this.setIconImage(new ImageIcon("img/akkadian.png").getImage());
        this.setTitle(bundle.getString("postagger"));
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.diffbutton=new JButton(bundle.getString("postagging"));
        this.exportResult=new JButton(bundle.getString("exportResult"));
        int ysize=550;
        JPanel mainPanel=new JPanel();
        this.statistics=new JTextField();
        this.statistics.setEnabled(false);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = y;
        c.gridwidth=1;
        //this.add(statistics,c);
        //y++;
        JLabel original=new JLabel(bundle.getString("postagger"));
        c.fill = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = y++;
        c.gridwidth=2;
        this.add(original,c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = y++;
        c.gridwidth=2;
        this.add(mainPanel,c);
        this.postagger=CharTypes.AKKADIAN.getCorpusHandlerAPI().getPOSTagger(false);
        this.resultarea=new JToolTipArea(CharTypes.AKKADIAN,CharTypes.AKKADIAN.getCorpusHandlerAPI().getUtilDictHandler(),bundle)
        {
            public boolean getScrollableTracksViewportWidth()
            {
                return getUI().getPreferredSize(this).width
                        <= getParent().getSize().width;
            }
        };
        ToolTipManager.sharedInstance().registerComponent(resultarea);
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
        this.scrollPane = new JScrollPane();
        this.scrollPane.setViewportView(this.resultarea);
        resultarea.setEditable(false);
        scrollPane.setPreferredSize(new Dimension(1060,ysize));
        TextLineNumber tln = new TextLineNumber(resultarea,new TreeMap<Integer,String>());
        scrollPane.setRowHeaderView( tln );
        this.resultarea.setFont(new Font(this.resultarea.getFont().getName(), 0, TRANSLITFONTSIZE));
        scrollPane.setPreferredSize(new Dimension(1060,ysize));
        mainPanel.add(scrollPane);
        mainPanel.setPreferredSize(new Dimension(1060,ysize));
        final JFileChooser trainfilechooser=new JFileChooser();
        JLabel trainingfilelabel=new JLabel(bundle.getString("postagfile"));
        final JTextField trainingfilefield=new JTextField(25);
        trainingfilefield.setEnabled(false);
        final JComboBox<CharTypes> chartypechooser = new JComboBox<CharTypes>(CharTypes.values());
        final JCheckBox checkbox=new JCheckBox();
        this.cuneiformCheckbox=new JCheckBox();
        this.cuneiformCheckbox.setSelected(true);
        final JLabel cuneiformLabel=new JLabel(bundle.getString("transliteration"));
        final JButton trainfilebutton=new JButton(bundle.getString("choose"));
        trainfilebutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                //Handle open trainfilebutton action.
                if (actionEvent.getSource() == trainfilebutton) {
                    int returnVal = trainfilechooser.showOpenDialog(POSTagMain.this);

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = trainfilechooser.getSelectedFile();
                        trainingfilefield.setText(file.getAbsolutePath());
                        if(cuneiformCheckbox.isSelected()){
                            POSTagMain.this.resultarea.setFont(new Font(POSTagMain.this.resultarea.getFont().getName(), 0, TRANSLITFONTSIZE));
                        }else{
                            POSTagMain.this.resultarea.setFont(new Font(POSTagMain.this.resultarea.getFont().getName(), 0, CUNEIFONTSIZE));
                        }
                        POSTagMain.this.resultarea.setText("");
                        try {
                            String result="";

                            if(checkbox.isSelected()){
                                BufferedReader reader=new BufferedReader(new FileReader(trainfilechooser.getSelectedFile()));
                                String temp;
                                while((temp=reader.readLine())!=null){
                                    result+=((CharTypes) chartypechooser.getSelectedItem()).getCorpusHandlerAPI().corpusToReformatted(temp);
                                }
                                POSTagMain.this.resultarea.setText(result);
                            }else{
                                POSTagMain.this.resultarea.read(new FileReader(trainfilechooser.getSelectedFile()),null);
                            }
                            TextLineNumber tln = new TextLineNumber(resultarea,new TreeMap<Integer, String>());
                            scrollPane.setRowHeaderView(tln);
                            POSTagMain.this.highlighted=false;
                        } catch (IOException e) {
                            JOptionPane.showMessageDialog(POSTagMain.this,
                                    e.getMessage(),bundle.getString("error"),JOptionPane.ERROR_MESSAGE);
                        }
                        System.out.println("Opening: " + file.getName() + ".");
                    } else {
                        System.out.println("Open command cancelled by user.");
                    }
                }
            }
        });
        System.out.println("TrainfilePanel");
        final JButton definitionReload=new JButton(bundle.getString("reloadDefs"));
        final JButton chooseColor=new JButton("Colors");
        chooseColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                Color newColor = JColorChooser.showDialog(
                        new JColorChooser(),
                        "Choose Color",
                        Color.yellow);
            }
        });
        final JButton translation=new JButton("Translation");
        translation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                new TranslationMain("Translation",resultarea.getText(),POSTagMain.this.postagger,((CharTypes)chartypechooser.getSelectedItem()),CharTypes.ENGLISH, TranslationMethod.LEMMA);
            }
        });

        final JButton regex=new JButton("Regex Tester");
        regex.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                new RegexTester();
            }
        });
        this.createLegend(new AkkadPOSTagger().getPoscolors());
        final JPanel trainfilepanel = new JPanel();
        final JLabel atflabel=new JLabel(bundle.getString("loadOriginalatf"));
        trainfilepanel.add(trainingfilelabel);
        trainfilepanel.add(trainingfilefield);
        trainfilepanel.add(trainfilebutton);
        this.treebutton=new JButton("Tree");
        treebutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                TreeForTreeLayout<POSInBox> tree = ((AkkadPOSTagger)POSTagMain.this.postagger).posTreeBuilder(1);

                // setup the tree layout configuration
                double gapBetweenLevels = 50;
                double gapBetweenNodes = 10;
                DefaultConfiguration<POSInBox> configuration = new DefaultConfiguration<POSInBox>(
                        gapBetweenLevels, gapBetweenNodes);

                // create the NodeExtentProvider for TextInBox nodes
                TextInBoxNodeExtentProvider nodeExtentProvider = new TextInBoxNodeExtentProvider();

                // create the layout
                TreeLayout<POSInBox> treeLayout = new TreeLayout<POSInBox>(tree,
                        nodeExtentProvider, configuration);

                // Create a panel that draws the nodes and edges and show the panel
                TextInBoxTreePane panel = new TextInBoxTreePane(treeLayout);
                JFrame dialog = new JFrame();
                dialog.setTitle("Dependency Tree");
                Container contentPane = dialog.getContentPane();
                ((JComponent) contentPane).setBorder(BorderFactory.createEmptyBorder(
                        10, 10, 10, 10));
                contentPane.add(panel);
                dialog.pack();
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
            }
        });
        JPanel configpanel=new JPanel();
        configpanel.add(checkbox);
        configpanel.add(atflabel);
        configpanel.add(definitionReload);
        configpanel.add(exportResult);
        configpanel.add(chooseColor);
        configpanel.add(regex);
        configpanel.add(translation);
        configpanel.add(treebutton);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = y;
        c.gridwidth=1;
        this.add(trainfilepanel,c);
        final JPanel charTypePanel = new JPanel();
        JLabel chartype = new JLabel(bundle.getString("chartype")+":");
        charTypePanel.add(cuneiformCheckbox);
        charTypePanel.add(cuneiformLabel);
        charTypePanel.add(chartype);
        charTypePanel.add(chartypechooser);
        chartypechooser.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent itemEvent) {
                POSTagMain.this.postagger = ((CharTypes) chartypechooser.getSelectedItem()).getCorpusHandlerAPI().getPOSTagger(false);
                POSTagMain.this.getContentPane().remove(POSTagMain.this.legendpanel);
                POSTagMain.this.repaint();
                if (POSTagMain.this.postagger != null)
                    POSTagMain.this.createLegend(POSTagMain.this.postagger.getPoscolors());
                POSTagMain.this.resultarea.setPostagger(((CharTypes) chartypechooser.getSelectedItem()));
                POSTagMain.this.resultarea.setDictHandler(((CharTypes) chartypechooser.getSelectedItem()).getCorpusHandlerAPI().getUtilDictHandler());
                POSTagMain.this.pack();
                POSTagMain.this.repaint();
            }
        });
        diffbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                POSTagMain.this.paintResultArea((CharTypes) chartypechooser.getSelectedItem());
            }
        });

        definitionReload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                //Handle open trainfilebutton action.
                POSTagMain.this.postagger=((CharTypes) chartypechooser.getSelectedItem()).getCorpusHandlerAPI().getPOSTagger(true);
                if(POSTagMain.this.highlighter!=null){
                    POSTagMain.this.highlighter.removeAllHighlights();
                }
                POSTagMain.this.highlighted=false;
                if(POSTagMain.this.legendpanel!=null) {
                    POSTagMain.this.getContentPane().remove(POSTagMain.this.legendpanel);
                    POSTagMain.this.repaint();
                }
                POSTagMain.this.resultarea.setPostagger(POSTagMain.this.postagger);
                POSTagMain.this.resultarea.setDictHandler(((CharTypes) chartypechooser.getSelectedItem()).getCorpusHandlerAPI().getUtilDictHandler());
                POSTagMain.this.createLegend(POSTagMain.this.postagger.getPoscolors());
                POSTagMain.this.pack();
                POSTagMain.this.repaint();
            }
        });
        exportResult.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
// parent component of the dialog
                JFrame parentFrame = new JFrame();

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Specify a file to save");
                int userSelection = fileChooser.showSaveDialog(parentFrame);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    try {
                        System.out.println("Save as file: " + fileToSave.getAbsolutePath());
                        FileWriter writer=new FileWriter(fileToSave);
                        writer.write(POSTagMain.this.postagger.textToPosTagXML(POSTagMain.this.resultarea.getText()));
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = y++;
        c.gridwidth=1;
        this.add(charTypePanel,c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = y++;
        c.gridwidth=2;
        this.add(configpanel,c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = ++y;
        c.anchor = GridBagConstraints.PAGE_END;
        c.gridwidth=2;
        this.add(diffbutton,c);
        //this.add(mainPanel);
        this.setPreferredSize(new Dimension(1100, 1000));
        this.pack();
        this.setVisible(true);

    }

    public static void main(String[] args){
        new POSTagMain();
    }

    /**
     * Creates a legend for the postag view.
     * @param legenddata the legenddata to display
     */
    public void createLegend(final java.util.Map<String,Color> legenddata){
        System.out.println("CreateLegend");
        this.legendpanel=new JPanel();
        legendpanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        int y=1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 2;
        c.gridy = y;
        c.gridwidth=1;
        this.add(legendpanel,c);

        for(String key:legenddata.keySet()){
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 0.5;
            c.gridx = 0;
            c.gridy = y++;
            c.gridwidth=1;
            final JLabel nounoradj;
            if(!bundle.containsKey(key)){
                nounoradj=new JLabel(key);
            }else{
                nounoradj=new JLabel(bundle.getString(key));
            }
            nounoradj.setName(key);
            nounoradj.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    DefaultHighlighter.DefaultHighlightPainter painter;
                    if(POSTagMain.this.postagger.getPoscolors().get(nounoradj.getName()).getRGB()==legenddata.get(Tags.DEFAULT.toString()).getRGB()){
                        return;
                    }
                    //System.out.println("POSTagger: "+POSTagMain.this.postagger.getPoscolors().get(nounoradj.getName()).getRGB()+" - "+nounoradj.getBackground().getRGB());
                    if(POSTagMain.this.postagger.getPoscolors().get(nounoradj.getName()).getRGB()==nounoradj.getBackground().getRGB()) {
                        painter= new DefaultHighlighter.DefaultHighlightPainter(POSTagMain.this.postagger.getPoscolors().get(Tags.DEFAULT.toString()));
                        nounoradj.setBackground(legenddata.get(Tags.DEFAULT.toString()));
                    }else {
                        painter = new DefaultHighlighter.DefaultHighlightPainter(POSTagMain.this.postagger.getPoscolors().get(nounoradj.getName()));
                        nounoradj.setBackground(POSTagMain.this.postagger.getPoscolors().get(nounoradj.getName()));
                    }
                    //System.out.println("Highlighter Color: "+POSTagMain.this.postagger.getPoscolors().get(nounoradj.getName()).getRGB());
                    //System.out.println("CaseToHighlights: "+POSTagMain.this.caseToHighlights.get(nounoradj.getName()).size());
                    List<Highlighter.Highlight> newhighlights=new LinkedList<Highlighter.Highlight>();
                    //System.out.println(POSTagMain.this.caseToHighlights);
                    for(Highlighter.Highlight high:POSTagMain.this.caseToHighlights.get(nounoradj.getName())){
                        try {
                            highlighter.removeHighlight(high);
                            newhighlights.add((Highlighter.Highlight)highlighter.addHighlight(high.getStartOffset(),high.getEndOffset(),painter));
                        } catch (BadLocationException e1) {
                            System.out.println(e1.getMessage());
                        }
                    }
                    POSTagMain.this.caseToHighlights.put(nounoradj.getName(),newhighlights);
                    POSTagMain.this.repaint();
                }

            });
            Border border = BorderFactory.createLineBorder(Color.BLACK, 1);

            // set the border of this component
            nounoradj.setBorder(border);
            nounoradj.setOpaque(true);
            nounoradj.setBackground(legenddata.get(key));
            legendpanel.add(nounoradj,c);
        }
    }

    /**
     * Paints postags according to recognized words.
     * @param resultarea the area to paint in
     * @param translittext the text to paint
     * @param cuneiform cuneiform indicator
     */
    private void paintPOSTags(final JTextArea resultarea,final String translittext,final Boolean cuneiform){
        this.all=0.;
        this.matches=0.;
        List<String> revised;
        revised=Arrays.asList(translittext.split("\n"));
        /*if(genOrOrig){
            revised = Arrays.asList(POSTagMain.this.genTranslit.split("\n"));
        }else{
            revised = Arrays.asList(POSTagMain.this.origTranslit.split("\n"));
        }*/
        this.caseToHighlights.clear();
        this.wordCountToHighlightData.clear();
        this.postagger.reset();
        for(String pos:this.postagger.getPoscolors().keySet()){
            this.caseToHighlights.put(pos,new LinkedList<Highlighter.Highlight>());
        }
        this.highlighter = resultarea.getHighlighter();
        if (!cuneiform) {
            System.out.println(revised);
            int position = 0, endposition = 0;
            for (String revi:revised) {
                String[] revisedwords = revi.split(" \\[");
                for (int w = 0; w < revisedwords.length; ) {
                    String word = revisedwords[w].trim();
                    System.out.println("Word: "+word);
                    position += word.length();
                    List<Integer> result=this.postagger.getPosTag(word,this.resultarea.getDictHandler());
                    System.out.println("GetPosTag: "+result.toString());
                    Color color=Color.white;
                    if(!result.isEmpty()){
                        color=new Color(result.get(0));
                    }else if(this.postagger.getPoscolors().containsKey(Tags.DEFAULT.toString())){
                        color=(this.postagger.getPoscolors().get(Tags.DEFAULT.toString()));
                    }
                    try {
                        endposition = resultarea.getText().indexOf("]", position - word.length())+1;
                        String paintword = resultarea.getText().substring(position - word.length(), endposition);
                        System.out.println(paintword);
                        if(!result.isEmpty() && (word.length()!=(result.get(2)+2) || result.get(1)!=0)){
                            int i=0,endindex,startindex;
                            while(i+2<result.size()){
                                endindex=endposition-word.length()+result.get(i+2);
                                System.out.println("Endposition: "+endposition);
                                System.out.println("Endindex: "+endindex);
                                System.out.println("Word: "+word+" "+word.length());
                                if(!resultarea.getText().substring(endindex-1,endindex).equals("-") && !resultarea.getText().substring(endindex-1,endindex).equals("]")) {
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
                                startindex=position-word.length()+result.get(i+1);
                                if(!this.resultarea.getText().substring(startindex,startindex+1).matches("[\\[|-]")){
                                    if(this.resultarea.getText().substring(startindex+1,startindex+2).matches("[\\[|-]")){
                                        startindex++;
                                    }else if(this.resultarea.getText().substring(startindex-1,startindex).matches("[\\[|-]")){
                                        startindex--;
                                    }
                                }
                                this.caseToHighlights.get(this.postagger.getColorToPos().get(new Color(result.get(i)).getRGB())).add((Highlighter.Highlight) highlighter.addHighlight(startindex,
                                        endindex, new DefaultHighlighter.DefaultHighlightPainter(new Color(result.get(i)))));
                                //highlighter.addHighlight(startindex, endindex, new DefaultHighlighter.DefaultHighlightPainter(new Color(result.get(i))));
                                i+=3;
                            }
                            if(this.postagger.getPoscolors().containsKey(Tags.DEFAULT.toString())){
                                this.caseToHighlights.get(Tags.DEFAULT.toString()).add((Highlighter.Highlight) highlighter.addHighlight(position - word.length(), endposition, new DefaultHighlighter.DefaultHighlightPainter(this.postagger.getPoscolors().get(Tags.DEFAULT.toString()))));
                            }
                        }else{
                            this.caseToHighlights.get(this.postagger.getColorToPos().get(color.getRGB())).add((Highlighter.Highlight) highlighter.addHighlight(position - word.length(), endposition/*+result.get(2)*/, new DefaultHighlighter.DefaultHighlightPainter(color)));
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
            System.out.println(revised);
            int position = 0, endposition = 0;
            for (String revi:revised) {
                String[] revisedwords = revi.split(" ");
                for (int w = 0; w < revisedwords.length; ) {
                    String word = revisedwords[w].trim();
                    System.out.println("Word: "+word);
                    position += word.length();
                    List<Integer> result=this.postagger.getPosTag(word,this.resultarea.getDictHandler());
                    System.out.println("GetPosTag: "+result.toString());
                    Color color=Color.white;
                    if(!result.isEmpty()){
                        color=new Color(result.get(0));
                    }else if(this.postagger.getPoscolors().containsKey(Tags.DEFAULT.toString())){
                        color=(this.postagger.getPoscolors().get(Tags.DEFAULT.toString()));
                    }
                    try {
                        endposition = resultarea.getText().indexOf(" ", position - word.length())+1;
                        String paintword = resultarea.getText().substring(position - word.length(), endposition);
                        System.out.println(paintword);
                        if(!result.isEmpty() && (word.length()!=(result.get(2)+2) || result.get(1)!=0)){
                            int i=0,endindex,startindex;
                            while(i+2<result.size()){
                                endindex=endposition-word.length()+result.get(i+2)-1;
                                startindex=position - word.length()+result.get(i+1)-1;
                                this.caseToHighlights.get(this.postagger.getColorToPos().get(new Color(result.get(i)).getRGB())).add((Highlighter.Highlight) highlighter.addHighlight(startindex,
                                        endindex, new DefaultHighlighter.DefaultHighlightPainter(new Color(result.get(i)))));
                                //highlighter.addHighlight(startindex, endindex, new DefaultHighlighter.DefaultHighlightPainter(new Color(result.get(i))));
                                i+=3;
                            }
                            if(this.postagger.getPoscolors().containsKey(Tags.DEFAULT.toString())){
                                this.caseToHighlights.get(Tags.DEFAULT.toString()).add((Highlighter.Highlight) highlighter.addHighlight(position - word.length()+1, endposition-1, new DefaultHighlighter.DefaultHighlightPainter(this.postagger.getPoscolors().get(Tags.DEFAULT.toString()))));
                            }
                        }else{
                            this.caseToHighlights.get(this.postagger.getColorToPos().get(color.getRGB())).add((Highlighter.Highlight) highlighter.addHighlight(position - word.length()+1, endposition-1/*+result.get(2)*/, new DefaultHighlighter.DefaultHighlightPainter(color)));
                        }
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                    position = endposition+1;
                    w++;
                }

                position++;
            }
            /*System.out.println("POSTagging Cuneiform...");
            System.out.println(revised);
            int position = 0, cuneiposition=0,endposition;
            for (String revi:revised) {
                String[] revisedwords = revi.split(" \\[");
                for (int w = 0; w < revisedwords.length; ) {
                    String word = revisedwords[w].trim();
                    System.out.println("Word: "+word);
                    position += word.length();
                    int length=charTypes.getChar_length();
                    if(word.contains("-")){
                        length=word.split("-").length*charTypes.getChar_length();
                    }
                    this.all+=length/charTypes.getChar_length();
                    cuneiposition+=length;
                    System.out.println("Length: "+length);
                    List<Integer> result=this.postagger.getPosTag(word,charTypes.getCorpusHandlerAPI().getUtilDictHandler());
                    System.out.println("GetPosTag: "+result.toString());
                    Color color=Color.white;
                    if(!result.isEmpty()){
                        color=new Color(result.get(0));
                    }else if(this.postagger.getPoscolors().containsKey(Tags.DEFAULT.toString())){
                        color=this.postagger.getPoscolors().get(Tags.DEFAULT.toString());
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
                                sylllength=word.substring(result.get(i+1),result.get(i+2)).split("-").length*charTypes.getChar_length();
                                start=word.substring(0,result.get(i+1)).split("-").length*charTypes.getChar_length()-charTypes.getChar_length();
                                System.out.println("Sylllength: "+sylllength);
                                System.out.println("Start: "+start);
                                System.out.println("Word: "+word+" Substring("+result.get(i+1)+","+result.get(i+2)+"): "+word.substring(result.get(i+1),result.get(i+2)));
                                System.out.println("Highlight("+((cuneiposition - length)+start)+","+((cuneiposition-length)+start+sylllength)+")");
                                highlighter.addHighlight((cuneiposition - length)+start,
                                        (cuneiposition-length)+start+sylllength, new DefaultHighlighter.DefaultHighlightPainter(new Color(result.get(i))));
                                i+=3;
                            }
                            this.matches+=word.substring(range[0],range[1]).split("-").length;
                            if(this.postagger.getPoscolors().containsKey(Tags.DEFAULT.toString())){
                                highlighter.addHighlight(cuneiposition - length, cuneiposition, new DefaultHighlighter.DefaultHighlightPainter(this.postagger.getPoscolors().get(Tags.DEFAULT.toString())));
                            }
                        }else{
                            highlighter.addHighlight(cuneiposition - length, cuneiposition, new DefaultHighlighter.DefaultHighlightPainter(color));
                            this.matches+=length/charTypes.getChar_length();
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
            }*/
        }

        Map<Integer,String> sentences=this.postagger.sentenceDetector(this.resultarea.getText().split("[\\s\\r\\n]+"),this.resultarea.getText().split(System.lineSeparator()));
        ((TextLineNumber)this.scrollPane.getRowHeader().getView()).setColorswitches(sentences);
        ((TextLineNumber)this.scrollPane.getRowHeader().getView()).documentChanged();
        //this.scrollPane.setRowHeaderView(new TextLineNumber(resultarea,sentences));



        this.statistics.setText(" Postagged/All: "+this.matches/this.all);
        System.out.println("PostaggedSylls: "+this.matches+" All Sylls: "+this.all+" Postagged/All: "+this.matches/this.all);

    }

    /**
     * Paints the words in given colors.
     * @param charType the language to use
     */
    public void paintResultArea(final CharTypes charType) {
        if(!this.highlighted){
            GUIWorker sw = new GUIWorker() {
                @Override
                protected Object doInBackground() throws Exception {

                    if (POSTagMain.this.cuneiformCheckbox.isSelected()) {
                        POSTagMain.this.paintPOSTags(POSTagMain.this.resultarea, POSTagMain.this.resultarea.getText(), false);
                    } else {
                        POSTagMain.this.paintPOSTags(POSTagMain.this.resultarea, POSTagMain.this.resultarea.getText(), true);
                    }
                    POSTagMain.this.highlighted = true;
                    return null;
                }
            };
            sw.execute();

        }
    }
}
