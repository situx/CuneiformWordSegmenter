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

package de.unifrankfurt.cs.acoli.akkad.dict.pos.cuneiform;

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;
import de.unifrankfurt.cs.acoli.akkad.dict.chars.cuneiform.CuneiChar;
import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.dict.pos.POSTagger;
import de.unifrankfurt.cs.acoli.akkad.dict.pos.util.GroupDefinition;
import de.unifrankfurt.cs.acoli.akkad.dict.pos.util.POSDefinition;
import de.unifrankfurt.cs.acoli.akkad.dict.translator.Translator;
import de.unifrankfurt.cs.acoli.akkad.main.gui.util.POSInBox;
import de.unifrankfurt.cs.acoli.akkad.util.Tuple;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.pos.POSTags;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Tags;
import org.abego.treelayout.TreeForTreeLayout;
import org.abego.treelayout.util.DefaultTreeForTreeLayout;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Akkadian POSTagger.
 */
public class AkkadPOSTagger extends POSTagger {



    /**
     * Constructor for Akkadian POSTagger.
     */
    public AkkadPOSTagger() {
        super(new TreeMap<String, Color>(), CharTypes.AKKADIAN.getLocale());
    }

    /**
     * Main method for testing.
     *
     * @param args
     * @throws IOException
     * @throws XMLStreamException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public static void main(String[] args) throws IOException, XMLStreamException, ParserConfigurationException, SAXException {
        AkkadPOSTagger tagger = new AkkadPOSTagger();
        tagger.importFromXML("src/posdata.xml");
        //tagger.toXML("test.xml");
    }

    public void buildTreeRecursive(POSInBox box,DefaultTreeForTreeLayout<POSInBox> tree){
        for(POSInBox b:box.children){
            try {
             tree.addChild(box,b);
            }catch(IllegalArgumentException e){

            }
             buildTreeRecursive(b,tree);
        }
    }

    /**
     * Gets the nth occurance of char c in string str.
     *
     * @param str the string to search in
     * @param c   the char to search
     * @param n   the occurance
     * @return the position
     */
    public int getNthOccurrence(String str, char c, int n) {
        int pos = str.indexOf(c, 0);
        while (n-- > 0 && pos != -1)
            pos = str.indexOf(c, pos + 1);
        return pos;
    }

    public List<Integer> getNumberOfWordsPerLine(String[] line){
        List<Integer> result=new LinkedList<>();
        int total=0;
        for(String lin:line){
            System.out.println("Line: "+lin);
            total+=(StringUtils.countMatches(lin.trim()," ")+1);
            result.add(total);
        }
        return result;
    }

