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

package de.unifrankfurt.cs.acoli.akkad.dict.pos.util;

import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;
import de.unifrankfurt.cs.acoli.akkad.methods.Methods;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.TransliterationMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.pos.POSTags;
import de.unifrankfurt.cs.acoli.akkad.util.enums.pos.PersonNumberCases;
import de.unifrankfurt.cs.acoli.akkad.util.enums.pos.Tenses;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Tags;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for defining a postag according to the regex postag specification.
 */
public class POSDefinition {
    public String currentword="";
    /**The classification of the postag.*/
    private String classification;
    private String cunei;
    private Map<Integer,String> currentgroupResults;
    private String desc;
    /**The equals value of the postag.*/
    private String equals;
    private String extrainfo;
    private Map<Integer,List<GroupDefinition>> groupconfig;
    private POSTags posTag;
    /**The regex of the postag.*/
    private Pattern regex;
    /**The tag of the postag..*/
    private String tag;
    private Tenses tense;
    /**The value of the postag.*/
    private String value;
    private PersonNumberCases wordCase;

    /**
     * Constructor for the postag.
     * @param tag  the tag to set
     * @param regex the regex to define
     * @param equals equals string
     * @param classification the classification
     * @param value the value
     */
    public POSDefinition(String tag,String regex,String equals, String classification,String value,String desc,String extrainfo,String cunei,Map<Integer,List<GroupDefinition>> groupconfig){
        this.tag=tag;
        this.regex=Pattern.compile(regex);
        this.equals=equals;
        this.classification=classification;
        if(value==null)
            this.value="";
        else
            this.value=value;
        if(extrainfo==null)
            this.extrainfo="";
        else
            this.extrainfo=extrainfo;
        if(cunei==null)
            this.cunei="";
        else
            this.cunei=cunei;
        this.desc=desc;
        this.groupconfig=groupconfig;
        this.posTag= POSTags.valueOf(desc.toUpperCase());
        this.currentgroupResults=new TreeMap<>();
    }

    public static String splitString(String toSplit,String splitter,Integer length){
        StringBuffer buf = new StringBuffer();

        if (toSplit != null)
        {
            while(toSplit.length() > length)
            {
                String block = toSplit.substring(0, length);
                int index = length;
                if (index >= 0){
                    buf.append(toSplit.substring(0, index) + splitter);
                }
                toSplit = toSplit.substring(index+1);
            }
            buf.append(toSplit);
            return buf.toString();
        }
        return " ";
    }

    /**
     * Gets the classification of this POSDefinition.
     * @return the classification as String
     */
    public String getClassification() {
        return classification;
    }

    /**
     * Sets the classification of this POSDefinition.
     * @param classification the classification to set
     */
    public void setClassification(final String classification) {
        this.classification = classification;
    }

    public String getCunei() {
        return cunei;
    }

    public void setCunei(final String cunei) {
        this.cunei = cunei;
    }

    public Map<Integer, String> getCurrentgroupResults() {
        return currentgroupResults;
    }

