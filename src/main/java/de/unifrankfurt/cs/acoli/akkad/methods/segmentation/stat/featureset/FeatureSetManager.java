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

package de.unifrankfurt.cs.acoli.akkad.methods.segmentation.stat.featureset;

import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.util.Tuple;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.FeatureSets;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.TestMethod;
import de.unifrankfurt.cs.acoli.akkad.util.enums.util.Files;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;

import static de.unifrankfurt.cs.acoli.akkad.util.ArffHandler.arrayToStr;

/**
 * Created by timo on 13.08.14.
 */
public class FeatureSetManager implements FeatureSetAPI {


    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, XMLStreamException {
        DictHandling dictHandler = CharTypes.CHINESE.getCorpusHandlerAPI().generateTestTrainSets("", "", 0., 0., TestMethod.FOREIGNTEXT,CharTypes.CHINESE);
        dictHandler.setCharType(CharTypes.CHINESE);
        //dictHandler.parseDictFile(new File(Files.AKKADXML.toString()));
        System.out.println("CharType: "+dictHandler.getChartype().toString());
        dictHandler.importMappingFromXML(Files.DICTDIR+CharTypes.CHINESE.getLocale()+Files.MAPSUFFIX);
        dictHandler.importDictFromXML(Files.DICTDIR+CharTypes.CHINESE.getLocale()+Files.DICTSUFFIX);
        dictHandler.importReverseDictFromXML(Files.DICTDIR+CharTypes.CHINESE.getLocale()+Files.REVERSE+Files.DICTSUFFIX);
        CharBasedFeatureSet set=(CharBasedFeatureSet)FeatureSets.CRF.getFeatureSet();
        arrayToStr(set.maxentSighan(new Tuple<StringBuffer, Integer>(new StringBuffer("abcdefghi"), 2), dictHandler, CharTypes.ENGLISH));
    }

    @Override
    public String[] getContext(final Tuple<StringBuffer, Integer> tagset, final FeatureSets featureSet,final String segmented, final DictHandling dicthandler, final CharTypes chartype) {
        String[] result;
        switch(featureSet){
            //case CRF:  result=((CharBasedFeatureSet)featureSet.getFeatureSet()).crf(tagset, dicthandler, chartype);break;
            case MAXENT: result=((CharBasedFeatureSet)featureSet.getFeatureSet()).maxent(tagset, dicthandler, chartype);break;
            case MAXENTPREV: result=((CharBasedFeatureSet)featureSet.getFeatureSet()).maxent2(tagset, dicthandler, chartype);break;
            case MAXENTSIGHAN: result=((CharBasedFeatureSet)featureSet.getFeatureSet()).maxentSighan(tagset, dicthandler, chartype);break;
            //case PASSIVEAGGRESSIVE:result=((WordBasedFeatureSet)featureSet.getFeatureSet()).passiveAggressive(tagset,segmented, dicthandler, chartype);break;
            //case PERCEPTRON: result=((WordBasedFeatureSet)featureSet.getFeatureSet()).perceptron(tagset, segmented,dicthandler, chartype);break;
            case CRF: result=((CharBasedFeatureSet)featureSet.getFeatureSet()).perceptronCRF(tagset, dicthandler, chartype);break;
            case UNLABELED: result=((WordBasedFeatureSet)featureSet.getFeatureSet()).unlabeled(tagset,segmented, dicthandler, chartype);break;
            default:    result=new String[1];
        }
        return result;
    }
}
