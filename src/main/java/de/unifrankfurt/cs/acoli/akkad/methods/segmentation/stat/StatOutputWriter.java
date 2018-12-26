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

package de.unifrankfurt.cs.acoli.akkad.methods.segmentation.stat;

import cc.mallet.fst.TransducerEvaluator;
import cc.mallet.fst.TransducerTrainer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Sequence;
import cc.mallet.types.TokenSequence;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Created with IntelliJ IDEA.
 * User: timo
 * Date: 08.12.13
 * Time: 16:11
 * To change this template use File | Settings | File Templates.
 */
public class StatOutputWriter extends  TransducerEvaluator{

        Sequence predOutput;

        String filenamePrefix;
        String outputEncoding = "UTF-8";

        public StatOutputWriter (String filenamePrefix, InstanceList[] instanceLists, String[] descriptions) {
            super (instanceLists, descriptions);
            this.filenamePrefix = filenamePrefix;
        }

        public StatOutputWriter (String filenamePrefix, InstanceList instanceList1, String description1) {
            this (filenamePrefix, new InstanceList[] {instanceList1}, new String[] {description1});
        }

        public StatOutputWriter (String filenamePrefix,
                              InstanceList instanceList1, String description1,
                              InstanceList instanceList2, String description2) {
            this (filenamePrefix, new InstanceList[] {instanceList1, instanceList2}, new String[] {description1, description2});
        }

        public StatOutputWriter (String filenamePrefix,
                              InstanceList instanceList1, String description1,
                              InstanceList instanceList2, String description2,
                              InstanceList instanceList3, String description3) {
            this (filenamePrefix, new InstanceList[] {instanceList1, instanceList2, instanceList3},
                    new String[] {description1, description2, description3});
        }

        protected void preamble (TransducerTrainer t) {
            // We don't want to print iteration number and cost, so here we override this behavior in the superclass.
        }

        @SuppressWarnings("unchecked")
        @Override
        public void evaluateInstanceList(TransducerTrainer transducerTrainer,	InstanceList instances, String description) {
            int iteration = transducerTrainer.getIteration();
            String viterbiFilename = filenamePrefix + description + iteration + ".viterbi";
            PrintStream viterbiOutputStream;
            try {
                FileOutputStream fos = new FileOutputStream (viterbiFilename);
                if (outputEncoding == null)
                    viterbiOutputStream = new PrintStream (fos);
                else
                    viterbiOutputStream = new PrintStream (fos, true, outputEncoding);
                //((CRF)model).write (new File(viterbiOutputFilePrefix + "."+description + iteration+".model"));
            } catch (IOException e) {
                System.err.println ("Couldn't open Viterbi output file '"+viterbiFilename+"'; continuing without Viterbi output trace.");
                return;
            }

            for (int i = 0; i < instances.size(); i++) {
                if (viterbiOutputStream != null)
                    viterbiOutputStream.println ("Viterbi path for "+description+" trainingSet #"+i);
                Instance instance = instances.get(i);
                Sequence input = (Sequence) instance.getData();
                TokenSequence sourceTokenSequence = null;
                if (instance.getSource() instanceof TokenSequence)
                    sourceTokenSequence = (TokenSequence) instance.getSource();

                Sequence trueOutput = (Sequence) instance.getTarget();
                assert (input.size() == trueOutput.size());
                predOutput = transducerTrainer.getTransducer().transduce (input);
                System.out.println(predOutput.size());
                System.out.println(predOutput.get(0));
                assert (predOutput.size() == trueOutput.size());

                for (int j = 0; j < trueOutput.size(); j++) {;
                    if (sourceTokenSequence != null)
                        viterbiOutputStream.print (sourceTokenSequence.get(j).getText()+": ");
                    viterbiOutputStream.println (predOutput.get(j).toString()/*+"  "+ fv.toString(true)*/);
                }
            }
        }

        public boolean getOutput(Integer position){
              switch(this.predOutput.get(position).toString()){
                  case "1": return true;
                  case "0": return false;
                  default: return false;
              }
        }

}
