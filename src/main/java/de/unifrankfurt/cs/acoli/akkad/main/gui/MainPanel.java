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

package de.unifrankfurt.cs.acoli.akkad.main.gui;

import de.unifrankfurt.cs.acoli.akkad.eval.EvalResult;
import de.unifrankfurt.cs.acoli.akkad.eval.EvalStatistics;
import de.unifrankfurt.cs.acoli.akkad.main.Main;
import de.unifrankfurt.cs.acoli.akkad.main.gui.chart.ChartGUI;
import de.unifrankfurt.cs.acoli.akkad.main.gui.util.UTF8Bundle;
import de.unifrankfurt.cs.acoli.akkad.methods.segmentation.stat.WekaException;
import de.unifrankfurt.cs.acoli.akkad.util.Config;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.*;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Files;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * The main panel integrated into MainGUI for accessing functions.
 */
public class MainPanel extends JPanel {
    /**The resource bundle to use.*/
    public ResourceBundle bundle=ResourceBundle.getBundle(Config.RESBUNDLENAME,Locale.getDefault(),new UTF8Bundle("UTF-8"));
    /**The main class for accessing functions.*/
    private Main main;
    /**The resultString for accessing evalresults.*/
    private String resultStr;

    /**
     * Constructor for this class.
     * @param main the main class for accessing functions
     */
    public MainPanel(final Main main){
        this.main=main;
        this.setLayout(new GridBagLayout());
        final JPanel classMethodPanel = new JPanel();
        JLabel comboLbl = new JLabel(bundle.getString("algorithm")+":");
        final JComboBox<ClassificationMethod> methodchooser = new JComboBox<ClassificationMethod>(ClassificationMethod.values());
        final JPanel featureSetPanel = new JPanel();
        JLabel featureset = new JLabel(bundle.getString("featureset")+":");
        final JComboBox<FeatureSets> featuresetchooser = new JComboBox<FeatureSets>(FeatureSets.values());
        final JPanel evaluationPanel = new JPanel();
        JLabel evaluation = new JLabel(bundle.getString("evaluation")+":");
        final JComboBox<EvaluationMethod> evaluationchooser = new JComboBox<EvaluationMethod>(EvaluationMethod.values());
        final JPanel charTypePanel = new JPanel();
        JLabel chartype = new JLabel(bundle.getString("chartype")+":");
        final JComboBox<CharTypes> chartypechooser = new JComboBox<CharTypes>(CharTypes.values());
        JLabel transliteration1 = new JLabel(bundle.getString("transliteration")+":");
        final JComboBox<TransliterationMethod> transliterationchooser = new JComboBox<TransliterationMethod>(TransliterationMethod.values());
        final JPanel transliterationPanel = new JPanel();
        JLabel options = new JLabel(bundle.getString("corpus")+":");
        final JComboBox<String> optionchooser = new JComboBox<String>(new File("source/").list());
        optionchooser.setSelectedItem("corpus.atf");
        final JPanel optionPanel = new JPanel();
        JLabel translation = new JLabel(bundle.getString("translation")+":");
        final JComboBox<TranslationMethod> translationchooser = new JComboBox<TranslationMethod>(TranslationMethod.values());
        final JPanel translationPanel = new JPanel();
        JLabel languagelabel = new JLabel(bundle.getString("language")+":");
        final JComboBox<CharTypes> languagechooser = new JComboBox<CharTypes>(CharTypes.values());
        languagechooser.setSelectedItem(CharTypes.ENGLISH);
        final JPanel languagePanel = new JPanel();
        final JPanel testMethodPanel = new JPanel();
        JLabel testMethodLabel = new JLabel(bundle.getString("testmethod")+":");
        final JComboBox<TestMethod> testmethodchooser = new JComboBox<TestMethod>(TestMethod.values());
        testmethodchooser.setSelectedItem(TestMethod.FOREIGNTEXT);

        classMethodPanel.add(comboLbl);
        classMethodPanel.add(methodchooser);
        featureSetPanel.add(featureset);
        featureSetPanel.add(featuresetchooser);
        evaluationPanel.add(evaluation);
        evaluationPanel.add(evaluationchooser);
        charTypePanel.add(chartype);
        charTypePanel.add(chartypechooser);
        transliterationPanel.add(transliteration1);
        transliterationPanel.add(transliterationchooser);
        optionPanel.add(options);
        optionPanel.add(optionchooser);
        translationPanel.add(translation);
        translationPanel.add(translationchooser);
        languagePanel.add(languagelabel);
        languagePanel.add(languagechooser);
        testMethodPanel.add(testMethodLabel);
        testMethodPanel.add(testmethodchooser);
        SpinnerModel model = new SpinnerNumberModel(0.0, 0, 100, 1);
        final JSpinner amountspinner = new JSpinner(model);
        JLabel amountlabel=new JLabel(bundle.getString("percentage"));
        SpinnerModel model2 = new SpinnerNumberModel(0.0, 0, 1000, 1);
        final JSpinner startspinner = new JSpinner(model2);
        JLabel startlabel=new JLabel(bundle.getString("startvalue"));

        final JFileChooser trainfilechooser=new JFileChooser();
        trainfilechooser.setCurrentDirectory(new File("reformatted/cuneiform/foreigntext"));
        JLabel trainingfilelabel=new JLabel(bundle.getString("trainingfile"));
        final JTextField trainingfilefield=new JTextField(25);
        trainingfilefield.setEnabled(false);
        final JButton trainfilebutton=new JButton(bundle.getString("choose"));
        trainfilebutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                //Handle open trainfilebutton action.
                if (actionEvent.getSource() == trainfilebutton) {
                    int returnVal = trainfilechooser.showOpenDialog(MainPanel.this);

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = trainfilechooser.getSelectedFile();
                        trainingfilefield.setText(file.getAbsolutePath());
                        //This is where a real application would open the file.
                        System.out.println("Opening: " + file.getName() + ".");
                    } else {
                        System.out.println("Open command cancelled by user.");
                    }
                }
            }
        });
        final JPanel testfilepanel = new JPanel();
        final JPanel modelpanel=new JPanel();
        final JPanel trainfilepanel = new JPanel();
        final JPanel amountpanel=new JPanel();
        amountpanel.add(amountspinner);
        amountpanel.add(amountlabel);
        amountpanel.add(startspinner);
        amountpanel.add(startlabel);
        final JTextField testfilefield=new JTextField(25);
        testfilefield.setEnabled(false);
        final JFileChooser testfilechooser=new JFileChooser();
        testfilechooser.setCurrentDirectory(new File("reformatted/cuneiform/foreigntext"));
        final JButton testfilebutton=new JButton(bundle.getString("choose"));
        testfilebutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                //Handle open trainfilebutton action.
                if (actionEvent.getSource() == testfilebutton) {
                    int returnVal = testfilechooser.showOpenDialog(MainPanel.this);

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = testfilechooser.getSelectedFile();
                        testfilefield.setText(file.getAbsolutePath());
                        //This is where a real application would open the file.
                        System.out.println("Opening: " + file.getName() + ".");
                    } else {
                        System.out.println("Open command cancelled by user.");
                    }
                }
            }
        });
        final JTextField modelfilefield=new JTextField(25);
        final JFileChooser modelfilechooser=new JFileChooser();
        final JButton modelfilebutton=new JButton(bundle.getString("choose"));
        modelfilebutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                //Handle open trainfilebutton action.
                if (actionEvent.getSource() == modelfilebutton) {
                    int returnVal = modelfilechooser.showOpenDialog(MainPanel.this);

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = modelfilechooser.getSelectedFile();
                        modelfilefield.setText(file.getAbsolutePath());
                        //This is where a real application would open the file.
                        System.out.println("Opening: " + file.getName() + ".");
                    } else {
                        System.out.println("Open command cancelled by user.");
                    }
                }
            }
        });
        JLabel modelfilelabel=new JLabel(bundle.getString("modelfile"));
        JLabel testfilelabel=new JLabel(bundle.getString("testingfile"));
        final JCheckBox evalcheckbox=new JCheckBox();
        JLabel evalLabel=new JLabel(bundle.getString("onlyeval"));
        final JCheckBox arffbox=new JCheckBox();
        JLabel arffLabel=new JLabel("Take Arff directly");
        trainfilepanel.add(trainingfilelabel);
        trainfilepanel.add(trainingfilefield);
        trainingfilefield.setText("reformatted/cuneiform/foreigntext/corpus.atf");
        testfilefield.setText("reformatted/cuneiform/foreigntext/first20.txt");
        trainfilepanel.add(trainfilebutton);
        testfilepanel.add(testfilelabel);
        testfilepanel.add(testfilefield);
        testfilepanel.add(testfilebutton);
        modelpanel.add(modelfilelabel);
        modelpanel.add(modelfilefield);
        modelpanel.add(modelfilebutton);
        modelpanel.add(evalcheckbox);
        modelpanel.add(evalLabel);
        modelpanel.add(arffbox);
        modelpanel.add(arffLabel);
        JButton startclassification = new JButton(bundle.getString("start"));
        JButton methodcomparison=new JButton(bundle.getString("methodcomparison"));

        //The ActionListener class is used to handle the
        //event that happens when the user clicks the trainfilebutton.
        //As there is not a lot that needs to happen we can
        //define an anonymous inner class to make the code simpler.
        startclassification.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent event) {
                if (testfilefield.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(MainPanel.this, bundle.getString("notestfile"));
                } else {
                    Thread thread=new Thread() {
                        public void run() {
                            try {
                                MainGUI.progressBar = new ProgressMonitor(MainPanel.this,
                                        bundle.getString("nowclassifying")+" "+testfilefield.getText(),
                                        "", 0, 100);
                                MainGUI.progressBar.setProgress(0);
                                MainGUI.progressBar.setNote(bundle.getString("nowclassifying")+" "+testfilefield.getText());
                                ClassificationMethod currentmethod=(ClassificationMethod)methodchooser.getSelectedItem();
                                                /*String resultfile=/*currentmethod.getHasFeatureSet()?testfilefield.getText().substring(testfilefield.getText().lastIndexOf('/')+1,
                                                        testfilefield.getText().lastIndexOf('.'))
                                                        +"_"+currentmethod.toString().toLowerCase()
                                                        +"_"+featuresetchooser.getSelectedItem().toString().toLowerCase()
                                                        +"_"+((TransliterationMethod)transliterationchooser.getSelectedItem()).getShortlabel().toLowerCase()
                                                        +Files.RESULT.toString():
                                                        testfilefield.getText().substring(testfilefield.getText().lastIndexOf('/')+1,
                                                        testfilefield.getText().lastIndexOf('.'))
                                                                +"_"+currentmethod.toString().toLowerCase()
                                                                +"_"+((TransliterationMethod)transliterationchooser.getSelectedItem()).getShortlabel().toLowerCase()
                                                                +Files.RESULT.toString();*/
                                MainPanel.this.resultStr = main.startClassification(
                                        trainingfilefield.getText(),
                                        testfilefield.getText(),modelfilefield.getText(), currentmethod,
                                        (FeatureSets) featuresetchooser.getSelectedItem(),
                                        (EvaluationMethod) evaluationchooser.getSelectedItem(),
                                        (TransliterationMethod)transliterationchooser.getSelectedItem(),
                                        (TranslationMethod) translationchooser.getSelectedItem(),
                                        (CharTypes)languagechooser.getSelectedItem(),
                                        (CharTypes)chartypechooser.getSelectedItem(),
                                        (TestMethod)testmethodchooser.getSelectedItem(),
                                        optionchooser.getSelectedItem().toString(),false,(Double) amountspinner.getValue(),
                                        (Double)startspinner.getValue(),evalcheckbox.isSelected(),arffbox.isSelected());
                                String resultfile=MainPanel.this.resultStr.substring(MainPanel.this.resultStr.lastIndexOf("/")+1);
                                String testfile=main.getTestFile();
                                Set<EvalResult> evalResultList=new TreeSet<EvalResult>();
                                if(testmethodchooser.getSelectedItem()==TestMethod.CROSSVALIDATION){
                                    for(EvalResult result: EvalStatistics.getInstance().calculateCrossFoldMean(optionchooser.getSelectedItem().toString(),
                                            (ClassificationMethod)currentmethod).values()){
                                        evalResultList.add(result);
                                    }
                                }else{
                                    for(EvalResult eval:EvalStatistics.getInstance().getEvalresults().get(Files.RESULTDIR.toString() +Files.TRANSLITDIR.toString() + resultfile).get(currentmethod)){
                                        evalResultList.add(eval);
                                    }
                                }

                                System.out.println("EvalStatistics: "+EvalStatistics.getInstance().getEvalresults());
                                System.out.println("Resultfile: "+resultfile);
                                List<MethodEnum> selected=new LinkedList<MethodEnum>();
                                selected.add((MethodEnum)methodchooser.getSelectedItem());
                                Boolean fileOrStr;
                                switch ((TestMethod)testmethodchooser.getSelectedItem()){
                                    case CROSSVALIDATION:
                                    case PERCENTAGE:
                                    case RANDOMSAMPLE:
                                        System.out.println("TestSetFile: "+main.getTestFile());
                                        testfile=main.getTestFile();
                                        break;
                                    case FOREIGNTEXT:
                                    default:
                                        testfile=testmethodchooser.getSelectedItem().toString().toLowerCase()+File.separator+testfilefield.getText().substring(testfilefield.getText().lastIndexOf('/') + 1);

                                }
                                ChartGUI chart=new ChartGUI(bundle.getString("result") +
                                        " [" + testfile + "]" +
                                        " (" + currentmethod + "," + evaluationchooser.getSelectedItem() + ") " +
                                        "["+transliterationchooser.getSelectedItem().toString()+"]",
                                        " [" + testfile + "]" +
                                                " ("+(currentmethod).toString()+","+transliterationchooser.getSelectedItem().toString()+")",
                                        evalResultList,testfile
                                        ,resultfile,selected, Locale.ENGLISH.toString(),false,main.dictHandler,(TestMethod)testmethodchooser.getSelectedItem(),false

                                );
                                MainGUI.progressBar.setProgress(100);
                                chart.setTextAreaContents(resultStr);
                                MainGUI.progressBar.close();

                            } catch (ArithmeticException|ParserConfigurationException | XMLStreamException | SAXException | IOException | WekaException e ) {
                                e.printStackTrace();
                                MainGUI.progressBar.close();
                                JOptionPane.showMessageDialog(MainPanel.this,
                                        e.getMessage(),bundle.getString("error"),JOptionPane.ERROR_MESSAGE);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                        }

                    };
                    thread.start();
                }
            }
        });
        methodcomparison.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {

                if (testfilefield.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(MainPanel.this, bundle.getString("notestfile"));
                } else {
                    Thread thread=new Thread() {
                        public void run() {
                            try {
                                MainGUI.progressBar = new ProgressMonitor(MainPanel.this,
                                        bundle.getString("nowclassifying")+" "+testfilefield.getText(),
                                        "", 0, 100);
                                MainGUI.progressBar.setProgress(0);
                                MainGUI.progressBar.setNote(bundle.getString("nowclassifying")+" "+testfilefield.getText());
                                JList<MethodEnum> list=new JList<MethodEnum>(ClassificationMethod.values());
                                list.setSelectionMode(
                                        ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                                JOptionPane.showMessageDialog(null, new JScrollPane(list));
                                list.getSelectedValuesList();
                                java.util.List<String> resultfilelist=new LinkedList<String>();
                                Boolean first=true;
                                for(MethodEnum currentmethod:list.getSelectedValuesList()){
                                    String resultfile=((ClassificationMethod)currentmethod).getHasFeatureSet()?testfilefield.getText().substring(testfilefield.getText().lastIndexOf('/')+1,
                                            testfilefield.getText().lastIndexOf('.'))
                                            +"_"+currentmethod.toString().toLowerCase()
                                            +"_"+featuresetchooser.getSelectedItem().toString().toLowerCase()
                                            +"_"+((TransliterationMethod)transliterationchooser.getSelectedItem()).getShortlabel().toLowerCase()
                                            +Files.RESULT.toString():
                                            testfilefield.getText().substring(testfilefield.getText().lastIndexOf('/')+1,
                                                    testfilefield.getText().lastIndexOf('.'))
                                                    +"_"+currentmethod.toString().toLowerCase()
                                                    +"_"+((TransliterationMethod)transliterationchooser.getSelectedItem()).getShortlabel().toLowerCase()
                                                    +Files.RESULT.toString();
                                    resultfilelist.add(resultfile);

                                    MainPanel.this.resultStr = main.startClassification(trainingfilefield.getText(), testfilefield.getText(),modelfilefield.getText(), (ClassificationMethod)currentmethod,
                                            (FeatureSets) featuresetchooser.getSelectedItem(), (EvaluationMethod) evaluationchooser.getSelectedItem(),
                                            (TransliterationMethod)transliterationchooser.getSelectedItem(),(TranslationMethod) translationchooser.getSelectedItem(),
                                            (CharTypes)languagechooser.getSelectedItem(),(CharTypes)chartypechooser.getSelectedItem(),(TestMethod)testmethodchooser.getSelectedItem(),
                                            optionchooser.getSelectedItem().toString(),!first,(Double) amountspinner.getValue(),(Double)startspinner.getValue(),evalcheckbox.isSelected(),arffbox.isSelected());

                                    System.out.println("EvalStatistics: "+EvalStatistics.getInstance().getEvalresults().keySet());
                                    System.out.println("Resultfile: "+resultfile);
                                    first=false;

                                }
                                java.util.List<EvalResult> evalResultList=new LinkedList<EvalResult>();
                                if(testmethodchooser.getSelectedItem().equals(TestMethod.CROSSVALIDATION)){
                                    //EvalStatistics.getInstance().calculateCrossFoldMean(optionchooser.getSelectedItem().toString(),(ClassificationMethod)currentmethod)
                                }else{
                                    int i=0;

                                    for(MethodEnum currentmethod:list.getSelectedValuesList()) {
                                        for(EvalResult eval:EvalStatistics.getInstance().getEvalresults().get(Files.RESULTDIR.toString() +
                                                Files.TRANSLITDIR.toString() + resultfilelist.get(i)).get(currentmethod)){
                                            evalResultList.add(eval);
                                        }
                                        i++;
                                    }
                                }

                                ChartGUI chart = new ChartGUI(bundle.getString("result") +
                                        " [" + testfilefield.getText().substring(testfilefield.getText().lastIndexOf('/') + 1) + "]" +
                                        " (" + list.getSelectedValuesList() + "," + evaluationchooser.getSelectedItem() + ") " +
                                        "[" + transliterationchooser.getSelectedItem().toString() + "]",
                                        " [" + testfilefield.getText().substring(testfilefield.getText().lastIndexOf('/') + 1) + "]" +
                                                " (" + list.getSelectedValuesList().toString() + "," + transliterationchooser.getSelectedItem().toString() + ")",
                                        new TreeSet<EvalResult>(evalResultList),
                                        testfilefield.getText(), resultfilelist.get(0),list.getSelectedValuesList(), Locale.ENGLISH.toString(),true,main.dictHandler,(TestMethod)testmethodchooser.getSelectedItem(),false
                                );
                                MainGUI.progressBar.setProgress(100);
                                chart.setTextAreaContents(resultStr);
                                MainGUI.progressBar.close();
                            } catch (ParserConfigurationException | XMLStreamException | SAXException | IOException | WekaException e ) {
                                e.printStackTrace();
                                MainGUI.progressBar.close();
                                JOptionPane.showMessageDialog(MainPanel.this,
                                        e.getMessage(),bundle.getString("error"),JOptionPane.ERROR_MESSAGE);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                        }

                    };
                    thread.start();
                }
            }});

        //The JFrame uses the BorderLayout layout manager.
        //Put the two JPanels and JButton in different areas.
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        this.add(classMethodPanel,c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 0;
        this.add(featureSetPanel,c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 1;
        this.add(transliterationPanel,c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 1;
        this.add(evaluationPanel,c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 2;
        this.add(translationPanel,c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 2;
        this.add(languagePanel,c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 3;
        this.add(charTypePanel,c);
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 4;
        this.add(amountpanel,c);
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 3;
        this.add(optionPanel,c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 4;
        c.gridwidth=2;
        this.add(testMethodPanel,c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 5;
        c.gridheight=2;
        c.gridwidth=1;
        this.add(trainfilepanel,c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 5;
        c.gridheight=2;
        this.add(testfilepanel,c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 7;
        c.gridwidth=2;
        c.gridheight=2;
        this.add(modelpanel,c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridwidth=1;
        c.gridx = 0;
        c.gridy = 9;
        this.add(startclassification,c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 9;
        this.add(methodcomparison,c);
        //make sure the JFrame is visible
        this.setVisible(true);
    }
}
