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

import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;
import de.unifrankfurt.cs.acoli.akkad.dict.chars.cuneiform.CuneiChar;
import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.dict.pos.POSTagger;
import de.unifrankfurt.cs.acoli.akkad.dict.pos.util.POSDefinition;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Tags;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * POSTagger for Sumerian.
 */
public class HittitePOSTagger extends POSTagger {

    /**
     * Constructor for this class.
     */
    public HittitePOSTagger() {
        super(new TreeMap<String, Color>(), CharTypes.HITTITECHAR.getLocale());
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

    /**
     * Gets the postag of a given word.
     *
     * @param word    the word
     * @param handler the dicthandler to use
     * @return the list of Integers to use
     */
    public java.util.List<Integer> getPosTag(String word, final DictHandling handler) {
        java.util.List<Integer> result = new LinkedList<>();
        word = word.replaceAll("\\]", "").replaceAll("\\[", "");
        CuneiChar wordchar;
        int i = 1;
        for (Integer key : this.classifiers.keySet()) {
            if (this.classifiers.get(key).isEmpty()) {
                continue;
            }
            for (POSDefinition def : this.classifiers.get(key)) {
                java.util.List<Integer> res = def.performCheck(word);
                if (!res.isEmpty()) {
                    System.out.println("Key: "+key);
                    System.out.println("RGB: " + this.poscolors.get(def.getDesc()).getRGB());
                    System.out.println("POSDefinition: " + def.toString());
                    res.add(0, this.poscolors.get(def.getDesc()).getRGB());
                    result.addAll(res);
                    /*if (res.get(2).equals(word.length()) && res.get(1).equals(0)) {
                        return res;
                    }*/
                }
            }
        }
        return result;
    }

    @Override
    public java.util.List<POSDefinition> getPosTagDefs(String word, final DictHandling handler/*,final Boolean dummy*/) {
        java.util.List<POSDefinition> result = new LinkedList<>();
        word = word.replaceAll("\\]", "").replaceAll("\\[", "");
        for (Integer key : this.classifiers.keySet()) {
            if (this.classifiers.get(key).isEmpty()) {
                continue;
            }
            for (POSDefinition def : this.classifiers.get(key)) {
                java.util.List<Integer> res = def.performCheck(word);
                if (!res.isEmpty()) {
                    System.out.println("Key: "+key);
                    System.out.println("RGB: " + this.poscolors.get(def.getDesc()).getRGB());
                    System.out.println("POSDefinition: " + def.toString());
                    result.add(def);
                    if(res.size()>1)
                        def.currentword=word.substring(res.get(0),res.get(1));
                    if (res.get(1).equals(word.length()) && res.get(0).equals(0)) {
                        return result;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Generates an xml representation of the postags.
     *
     * @param path the path for saving the xml representation
     * @throws javax.xml.stream.XMLStreamException
     * @throws java.io.FileNotFoundException
     * @throws java.io.UnsupportedEncodingException
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
        for (java.util.List<POSDefinition> poss : this.classifiers.values()) {
            for (POSDefinition akkadchar : poss) {
                writer.writeCharacters(akkadchar.toXML() + System.lineSeparator());
            }
        }
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.close();
    }

    /**
     * Creates an xml String to export POSTag information.
     * @param translittext the text to export
     * @return the POSTagged XML text
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
            java.util.List<String> revised = Arrays.asList(translittext.split("\n"));
            for (String revi : revised) {
                String[] revisedwords = revi.split(" \\[");
                for (int w = 0; w < revisedwords.length;w++ ) {
                    String word = revisedwords[w].trim();
                    System.out.println("Word: " + word);
                    java.util.List<POSDefinition> result = this.getPosTagDefs(word, CharTypes.AKKADIAN.getCorpusHandlerAPI().getUtilDictHandler());
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
}