    public void setCurrentgroupResults(final Map<Integer, String> currentgroupResults) {
        this.currentgroupResults = currentgroupResults;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(final String desc) {
        this.desc = desc;
    }

    /**
     * Gets the equals String of this postagger.
     * @return the equals String
     */
    public String getEquals() {
        return equals;
    }

    /**Sets the equals String of this postagger.
     *
     * @param equals the equals String to set
     */
    public void setEquals(final String equals) {
        this.equals = equals;
    }

    public String getExtrainfo() {
        return extrainfo;
    }

    public void setExtrainfo(final String extrainfo) {
        this.extrainfo = extrainfo;
    }

    public Map<Integer, List<GroupDefinition>> getGroupconfig() {
        return groupconfig;
    }

    public void setGroupconfig(final Map<Integer, List<GroupDefinition>> groupconfig) {
        this.groupconfig = groupconfig;
    }

    public POSTags getPosTag() {
        return posTag;
    }

    public void setPosTag(final POSTags posTag) {
        this.posTag = posTag;
    }

    /**
     * Gets the regex of this POSDefinition.
     * @return the regex
     */
    public Pattern getRegex() {
        return regex;
    }

    /**
     * Sets the regex of this POSDefinition.
     * @param regex  the regex to set
     */
    public void setRegex(final Pattern regex) {
        this.regex = regex;
    }

    /**
     * Gets the tag of this POSDefinition.
     * @return  the tag as String
     */
    public String getTag() {
        return tag;
    }

    /**
     * Sets the tag of this POSDefinition.
     * @param tag the tag to set
     */
    public void setTag(final String tag) {
        this.tag = tag;
    }

    public Tenses getTense() {
        return tense;
    }

    public void setTense(final Tenses tense) {
        this.tense = tense;
    }

    /**
     * Gets the value of this POSDefinition.
     * @return the value as String
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of this POSDefinition.
     * @param value the value to set
     */
    public void setValue(final String value) {
        this.value = value;
    }

    public PersonNumberCases getWordCase() {
        return wordCase;
    }

    public void setWordCase(final PersonNumberCases wordCase) {
        this.wordCase = wordCase;
    }

    /**
     * Performs a check to confirm that the current POSDefinition applies.
     * @param tocheck the String to check
     * @return
     */
    public List<Integer> performCheck(final String tocheck){
        List<Integer> result=new LinkedList<>();
        Integer temp;
        if(!equals.isEmpty()){
            if((temp=tocheck.equals(equals)?equals.length():-1)==-1){
                result.clear();
                return result;
            }else{
                result.add(0);
                result.add(temp);
            }
        }
        if(!regex.toString().isEmpty()){
            //System.out.println("Regex: "+regex.toString());
            if(!(regex.matcher(tocheck).find())){
                result.clear();
                return result;
            }else{
                Matcher m=regex.matcher(tocheck);
                while (m.find()) {
                    for(int k=0;k<=m.groupCount() && !groupconfig.isEmpty();k++){
                        if(this.groupconfig.containsKey(k)){
                            this.currentgroupResults.put(k,m.group(k));
                            if(m.group(k)!=null) {
                                for (GroupDefinition groupdef : groupconfig.get(k)) {
                                    if (groupdef.getRegex().matcher(this.currentgroupResults.get(k)).matches() && groupdef.getGroupCase()!=null) {
                                        switch (groupdef.getGroupCase()) {
                                            case "tense":
                                                this.tense = Tenses.valueOf(groupdef.getValue());
                                                break;
                                            case "declination":
                                                this.wordCase=PersonNumberCases.valueOf(groupdef.getValue());
                                                break;
                                        }
                                    }
                                }
                            }

                        }
                    }
                    result.add(m.start());
                    result.add(m.end());
                }
            }
        }
        return result;
    }

    public String toHTMLString(ResourceBundle bundle,CharTypes charType) {
        String result = "";
        if (!currentword.isEmpty()) {
            result += currentword.endsWith("-") ? currentword.substring(0, currentword.length() - 1) : currentword;
            if (!cunei.isEmpty()) {
                result += " (" + cunei + ")";
            } else {
                String temp;
                if (currentword.matches(charType.getLegalTranslitCharsRegex())) {
                    try {
                        temp=charType.getCorpusHandlerAPI().transliterationToText(currentword.toLowerCase(), 0, charType.getCorpusHandlerAPI().getUtilDictHandler(), false, true);
                        if(temp.isEmpty() || temp.matches("[ ]+")){
                            return "";
                        }
                        result += " ("+temp + ")";
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    temp=new Methods().assignTransliteration(currentword.split(" "), charType.getCorpusHandlerAPI().getUtilDictHandler(), TransliterationMethod.MAXPROB) + " (generated)";
                    if(temp.isEmpty() || temp.matches("[ ]+")){
                        return "";
                    }
                    result += temp;
                }

        }
    }
        result+="<br>";
        if(!desc.isEmpty()){
            result+=bundle.getString("type")+": "+splitString(!bundle.containsKey(desc)?desc:bundle.getString(desc),"<br>",100);
            if(!classification.isEmpty()){
                result+=" ("+splitString(classification,"<br>",100)+")<br>";
            } else{
                result+="<br>";
            }
        }
        if(!regex.toString().isEmpty()){
            result+=bundle.getString("regex")+": "+splitString(regex.toString(), "<br>",100)+"<br>";
        }
        if(!equals.isEmpty()){
            result+=bundle.getString("eq")+": "+splitString(equals,"<br>",100)+"<br>";
        }
        if(!value.isEmpty()){
            switch (posTag){
                case NUMBER:
                    result+=bundle.getString("value")+": "+Integer.valueOf(value)*(StringUtils.countMatches(currentword,"-")+1)+"<br>";
                    break;
                case VERB:
                    result+=bundle.getString("value")+": to "+splitString(value,"<br>",100)+"<br>";
                    break;
                default:
                    result+=bundle.getString("value")+": "+splitString(value,"<br>",100)+"<br>";
            }
        }else{
            switch (posTag){
                case NAMEDENTITY:
                    currentword=currentword.replaceAll("[A-Z]","").replace("a-a-a", "aja").replace("e-e-e", "eje").replaceAll("-", "").replaceAll("[0-9]", "")
                            .replaceAll("[a]+", "a").replaceAll("[e]+", "e").replaceAll("[u]+", "u").replaceAll("[i]+", "i")+"<br>";
                    result+=bundle.getString("value")+": "+(currentword.charAt(0)+"").toUpperCase()+currentword.substring(1,currentword.length());
                    break;

                default:
            }
        }
        for(Integer key:this.currentgroupResults.keySet()){
            if(this.currentgroupResults.get(key)!=null){
                for(GroupDefinition groupdef:this.groupconfig.get(key)){
                    if(groupdef.getRegex().matcher(this.currentgroupResults.get(key)).matches()){
                        result+=groupdef.getDescription()+"<br>";
                    }
                }
            }
        }
        if(!extrainfo.isEmpty()){
            result+=bundle.getString("info")+": "+splitString(StringEscapeUtils.unescapeJava(extrainfo),"<br>",100)+"<br>";
        }
        currentword="";
        return result.substring(0,result.length()-4);
    }

    @Override
    public String toString() {
        return this.tag+" "+this.regex.toString()+" "+this.classification+" "+equals;
    }

    /**
     * Converts the POSDefinition to XML.
     * @return the XML String
     */
    public String toXML(){
        StringWriter strwriter=new StringWriter();
        XMLOutputFactory output3 = XMLOutputFactory.newInstance();
        output3.setProperty(Tags.ESCAPECHARACTERS.toString(), false);
        XMLStreamWriter writer;
        try {
            writer = new IndentingXMLStreamWriter(output3.createXMLStreamWriter(strwriter));
            writer.writeStartElement("tag");
            writer.writeAttribute("equals",this.equals);
            writer.writeAttribute("name",this.tag);
            writer.writeAttribute("regex",this.regex.toString());
            writer.writeAttribute("case",this.classification);
            writer.writeEndElement();
        } catch (XMLStreamException | FactoryConfigurationError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //System.out.println("ToString: "+strwriter.toString());
        return strwriter.toString();
    }
}
