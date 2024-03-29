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

package de.unifrankfurt.cs.acoli.akkad.dict.utils;

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;
import de.unifrankfurt.cs.acoli.akkad.util.Tuple;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Tags;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class symbolising a following word and a preceding word/character.
 */
public class Preceding implements Comparable<Following>{
    /**Map of the following occurrances.*/
    private Tuple<Double,Double> following;
    /**The following String of this preceding character.*/
    private String followingstr;
    /**Indicates if this preceding char is a stop char.*/
    private Boolean isStopChar;
    /**Map of preceding Strings with border/non border frequencies.*/
    private Map<String,Tuple<Double,Double>> preceding;
    /**Constructor for this class.*/
    public Preceding(){
        this.preceding=new TreeMap<>();
        this.following=new Tuple<Double,Double>(0.,0.);
    }
    /**Constructor for this class.*/
    public Preceding(Tuple<Double,Double> following,String followingstr,String preceding,Boolean isStopChar,Boolean separator){
        this.preceding=new TreeMap<>();
        this.following=following;
        if(separator){
            this.preceding.put(preceding,new Tuple<Double, Double>(0.,1.));
        }else{
            this.preceding.put(preceding,new Tuple<Double, Double>(1.,0.));
        }
        this.isStopChar=isStopChar;
        this.followingstr=followingstr;
    }
    /**Adds a preceding char or word to this class.*/
    public void addPreceding(final String precedingcollector, final Double precedingtemp, final Double precedingtemp2) {
        this.preceding.put(precedingcollector,new Tuple<Double, Double>(precedingtemp,precedingtemp2));
    }

    @Override
    public int compareTo(final Following following) {
        return 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Preceding preceding1 = (Preceding) o;

        if (following != null ? !following.equals(preceding1.following) : preceding1.following != null) return false;
        if (followingstr != null ? !followingstr.equals(preceding1.followingstr) : preceding1.followingstr != null)
            return false;
        if (isStopChar != null ? !isStopChar.equals(preceding1.isStopChar) : preceding1.isStopChar != null)
            return false;
        if (preceding != null ? !preceding.equals(preceding1.preceding) : preceding1.preceding != null) return false;

        return true;
    }

    public Tuple<Double,Double> getFollowing() {
        return following;
    }

    public String getFollowingstr() {
        return followingstr;
    }

    public void setFollowingstr(final String followingstr) {
        this.followingstr = followingstr;
    }

    public Boolean getIsStopChar() {
        return isStopChar;
    }

    public void setIsStopChar(final Boolean isStopChar) {
        this.isStopChar = isStopChar;
    }

    public Map<String, Tuple<Double,Double>> getPreceding() {
        return preceding;
    }

    public void setPreceding(final Map<String, Tuple<Double,Double>> preceding) {
        this.preceding = preceding;
    }

    @Override
    public int hashCode() {
        int result = following != null ? following.hashCode() : 0;
        result = 31 * result + (followingstr != null ? followingstr.hashCode() : 0);
        result = 31 * result + (preceding != null ? preceding.hashCode() : 0);
        result = 31 * result + (isStopChar != null ? isStopChar.hashCode() : 0);
        return result;
    }
    /**Increments the following word frequency.*/
    public void incFollowing(Boolean border){
        if(border){
            this.following.setTwo(this.following.getTwo()+1);
        }else{
            this.following.setOne(this.following.getOne() + 1);
        }
    }

    public void setFollowing(final Double following,Double following2) {
        this.following = new Tuple<Double,Double>(following,following2);
    }

    @Override
    public String toString() {
        return this.preceding+" "+this.following;
    }
    /**Transforms this preceding char to an xml export string.*/
    public String toXML(){
        StringWriter strwriter=new StringWriter();
        XMLOutputFactory output3 = XMLOutputFactory.newInstance();
        output3.setProperty(Tags.ESCAPECHARACTERS.toString(), false);
        XMLStreamWriter writer;
        try {
            writer = new IndentingXMLStreamWriter(output3.createXMLStreamWriter(strwriter));
            writer.writeStartElement(Tags.PRECEDING);
            writer.writeAttribute(Tags.ABSOCC.toString(), this.following.getOne().toString());
            writer.writeAttribute(Tags.ABSBD.toString(), this.following.getTwo().toString());
            for(String key:this.preceding.keySet()){
                writer.writeStartElement(Tags.FOLLOWING.toString());
                writer.writeAttribute(Tags.FOLLOWING.toString(), key);
                writer.writeAttribute(Tags.ABSOCC.toString(),this.preceding.get(key).getOne().toString());
                writer.writeAttribute(Tags.ABSBD.toString(),this.preceding.get(key).getTwo().toString());
                writer.writeEndElement();
            }
            writer.writeCharacters("\n"+this.followingstr);
            writer.writeEndElement();
        } catch (XMLStreamException | FactoryConfigurationError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return strwriter.toString();
    }

}

