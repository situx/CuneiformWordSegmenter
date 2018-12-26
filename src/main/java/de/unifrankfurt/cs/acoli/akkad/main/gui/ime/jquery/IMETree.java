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

package de.unifrankfurt.cs.acoli.akkad.main.gui.ime.jquery;


import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Tags;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * Implementation of an IMETree.
 */
public class IMETree {
    /**Cached words to append to a tree node.*/
    private Map<Integer,Map<String,Set<String>>> cachewords;
    /**The children of the current tree node.*/
    private List<IMETree> children;
    /**The frequency of the current tree node.*/
    private Integer frequency;
    /**Indicates if this tree node represents a word.*/
    private Boolean isWord;
    /**Represents the String and its remaining transliteration chars.*/
    private String word,chars;
    /**Constructor for this class.*/
    public IMETree(String word,String chars,Integer frequency){
        this.children=new LinkedList<>();
        this.cachewords=new TreeMap<>();
        this.isWord=true;
        this.word=word;
        this.chars=chars;
        this.frequency=frequency;
    }
    /**Constructor for this class.*/
    public IMETree(){
        this.children=new LinkedList<>();
        this.cachewords=new TreeMap<>();
        this.isWord=false;
        this.word="";
        this.chars="";
        this.frequency=0;
    }
    /**Adds a child to the current tree.*/
    public void addChild(IMETree child){
        this.children.add(child);
    }
    /**Checks if the current node contains a child with a given nodevalue.*/
    public IMETree containsChild(String nodevalue){
        for(IMETree tree:this.children){
            if(tree.word.equals(nodevalue)){
                return tree;
            }
        }
        return null;
    }

    public Map<Integer,Map<String, Set<String>>> getCachewords() {
        return cachewords;
    }

    public void setCachewords(final Map<Integer,Map<String, Set<String>>> cachewords) {
        this.cachewords = cachewords;
    }

	public String getChars() {
        return chars;
    }

	public void setChars(String chars) {
		this.chars = chars;
	}

    public List<IMETree> getChildren(){
        return this.children;
    }

    public void setChildren(final List<IMETree> children) {
        this.children = children;
    }

    public Integer getFrequency() {
		return frequency;
	}

	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}

    public Boolean getIsWord() {
        return isWord;
    }

    public void setIsWord(final Boolean isWord) {
        this.isWord = isWord;
    }

    public String getWord() {
        return word;
    }

    public void setWord(final String word) {
        this.word = word;
    }

    public Boolean hasChildren(){
        return !this.children.isEmpty();
    }

    @Override
    public String toString() {
        return "Nodevalue: "+word+" IsWord: "+isWord+" Freq: "+frequency+" Children: "+this.children.size()+" "+this.cachewords.toString();
    }
    /**Exports the tree to xml.*/
    public String toXML(){
        StringWriter strwriter;
        XMLOutputFactory output3;
        XMLStreamWriter writer;
        strwriter=new StringWriter();
        output3 = XMLOutputFactory.newInstance();
        output3.setProperty(Tags.ESCAPECHARACTERS.toString(), false);
        try {
            writer = new IndentingXMLStreamWriter(output3.createXMLStreamWriter(strwriter));
            for(Integer key:this.cachewords.keySet()){
                for(String innerkey:this.cachewords.get(key).keySet()){
                    for(String form:this.cachewords.get(key).get(innerkey)) {
                        writer.writeStartElement(Tags.CANDIDATES);
                        writer.writeAttribute("isWord",((Boolean)!form.matches("[A-Z]")).toString());
                        writer.writeAttribute(Tags.FREQ,this.frequency.toString());
                        writer.writeAttribute(Tags.TRANSLIT,innerkey);
                        writer.writeAttribute(Tags.CHARS,form);
                        writer.writeEndElement();
                        writer.writeCharacters("\n");
                    }

                }

            }

        } catch (XMLStreamException | FactoryConfigurationError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return strwriter.toString();
    }
}
