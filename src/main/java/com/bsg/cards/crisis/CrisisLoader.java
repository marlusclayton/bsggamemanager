package com.bsg.cards.crisis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.bsg.Expansion;
import com.bsg.InvalidConfigException;

public class CrisisLoader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CrisisLoader.class);
	
	private List<CrisisCard> cards;
	private Document doc;
	private File xmlFile;
	
	
	public CrisisLoader(File xmlFile) throws SAXException, IOException, ParserConfigurationException, InvalidConfigException {
		this.xmlFile = xmlFile;
		
		cards = new ArrayList<CrisisCard>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder db = dbf.newDocumentBuilder();
		doc = db.parse(xmlFile);
		
		parse();
	}
	
	private void parse() throws InvalidConfigException {
		Element root = doc.getDocumentElement();
		String expansion_str = root.getAttribute("expansion");
		LOGGER.info("Read expansion {}", expansion_str);
		Expansion expansion = Expansion.valueOf(expansion_str);
		if (expansion == null)
			throw new InvalidConfigException(String.format("\"%s\" is not a valid expansion", expansion_str));
		
		NodeList nl = root.getElementsByTagName("crisiscard");
		if (nl == null)
			return;
		
		LOGGER.info("Read {} crisis cards", nl.getLength());

		
		for (int i = 0; i < nl.getLength(); ++i) {
			Element el = (Element) nl.item(i);
			
			String name = el.getAttribute("name");
			String type = el.getAttribute("type");
			String description = el.getTextContent();
			
			if (StringUtils.isEmpty(name))
				throw new InvalidConfigException(String.format("Error in %s: Crisis Card name was empty", xmlFile.getName()));
			if (StringUtils.isEmpty(type))
				throw new InvalidConfigException(String.format("Error in %s: %s type was empty", xmlFile.getName(), name));
			
			CrisisCard cc = new CrisisCard(name, CrisisType.valueOf(type), description, expansion);
			cards.add(cc);
			LOGGER.info("Added crisis card {}", name);
		}
	}
	
	public List<CrisisCard> getCards() {
		return new ArrayList<CrisisCard>(cards);
	}
	
	private static String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}
}
