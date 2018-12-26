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
import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Tags;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.util.*;

/**
 * TreeBuilder class for building in IME Tree
 */
public class TreeBuilder extends DefaultHandler2{
    /**The root node of the tree.*/
    private IMETree root=new IMETree();

    /**
     * Constructor for this class.
     * @param file the input file to read from
     */
    public TreeBuilder(final InputStream file){
        System.setProperty("avax.xml.parsers.SAXParserFactory",
                "org.apache.xerces.parsers.SAXParser");
        System.out.println("Now parsing TreeBuilder....");
    	SAXParser parser;
		try {
			parser = SAXParserFactory.newInstance().newSAXParser();
	    	parser.parse(file,this);
            //this.treeToXML("");
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


    }

    /**
     * Constructor for this class.
     * @param dictHandler the dicthandler for data
     */
    public TreeBuilder(DictHandling dictHandler){
    	buildTree(root,dictHandler);
    }

    /**
     * Main testing method.
     * @param args
     */
    public static void main(String[] args){
        try {
            TreeBuilder builder=new TreeBuilder(new FileInputStream(new File("ime/corpus_akkadian char_jquery.txt")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    /**Sorts a map by values.*/
    public static Map<Integer, Map<String ,String>> sortByValues(final Map<Integer, Map<String ,String>> map) {
    	Comparator<Integer> valueComparator =  new Comparator<Integer>() {
    	    public int compare(Integer k1, Integer k2) {
    	        int compare = k2.compareTo(k1);
    	        if (compare == 0) return 1;
    	        else return compare;
    	    }
    	};
    	Map<Integer, Map<String ,String>> sortedByValues = new TreeMap<Integer, Map<String ,String>>(valueComparator);
    	sortedByValues.putAll(map);
    	return sortedByValues;
    }
    /**Adds a word combination to the given tree.*/
    private void addWordComboToTree(String translit,String chars,Integer frequency,Integer depth){
        IMETree curnode=root;
        for(int i=0;i<translit.length();i++){
        	String wordchar=translit.substring(i,i+1);
            IMETree res=curnode.containsChild(curnode.getWord()+wordchar);
            if(res==null){
                curnode=depthbuildWODictHandler(curnode, wordchar,chars,translit,frequency, (i == translit.length() - 1),++depth,translit.length());
                //System.out.println("Created Curnode: "+curnode.toString()+" "+curnode.getCachewords().toString());
            }else{
                curnode=res;
                //System.out.println("Found Curnode: "+curnode.toString()+" "+curnode.getCachewords().toString());
                if(!curnode.getCachewords().containsKey(frequency)){
                	curnode.getCachewords().put(frequency, new TreeMap<String,Set<String>>());
                }
                if(i+1==translit.length()){
                	curnode.setIsWord(true);
                	if(!curnode.getCachewords().get(frequency).containsKey(curnode.getWord())){
                		curnode.getCachewords().get(frequency).put(curnode.getWord(),new TreeSet<String>());
                	}
                	curnode.getCachewords().get(frequency).get(curnode.getWord()).add(chars);
                }else{
                	if(!curnode.getCachewords().get(frequency).containsKey(curnode.getWord())){
                		curnode.getCachewords().get(frequency).put(curnode.getWord(),new TreeSet<String>());
                	}
                	curnode.getCachewords().get(frequency).get(curnode.getWord()).add(chars+translit.substring(++depth,translit.length()));
                }
            }
        }
    }
    /**Builds the tree by adding word combinations and children recursively.*/
    public void buildTree(IMETree root,DictHandling dictHandler){
        IMETree curnode;
        for(String str:dictHandler.getTranscriptToWordDict().keySet()){
        	this.addWordComboToTree(str, dictHandler.getTranscriptToWordDict().get(str), dictHandler.matchWord(dictHandler.getTranscriptToWordDict().get(str)).getOccurances().intValue(), str.length());
        }
    }
    
    public IMETree depthbuildWODictHandler(IMETree node,String newchar,String chars,String translit,Integer frequency,Boolean isWord,Integer depth,Integer curmaxdepth){
        IMETree newnode=new IMETree();
        newnode.setIsWord(isWord);
        newnode.setWord(node.getWord()+newchar);
        if(isWord){
        	newnode.setFrequency(frequency);
            newnode.setChars(chars);
        	if(!newnode.getCachewords().containsKey(newnode.getFrequency())){
        		newnode.getCachewords().put(newnode.getFrequency(),new TreeMap<String,Set<String>>());
        	}
        	if(!newnode.getCachewords().get(frequency).containsKey(newnode.getWord())){
        		newnode.getCachewords().get(frequency).put(newnode.getWord(),new TreeSet<String>());
        	}
        	newnode.getCachewords().get(frequency).get(newnode.getWord()).add(newnode.getChars());

        }else{
        	newnode.setFrequency(frequency);
            newnode.setChars(chars);
        	if(!newnode.getCachewords().containsKey(newnode.getFrequency())){
        		newnode.getCachewords().put(newnode.getFrequency(),new TreeMap<String,Set<String>>());
        	}
        	if(!newnode.getCachewords().get(frequency).containsKey(newnode.getWord())){
        		newnode.getCachewords().get(frequency).put(newnode.getWord(),new TreeSet<String>());
        	}
        	newnode.getCachewords().get(frequency).get(newnode.getWord()).add(newnode.getChars()+translit.substring(depth,curmaxdepth));
        }
        node.addChild(newnode);
        return newnode;
    }
    /**Queries the tree for a specific node recusively, returning the map of candidates.*/
    private Map<Integer,Map<String,Set<String>>> query(IMETree node,String query,Map<Integer,Map<String,Set<String>>> result){
        if(query.isEmpty()){
            return node.getCachewords();
        }
        IMETree curnode=node.containsChild(node.getWord()+query.substring(0,1));
        if(curnode==null){
            return new TreeMap<>();
        }else{
            result=this.query(curnode, query.substring(1, query.length()), result);
        }
        return result;
    }
    /**Queries the tree for a certain depth and query string.*/
    public String query(String query,Integer num){
    	Map<Integer,Map<String,Set<String>>> result=this.query(this.root, query, new TreeMap<Integer,Map<String,Set<String>>>());
    	if(result.isEmpty()){
    		return "";
    	}
    	String ret="_callbacks_.loadWords([\"SUCCESS\",[[\""+query+"\",[";
    	System.out.println(result.toString());
    	//result=sortByValues(result);
    	int i=0;
    	Iterator<Integer> iter1=result.keySet().iterator();
    	System.out.println(result.toString());
    	for(;iter1.hasNext();){
    		Integer outerkey=iter1.next();
    		for(String middlekey:result.get(outerkey).keySet()){
    			for(String key:result.get(outerkey).get(middlekey)){
        			if(i>num){
        				break;
        			}
            		ret+="\""+key+"\",";
            		i++;
    			}

    		}
    	}
    	ret=ret.substring(0,ret.length()-1);
    	ret+="]]]])";
    	System.out.println("Queryresult for "+query+": "+ret);
        return ret;
    }
    /**Sets up a query in the tree for querying an array.*/
    public String[] queryToArray(String query,Integer num){
        Map<Integer,Map<String,Set<String>>> result=this.query(this.root, query, new TreeMap<Integer,Map<String,Set<String>>>());
        if(result.isEmpty()){
            return new String[0];
        }
        String[] ret=new String[num+1];
        System.out.println(result.toString());
        int i=0;
        Iterator<Integer> iter1=result.keySet().iterator();
        for(;iter1.hasNext();){
            Integer outerkey=iter1.next();
            for(String middlekey:result.get(outerkey).keySet()){
                for(String key:result.get(outerkey).get(middlekey)){
                    if(i>num){
                        break;
                    }
                    ret[i]=(i+1)+". "+key;
                    i++;
                }

            }
        }
        System.out.println("Queryresult for "+query+": "+ret);
        return ret;
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        switch(qName){
            case "word": this.addWordComboToTree(attributes.getValue("translit"),attributes.getValue("chars"),Integer.valueOf(attributes.getValue("freq")),0);break;
        }
    }
    /**Formats the tree to an xml representation.*/
    public String toXML(IMETree node) throws IOException, XMLStreamException {
        if(!node.hasChildren()) {
            return "";
        }
        StringWriter strwriter;
        XMLOutputFactory output3;
        XMLStreamWriter writer;
        strwriter=new StringWriter();
        output3 = XMLOutputFactory.newInstance();
        output3.setProperty(Tags.ESCAPECHARACTERS.toString(), false);
        writer = new IndentingXMLStreamWriter(output3.createXMLStreamWriter(strwriter));
        for(IMETree child:node.getChildren()){
            writer.writeStartElement(Tags.NODE);
            writer.writeCharacters(child.toXML());
            writer.writeCharacters(this.toXML(child));
            writer.writeEndElement();
            writer.writeCharacters("\n");
        }
        return strwriter.toString();
    }
    /**Exports the tree to xml.*/
    public void treeToXML(String filepath) throws IOException, XMLStreamException {
        StringWriter strwriter;
        XMLOutputFactory output3;
        XMLStreamWriter writer;
        strwriter=new StringWriter();
        output3 = XMLOutputFactory.newInstance();
        output3.setProperty(Tags.ESCAPECHARACTERS.toString(), false);
        writer = new IndentingXMLStreamWriter(output3.createXMLStreamWriter(new BufferedWriter(new FileWriter(new File("ime/test.xml")))));
        writer.writeStartDocument();
        writer.writeStartElement("data");
        writer.writeCharacters(this.toXML(root));
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.close();
    }
    
}
