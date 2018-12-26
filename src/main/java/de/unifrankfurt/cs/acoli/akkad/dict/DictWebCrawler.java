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

package de.unifrankfurt.cs.acoli.akkad.dict;

import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.cuneiform.AkkadDictHandler;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Tags;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * WebCrawler for crawling a dictionary corpusimport file.
 * User: timo
 * Date: 13.10.13
 * Time: 17:15
 */
public class DictWebCrawler {
    /**URL Prefixes for DictWebCrawler.*/
    public static final String urlprefix="http://www.premiumwanadoo.com/cuneiform.languages/dictionary/";
    public static final String urlprefix2="http://www.assyrianlanguages.org/akkadian/";
    public static final String urlprefix3="http://psd.museum.upenn.edu/epsd/";
    HttpClient httpclient;
    String url;

    /**Constructor for this class.*/
    public DictWebCrawler(){
         this.httpclient=new DefaultHttpClient();
    }

    /**
     * Testing main method.
     * @param args
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XMLStreamException
     */
public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, XMLStreamException {
        DictWebCrawler crawler=new DictWebCrawler();
    AkkadDictHandler akkad=new AkkadDictHandler(CharTypes.AKKADIAN.getStopchars());
    akkad.parseDictFile(new File("akkad.xml"));
        crawler.crawlAssDict(akkad,"http://www.premiumwanadoo.com/cuneiform.languages/dictionary/list.php");
    }

    /**
     * Starts the crawling method for the given url.
     * @param dict the dicthandler to fill
     * @param urlstring the String of the url
     * @throws IOException on error
     */
    public void crawl(final DictHandling dict, final String urlstring) throws IOException {
        HttpGet method = new HttpGet(urlstring);
        HttpResponse response=this.httpclient.execute(method);
        Source htmlparser = new Source(response.getEntity().getContent());
        List<StartTag> elems=htmlparser.getAllStartTags("td");
        List<String> urls=new LinkedList<String>();
        for(StartTag elem:elems){
                if(!elem.getElement().getChildElements().isEmpty())  {
                    System.out.println(urlprefix+elem.getElement().getChildElements().get(0).getAttributeValue("href"));
                    urls.add(urlprefix+elem.getElement().getChildElements().get(0).getAttributeValue("href"));
                }
        }
        System.out.println(urls.size());
        for(String url:urls){
            htmlparser=new Source(this.httpclient.execute(new HttpGet(url)).getEntity().getContent());
            StartTag tag=htmlparser.getFirstStartTag("font");
            StartTag tag2=htmlparser.getFirstStartTag("img");
            System.out.println(tag.getElement().getContent().getTextExtractor().toString());
            System.out.println(dict.matchWord(tag.getElement().getContent().getTextExtractor().toString()));
            if(tag2!=null){
                System.out.println(tag2.getElement().getAttributeValue("src").substring(tag2.getElement().getAttributeValue("src").indexOf("=")+1));
            }
        }
        System.out.println();

    }

