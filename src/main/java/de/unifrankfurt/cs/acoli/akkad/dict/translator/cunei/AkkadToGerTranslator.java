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

package de.unifrankfurt.cs.acoli.akkad.dict.translator.cunei;

import de.unifrankfurt.cs.acoli.akkad.dict.chars.LangChar;
import de.unifrankfurt.cs.acoli.akkad.dict.dicthandler.DictHandling;
import de.unifrankfurt.cs.acoli.akkad.dict.pos.POSTagger;
import de.unifrankfurt.cs.acoli.akkad.dict.pos.util.GroupDefinition;
import de.unifrankfurt.cs.acoli.akkad.dict.pos.util.POSDefinition;
import de.unifrankfurt.cs.acoli.akkad.dict.translator.Translator;
import de.unifrankfurt.cs.acoli.akkad.main.gui.util.HighlightData;
import de.unifrankfurt.cs.acoli.akkad.util.enums.methods.CharTypes;
import de.unifrankfurt.cs.acoli.akkad.util.enums.pos.POSTags;
import org.apache.commons.lang3.StringUtils;
import simplenlg.features.Feature;
import simplenlg.features.NumberAgreement;
import simplenlg.features.Person;
import simplenlg.features.Tense;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;


import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;


/**
 * Translation class from Akkadian to German.
 */
public class AkkadToGerTranslator extends Translator {
    /**Language generator factory for creating verbs.*/
    private NLGFactory factory;
    /**Realiser for creating chosen verbs.*/
    private Realiser realiser;



    /**Constructor for this class.*/
    public AkkadToGerTranslator(DictHandling dictHandling,POSTagger posTagger){
        this.posTagger=posTagger;
        this.dictHandler=dictHandling;
        this.lastWritten="";
        this.length=new LinkedList<>();
        Lexicon lexicon = Lexicon.getDefaultLexicon();
        this.factory = new NLGFactory(lexicon);
        this.realiser = new Realiser(lexicon);
    }
    /**Constructor for this class.*/
    public AkkadToGerTranslator(CharTypes charTypes){
        this.posTagger=charTypes.getCorpusHandlerAPI().getPOSTagger(false);
        this.dictHandler=charTypes.getCorpusHandlerAPI().getUtilDictHandler();
        this.lastWritten="";
        this.length=new LinkedList<>();
        Lexicon lexicon = Lexicon.getDefaultLexicon();
        this.factory = new NLGFactory(lexicon);
        this.realiser = new Realiser(lexicon);
    }
    @Override
    public void wordByWordPOStranslate(String translationText,Boolean pinyin,Integer initialPos){
        this.result="";
        this.length.clear();
        this.currentpos=initialPos;
        this.lastWritten="";
        this.lasttranslation=null;
        LangChar tempword=null;
        if(pinyin){
            for(String word:translationText.split(" ")){
                word=word.replace("[","").replace("]","");
                System.out.println("Word: "+word);
                List<POSDefinition> defs=this.posTagger.getPosTagDefs(word,dictHandler);
                //Collections.reverse(defs);
                if((dictHandler.matchWordByTransliteration(word)!=null && tempword!=null && tempword.getTranslationSet(Locale.ENGLISH)!=null && !tempword.getTranslationSet(Locale.ENGLISH).isEmpty())) {


                }else{
                    tempword=null;
                }
                if(defs.isEmpty()){
                    lastWritten="("+word+") ";
                    lasttranslation=new POSDefinition("","","","","","UNKNOWN","","",new TreeMap<Integer,List<GroupDefinition>>());
                    result+=lastWritten;
                    this.length.add(new HighlightData(this.currentpos,this.currentpos+=lastWritten.length(),"DEFAULT",lasttranslation.getPosTag(),word));
                    continue;
                }
                else if(defs.size()>0){
                    this.lastWritten=this.POSTagToRule(defs.get(0),word,tempword)+" ";
                    this.lasttranslation=defs.get(0);
                    result+=lastWritten;
                }
                this.length.add(new HighlightData(this.currentpos,this.currentpos+=lastWritten.length(),defs.get(0).getDesc(),lasttranslation.getPosTag(),word));

                //currentpos=lastWritten.length();

            }
        }

    }


