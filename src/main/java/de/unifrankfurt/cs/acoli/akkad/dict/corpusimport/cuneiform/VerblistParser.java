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

package de.unifrankfurt.cs.acoli.akkad.dict.corpusimport.cuneiform;

import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


/**
 * Created by timo on 13.07.14.
 * Parses akkadian verbs for postagging
 */
public class VerblistParser {
    /**
     * Set of regexes for recognizing verbs.
     */
    public Set<String> verbregexes;
    /**HTMLParser for parsing the source file.*/
    private Source source;

    /**
     * Constructor for this class.
     * @param filepath the path to the verb file
     * @throws IOException on error
     */
    public VerblistParser(String filepath) throws IOException {
        this.source=new Source(new File(filepath));

    }

    /**
     * Test main method for verblist parser.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        VerblistParser parser=new VerblistParser("verbs.html");
        parser.parseList();
    }

    /**
     * Parses the verbs from the given input file.
     * @throws IOException on error
     */
    public void parseList() throws IOException {
        List<StartTag> elems=source.getAllStartTags("tr");
        List<String> urls=new LinkedList<String>();
        List<String> infi=new LinkedList<String>();
        List<String> vowels=new LinkedList<String>();
        List<String> trans=new LinkedList<String>();
        List<String> stems=new LinkedList<>();
        String transstr;
        for(StartTag elem:elems){
            if(!elem.getElement().getChildElements().isEmpty())  {
                System.out.println(elem.getElement().getChildElements().get(1).getTextExtractor().toString());
                urls.add(elem.getElement().getChildElements().get(1).getTextExtractor().toString());
                vowels.add(elem.getElement().getChildElements().get(2).getTextExtractor().toString().replace("(","").replace(")",""));
                infi.add(elem.getElement().getChildElements().get(3).getTextExtractor().toString());
                transstr=elem.getElement().getChildElements().get(4).getTextExtractor().toString();
                transstr=transstr.replace("\"","");
                if(transstr.startsWith("(") && (StringUtils.isAllUpperCase(transstr.substring(1,2)) || transstr.indexOf(")")<4)){
                    stems.add(transstr.substring(transstr.indexOf("(")+1,transstr.indexOf(")")));
                    transstr=transstr.substring(transstr.indexOf(" ")+1);
                }else if(urls.get(urls.size()-1).substring(0,1).equals("n")){
                    stems.add("N");
                }else{
                    stems.add("G");
                }
                String result="";
                List<String> transstrlist=new LinkedList<>();
                for(String str:transstr.split(";|,")){
                    if(str.contains("(") && str.contains(")")){
                        str=str.substring(str.indexOf(")")+1);
                    }
                    str=str.replace(";","");
                    if(!str.isEmpty() || !str.matches("[ ]+")){
                        result+=str+";";
                    }

                }
                result=result.replaceAll("^[;]+","");
                result=result.trim();
                result=!result.isEmpty()?result.substring(0,result.length()-1).trim():result;
                trans.add(result);
            }
        }
        System.out.println(urls.toString());
        verbregexes=new TreeSet<>();
        String buildregex1="(^(l[aeiu]-|n[aeiu]-)?([aeiu][0-9]?-|[aeiu][0-9]?)?(",buildregex2=")([aeiu][0-9]?)?-(ta-|te-)?([aeiu][0-9]?)?(",buildregex3=")([aeiu][0-9]?-)?([aeiu][0-9]?)?(",buildregex4=")([aeiu][0-9]?)?(-u|-i|-mi|-n(a|u))*$)";
        String buildregex32=")([aeiu][0-9]?-)?([aeiu][0-9]?)?(";
        String root32="[^cdfghjklmnpqrstvwxyz]";
        String regex="",regex2="";
        BufferedWriter writer=new BufferedWriter(new FileWriter(new File("verbregexes.txt")));
        int i=0;
        for(String url:urls){

            String root1=url.substring(0,1),root2=url.substring(1,2)+"-"+url.substring(1,2)+"|"+url.substring(1,2)+url.substring(1,2)+"|"+url.substring(1,2),root3=url.substring(2,3);

            //Detect D-Stem Verb
            if(vowels.get(i).startsWith("d")){
                System.out.println("D-Stem Root: "+url);
                root2=url.substring(1,2)+url.substring(1,2);
            }
            //Detect N-Stem Verb
            if(url.substring(0,1).equals("n")){
                root1=root1+"|b-b|c-c|d-d|f-f|g-g|k-k|l-l|m-m||p-p|q-q|r-r|s-s|sz-sz|t-t[^ae]|z-z";
            }
            if(!url.contains("ʾ")){
                regex=buildregex1+root1+buildregex2+root2+buildregex3+"(-)?"+root3+buildregex4;
            }else{
                if(root1.contains("ʾ") && !root2.contains("ʾ") && !root3.contains("ʾ")){
                    regex=buildregex1.substring(0,buildregex1.length()-1)+"[aeiu]?"+buildregex2.substring(1,buildregex2.length())+root2+buildregex3+"(-)?"+root3+buildregex4;
                }else if(!root1.contains("ʾ") && root2.contains("ʾ") && !root3.contains("ʾ")){
                   regex=buildregex1+root1+buildregex2.substring(0,buildregex2.length()-1)+"[aeiu]?-[aeiu]?("+"(-)?"+root3+buildregex4;
                }else if(!root1.contains("ʾ") && !root2.contains("ʾ") && root3.contains("ʾ")){
                    regex=buildregex1+root1+buildregex2+root2+buildregex3+"(-)?"+root32+buildregex4;
                }
            }
            verbregexes.add(regex);
            //verbregexes.add(regex2);
            writer.write("<tag desc=\"verb\" name=\"VV\" equals=\"\" regex=\""+regex+"\" case=\"VERB\" value=\""+trans.get(i)+"\" extrainfo=\"\\u221A"+url.substring(0,1)+url.substring(1,2)+url.substring(2,3)+" ("+infi.get(i)+")&lt;br&gt;Stem: "+stems.get(i)+"\"/>\n");
            i++;
        }
        writer.close();
        System.out.println(verbregexes.toString());
    }


}