    /**
     * Gets the postag of a given word.
     *
     * @param word    the word
     * @param handler the dicthandler to use
     * @return the list of Integers to use
     */
    public java.util.List<Integer> getPosTag(String word, final DictHandling handler) {
        List<Integer> result = new LinkedList<>();
        Boolean onlyVerbmatch=true;
        POSDefinition tempmatch=new POSDefinition("","","","","","UNKNOWN","","",new TreeMap<Integer,List<GroupDefinition>>()),
                firstmatch=null;
        word = word.replaceAll("\\]", "").replaceAll("\\[", "");
        CuneiChar wordchar;
        int i = 1;
        for (Integer key : this.classifiers.keySet()) {
            if (this.classifiers.get(key).isEmpty()) {
                continue;
            }
            for (POSDefinition def : this.classifiers.get(key)) {
                List<Integer> res = def.performCheck(word);
                if (!res.isEmpty()) {
                    System.out.println("RGB: " + this.poscolors.get(def.getDesc()).getRGB());
                    System.out.println("POSDefinition: " + def.toString());
                    if(firstmatch==null){
                        firstmatch=def;
                    }
                    for(int j=0;j<res.size();j+=3){
                        res.add(j, this.poscolors.get(def.getDesc()).getRGB());
                    }
                    result.addAll(res);
                    tempmatch=def;
                    /*res.add(0, this.poscolors.get(def.getTag()).getRGB());
                    result.addAll(res); */
                    if((def.getPosTag()!=POSTags.VERB && def.getPosTag()!=POSTags.NOUNORADJ) && onlyVerbmatch){
                       onlyVerbmatch=false;
                    }
                   /* if (res.get(2).equals(word.length()) && res.get(1).equals(0)) {
                       return result;
                    }
                    */
                }
            }
        }
        System.out.println("IsAllUpperCase? "+lastmatchedword+" "+Translator.isAllUpperCaseOrNumberOrDelim(lastmatchedword));
        if(result.isEmpty() || onlyVerbmatch && lastmatched!=null && lastmatched.getTag().equals("noun")){
            boolean matched=false;
            for(POSDefinition def:this.classifiers.get(this.orderToPOS.get("DET"))){
                /*if(!lastmatchedword.endsWith("-")){
                    lastmatchedword+="-";
                }*/
                System.out.println("Currentregex: "+def.getRegex().toString());
                if(def.getRegex().matcher(lastmatchedword).find()){
                    matched=true;
                    break;
                }
            }
            System.out.println("Matches DetString?: "+matched);
            if((!matched && Translator.isAllUpperCaseOrNumberOrDelim(lastmatchedword)) || (matched && Translator.isAllUpperCaseOrNumberOrDelim(lastmatchedword) && result.isEmpty())){
                result.clear();
                result.add(0, this.poscolors.get("namedentity").getRGB());
                result.add(0);
                result.add(word.length());
                POSDefinition posdef=new POSDefinition("","","","","","namedentity","","",new TreeMap<Integer,List<GroupDefinition>>());
                posdef.setPosTag(POSTags.NAMEDENTITY);
                posdef.setClassification("NE");
                posdef.currentword=word;
                posdef.setValue(word);
                posdef.setEquals("");
                posdef.setRegex(Pattern.compile("(.*)"));
                lastmatched=posdef;
                lastmatchedword=word;
            }
            firstmatch=lastmatched;
        }
        lastmatched=tempmatch;
        lastmatchedword=word;
        this.classificationResult.put(this.wordcounter++,new Tuple<String, POSDefinition>(word,firstmatch));
        return result;
    }

    @Override
    public List<POSDefinition> getPosTagDefs(String word, final DictHandling handler/*,final Boolean dummy*/) {
        List<POSDefinition> result = new LinkedList<>();
        Boolean onlyVerbmatch=true,namedentity=false;
        POSDefinition tempmatch=new POSDefinition("","","","","","UNKNOWN","","",new TreeMap<Integer,List<GroupDefinition>>()),
                firstmatch=null;
        word = word.replaceAll("\\]", "").replaceAll("\\[", "");
        for (Integer key : this.classifiers.keySet()) {
            if (this.classifiers.get(key).isEmpty()) {
                continue;
            }
            for (POSDefinition def : this.classifiers.get(key)) {
                List<Integer> res = def.performCheck(word);
                if (!res.isEmpty()) {
                    System.out.println("RGB: " + this.poscolors.get(def.getDesc()).getRGB());
                    System.out.println("POSDefinition: " + def.toString());
                    if(firstmatch==null){
                        firstmatch=def;
                    }
                    //if((!namedentity && def.getPosTag()==POSTags.NOUN) || def.getPosTag()!=POSTags.NOUN){
                        tempmatch=def;
                        result.add(def);
                    //}

                    if((def.getPosTag()!=POSTags.VERB && def.getPosTag()!=POSTags.NOUNORADJ)&& onlyVerbmatch){
                        onlyVerbmatch=false;
                    }
                    //if(def.getPosTag()==POSTags.NAMEDENTITY){
                    //    namedentity=true;
                    //}
                    if(res.size()>1)
                        def.currentword=word.substring(res.get(0),res.get(1));
                   /* if (res.get(2).equals(word.length()) && res.get(1).equals(0)) {
                       return result;
                    }
                    */
                }
            }
        }
        System.out.println("IsAllUpperCase? "+lastmatchedword+" "+Translator.isAllUpperCaseOrNumberOrDelim(lastmatchedword));

        if((result.isEmpty() || onlyVerbmatch) && lastmatched!=null && lastmatched.getTag().equals("noun") && Translator.isAllUpperCaseOrNumberOrDelim(lastmatchedword)){
            result.clear();
            POSDefinition posdef=new POSDefinition("","","","","","namedentity","","",new TreeMap<Integer,List<GroupDefinition>>());
            posdef.setPosTag(POSTags.NAMEDENTITY);
            posdef.setClassification("NE");
            posdef.currentword=word;
            posdef.setValue(word);
            posdef.setEquals("");
            posdef.setRegex(Pattern.compile("(.*)"));
           result.add(posdef);
            tempmatch=posdef;
            firstmatch=tempmatch;
            lastmatchedword=word;
        }
        //if(!result.isEmpty()){
            lastmatched=tempmatch;
            lastmatchedword=word;
        this.classificationResult.put(this.wordcounter++,new Tuple<String, POSDefinition>(word,firstmatch));
        //}
        return result;
    }

