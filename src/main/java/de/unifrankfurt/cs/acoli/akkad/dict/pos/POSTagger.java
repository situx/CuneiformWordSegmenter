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

package de.unifrankfurt.cs.acoli.akkad.dict.pos;

import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.dict.pos.util.GroupDefinition;
import de.unifrankfurt.cs.acoli.akkad.dict.pos.util.POSDefinition;
import de.unifrankfurt.cs.acoli.akkad.util.Tuple;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Files;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Tags;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by timo on 21.07.14.
 */
public class POSTagger extends DefaultHandler2 {
    /**Map of lines to sentences.*/
    public Map<Integer, String> sentences;
    /**Map of sentence to word position in the text.*/
    public Map<Integer, Tuple<Integer,Integer>> sentencesByWordPosition;
    /**The map of POSDefinitions.*/
    protected Map<Integer,Tuple<String,POSDefinition>> classificationResult;
    /**The map of POSDefinitions.*/
    protected Map<Integer,List<POSDefinition>> classifiers;
    /**Map from classification to color for the GUI.*/
    protected Map<Integer, String> colorToPos;
    /**Map of dependencies for creating a dependency grammar.*/
    protected Map<String,List<String>> dependencies;
    /**Indicates if groups should be used.*/
    protected boolean groupconfig=false;
    /**Map of group configurations parsed from the xml file.*/
    protected Map<String,Map<Integer,List<GroupDefinition>>> groupconfigs;
    /**The last matched POSTag in the text.*/
    protected POSDefinition lastmatched;
    /**The last matched word in the text.*/
    protected String lastmatchedword="";
    /**The order of a String to a POSTag.*/
    protected Map<String,Integer> orderToPOS;
    /**Map from classification to color for the GUI.*/
    protected Map<String, Color> poscolors;
    /**The wordcouner for counting the words of a text continously.*/
    protected Integer wordcounter=0;

    /**
     * Constructor for this class.
     * @param poscolors the colors to set
     */
    public POSTagger(final Map<String, Color> poscolors,final String locale){
        this.poscolors=poscolors;
        this.dependencies=new TreeMap<>();
        this.classifiers =new TreeMap<>();
        this.classificationResult=new TreeMap<>();
        this.colorToPos=new TreeMap<>();
        this.orderToPOS=new TreeMap<>();
        this.groupconfigs=new TreeMap<>();
        try {
            this.importFromXML(Files.POSDIR+locale+Files.XMLSUFFIX.toString());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if(qName.equals("groupconfig")){
            this.groupconfig=false;
        }
    }

    public Map<Integer, String> getColorToPos() {
        return colorToPos;
    }

    public void setColorToPos(final Map<Integer, String> colorToPos) {
        this.colorToPos = colorToPos;
    }

    /**
     *
     * @param word
     * @param dicthandler
     * @return
     */
    public List<Integer> getPosTag(String word,DictHandling dicthandler){
        return new LinkedList<>();
    }

    public List<POSDefinition> getPosTag(String word,DictHandling dicthandler,Boolean dummy){
        return new LinkedList<POSDefinition>();
    }

    public List<POSDefinition> getPosTagDefs(String word, DictHandling handler/*,final Boolean dummy*/){
        return null;
    }

    /**
     * Gets the POSColors for this POSTagger.
     * @return  the colormap
     */
    public Map<String, Color> getPoscolors() {
        return poscolors;
    }

    /**
     * Imporst POSTag Definitions from XML.
     * @param filepath the filepath for reading the definitions
     * @throws ParserConfigurationException on error
     * @throws SAXException on error
     * @throws IOException on error
     */
    public void importFromXML(String filepath) throws ParserConfigurationException, SAXException, IOException {
        this.classifiers.clear();
        this.colorToPos.clear();
        System.setProperty("avax.xml.parsers.SAXParserFactory",
                "org.apache.xerces.parsers.SAXParser");
        SAXParser parser= SAXParserFactory.newInstance().newSAXParser();
        java.io.InputStream in = new FileInputStream(new File(filepath));
        InputSource is = new InputSource(in);
        is.setEncoding(Tags.UTF8.toString());
        parser.parse(in, this);
        parser.reset();
        //System.out.println(this.poscolors.toString());
        //System.out.println(this.classifiers.toString());
        for(Integer key:this.classifiers.keySet()){
            StringBuffer res=new StringBuffer();
            for(POSDefinition def:this.classifiers.get(key)){
                if(!def.getRegex().toString().isEmpty()){
                    res.append(def.getRegex().toString()+"|");
                }
            }
        }
        //System.out.println("JoinedRegexes: "+this.joinedregexes.toString());
    }

    /**
     * Util method for parsing html colors.
     * @param htmlcolor the html color to parse
     * @return the html color as Color object
     */
    public Color parseHTMLColor(String htmlcolor){
        int red,green,blue;
        htmlcolor=htmlcolor.substring(1);
        red=Integer.valueOf(htmlcolor.substring(0,2),16);
        green=Integer.valueOf(htmlcolor.substring(2,4),16);
        blue=Integer.valueOf(htmlcolor.substring(4,6),16);
        return new Color(red,green,blue);
    }

    public void reset(){
        this.classificationResult.clear();
        this.wordcounter=0;
    }

    public Map<Integer,String> sentenceDetector(String[] words,String[] lines){
        return new TreeMap<>();
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        switch (qName){
            case "tagcolor": this.poscolors.put(attributes.getValue("desc"),this.parseHTMLColor(attributes.getValue("color")));
                this.colorToPos.put(this.parseHTMLColor(attributes.getValue("color")).getRGB(),attributes.getValue("desc"));
                this.orderToPOS.put(attributes.getValue("tag"),Integer.valueOf(attributes.getValue("matchorder")));
                this.dependencies.put(attributes.getValue("tag"),new LinkedList<String>());
                break;
            case "groupconfig": groupconfig=true;
                this.groupconfigs.put(attributes.getValue("tag"),new TreeMap<Integer,List<GroupDefinition>>());
                break;
            case "dependence": groupconfig=true;
                String dependee=attributes.getValue("dependee");
                this.dependencies.get(dependee).add(attributes.getValue("depender"));
                break;
            case "group":
                int group=Integer.valueOf(attributes.getValue("group"));
                //System.out.println("Group: "+group);
                if(groupconfig){
                    if(!this.groupconfigs.get(attributes.getValue("tag")).containsKey(group)){
                        this.groupconfigs.get(attributes.getValue("tag")).put(group,new LinkedList<GroupDefinition>());
                    }
                    this.groupconfigs.get(attributes.getValue("tag")).get(group).add(new GroupDefinition(attributes.getValue("regex"),attributes.getValue("equals"),attributes.getValue("name"),attributes.getValue("case"),attributes.getValue("value")));
                }
                break;
            case "tag":  String tag=attributes.getValue("name");
                Integer order= this.orderToPOS.get(tag);
                if(!this.classifiers.containsKey(order)){
                    this.classifiers.put(order,new LinkedList<POSDefinition>());
                }

                POSDefinition def=new POSDefinition(tag,attributes.getValue("regex"),attributes.getValue("equals"),attributes.getValue("case"),attributes.getValue("value"),attributes.getValue("desc"),attributes.getValue("extrainfo"),attributes.getValue("cunei"),!this.groupconfigs.containsKey(attributes.getValue("desc"))?new TreeMap<Integer,List<GroupDefinition>>():this.groupconfigs.get(attributes.getValue("desc")));
                this.classifiers.get(order).add(def);
                break;
        }
    }

    public String textToPosTagXML(String translittext){
        return null;
    }



}
