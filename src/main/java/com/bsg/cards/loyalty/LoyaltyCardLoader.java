package com.bsg.cards.loyalty;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

public class LoyaltyCardLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoyaltyCardLoader.class);
	
	private File xmlFile;
	private List<LoyaltyCard> cards;
	private Document doc;
	
	public LoyaltyCardLoader(File xmlFile) throws ParserConfigurationException, SAXException, IOException, InvalidConfigException {
		this.xmlFile = xmlFile;
		
		cards = new ArrayList<LoyaltyCard>();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder db = dbf.newDocumentBuilder();
		doc = db.parse(xmlFile);
		
		parse();
	}

	private void parse() throws InvalidConfigException {
		Element root = doc.getDocumentElement();
		String expansion_str = root.getAttribute("expansion");
		LOGGER.info("Read expansion {}", expansion_str);
		Expansion exp = null;
		try {
			exp = Expansion.valueOf(expansion_str);
		} catch (Exception e) {
			throw new InvalidConfigException(String.format("%s: Expansion \"%s\" is not valid", xmlFile.getName(), expansion_str));
			
		}

		NodeList nl = root.getElementsByTagName("loyaltycard");
		if (nl == null)
			return;
		
		LOGGER.info("Read {} loyalty cards from {}", nl.getLength(), xmlFile.getName());
		
		for (int i = 0; i < nl.getLength(); ++i) {
			Element el = (Element) nl.item(i);
			
			String title = el.getAttribute("title");
			LoyaltyCardType type = LoyaltyCardType.valueOf(el.getAttribute("type"));
			if (type == null)
				throw new InvalidConfigException(String.format("%s has invalid loyalty type", title));
			String text = el.getTextContent();
			
			LOGGER.info("Found card {}", title);
			cards.add(new LoyaltyCard(title, type, text, exp));
			
		}
	}
	
	public List<LoyaltyCard> getCards() {
		return new ArrayList<LoyaltyCard>(cards);
	}
}