    /**
     * Detects phrases within a sentence to build a parse tree.
     */
    public List<POSInBox> phraseDetector(final List<POSInBox> posNodes){
        List<POSInBox> headlist=new LinkedList<>();
        List<POSInBox> wordlist=new LinkedList<>();
        for(POSInBox posbox:posNodes){
            System.out.println("Headlist: "+headlist);
            System.out.println("Wordlist: "+wordlist);
            System.out.println("Dependencies: "+dependencies);
            wordlist.add(0,posbox);
            List<POSInBox> toremove=new LinkedList<>();
            List<POSInBox> toadd=new LinkedList<>();
            for(POSInBox head:headlist){
                System.out.println(posbox.posdef.getTag()+" "+head.posdef.getTag());
                if(this.dependencies.get(head.posdef.getTag()).contains(posbox.posdef.getTag())){
                    System.out.println("YES! Add "+head.posdef.getTag()+" to "+posbox.posdef.getTag());
                    posbox.children.add(head);
                    if(!headlist.contains(posbox)){
                        toadd.add(posbox);
                    }
                    toremove.add(head);
                }
            }
            headlist.addAll(toadd);
            for(POSInBox rem:toremove){
                headlist.remove(rem);
            }
            boolean wasfound=false;
            for(POSInBox word:wordlist){
                System.out.println(posbox.posdef.getTag()+" "+word.posdef.getTag());
                if(this.dependencies.get(posbox.posdef.getTag()).contains(word.posdef.getTag())){
                    System.out.println("YES2! Add "+word.posdef.getTag()+" to "+posbox.posdef.getTag());
                    word.children.add(posbox);
                    wasfound=true;
                    break;
                }
            }
            if(!wasfound){
                headlist.add(posbox);
            }
        }

        return headlist;
    }

    /**
     * Builds a tree for POSTagging in order to create grammars for context.
     * @param lineNumber the linenumber to consider
     * @return the POSTree
     */
    public TreeForTreeLayout<POSInBox> posTreeBuilder(final Integer lineNumber){
        POSInBox root = new POSInBox("root",new POSDefinition("","","","","","UNKNOWN","","",new TreeMap<Integer,List<GroupDefinition>>()), 100, 20);
        DefaultTreeForTreeLayout<POSInBox> tree = new DefaultTreeForTreeLayout<>(
                root);
        //Generate leafs of the tree
        List<POSInBox> leafs=new LinkedList<>();
        String sentence=this.sentences.get(lineNumber);
        for(int i=this.sentencesByWordPosition.get(lineNumber).getOne();i<=this.sentencesByWordPosition.get(lineNumber).getTwo();i++){
            leafs.add(new POSInBox(POSDefinition.splitString(this.classificationResult.get(i).getOne()+"("+this.classificationResult.get(i).getTwo().getTag()+")",System.lineSeparator(),210),this.classificationResult.get(i).getTwo(),210,36));
        }
        List<POSInBox> nextstage=this.phraseDetector(leafs);
        for(POSInBox posInBox:nextstage){
            try {
            tree.addChild(root,posInBox);
        }catch(IllegalArgumentException e){

        }
            buildTreeRecursive(posInBox,tree);
        }

        return tree;
    }

