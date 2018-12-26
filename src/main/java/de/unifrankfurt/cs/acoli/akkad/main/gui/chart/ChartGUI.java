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

package de.unifrankfurt.cs.acoli.akkad.main.gui.chart;

import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.dict.importhandler.ImportHandler;
import de.unifrankfurt.cs.acoli.akkad.eval.EvalResult;
import de.unifrankfurt.cs.acoli.akkad.main.gui.comp.POSTagGUI;
import de.unifrankfurt.cs.acoli.akkad.main.gui.comp.SegmentationGUI;
import de.unifrankfurt.cs.acoli.akkad.main.gui.comp.TranscriptionGUI;
import de.unifrankfurt.cs.acoli.akkad.main.gui.comp.TranslationGUI;
import de.unifrankfurt.cs.acoli.akkad.main.gui.util.GUIFormat;
import de.unifrankfurt.cs.acoli.akkad.main.gui.util.GUIWorker;
import de.unifrankfurt.cs.acoli.akkad.main.gui.util.TableModel;
import de.unifrankfurt.cs.acoli.akkad.main.gui.util.TableRenderer;
import de.unifrankfurt.cs.acoli.akkad.util.ArffHandler;
import de.unifrankfurt.cs.acoli.akkad.util.Tuple;
import de.unifrankfurt.cs.acoli.akkad.util.enums.EvalResultType;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.EvaluationMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.MethodEnum;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.TestMethod;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Creates the Statistics GUI.
 */
public class ChartGUI extends GUIFormat {

    private static final long serialVersionUID = 1L;
    /**The chart panel for viewing statistics.*/
    private ChartPanel chartPanel;
    /**Methods to be evaluated.*/
    private List<MethodEnum> methods;
    /**The textarea for displaying the log.*/
    private JTextArea resultarea;

    /**
     * Constructor for this class.
     * @param applicationTitle
     * @param chartTitle
     * @param results
     * @param resultfile
     * @param sourcefile
     * @param selectedMethods
     * @param locale
     * @param comparison
     * @param dicthandler
     * @param testMethod
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public ChartGUI(final String applicationTitle, final String chartTitle, final Set<EvalResult> results, final String resultfile,
                    final String sourcefile, final List<MethodEnum> selectedMethods, final String locale, final Boolean comparison,
                    final DictHandling dicthandler,final TestMethod testMethod,final Boolean simple) throws ExecutionException, InterruptedException {
        System.out.println("Resultfile: "+resultfile+" Sourcefile: "+sourcefile);
        this.buildMenu();

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setTitle(applicationTitle);
        final XYDataset dataset;
        final JPanel chartandchoosepanel=new JPanel();
        chartandchoosepanel.setLayout(new GridBagLayout());
        final JPanel featureSetPanel=new JPanel();
        if(comparison){
            List<EvaluationMethod> evalmethods=new LinkedList<>();
            for(EvalResult evalres:results){
                evalmethods.add(evalres.method);
            }
            // This will create the dataset
            dataset = createComparisonDataSet(results,selectedMethods,evalmethods.get(0));
            JLabel featureset = new JLabel(bundle.getString("evaluation")+":");
            Set<EvaluationMethod> evalset=new TreeSet<>(evalmethods);

            final JComboBox<EvaluationMethod> featuresetchooser = new JComboBox<EvaluationMethod>(evalset.toArray(new EvaluationMethod[evalset.size()]));
            featuresetchooser.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent actionEvent) {
                    ChartGUI.this.chartPanel.setChart(ChartGUI.this.createChart(
                            ChartGUI.this.createComparisonDataSet(results, selectedMethods,(EvaluationMethod)featuresetchooser.getSelectedItem()),chartTitle,comparison));
                    ChartGUI.this.chartPanel.repaint();
                }
            });
            featureSetPanel.add(featureset);
            featureSetPanel.add(featuresetchooser);
        }else{
            // This will create the dataset
            dataset = createDataset(results);
        }

        //chartandchoosepanel.setLayout(new GridBagLayout());
        // based on the dataset we create the chart
       /* JFreeChart chart = createChart(dataset, chartTitle,comparison);
        //GridBagConstraints c = new GridBagConstraints();
        // we put the chart into a panel
        this.chartPanel = new ChartPanel(chart);
        if(comparison){
            chartandchoosepanel.add(featureSetPanel,BorderLayout.NORTH);
        }
        chartandchoosepanel.add(chartPanel,BorderLayout.SOUTH);
        chartandchoosepanel.setPreferredSize(new java.awt.Dimension(700, 550));
        // default size
        chartPanel.setPreferredSize(new java.awt.Dimension(700, 500));*/