    /**Maps a detected POSTag to a translation rule according to the recognized POSTag.*/
    public String POSTagToRule(POSDefinition def,String word,LangChar translations){
        System.out.println("GETPOSTAG: "+def.getTag()+" "+word);
        switch (def.getPosTag()){
            case ADJECTIVE:
                return translations==null?"("+word.replace("[","").replace("]","")+")":translations.getFirstTranslation(Locale.ENGLISH);
            case NOUN:
                if(word.replace("[","").replace("]","").equals("DUMU")){
                    return "Sohn des ";
                }else if(word.replace("[","").replace("]","").equals("IGI")){
                    return "Zeuge ";
                }else if(word.replace("[","").replace("]","").equals("NA4")){
                    return "Unterzeichnet von ";
                }else if(word.replace("[","").replace("]","").equals("DAM-at")){
                    return "Ehefrau von ";
                }
                else if(word.replace("[","").replace("]","").equals("LUGAL")){
                    return "König ";
                }
                else if(word.replace("[","").replace("]","").equals("DUMU-MESZ")){
                    return "Kinder von ";
                }else if(word.replace("[","").replace("]","").equals("DUMU-MUNUS")){
                    return "Tochter von ";
                }
                return translations==null?"("+word.replace("[","").replace("]","")+")":translations.getFirstTranslation(Locale.ENGLISH);
            case NOUNORADJ:
                return translations==null?"("+word.replace("[","").replace("]","")+")":translations.getFirstTranslation(Locale.ENGLISH);
            case NUMBER:
                System.out.println("LastTranslation: "+lastWritten+" - "+lasttranslation);
                if(lasttranslation!=null && lasttranslation.getTag().equals(def.getTag())){
                    result=result.substring(0,result.length()-lastWritten.length());
                    this.length.remove(length.size()-1);
                    this.currentpos-=lastWritten.length();
                    return Integer.valueOf(lastWritten.trim())+Integer.valueOf(def.getValue())*(StringUtils.countMatches(word, "-")+1)+"";
                }
                return Integer.valueOf(def.getValue())*(StringUtils.countMatches(word,"-")+1)+"";
            case DETERMINATIVE:
                int position=0;
                String detstranslation="";
                String detcollections="";
                for(String spl:word.split("-")){
                    if(Translator.isAllUpperCaseOrNumber(spl)){
                        System.out.println("IsAllUpperCaseOrNumber: "+spl);
                        detcollections+=spl+"-";
                        position+=spl.length()+1;
                    }else{
                        break;
                    }
                }
                if(detcollections.isEmpty()){
                    break;
                }else{
                    System.out.println("Detcollection: "+detcollections);
                    switch (detcollections.substring(0,detcollections.length()-1)){
                        case "MUNUS": detstranslation+="Frau ";
                            break;
                        case "DISZ-MUNUS":  detstranslation+="Ehemann von ";
                            break;
                        case "NA4-KISZIB":
                            detstranslation+="Unterzeichnet von ";
                            break;
                        case "DISZ":  detstranslation+="Herr ";
                            break;
                        case "URU":  detstranslation+="am Ort ";
                            break;
                        default:
                    }
                }
                if(detstranslation.isEmpty() && position<word.length())
                    word=word.substring(0,position)+Translator.separateConsonants(Character.toString(word.substring(position,position+1).charAt(0))
                            .toUpperCase()+word.substring(position+1,word.length()).replace("a-a-a","aja").replace("e-e-e","eje").replaceAll("-","").replaceAll("[0-9]","")
                            .replaceAll("[a]+","a").replaceAll("[e]+","e").replaceAll("[u]+","u").replaceAll("[i]+","i"));
                else if(detstranslation.isEmpty() && position>=word.length()){
                    word=detcollections.substring(0,detcollections.length()-1);
                }else if(detstranslation.length()>=word.length()){
                    word=detstranslation;
                }else{
                    word = detstranslation + Translator.separateConsonants(Character.toString(word.substring(position, position + 1).charAt(0))
                            .toUpperCase() + word.substring(position + 1, word.length()).replace("a-a-a", "aja").replace("e-e-e", "eje").replaceAll("-", "").replaceAll("[0-9]", "")
                            .replaceAll("[a]+", "a").replaceAll("[e]+", "e").replaceAll("[u]+", "u").replaceAll("[i]+", "i"));
                }
                word=word.replace("LUGAL"," der König");
                return  word.replace("]","");
            case NAMEDENTITY:
                return Character.toString(word.substring(0,1).charAt(0))
                        .toUpperCase()+word.substring(1,word.length()).replace("a-a-a","aja").replace("e-e-e","eje").replaceAll("-","").replaceAll("[0-9]","")
                        .replaceAll("[a]+","a").replaceAll("[e]+","e").replaceAll("[u]+","u").replaceAll("[i]+","i");
            case VERB:
                if(def.getValue().isEmpty())
                    return translations==null?"("+word.replace("[","").replace("]","")+")":this.getRightVerbConfiguration(def, translations.getFirstTranslation(Locale.ENGLISH));
                return this.getRightVerbConfiguration(def, def.getValue());
            default:if(!def.getValue().isEmpty()){
                return def.getValue();
            }else{
                return "("+word+") ";
            }
        }
        return "[]";
    }

