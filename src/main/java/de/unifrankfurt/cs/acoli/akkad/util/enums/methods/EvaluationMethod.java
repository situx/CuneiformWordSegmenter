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

package de.unifrankfurt.cs.acoli.akkad.util.enums.methods;

import de.unifrankfurt.cs.acoli.akkad.util.enums.EvalResultType;

/**
 * Enum of EvaluationMethods.
 */
public enum EvaluationMethod implements MethodEnum {
    ALL("===============================All Evaluations======================================","All Evaluations","ALL",EvalResultType.FSCORE),
    /**Binary evaluate method.*/
    BINARYEVALUATION("===============================Binary Decision Evaluation======================================","Binary Decision Evaluation","BE", EvalResultType.FSCORE),
    /**Boundary edit distance method.*/
    BOUNDARYEDITDISTANCE("================================Boundary Edit Distance Evaluation==========================","Boundary Edit Distance Evaluation","WED",EvalResultType.ONESCORE),
    /**Boundary evaluate method.*/
    BOUNDARYEVALUATION("===============================Boundary Based Evaluation====================================","Boundary Based Evaluation","AE",EvalResultType.FSCORE),
    /**Boundary similarity evaluate method.*/
    BOUNDARYSIMILARITY("===============================Boundary Similarity Evaluation===============================","Boundary Similarity Evaluation","BS",EvalResultType.ONESCORE),
    /**PK Evaluation method.*/
    PKEVALUATION("====================================PK Evaluation=================================================","PK Evaluation","PK",EvalResultType.ONESCORE),
    /**Segmentation evaluate method.*/
    SEGMENTATIONEVALUATION("===============================Segmentation Evaluation======================================","Segmentation Evaluation","SE",EvalResultType.FSCORE),
    /**Transliteration evaluate method.*/
    TRANSLITEVALUATION("===============================Transliteration Evaluation====================================","Transliteration Evaluation","TE",EvalResultType.TWOSCORE),
    /**Window Diff Evaluation method.*/
    WINDOWDIFFEVALUATION("================================Window Diff Evaluation=====================================","Window Diff Evaluation","WF",EvalResultType.ONESCORE),
    /**Word boundary evaluate method.*/
    WORDBOUNDARYEVALUATION("======================Word Boundary Based Evaluation====================================","Word Boundary Base Evaluation","WE",EvalResultType.FSCORE)
    , /**WinPR Evaluation method.*/
    WINPR("================================Window PR Evaluation=======================================","WinPR","WPR",EvalResultType.FSCORE);
    private final String shortname;
    /**The evaluation result type to fit the metric.*/
    private EvalResultType evalResultType;
    /**Description of the method.*/
    private String method,methodname;

    /**Constructor for this class.*/
    private EvaluationMethod(final String method,final String methodname,final String shortname,final EvalResultType evalResultType){
       this.method =method;
        this.methodname=methodname;
        this.shortname=shortname;
        this.evalResultType=evalResultType;
    }

    public EvalResultType getEvalResultType() {
        return evalResultType;
    }

    public void setEvalResultType(final EvalResultType evalResultType) {
        this.evalResultType = evalResultType;
    }

    public String getEvalString(){
           return this.method;
    }


    public String getShortname(){
        return this.shortname;
    }

    @Override
    public String toString() {
        return this.methodname;
    }
}