        this.resultarea=new JTextArea(20,60);
        JPanel mainPanel=new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        String[] names=new String[]{"Method","F-Measure",
                "Accuracy",
                "Precision",
                "Recall"};

        int i=0;
        for(EvaluationMethod method:EvaluationMethod.values()){
            for(EvalResult evalResult:results){
                if(evalResult.method!=method || method.getEvalResultType()!= EvalResultType.FSCORE){
                    continue;
                }
                i++;
            }
        }
        Object[][] fscorearray = new Object[i][names.length];
        i=0;
        for(EvaluationMethod method:EvaluationMethod.values()){
            for(EvalResult evalResult:results){
                if(evalResult.method!=method || method.getEvalResultType()!= EvalResultType.FSCORE){
                    continue;
                }
                fscorearray[i][0]=evalResult.classmethod.getShortname()+" ("+evalResult.method.getShortname()+")";
                fscorearray[i][1]=ImportHandler.formatDouble(evalResult.getFScore());
                fscorearray[i][2]=ImportHandler.formatDouble(evalResult.getAccuracy());
                fscorearray[i][3]=ImportHandler.formatDouble(evalResult.getPrecision());
                fscorearray[i++][4]=ImportHandler.formatDouble(evalResult.getRecall());
                //fscorearray[i][5]=ImportHandler.formatDouble(evalResult.getMCC());
                //fscorearray[i][6]=ImportHandler.formatDouble(evalResult.getChiSquared());
                //fscorearray[i++][7]=ImportHandler.formatDouble(evalResult.getFisher());
            }
        }
        Map<String,Tuple<Integer,Integer>> tempmap=new HashMap<>();
        List<String> onescorenames=new LinkedList<String>();
        onescorenames.add("Method");
        i=0;
        int j=0;
        String currentmethod="";
        for(EvaluationMethod method:EvaluationMethod.values()){
            for(EvalResult evalResult:results){
                if(evalResult.method!=method || method.getEvalResultType()!= EvalResultType.ONESCORE){
                    continue;
                }
                if(!tempmap.containsKey(evalResult.classmethod.getShortname())){
                    j++;
                    tempmap.put(evalResult.classmethod.getShortname(),new Tuple<>(j,i));
                }
                onescorenames.add(evalResult.method.getShortname());
                i++;
            }
        }
        Object[][] oneScoreArray = new Object[j][i+1];
        i=0;
        currentmethod="";
        j=-1;
        int current=0;
        tempmap.clear();
        for(EvaluationMethod method:EvaluationMethod.values()){
            for(EvalResult evalResult:results){

                if(evalResult.method!=method || method.getEvalResultType()!= EvalResultType.ONESCORE){
                    continue;
                }
                if(currentmethod.equals("") || !tempmap.containsKey(evalResult.classmethod.getShortname())){
                    j++;
                    i=0;
                    tempmap.put(evalResult.classmethod.getShortname(),new Tuple<>(j,i));
                    current=j;
                    oneScoreArray[current][i++]=evalResult.classmethod.getShortname();
                    currentmethod=evalResult.classmethod.getShortname();
                }else if(tempmap.containsKey(evalResult.classmethod.getShortname())){
                    current=tempmap.get(evalResult.classmethod.getShortname()).getOne();
                    i=tempmap.get(evalResult.classmethod.getShortname()).getTwo();
                }
                if(i<oneScoreArray[current].length){
                    oneScoreArray[current][i++]=ImportHandler.formatDouble(evalResult.getResult());
                    tempmap.get(evalResult.classmethod.getShortname()).setTwo(i);
                }
            }
        }
        i=0;
        String[] twoscorenames=new String[]{"Method","Matches","Misses"};
        for(EvaluationMethod method:EvaluationMethod.values()){
            for(EvalResult evalResult:results){
                if(evalResult.method!=method || method.getEvalResultType()!= EvalResultType.TWOSCORE){
                    continue;
                }
                i++;
            }
        }
        Object[][] twoScoreArray = new Object[i][twoscorenames.length];