    /**Gets the right verb configuration according to the POSTag detection.*/
    public String getRightVerbConfiguration(final POSDefinition def,String translation){
        SPhraseSpec p = this.factory.createClause();
        p.setVerb(translation);
        p.setFeature(Feature.PERSON, Person.THIRD);
        p.setFeature(Feature.PERFECT,true);
        System.out.println(realiser.realise(p));
        if(def.getWordCase()!=null){
            switch (def.getWordCase()){
                case FIRST_SINGULAR:
                    p.setFeature(Feature.PERSON,Person.FIRST);
                    p.setFeature(Feature.NUMBER, NumberAgreement.SINGULAR);
                    break;
                case SECOND_SINGULAR:
                case SECOND_SINGULAR_FEMALE:
                    p.setFeature(Feature.PERSON,Person.SECOND);
                    p.setFeature(Feature.NUMBER, NumberAgreement.SINGULAR);
                    break;
                case THIRD_SINGULAR:
                case THIRD_SINGULAR_MALE:
                case THIRD_SINGULAR_FEMALE:
                case THIRD_SINGULAR_THING:
                    p.setFeature(Feature.PERSON,Person.THIRD);
                    p.setFeature(Feature.NUMBER, NumberAgreement.SINGULAR);
                    break;
                case FIRST_PLURAL:
                    p.setFeature(Feature.PERSON,Person.FIRST);
                    p.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
                    break;
                case SECOND_PLURAL:
                    p.setFeature(Feature.PERSON,Person.SECOND);
                    p.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
                    break;
                case THIRD_PLURAL:
                case THIRD_PLURAL_MALE:
                case THIRD_PLURAL_FEMALE:
                case THIRD_PLURAL_THING:
                    p.setFeature(Feature.PERSON,Person.THIRD);
                    p.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
                    break;
            }
        }
        if(def.getTense()!=null){
            switch (def.getTense()){
                case PAST:
                    p.setFeature(Feature.TENSE, Tense.PAST);
                    break;
                case PERFECT:
                    p.setFeature(Feature.PERFECT,true);
                    break;
                case FUTURE:
                    p.setFeature(Feature.TENSE,Tense.FUTURE);
                    break;
                case PRESENT:
                    p.setFeature(Feature.TENSE,Tense.PRESENT);
                    break;
            }
            /*switch (def.getTense()){
                case PAST: translation+=translation.endsWith("e")?"d":"ed";;
                    break;
                case PERFECT:
                    if(def.getWordCase()!=null){
                        switch (def.getWordCase()){
                            case FIRST_SINGULAR: translation="have "+translation;

                                break;
                            case SECOND_SINGULAR:
                            case SECOND_SINGULAR_FEMALE:
                                translation="have "+translation;
                                break;
                            case THIRD_SINGULAR:
                            case THIRD_SINGULAR_MALE:
                            case THIRD_SINGULAR_FEMALE:
                            case THIRD_SINGULAR_THING:
                                translation="has "+translation;
                                break;
                            case FIRST_PLURAL:
                                translation="have "+translation;
                                break;
                            case SECOND_PLURAL:
                                translation="have "+translation;
                                break;
                            case THIRD_PLURAL:
                            case THIRD_PLURAL_MALE:
                            case THIRD_PLURAL_FEMALE:
                            case THIRD_PLURAL_THING:
                                translation="have "+translation;
                                break;
                            default:
                        }
                    }
                    translation+=translation.endsWith("e")?"d":"ed";
                     break;
                case PRESENT:
                    if(def.getWordCase()!=null){
                        switch (def.getWordCase()){
                            case FIRST_SINGULAR:
                            case SECOND_SINGULAR:
                            case SECOND_SINGULAR_FEMALE:
                                break;
                            case THIRD_SINGULAR:
                            case THIRD_SINGULAR_MALE:
                            case THIRD_SINGULAR_FEMALE:
                            case THIRD_SINGULAR_THING:
                                translation+="s";
                                break;
                            case FIRST_PLURAL:
                            case SECOND_PLURAL:
                            case THIRD_PLURAL:
                            case THIRD_PLURAL_MALE:
                            case THIRD_PLURAL_FEMALE:
                            case THIRD_PLURAL_THING:
                                break;
                            default:
                        }
                    }
                    break;
                case FUTURE:
                    if(def.getWordCase()!=null){
                        switch (def.getWordCase()){
                            case FIRST_SINGULAR:
                            case SECOND_SINGULAR:
                            case SECOND_SINGULAR_FEMALE:
                                break;
                            case THIRD_SINGULAR:
                            case THIRD_SINGULAR_MALE:
                            case THIRD_SINGULAR_FEMALE:
                            case THIRD_SINGULAR_THING:
                                translation+="s";
                                break;
                            case FIRST_PLURAL:
                            case SECOND_PLURAL:
                            case THIRD_PLURAL:
                            case THIRD_PLURAL_MALE:
                            case THIRD_PLURAL_FEMALE:
                            case THIRD_PLURAL_THING:
                                break;
                            default:
                        }
                    }
                    break;
                default:
            }*/
        }
        translation=realiser.realise(p).toString();
        return translation;
    }
}

