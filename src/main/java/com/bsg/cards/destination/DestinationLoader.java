package com.bsg.cards.destination;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.bsg.Expansion;
import com.bsg.InvalidConfigException;

public class DestinationLoader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DestinationLoader.class);

	private File xmlFile;
	private List<DestinationCard> cards;
	private Document doc;
	
	public DestinationLoader(File xmlFile) throws SAXException, IOException, ParserConfigurationException, InvalidConfigException {
		this.xmlFile = xmlFile;
		
		cards = new ArrayList<DestinationCard>();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder db = dbf.newDocumentBuilder();
		doc = db.parse(xmlFile);
		
		parse();
	}
	
	private void parse() throws InvalidConfigException {
		Element root = doc.getDocumentElement();
		String expansion_str = root.getAttribute("expansion");
		Expansion expansion = Expansion.valueOf(expansion_str);
		if (expansion == null)
			throw new InvalidConfigException(String.format("\"%s\" is not a valid expansion", expansion_str));
		
		NodeList nl = root.getElementsByTagName("destination");
		if (nl == null)
			return;
		
		LOGGER.info("Read {} destination cards", nl.getLength());
		
		for (int i = 0; i < nl.getLength(); ++i) {
			Element el = (Element) nl.item(i);
			
			String name = getTextValue(el, "name");
			String distance = getTextValue(el, "distance");
			String fuelLoss = getTextValue(el, "fuelloss");
			String other = getTextValue(el, "other");
			String special = getTextValue(el, "special");
			
			DestinationCard dc = new DestinationCard(name, 
					Integer.parseInt(distance),
					Integer.parseInt(fuelLoss),
					other,
					special,
					expansion);
			
			LOGGER.info("Read destination {}", name);
			cards.add(dc);
		}
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

	public List<DestinationCard> getCards() {
		return new ArrayList<DestinationCard>(cards);
	}
}