    /**
     * Detects sentences in a String of words according to given rules.
     * @param words the words for detecting
     * @param lines the corresponding lines
     * @return A map of sentence number to words
     */
    public Map<Integer,String> sentenceDetector(String[] words,String[] lines){
        List<Integer> linecount=this.getNumberOfWordsPerLine(lines);
        int beginposition=0;
        System.out.println("Linecount: "+linecount);
        this.sentences=new TreeMap<Integer,String>();
        this.sentencesByWordPosition=new TreeMap<Integer,Tuple<Integer,Integer>>();
        String collectwords="";
        for(int i=0;i<words.length;i++){
            System.out.print(words[i]+",");
        }
        System.out.println(System.lineSeparator());
        System.out.println("Words.length(): "+words.length);
        System.out.println("Wordcounter: "+this.wordcounter);
        int currentline=1,beginline=1,lastpos=0;
        for(Integer position:this.classificationResult.keySet()){
            System.out.println("Position: "+position+" Currentline: "+currentline);
            if(position>linecount.get(currentline)){
                currentline++;
            }
            switch (classificationResult.get(position).getTwo().getPosTag()) {
                case VERB:  if(classificationResult.size()>position+1){
                    POSTags next=classificationResult.get(position+1).getTwo().getPosTag();
                    collectwords+=words[position]+" ";
                    switch (next){
                        case CONJUNCTION: if(!words[position+1].replace("[","").replace("]","").equals("u3")){
                            for(int i=beginline;i<=currentline+1;i++){
                                System.out.println("Adding: "+i+" "+collectwords);
                                sentences.put(i,collectwords+".");
                                this.sentencesByWordPosition.put(i,new Tuple<Integer, Integer>(beginposition,position));
                            }
                            beginposition=position;
                            beginline=currentline+2;
                            collectwords="";
                        }
                        break;
                        default:
                            for(int i=beginline;i<=currentline+1;i++){
                                sentences.put(i,collectwords+".");
                                this.sentencesByWordPosition.put(i,new Tuple<Integer, Integer>(beginposition,position));
                                System.out.println("Adding: "+i+" "+collectwords);
                            }
                            beginposition=position;
                            beginline=currentline+2;
                            collectwords="";
                    }
                }break;
                default:
                    collectwords+=words[position]+" ";
            }
            lastpos=position;
        }
        for(int i=beginline;i<=currentline+1;i++){
            sentences.put(i,collectwords+".");
            this.sentencesByWordPosition.put(i,new Tuple<Integer, Integer>(beginposition,lastpos));
            System.out.println("Adding: "+i+" "+collectwords);
        }
        System.out.println("Sentences: "+sentences);
        return sentences;
    }