        i=0;
        for(EvaluationMethod method:EvaluationMethod.values()){
            for(EvalResult evalResult:results){
                if(evalResult.method!=method || method.getEvalResultType()!= EvalResultType.TWOSCORE){
                    continue;
                }
                twoScoreArray[i][0]=evalResult.classmethod.getShortname()+" ("+evalResult.method.getShortname()+")";
                twoScoreArray[i][1]=ImportHandler.formatDouble(evalResult.getCountmatches());
                twoScoreArray[i++][2]=ImportHandler.formatDouble(evalResult.getCountmisses());
            }
        }

        JTable fscoretable=new JTable(new TableModel(names,fscorearray));
        System.out.println("Fscoretable");
        fscoretable.setDefaultRenderer(String.class, new TableRenderer());
        setTableHeight(fscoretable,fscorearray.length);
        JScrollPane fscoretablepane=new JScrollPane(fscoretable);
        JTable onescoretable=new JTable(new TableModel(onescorenames.toArray(new String[onescorenames.size()]),oneScoreArray));
        System.out.println("Onescoretable");
        onescoretable.setDefaultRenderer(String.class, new TableRenderer());
        setTableHeight(onescoretable,onescorenames.size());
        JScrollPane onescoretablepane=new JScrollPane(onescoretable);
        JTable twoscoretable=new JTable(new TableModel(twoscorenames,twoScoreArray));
        System.out.println("Twoscoretable");
        twoscoretable.setDefaultRenderer(String.class, new TableRenderer());
        setTableHeight(twoscoretable,twoScoreArray.length);
        JScrollPane twoscoretablepane=new JScrollPane(twoscoretable);
        final GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        mainPanel.add(chartandchoosepanel,c);
        JPanel optionPanel=new JPanel();
        List<String> values=new LinkedList<>();
        values.add("Total");
        values.add("Precision");
        values.add("Recall");
        values.add("F-Score");
        values.add("Accuracy");
        values.add("Misses (FP+FN)");
        values.add("G-Score");
        values.add("MCC");
        int k=0;
        for(String res:values){
            JLabel label=new JLabel(res);
            final JCheckBox checkBox=new JCheckBox();
            checkBox.setEnabled(true);
            checkBox.setSelected(true);
            final int id=k;
            checkBox.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(final ChangeEvent changeEvent) {
                    chartPanel.getChart().getXYPlot().getRenderer().setSeriesVisible(id,checkBox.isSelected());
                }
            });
            optionPanel.add(checkBox);
            optionPanel.add(label);
            k++;
        }
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 1;
        mainPanel.add(optionPanel,c);
        JScrollPane scrollPane = new JScrollPane(resultarea);
        /*c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth=1;
        c.gridheight=2;
        mainPanel.add(scrollPane,c);*/
        //mainPanel.setPreferredSize(new java.awt.Dimension(700, 800));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth=1;
        mainPanel.add(fscoretablepane,c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth=1;
        mainPanel.add(onescoretablepane,c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth=1;
        mainPanel.add(twoscoretablepane,c);
        final JScrollPane framescroller=new JScrollPane(mainPanel);
        // add it to our application
        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(1110,1000));
        getContentPane().add(tabbedPane);
        System.out.println("Tabbed Pane yeah!");
        GUIWorker sw5=new GUIWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                System.out.println("Postagview");
                tabbedPane.addTab(bundle.getString("postagview"),new POSTagGUI(sourcefile,resultfile,selectedMethods,dicthandler,null,testMethod));
                System.out.println("Postagview finished");
                return null;
            }
        };
        GUIWorker sw4=new GUIWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                JFreeChart chart = createChart(dataset, chartTitle,comparison);
                //GridBagConstraints c = new GridBagConstraints();
                // we put the chart into a panel
                ChartGUI.this.chartPanel = new ChartPanel(chart);
                int y=0;
                if(comparison){
                    c.fill = GridBagConstraints.HORIZONTAL;
                    c.weightx = 0.5;
                    c.gridx = 0;
                    c.gridy = y++;
                    chartandchoosepanel.add(featureSetPanel,c);
                }
                c.fill = GridBagConstraints.HORIZONTAL;
                c.weightx = 0.5;
                c.gridx = 0;
                c.gridy = y;
                chartandchoosepanel.add(chartPanel,c);
                chartandchoosepanel.setPreferredSize(new java.awt.Dimension(700, 550));
                // default size
                chartPanel.setPreferredSize(new java.awt.Dimension(700, 500));
                tabbedPane.addTab(bundle.getString("charts"), framescroller);
                return chartPanel;
            }
        };
        GUIWorker sw = new GUIWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                for (EvalResult res : results) {
                    if (res.method == EvaluationMethod.TRANSLITEVALUATION) {
                        tabbedPane.addTab(bundle.getString("compareview"), new SegmentationGUI(applicationTitle, sourcefile, resultfile, selectedMethods, res, dicthandler, testMethod));
                        return null;
                    }
                }

                return null;
            }
        };
        GUIWorker sw2 = new GUIWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                tabbedPane.addTab(bundle.getString("translationview"), new TranslationGUI(applicationTitle, sourcefile, resultfile, locale, selectedMethods, dicthandler, null));
                return null;
            }
        };
        GUIWorker sw3 = new GUIWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                for (EvalResult res : results) {
                    if (res.method == EvaluationMethod.SEGMENTATIONEVALUATION) {
                        tabbedPane.addTab(bundle.getString("transliterationview"), new TranscriptionGUI(applicationTitle, sourcefile, resultfile, locale, selectedMethods, res));
                        return null;
                    }
                }

                return null;
            }
        };
        sw4.execute();
        sw.execute();
        if(!simple){
            sw2.execute();
            sw3.execute();
            sw5.execute();
        }
        //tabbedPane.setMnemonicAt(0, KeyEvent.VK_2);

        //tabbedPane.setMnemonicAt(1, KeyEvent.VK_3);

        //tabbedPane.setMnemonicAt(1, KeyEvent.VK_4);
        this.setPreferredSize(new Dimension(1100,1000));
        this.pack();
        this.setVisible(true);

    }

    public static void setTableHeight(JTable table, int rows)
    {
        int width = table.getPreferredSize().width;
        int height = rows * table.getRowHeight();
        table.setPreferredScrollableViewportSize(new Dimension(width, height));
    }

    /**
     * Creates a chart
     */

    private JFreeChart createChart(XYDataset dataset, String title,Boolean comparison) {

        // create the chart...
        final JFreeChart chart = ChartFactory.createXYLineChart(
                title,      // chart title
                "X",                      // x axis label
                "Y",                      // y axis label
                dataset,                  // data
                PlotOrientation.VERTICAL,
                true,                     // include legend
                true,                     // tooltips
                false                     // urls
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint(Color.white);

//        final StandardLegend legend = (StandardLegend) chart.getLegend();
        //      legend.setDisplaySeriesShapes(true);

        // get a reference to the plot for further customisation...
        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
        //    plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShapesVisible(1, false);
        plot.setRenderer(renderer);
        String[] grade=new String[this.methods.size()];
        int i=0;
        for(MethodEnum evalmethod:this.methods){
            grade[i++]=(evalmethod).getShortname();
        }
        ArffHandler.arrayToStr(grade);
        SymbolAxis rangeAxis;
        if(comparison){
            rangeAxis= new SymbolAxis("ClassificationMethod", grade);
        }else{
            rangeAxis= new SymbolAxis("EvaluationMethod", grade);
        }


        rangeAxis.setTickUnit(new NumberTickUnit(1));
        rangeAxis.setRange(0, grade.length);
        plot.setDomainAxis(rangeAxis);
        return chart;

    }

    public XYDataset createComparisonDataSet(Set<EvalResult> results, final List<MethodEnum> selectedMethods,final EvaluationMethod filter){
        this.methods=selectedMethods;
        final XYSeries accuracy = new XYSeries("Accuracy");
        final XYSeries precision = new XYSeries("Precision");
        final XYSeries recall = new XYSeries("Recall");
        final XYSeries fscore = new XYSeries("F-Score");
        final XYSeries gscore = new XYSeries("G-Score");
        final XYSeries total=new XYSeries("Total");
        final XYSeries mcc=new XYSeries("MCC");
        final XYSeries falsenegative=new XYSeries("Misses (FP+FN)");

        double i=0.;
        for(EvalResult result:results){
            if(filter!=result.method){
                continue;
            }
            accuracy.add(i,result.getAccuracy());
            precision.add(i,result.getPrecision());
            recall.add(i,result.getRecall());
            fscore.add(i,result.getFScore());
            gscore.add(i,result.getGScore());
            total.add(i, result.relTotal);
            mcc.add(i, result.getNormalizedMCC());
            falsenegative.add(i++,result.relCountMisses);
        }


        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(total);

        dataset.addSeries(precision);
        dataset.addSeries(recall);
        dataset.addSeries(fscore);
        dataset.addSeries(accuracy);
        dataset.addSeries(falsenegative);
        dataset.addSeries(gscore);
        dataset.addSeries(mcc);


        return dataset;

    }

    /**
     * Creates a sample dataset
     */

    private  XYDataset createDataset(Set<EvalResult> results) {
        final XYSeries accuracy = new XYSeries("Accuracy");
        final XYSeries precision = new XYSeries("Precision");
        final XYSeries recall = new XYSeries("Recall");
        final XYSeries fscore = new XYSeries("F-Score");
        final XYSeries gscore = new XYSeries("G-Score");
        final XYSeries total=new XYSeries("Total");
        final XYSeries mcc=new XYSeries("MCC");
        final XYSeries falsenegative=new XYSeries("Misses (FP+FN)");
        this.methods=new LinkedList<>();
        double i=0.;
        for(EvalResult result:results){
            this.methods.add(result.method);
             accuracy.add(i,result.getAccuracy());
             precision.add(i,result.getPrecision());
             recall.add(i,result.getRecall());
             fscore.add(i,result.getFScore());
             gscore.add(i,result.getGScore());
            total.add(i, result.relTotal);
            mcc.add(i, result.getNormalizedMCC());
            falsenegative.add(i++,result.relCountMisses);
        }


        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(total);

        dataset.addSeries(precision);
        dataset.addSeries(recall);
        dataset.addSeries(fscore);
        dataset.addSeries(accuracy);
        dataset.addSeries(falsenegative);
        dataset.addSeries(gscore);
        dataset.addSeries(mcc);


        //dataset.addSeries(truenegative);


        return dataset;

    }

    /**
     * Sets textarea contents.
     * @param text the text to set
     */
    public void setTextAreaContents(String text){
        this.resultarea.setText(text);
    }
}