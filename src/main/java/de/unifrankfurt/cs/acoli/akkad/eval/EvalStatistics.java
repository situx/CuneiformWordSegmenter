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

package de.unifrankfurt.cs.acoli.akkad.eval;

import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.ClassificationMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.EvaluationMethod;

import java.util.*;

/**
 * Statistics on how certain methods performed on certain files should be generated in this class.
 */
public class EvalStatistics {
    /**The current instance.*/
    private static EvalStatistics instance;
    /**The crossfold mean to calculate.*/
    private Double crossFoldMean=0.0;
    /**The Map of file to evalresults available.*/
    private Map<String, TreeMap<ClassificationMethod, Set<EvalResult>>> evalresults;
    /**The average fscore achieved.*/
    private Double totalavgfscore;

    private EvalStatistics(){
         this.evalresults=new TreeMap<>();
    }

    public static EvalStatistics getInstance(){
        if(instance==null){
            instance=new EvalStatistics();
        }
        return instance;
    }

    /**
     * Adds a result to the given map of result sets.
     * @param filename the filename to consider
     * @param result the result to add
     * @param method  the the method to evaluate
     */
    public void addResult(String filename,EvalResult result,ClassificationMethod method){
        if(!this.evalresults.containsKey(filename)){
           this.evalresults.put(filename,new TreeMap<ClassificationMethod, Set<EvalResult>>());
           this.evalresults.get(filename).put(method,new TreeSet<EvalResult>());
        }
        this.evalresults.get(filename).get(method).add(result);
    }

    /**
     * Calculates the crossfold mean on the given evalResults.
     * @param filename the filename to consider
     * @param method the method to evaluate
     * @return the results to return
     */
    public Map<EvaluationMethod,EvalResult> calculateCrossFoldMean(String filename,ClassificationMethod method){
        Map<EvaluationMethod,EvalResult> realresult=new TreeMap<>();
        if(this.evalresults.containsKey(filename)){
            for(EvalResult eval:this.evalresults.get(filename).get(method)){
                if(!realresult.containsKey(eval.method)){
                    realresult.put(eval.method,new EvalResult(eval.method,eval.classmethod,eval.getDictHandler()));
                }else{
                    realresult.get(eval.method).occurances++;
                }
                realresult.get(eval.method).errorterm+=eval.countmisses/eval.total;
            }
        }
        return realresult;
    }

    public void compareMethodsOnFile(String filename,ClassificationMethod method){
          if(this.evalresults.containsKey(filename)){
              for(EvalResult eval:this.evalresults.get(filename).get(method)){

              }
          }
    }

    public void compareResults(List<String> files,ClassificationMethod method){
        for(String file:files){
            for(EvalResult res:this.evalresults.get(file).get(method)){

            }
        }

    }

    private void compareSingleResult(EvalResult result1,EvalResult result2){
           if(result1.method==result2.method){

           }
    }

    public Map<String, TreeMap<ClassificationMethod, Set<EvalResult>>> getEvalresults() {
        return evalresults;
    }

    public void setEvalresults(final Map<String, TreeMap<ClassificationMethod, Set<EvalResult>>> evalresults) {
        this.evalresults = evalresults;
    }

    public void removeResults(String filename, ClassificationMethod method){
          this.evalresults.get(filename).get(method).clear();
    }
}