    /**
     * Exports a POSTagged text to an xml format-
     * @param translittext the text to consider
     * @return  the String of the xml to export
     */
    public String textToPosTagXML(String translittext) {
        String strresult = "";
        StringWriter strwriter = new StringWriter();
        XMLOutputFactory output3 = XMLOutputFactory.newInstance();
        output3.setProperty(Tags.ESCAPECHARACTERS.toString(), false);
        XMLStreamWriter writer;
        try {
            writer = new IndentingXMLStreamWriter(output3.createXMLStreamWriter(strwriter));
            writer.writeStartDocument();
            writer.writeStartElement(Tags.TEXT);
            List<String> revised = Arrays.asList(translittext.split("\n"));
            for (String revi : revised) {
                String[] revisedwords = revi.split(" \\[");
                for (int w = 0; w < revisedwords.length;w++ ) {
                    String word = revisedwords[w].trim();
                    System.out.println("Word: " + word);
                    List<POSDefinition> result = this.getPosTagDefs(word, CharTypes.AKKADIAN.getCorpusHandlerAPI().getUtilDictHandler());
                    writer.writeStartElement(Tags.WORD);
                    writer.writeAttribute(Tags.POSTAG, (result==null||result.isEmpty() || result.get(0).getTag()==null)?" ":result.get(0).getTag());
                    writer.writeAttribute(Tags.RULE, (result==null||result.isEmpty() || result.get(0).getRegex()==null)?" ":result.get(0).getRegex().toString());
                    writer.writeAttribute(Tags.POSCASE, (result==null||result.isEmpty() || result.get(0).getClassification()==null)?" ":result.get(0).getClassification());
                    writer.writeCharacters(word.replace("[","").replace("]",""));
                    writer.writeEndElement();
                    writer.writeCharacters("\n");
                }
            }
            writer.writeEndElement();
            writer.writeEndDocument();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        return strwriter.toString();
    }

    /**
     * Generates an xml representation of the postags.
     *
     * @param path the path for saving the xml representation
     * @throws XMLStreamException
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public void toXML(String path) throws XMLStreamException, FileNotFoundException, UnsupportedEncodingException {
        XMLOutputFactory output = XMLOutputFactory.newInstance();
        output.setProperty(Tags.ESCAPECHARACTERS.toString(), false);
        OutputStreamWriter outwriter = new OutputStreamWriter(new FileOutputStream(path), Tags.UTF8.toString());
        XMLStreamWriter writer = new IndentingXMLStreamWriter(output.createXMLStreamWriter(outwriter));
        writer.writeStartDocument(Tags.UTF8.toString(), Tags.XMLVERSION.toString());
        //writer.writeCharacters("\n");
        writer.writeStartElement(Tags.DATA.toString());
        writer.writeCharacters("\n");
        for (String poscol : this.poscolors.keySet()) {
            writer.writeStartElement("tagcolor");
            writer.writeAttribute("tag", poscol);
            writer.writeAttribute("pos", poscol);
            writer.writeAttribute("color", this.poscolors.get(poscol).toString());
            writer.writeEndElement();
        }
        for (List<POSDefinition> poss : this.classifiers.values()) {
            for (POSDefinition akkadchar : poss) {
                writer.writeCharacters(akkadchar.toXML() + System.lineSeparator());
            }
        }
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.close();
    }

    public Set<String> verbGenerator(String verb,String root){
        Set<String> result=new TreeSet<>();
        String root1=root.substring(0,1),root2=root.substring(1,2),root3=root.substring(2,3);
        if(root.startsWith("n")){

        }else{
           result.add("a-"+root1+root2+"u"+root3);
           result.add("ni-"+root1+root2+"u"+root3);
           result.add("ta-"+root1+root2+"u"+root3);
           result.add("ta-"+root1+root2+"u"+root3+"-i2");
           result.add("ta-"+root1+root2+"u"+root3+"-a2");
           result.add("i-"+root1+root2+"u"+root3);
           result.add("i-"+root1+root2+"u"+root3+"-u2");
           result.add("i-"+root1+root2+"u"+root3+"-a2");
            result.add("a-"+root1+"a"+root2+root2+"u"+root3);
            result.add("ni-"+root1+"a"+root2+root2+"u"+root3);
            result.add("ta-"+root1+"a"+root2+root2+"u"+root3);
            result.add("ta-"+root1+"a"+root2+root2+"u"+root3+"-i2");
            result.add("ta-"+root1+"a"+root2+root2+"u"+root3+"-a2");
            result.add("i-"+root1+"a"+root2+root2+"u"+root3);
            result.add("i-"+root1+"a"+root2+root2+"u"+root3+"-u2");
            result.add("i-"+root1+"a"+root2+root2+"u"+root3+"-a2");
           result.add("a-"+root1+"-ta-"+root2+"u"+root3);
           result.add("ni-"+root1+"-ta-"+root2+"u"+root3);
           result.add("ta-"+root1+"-ta-"+root2+"u"+root3);
           result.add("ta-"+root1+"-ta-"+root2+"u"+root3+"-i2");
           result.add("ta-"+root1+"-ta-"+root2+"u"+root3+"-a2");
           result.add("i-"+root1+"-ta-"+root2+"u"+root3);
           result.add("i-"+root1+"-ta-"+root2+"u"+root3+"-u2");
           result.add("i-"+root1+"-ta-"+root2+"u"+root3+"-a2");
           result.add("u-"+root1+"a"+root2+root2+"i"+root3);
           result.add("nu-"+root1+"a"+root2+root2+"i"+root3);
           result.add("tu-"+root1+"a"+root2+root2+"i"+root3);
           result.add("tu-"+root1+"a"+root2+root2+"i"+root3+"-i2");
           result.add("tu-"+root1+"a"+root2+root2+"i"+root3+"-a2");
           result.add("u-"+root1+"a"+root2+root2+"i"+root3+"-u2");
           result.add("u-"+root1+"a"+root2+root2+"i"+root3+"-a2");
        }
        return result;
    }
}
