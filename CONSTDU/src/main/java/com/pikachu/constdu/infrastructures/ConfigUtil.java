package com.pikachu.constdu.infrastructures;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ConfigUtil {

    public Document getConfigDocument() {
        File config = new File("./.config/config.xml");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document doc = null;
        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(config);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return doc;
    }

    public HashMap<String, String> getDobCountries(){
        Document config = getConfigDocument();
        NodeList list = config.getElementsByTagName("DOBCOUNTRIES");
        Node dobcountries = list.item(0);
        NodeList countries = dobcountries.getChildNodes();
        HashMap<String, String> result = new HashMap<>();
        for(int i = 0; i < countries.getLength(); i++){
            Node node = countries.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE){
                Element country = (Element) node;
                result.put(country.getElementsByTagName("NAME").item(0).getTextContent(),
                        country.getElementsByTagName("CODE").item(0).getTextContent());
            }
        }

        return result;
    }

    public String getCountryOfBirthCodeByContryNameOfBirth(String countryName){
        HashMap<String, String> map = getDobCountries();
        return map.get(countryName);
    }

}