    /**
     * Starts the crawling method for the given url.
     * @param dict the dicthandler to fill
     * @param urlstring the String of the url
     * @throws IOException on error
     */
    public void crawlAssDict(final DictHandling dict, final String urlstring) throws IOException {
        char c='A';
        List<String> urls=new LinkedList<String>();
        Source htmlparser;
        while(c<'Z'+1){
            HttpGet method = new HttpGet("http://psd.museum.upenn.edu/epsd/signnames-toc-"+c+++".html");
            HttpResponse response=this.httpclient.execute(method);
            htmlparser = new Source(response.getEntity().getContent());
            List<StartTag> elems=htmlparser.getAllStartTags("a");
            for(StartTag elem:elems){
                if(!elem.getElement().getChildElements().isEmpty())  {
                    System.out.println(elem.getAttributeValue("href"));
                    if(elem.getAttributeValue("href").contains("\'")){
                        System.out.println(urlprefix3+elem.getAttributeValue("href").substring(elem.getAttributeValue("href").indexOf("\'")+1,elem.getAttributeValue("href").lastIndexOf("\'")));
                        urls.add(urlprefix3+elem.getAttributeValue("href").substring(elem.getAttributeValue("href").indexOf("\'")+1,elem.getAttributeValue("href").lastIndexOf("\'")));

                    }
                }
            }
        }
        XMLOutputFactory output = XMLOutputFactory.newInstance();
        output.setProperty(Tags.ESCAPECHARACTERS.toString(), false);
        OutputStreamWriter outwriter=new OutputStreamWriter(new FileOutputStream("newwordssumerian.xml",false), Tags.UTF8.toString());
        try{
            XMLStreamWriter writer = output.createXMLStreamWriter(outwriter);
            writer.writeStartDocument();
            writer.writeStartElement("translations");
            System.out.println(urls.size());
            int i=0;
            for(String url:urls){

                System.out.println("URL: "+url);

                htmlparser=new Source(this.httpclient.execute(new HttpGet(url)).getEntity().getContent());
                List<StartTag> tag=htmlparser.getAllStartTags("h3");
                List<StartTag> tag2=htmlparser.getAllStartTags("h1");
                List<StartTag> tag3=htmlparser.getAllStartTags("span");
                for(StartTag start:tag){
                    writer.writeStartElement(Tags.TRANSLATION);
                    writer.writeAttribute("origlocale",Tags.AKKADIAN.toString());
                    writer.writeAttribute("destlocale",Locale.ENGLISH.toString());
                    System.out.println("Meaning: "+tag2.get(0).getElement().getChildElements().get(1).getTextExtractor().toString());
                    writer.writeAttribute("meaning",tag2.get(0).getElement().getChildElements().get(1).getTextExtractor().toString());
                    System.out.println("Origvalue: "+tag3.get(5).getElement().getTextExtractor().toString());
                    writer.writeAttribute("origvalue",tag3.get(5).getElement().getTextExtractor().toString());
                    System.out.println("DestValue: "+start.getElement().getTextExtractor().toString().substring(start.getTextExtractor().toString().indexOf('.')+1));
                    writer.writeAttribute("destvalue",start.getElement().getTextExtractor().toString().substring(start.getTextExtractor().toString().indexOf('.')+1));
                    writer.writeEndElement();
                }

                writer.flush();
                outwriter.write("\n");
                i++;

            }
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.flush();
            writer.close();
            /*BufferedWriter fwriter=new BufferedWriter(new FileWriter(new File("newwords.xml")));
            fwriter.write(outwriter.toString());
            writer.close();*/
        } catch (FactoryConfigurationError | Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("FINISHED");

    }

        /**
     * Starts the crawling method for the given url.
     * @param dict the dicthandler to fill
     * @param urlstring the String of the url
     * @throws IOException on error
     */
    public void crawlUniLP(final DictHandling dict, final String urlstring) throws IOException {
        HttpGet method = new HttpGet("http://www.assyrianlanguages.org/akkadian/list.php");
        HttpResponse response=this.httpclient.execute(method);
        Source htmlparser = new Source(response.getEntity().getContent());
        List<StartTag> elems=htmlparser.getFirstElement("table").getAllStartTags("td");
        List<String> urls=new LinkedList<String>();
        for(StartTag elem:elems){
            if(!elem.getElement().getChildElements().isEmpty())  {
                System.out.println(urlprefix2+elem.getElement().getChildElements().get(0).getAttributeValue("href"));
                urls.add(urlprefix2+elem.getElement().getChildElements().get(0).getAttributeValue("href"));
            }
        }
        XMLOutputFactory output = XMLOutputFactory.newInstance();
        output.setProperty(Tags.ESCAPECHARACTERS.toString(), false);
        OutputStreamWriter outwriter=new OutputStreamWriter(new FileOutputStream("newwords.xml",true), Tags.UTF8.toString());
        try{
        XMLStreamWriter writer = output.createXMLStreamWriter(outwriter);
            writer.writeStartDocument();
            writer.writeStartElement("translations");
        System.out.println(urls.size());
            int i=0;
        for(String url:urls){
            if(i<8588){
                i++;
                continue;
            }
            writer.writeStartElement(Tags.TRANSLATION);
            System.out.println("URL: "+url);

            htmlparser=new Source(this.httpclient.execute(new HttpGet(url)).getEntity().getContent());
            StartTag tag=htmlparser.getFirstStartTag("font");
            StartTag tag2=htmlparser.getAllStartTags("p").get(2);
            //System.out.println(tag.getElement().getContent().getTextExtractor().toString());
            //System.out.println(dict.matchWord(tag.getElement().getContent().getTextExtractor().toString()));
            if(tag2!=null){
                writer.writeAttribute("origlocale",Tags.AKKADIAN.toString());
                writer.writeAttribute("destlocale",Locale.ENGLISH.toString());
                writer.writeAttribute("origvalue",tag.getElement().getContent().getTextExtractor().toString());
                writer.writeAttribute("destvalue",tag2.getElement().getContent().getTextExtractor().toString());

                System.out.println(tag.getElement().getContent().getTextExtractor().toString()+" - "+tag2.getElement().getTextExtractor().toString());
            }
            writer.writeEndElement();
            writer.flush();
            outwriter.write("\n");
            i++;

        }

        writer.writeEndDocument();
            writer.flush();
            /*BufferedWriter fwriter=new BufferedWriter(new FileWriter(new File("newwords.xml")));
            fwriter.write(outwriter.toString());
            writer.close();*/
        } catch (FactoryConfigurationError | Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("FINISHED");

    }
/**
     * Starts the crawling method for the given url.
     * @param dict the dicthandler to fill
     * @param urlstring the String of the url
     * @throws IOException on error
     */
   /* public void crawlUniLP(final DictHandling dict, final String urlstring) throws IOException {
        HttpGet method = new HttpGet("http://www.assyrianlanguages.org/akkadian/list.php");
        HttpResponse response=this.httpclient.execute(method);
        Source htmlparser = new Source(response.getEntity().getContent());
        List<StartTag> elems=htmlparser.getFirstElement("table").getAllStartTags("td");
        List<String> urls=new LinkedList<String>();
        for(StartTag elem:elems){
            if(!elem.getElement().getChildElements().isEmpty())  {
                System.out.println(urlprefix2+elem.getElement().getChildElements().get(0).getAttributeValue("href"));
                urls.add(urlprefix2+elem.getElement().getChildElements().get(0).getAttributeValue("href"));
            }
        }
        XMLOutputFactory output = XMLOutputFactory.newInstance();
        output.setProperty(Tags.ESCAPECHARACTERS.toString(), false);
        OutputStreamWriter outwriter=new OutputStreamWriter(new FileOutputStream("newwords.xml",true), Tags.UTF8.toString());
        try{
        XMLStreamWriter writer = output.createXMLStreamWriter(outwriter);
            writer.writeStartDocument();
            writer.writeStartElement("translations");
        System.out.println(urls.size());
            int i=0;
        for(String url:urls){
            if(i<8588){
                i++;
                continue;
            }
            writer.writeStartElement(Tags.TRANSLATION);
            System.out.println("URL: "+url);

            htmlparser=new Source(this.httpclient.execute(new HttpGet(url)).getEntity().getContent());
            StartTag tag=htmlparser.getFirstStartTag("font");
            StartTag tag2=htmlparser.getAllStartTags("p").get(2);
            //System.out.println(tag.getElement().getContent().getTextExtractor().toString());
            //System.out.println(dict.matchWord(tag.getElement().getContent().getTextExtractor().toString()));
            if(tag2!=null){
                writer.writeAttribute("origlocale",Tags.AKKADIAN.toString());
                writer.writeAttribute("destlocale",Locale.ENGLISH.toString());
                writer.writeAttribute("origvalue",tag.getElement().getContent().getTextExtractor().toString());
                writer.writeAttribute("destvalue",tag2.getElement().getContent().getTextExtractor().toString());

                System.out.println(tag.getElement().getContent().getTextExtractor().toString()+" - "+tag2.getElement().getTextExtractor().toString());
            }
            writer.writeEndElement();
            writer.flush();
            outwriter.write("\n");
            i++;

        }

        writer.writeEndDocument();
            writer.flush();
            /*BufferedWriter fwriter=new BufferedWriter(new FileWriter(new File("newwords.xml")));
            fwriter.write(outwriter.toString());
            writer.close();*
        } catch (FactoryConfigurationError | Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("FINISHED");

    } */
}
