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

import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.cuneiform.AkkadDictHandler;
import de.unifrankfurt.cs.acoli.akkad.util.Tuple;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.*;

/**
 * Reimplementation of the boundary edit distance metric.
* Copyright (c) 2011-2013 Chris Fournier
*
*All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* Redistributions of source code must retain the above copyright
*    notice, this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright
*      notice, this list of conditions and the following disclaimer in the
*      documentation and/or other materials provided with the distribution.
* Neither the name of the author nor the names of its contributors may
*      be used to endorse or promote products derived from this software
*      without specific prior written permission.
*
*THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
*AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
*IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
*DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
*FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
*DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
*SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
*CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
*OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
*OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class BoundaryEditDistance {
    /**The number of matches.*/
    public Integer matches;
    /**The number of tests.*/
    public Integer total;
    /**The list of additions added.*/
    private List<Addition> added;
    /**The list of additions.*/
    private List<Addition> additions;
    /**The dicthandler to use.*/
    private DictHandling dictHandler;
    /**The list of substitutions.*/
    private List<Substitution> substitutions;
    /**The list of transpositions.*/
    private List<Transposition> transpositions;
    /**Constructor for this class.*/
    public BoundaryEditDistance(DictHandling dictHandler){
        this.dictHandler=dictHandler;
    }

    public static void main(String[] args) throws IOException {
         BoundaryEditDistance editDistance=new BoundaryEditDistance(new AkkadDictHandler(CharTypes.AKKADIAN.getStopchars()));

        System.out.println("Boundary Edit Distance: "+editDistance.boundaryEditDistance("a bc de fgh ijk l mn",
                 "a bc d ef ghijkl mn", 3));
        System.out.println("Boundary Similarity: "+editDistance.boundarySimilarity("a bc de fgh ijk l mn",
                "a bc d ef ghijkl mn", 3));
         System.out.println("PK: "+editDistance.pkEvaluation("a bc de fgh ijk l mn","a bc d ef ghijkl mn",2));
        System.out.println("WindowDiff: "+editDistance.windowDiffEvaluation("a bc de fgh ijk l mn","a bc d ef ghijkl mn",2,false));
    }
	/**Permutes a list of given integers and returns its permutations.*/
        private static List<List<Integer>> permute(Integer...myInts){

            if(myInts.length==1){
                List<Integer> arrayList = new ArrayList<Integer>();
                arrayList.add(myInts[0]);
                List<List<Integer> > listOfList = new ArrayList<List<Integer>>();
                listOfList.add(arrayList);
                return listOfList;
            }

            Set<Integer> setOf = new HashSet<Integer>(Arrays.asList(myInts));

            List<List<Integer>> listOfLists = new ArrayList<List<Integer>>();

            for(Integer i: myInts){
                ArrayList<Integer> arrayList = new ArrayList<Integer>();
                arrayList.add(i);

                Set<Integer> setOfCopied = new HashSet<Integer>();
                setOfCopied.addAll(setOf);
                setOfCopied.remove(i);

                Integer[] isttt = new Integer[setOfCopied.size()];
                setOfCopied.toArray(isttt);

                List<List<Integer>> permute = permute(isttt);
                Iterator<List<Integer>> iterator = permute.iterator();
                while (iterator.hasNext()) {
                    List<java.lang.Integer> list = iterator.next();
                    list.add(i);
                    listOfLists.add(list);
                }
            }

            return listOfLists;
        }

    private int[] addSubCheck(Set<Integer> d,Set<Integer> a,Set<Integer> b){
        Integer additions = Math.abs(a.size() - b.size());
        Integer substitutions = (d.size() - additions) / 2;
        return new int[]{additions, substitutions};
    }
    /**Adds possible substitutions to the list of modifications.*/
    private List<Substitution> addSubs(Set<Integer> d,Set<Integer> a,Set<Integer> b){
        List<Tuple<List<Integer>,List<Integer>>> substitutions = new LinkedList<>();
        Double delta = null;
        List<Integer> permalist=new LinkedList<>();
        permalist.addAll(a);
        int j=0;
        for(List<Integer> perm_a :permute(permalist.toArray(new Integer[permalist.size()]))){
            List<Integer> permblist=new LinkedList<>();
            permalist.addAll(b);
            for (List<Integer> perm_b :permute(permblist.toArray(new Integer[permalist.size()]))){
                List<Tuple<List<Integer>,List<Integer>>> current_substitutions=new LinkedList<>();
                //current_substitutions = zip(perm_a, perm_b)
                //current_substitutions = set(current_substitutions);
                double sum=0.;
                for (int i=0;i<current_substitutions.size();i++){
                    sum+=Math.abs(perm_a.get(i) - perm_b.get(i));
                }
                double current_delta=sum;
                if(delta==null || current_delta < delta){
                    delta = current_delta;
                    substitutions = current_substitutions;
                }
            }
        }
        List<Integer> substituted = new LinkedList<>();
        this.added = new LinkedList<>();
        for(int i=0;i<substitutions.size();i++){
            substituted.addAll(substitutions.get(i).getOne());
            substituted.addAll(substitutions.get(i).getTwo());
        }
        List<Integer> additions=new LinkedList<>();
        additions.addAll(d);
        additions.removeAll(new LinkedList<Integer>(new TreeSet<Integer>(substituted)));
        List<Integer> a_l=new LinkedList<>();
        a_l.addAll(a);
        a_l.removeAll(new LinkedList<Integer>(new TreeSet<Integer>(substituted)));
        List<Integer> b_l=new LinkedList<>();
        b_l.addAll(b);
        b_l.removeAll(new LinkedList<Integer>(new TreeSet<Integer>(substituted)));

        for(Integer addition : a_l){
            added.add(new Addition(addition, "a"));
        }
        for(Integer addition : b_l){
            added.add(new Addition(addition, "b"));
        }
        Set<Substitution> subs=new TreeSet<>();
        for(int i=0;i<substitutions.size();i++){
            subs.add(new Substitution(substitutions.get(i).getOne().get(i), substitutions.get(i).getTwo().get(i)));
        }
        return new LinkedList<>(subs);
    }

    /*private void winPR(final Integer originalcount,final Integer revisedcount,final Integer windowSize){


        truepositive += Math.min( originalcount, revisedcount );
        truenegative += Math.max( 0, windowSize - Math.max( originalcount, revisedcount ) );

        if( revisedcount- originalcount > 0 )
            falsepositive += revisedcount - originalcount;
        else
            falsenegative += originalcount - revisedcount;
       */

    /**
     * Evaluates the given result using the Edit distance evaluate method.
     * @throws java.io.IOException
     */
    public Double boundaryEditDistance(String original,String revised,final Integer transpositionSize) throws IOException {
        original=original.replaceAll("[ ]+",this.getCharLengthStr());
        revised=revised.replaceAll("[ ]+",this.getCharLengthStr());
        this.added=new LinkedList<>();
        this.additions=new LinkedList<>();
        this.substitutions=new LinkedList<>();
        this.transpositions=new LinkedList<>();
        List<Set<Integer>> originalMass=this.stringToMasses(original);
        List<Set<Integer>> revisedMass=this.stringToMasses(revised);
        System.out.println("OriginalMass: "+originalMass);
        System.out.println("RevisedMasss: "+revisedMass);
        //Search for transpositions
        Map<Integer,Difference> options_set =this.options(originalMass,revisedMass);
        System.out.println("Optionsset: "+options_set);
        List<Integer> ntlist=new LinkedList<>();
        for(int i=1;i<transpositionSize;i++){
            ntlist.add(i);
        }
       this.transpositions = this.getTranspositions(originalMass,revisedMass,ntlist,options_set);

        for(Difference option:options_set.values()){
            List<Substitution> current_substitutions=addSubs(option.d, option.a, option.b);
            additions.addAll(this.added);
            substitutions.addAll(current_substitutions);
        }
        System.out.println("Additions: "+additions);
        System.out.println("Substitutions: "+substitutions);
        System.out.println("Transpositions: "+transpositions);
        System.out.println("Boundary Edit Distance: "+Double.valueOf(additions.size()+substitutions.size()+transpositions.size()/2));
        return Double.valueOf(additions.size()+substitutions.size()+transpositions.size()/2);
    }

    public Double boundarySimilarity(String original,String revised,Integer transpositionSize) throws IOException {
        this.boundaryEditDistance(original,revised,transpositionSize);
        Integer count_unweighted = additions.size() + substitutions.size() + transpositions.size();
        original=original.replaceAll("[ ]+",this.getCharLengthStr());
        revised=revised.replaceAll("[ ]+",this.getCharLengthStr());
        this.added=new LinkedList<>();
        this.additions=new LinkedList<>();
        this.substitutions=new LinkedList<>();
        this.transpositions=new LinkedList<>();
        List<Set<Integer>> originalMass=this.stringToMasses(original);
        List<Set<Integer>> revisedMass=this.stringToMasses(revised);
        Set<Integer> ori,revi;
        Set<Integer> matches=new TreeSet<>();
        Set<Integer> fullMisses=new TreeSet<>();
        Integer boundaries_all=0;
        for(int i=0;i<originalMass.size();i++){
            ori=originalMass.get(i);
            revi=i<revisedMass.size()?revisedMass.get(i):new TreeSet<Integer>();
            fullMisses.addAll(CollectionUtils.disjunction(ori,revi));
            ori.retainAll(revi);
            matches.addAll(ori);
            boundaries_all+=ori.size()+revi.size();
        }

        for(int i=0;i<originalMass.size() && i<revisedMass.size();i++){
            ori=originalMass.get(i);
        }
        System.out.println("Count_unweighted: "+count_unweighted);
        System.out.println("Matches: "+matches);

        Double denominator = Double.valueOf(count_unweighted + matches.size());
        Double numerator = Double.valueOf(denominator - count_unweighted);
        System.out.println("Denominator: "+denominator);
        System.out.println("Numerator: "+numerator);
        Double truepositive,falsepositive,truenegative,falsenegative;
        Double result;
        this.matches=count_unweighted-fullMisses.size();
        this.total=count_unweighted;
        if(denominator>0){
            result = numerator / denominator;
            System.out.println("Result: "+result);
        }else{
           result=1.;
        }
        return 1-result;
    }

    private int[][] buildBoundarySimilarityConfusionMatrix(final Set<Match> matches, Set<Addition> additions, Set<Substitution> substitutions,Set<Difference> differences, Integer n_t){
        /*weight = kwargs['weight']
        # Initialize
                matrix = cm()*/
        int[][] matrix=new int[5][5];
        //fnc_weight_t = weight[2]
        for(Match match :matches){
            matrix[match.i][match.j] += 1;
        }
        for(Transposition trans:transpositions){
            int matchh=trans.d;
            //matrix[matchh][matchh]+= fnc_weight_t([trans], n_t);
        }
        for(Substitution sub:substitutions){
           matrix[sub.a][sub.b]+=1;
        }
        for(Addition add:additions){
            if(add.b.equals("a")){
                matrix[0][add.a]+=1;
            }
            if(add.b.equals("b")){
                matrix[add.a][0]+=1;
            }
        }
        return matrix;
    }

    private Boolean check_position(Integer position, Integer boundary,Map<Integer,Set<Transposition>> options_transp) {
        System.out.println("Position: "+position+" Boundary: "+boundary+" Options_transp: "+options_transp);
        if(options_transp.containsKey(position)){
            for(Transposition trans:options_transp.get(position)){
                if(trans.d.equals(boundary)){
                    System.out.println("Return True");
                    return true;
                }
            }
        }
        System.out.println("Return False");
        return false;
    }

    public List<Tuple<String,String>> createPairedWindow(final String original,final String revised,final Integer windowsize){
        List<Tuple<String,String>> tuples=new LinkedList<>();
        Tuple<String,String> tuple;
        if(original.length()>revised.length()){
            for(int i=0;i<original.length()-windowsize;i++){
                if(i<revised.length()-windowsize)
                    tuple=new Tuple<>(original.substring(i,i+windowsize),revised.substring(i,i+windowsize));
                else
                    tuple=new Tuple<>(original.substring(i,i+windowsize),"");
                tuples.add(tuple);
            }
        }else{
            for(int i=0;i<revised.length()-windowsize;i++){
                if(i<original.length()-windowsize)
                    tuple=new Tuple<>(original.substring(i,i+windowsize),revised.substring(i,i+windowsize));
                else
                    tuple=new Tuple<>("",revised.substring(i,i+windowsize));
                tuples.add(tuple);
            }
        }
        return tuples;
    }

    private String getCharLengthStr(){
        String charlengthstr="";
        for(int i=0;i<dictHandler.getChartype().getChar_length();i++){
            charlengthstr+=" ";
        }
        return charlengthstr;
    }

    private List<Transposition> getTranspositions(final List<Set<Integer>> original,final List<Set<Integer>> revised,final List<Integer> nt,final Map<Integer,Difference> options){
        java.util.Map<Integer,Set<Transposition>> options_transp = new HashMap<Integer,Set<Transposition>>();
        List<Transposition> result = new LinkedList<Transposition>();
        for(Integer n_i:nt){
            n_i--;
           for(int i=0;i<original.size()-n_i && i<revised.size()-n_i;i++){
              int j=i+n_i;
              Set<Integer> a_i =original.get(i);
              Set<Integer> a_j = original.get(j);
              Set<Integer> b_i = revised.get(i);
              Set<Integer> b_j = revised.get(j);
              Collection<Integer> diff_i = CollectionUtils.disjunction(a_i, b_i);
              Collection<Integer> diff_j = CollectionUtils.disjunction(a_j, b_j);
              Collection<Integer> diff_a = CollectionUtils.disjunction(a_i, a_j);
              Collection<Integer> diff_b = CollectionUtils.disjunction(b_i, b_j);
               /*System.out.println("i: "+i+" - j:"+j);
               System.out.println("a_i: "+a_i+" b_i: "+b_i+" a_j: "+a_j+" b_j: "+b_j);
               System.out.println("diff_i: "+diff_i+" diff_j: "+diff_j+" diff_a: "+diff_a+" diff_b: "+diff_b);*/
               Set<Integer> potential_transposition = new TreeSet<>();
               //System.out.println("Diff i: "+diff_i);
               diff_i.retainAll(diff_j);
               //System.out.println("Diff i+Diff_j: "+diff_i);
               diff_i.retainAll(diff_a);
               //System.out.println("Diff i+Diff_j+Diff_a: "+diff_i);
               diff_i.retainAll(diff_b);
               //System.out.println("Diff i+Diff_j+Diff_a+Diff_b: "+diff_i);
               potential_transposition=new TreeSet<Integer>(diff_i);
               System.out.println("Potential Transpositions: "+potential_transposition);
               for(Integer pottrans:potential_transposition){
                   Transposition option_transp = new Transposition(i, j, pottrans);
                   if(!overlapsExisting(i,j,pottrans,options_transp) && !hasSubstitutions(i,j,pottrans,options)){
                       result.add(option_transp);
                       System.out.println("Add Transp: "+option_transp);
                       if(option_transp.i.equals(i)){
                          options_transp.put(i,new TreeSet<Transposition>());
                       }
                       if(option_transp.j.equals(j)){
                           options_transp.put(j,new TreeSet<Transposition>());
                       }
                       options_transp.get(i).add(option_transp);
                       options_transp.get(j).add(option_transp);
                       //options.remove(i);
                       //options.remove(j);
                       /*# Removing potential set errors that overlap
                       options_set[i][0].discard(d)
                       options_set[i][1].discard(d)
                       options_set[i][2].discard(d)
                       options_set[j][0].discard(d)
                       options_set[j][1].discard(d)
                       options_set[j][2].discard(d)*/
                   }
               }
              //

           }
        }
        return result;
    }

    private Boolean hasSubstitutions(Integer i, Integer j, Integer boundary, Map<Integer,Difference> options_set){
        Boolean present = false;
        Set<Integer> d_i,a_i,b_i,d_j,a_j,b_j;
        if(options_set.containsKey(i)
                && options_set.get(i).d.contains(boundary)
                && options_set.containsKey(j)
                && options_set.get(j).d.contains(boundary)){
            d_i=options_set.get(i).d;
            a_i=options_set.get(i).a;
            b_i=options_set.get(i).b;
            d_j=options_set.get(j).d;
            a_j=options_set.get(j).a;
            b_j=options_set.get(j).b;
            if(addSubCheck(d_i, a_i, b_i)[1]>0 && addSubCheck(d_j, a_j, b_j)[1]>0){
                present = true;
            }
        }

        return present;
    }

    private Map<Integer,Difference> options(List<Set<Integer>> originalMass, List<Set<Integer>> revisedMass){
        Map<Integer,Difference> optionsSet=new TreeMap<>();
        int i=0;
        for(Set<Integer> ori:originalMass){
            Set<Integer> revised=i<revisedMass.size()?revisedMass.get(i):new TreeSet<Integer>();
            Set<Integer> a =new TreeSet<>();
            a.addAll(ori);
            a.removeAll(revised);
            Set<Integer> b =new TreeSet<>();
            b.addAll(revised);
            b.removeAll(ori);
            Set<Integer> d=new TreeSet<>(CollectionUtils.disjunction(ori,revised));
            if(d.size()>0){
                optionsSet.put(i,new Difference(a,b,d));
            }
            i++;
        }
        return optionsSet;
    }

    private Boolean overlapsExisting(Integer i, Integer j, Integer boundary, Map<Integer,Set<Transposition>> options_set){
       return check_position(i,boundary,options_set) || check_position(j,boundary,options_set);
    }

    /**
     * Evaluates the given result using the pk Evaluation method.
     * @param originalarray the original array
     * @param comparearray the comparison array
     */
    private Double pkEvaluation(String originalarray, String comparearray,Integer windowsize){
        String originalwindowstart=originalarray.substring(0,dictHandler.getChartype().getChar_length());
        String originalwindowend=originalarray.substring(originalarray.length()-dictHandler.getChartype().getChar_length(),originalarray.length());
        String comparewindowstart=comparearray.substring(0,dictHandler.getChartype().getChar_length());
        String comparewindowend=comparearray.substring(comparearray.length()-dictHandler.getChartype().getChar_length(),comparearray.length());
        String originalMass=this.stringToPositions(originalarray.replaceAll("[ ]+",this.getCharLengthStr()));
        String revisedMass=this.stringToPositions(comparearray.replaceAll("[ ]+",this.getCharLengthStr()));
        //System.out.println("Originalmass/Originalarray: "+originalMass+" - "+originalarray.replaceAll("[ ]+",this.getCharLengthStr()));
        //System.out.println("Revisedmass/Revisedarray: "+revisedMass+" - "+comparearray.replaceAll("[ ]+",this.getCharLengthStr()));
        originalarray=originalarray.replaceAll("[ ]+",this.getCharLengthStr());
        comparearray=comparearray.replaceAll("[ ]+",this.getCharLengthStr());
        Double countmisses=0.,total=0.;
        String originaltemp,revisedtemp;
        for(int i=0;i<originalarray.length()-windowsize.intValue() && i<comparearray.length()-windowsize.intValue();i+=dictHandler.getChartype().getChar_length()){
            originaltemp=originalarray.substring(i,i+windowsize.intValue());
            revisedtemp=comparearray.substring(i,i+windowsize.intValue());
            originaltemp=originaltemp.replaceAll("[ ]+",this.getCharLengthStr());
            revisedtemp=revisedtemp.replaceAll("[ ]+",this.getCharLengthStr());
            System.out.println("Originaltemp: "+originaltemp+"-"+originaltemp.substring(0,dictHandler.getChartype().getChar_length())+"-"+originaltemp.substring(originaltemp.length()-dictHandler.getChartype().getChar_length(),originaltemp.length()));
            System.out.println("Revisedtemp: "+revisedtemp+"-"+revisedtemp.substring(0,dictHandler.getChartype().getChar_length())+"-"+revisedtemp.substring(revisedtemp.length()-dictHandler.getChartype().getChar_length(),revisedtemp.length()));
            System.out.println("OriginalMass: "+originalMass.substring(i,i+1)+" "+originalMass.substring(i+windowsize.intValue()-1,i+windowsize.intValue()));
            System.out.println("RevisedMasss: "+revisedMass.substring(i,i+1)+" "+revisedMass.substring(i+windowsize.intValue()-1,i+windowsize.intValue()));
            Boolean originalIsValidWindow2=originalMass.substring(i,i+1).equals(originalMass.substring(i+windowsize.intValue()-1,i+windowsize.intValue()));
            Boolean revisedIsValidWindow2=revisedMass.substring(i,i+1).equals(revisedMass.substring(i+windowsize.intValue()-1,i+windowsize.intValue()));
            System.out.println("OriginalMass: "+originalIsValidWindow2);
            System.out.println("RevisedMass: "+revisedIsValidWindow2);
            Boolean originalIsValidWindow=originaltemp.substring(0,dictHandler.getChartype().getChar_length()).equals(originaltemp.substring(originaltemp.length()-dictHandler.getChartype().getChar_length(),originaltemp.length()));
            Boolean revisedIsValidWindow=revisedtemp.substring(0,dictHandler.getChartype().getChar_length()).equals(revisedtemp.substring(revisedtemp.length()-dictHandler.getChartype().getChar_length(),revisedtemp.length()));
            if(originalIsValidWindow2!=revisedIsValidWindow2){
                countmisses++;
            }
            total++;
        }
        System.out.println("Countmisses: "+countmisses);
        System.out.println("Total: "+total);
        System.out.println("PKDistance Result: "+(countmisses>0?(1.0-(countmisses / total))*100:0));
        return countmisses>0?(1.0-(countmisses / total))*100:0;

    }

    private List<Set<Integer>> stringToMass(String str){
        str=str.replaceAll("[ ]+",this.getCharLengthStr());
        List<Set<Integer>> result=new LinkedList<>();
        int counter=0;
        String temp;
        for(int i=0;i<str.length();i+=dictHandler.getChartype().getChar_length()){
            temp=str.substring(i,i+dictHandler.getChartype().getChar_length());
            Set<Integer> addset=new TreeSet<>();
            if(temp.substring(0,1).equals(" ")){
                addset.add(1);
                result.add(addset);
                counter=0;
            }else{
                result.add(addset);
                counter++;
            }
        }
        return result;
    }

    private List<Set<Integer>> stringToMasses(String s){
        s=s.replaceAll("[ ]+"," ");
        List<Set<Integer>> result=new LinkedList<>();
        Integer counter=0;
        System.out.println("String: "+s);
        for(int i=0;i<s.length()-1;i++){
            Set<Integer> addset=new TreeSet<>();
            System.out.println(s.substring(i+1,i+2));
            if(s.substring(i+1,i+2).equals(" ")){
                addset.add(1);
                result.add(addset);
                i++;
            }else{
                result.add(addset);
            }
        }
        return result;
    }

    private String stringToPositions(String s){
        String result="";
        Integer counter=0;
        for(int i=0;i<s.length();i++){
            if(s.substring(i,i+1).equals(" ")){
                result+=(++counter).toString();
            }else{
                result+=counter.toString();
            }
        }
        return result;
    }

    private Double windowDiffEvaluation(String originalarray, String comparearray,final Integer windowSize,final Boolean winPR) {
        originalarray=originalarray.replaceAll("[ ]+",this.getCharLengthStr());
        comparearray=comparearray.replaceAll("[ ]+",this.getCharLengthStr());
        List<Tuple<String, String>> units_ref_hyp = this.createPairedWindow(originalarray, comparearray, windowSize);
        String originalwindow,revisedwindow,currentoriginal,currentrevised;
        //System.out.println("OriginalArray: "+originalarray);
        //System.out.println("RevisedArray: "+comparearray);
        Double total=0.,countmisses=0.;
        for (int i = 0; i < units_ref_hyp.size(); i++) {
            int ref_boundaries = 0;
            int hyp_boundaries = 0;
            originalwindow = units_ref_hyp.get(i).getOne();
            revisedwindow=units_ref_hyp.get(i).getTwo();
            //System.out.println("Originalwindow: "+originalwindow);
            //System.out.println("Revisedwindow: "+revisedwindow);
            for (int j = 0; j < originalwindow.length() && j<revisedwindow.length(); j += dictHandler.getChartype().getChar_length()) {
                currentoriginal=originalwindow.substring(j,j+dictHandler.getChartype().getChar_length());
                currentrevised=revisedwindow.substring(j,j+dictHandler.getChartype().getChar_length());
                //System.out.println("Currentoriginal: "+currentoriginal);
                //System.out.println("Currentrevised: "+currentrevised);
                if (!currentoriginal.equals(this.getCharLengthStr())) {
                    ref_boundaries++;
                    total++;
                }
                if (!currentrevised.equals(this.getCharLengthStr())) {
                    hyp_boundaries++;
                }
            }
            if(winPR){
                //this.winPR(ref_boundaries,hyp_boundaries,windowSize);
            }else if (ref_boundaries != hyp_boundaries) {
                countmisses++;
            }
        }
        return countmisses>0?(1.0-(countmisses / (total-windowSize)))*100:0;
    }

    private class Difference{

        public Set<Integer> a,b,d;

        public Difference(Set<Integer> a,Set<Integer> b, Set<Integer> d){
            this.a=a;
            this.b=b;
            this.d=d;
        }

        @Override
        public boolean equals(final Object obj) {
            if(obj instanceof Difference){
                Difference d=(Difference)obj;
                return this.a.equals(d.a) && this.b.equals(d.b) && this.d.equals(d.d);
            }
            return false;
        }

        @Override
        public String toString() {
            return "A: "+a+" B: "+b+" D:"+d;
        }
    }

    private class Addition{

        public Integer a;

        public String b;

        public Addition(Integer a,String b){
            this.a=a;
            this.b=b;
        }

        @Override
        public boolean equals(final Object obj) {
            if(obj instanceof Addition){
                Addition d=(Addition)obj;
                return this.a.equals(d.a) && this.b.equals(d.b);
            }
            return false;
        }

        @Override
        public String toString() {
            return "A: "+a+" B: "+b;
        }
    }

    private class Substitution{

        public Integer a,b;

        public Substitution(Integer a,Integer b){
            this.a=a;
            this.b=b;
        }

        @Override
        public boolean equals(final Object obj) {
            if(obj instanceof Difference){
                Difference d=(Difference)obj;
                return this.a.equals(d.a) && this.b.equals(d.b);
            }
            return false;
        }

        @Override
        public String toString() {
            return "A: "+a+" B: "+b;
        }
    }

    private class Transposition implements Comparable<Transposition>{

        public Integer i,j,d;

        public Transposition(Integer i,Integer j, Integer d){
            this.i=i;
            this.j=j;
            this.d=d;
        }

        @Override
        public int compareTo(final Transposition transposition) {
            return 0;
        }

        @Override
        public boolean equals(final Object obj) {
            if(obj instanceof Transposition){
                Transposition d=(Transposition)obj;
                return this.i.equals(d.i) && this.j.equals(d.j) && this.d.equals(d.d);
            }
            return false;
        }

        @Override
        public String toString() {
            return "I: "+i+" J: "+j+" D:"+d;
        }
    }

    private class Match {

        public Integer i,j;

        public Match(Integer i,Integer j){
            this.i=i;
            this.j=j;
        }

        @Override
        public boolean equals(final Object obj) {
            if(obj instanceof Transposition){
                Transposition d=(Transposition)obj;
                return this.i.equals(d.i) && this.j.equals(d.j);
            }
            return false;
        }

        @Override
        public String toString() {
            return "I: "+i+" J: "+j;
        }
    }


}
