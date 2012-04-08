package com.bsg.cards.loyalty;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.bsg.InvalidConfigException;
import com.bsg.utils.Loader;

public class LoyaltyCardLoader extends Loader {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoyaltyCardLoader.class);
	
	private List<LoyaltyCard> cards;
	
	public LoyaltyCardLoader(File xmlFile) throws ParserConfigurationException, SAXException, IOException, InvalidConfigException {
		super(xmlFile);
		cards = new ArrayList<LoyaltyCard>();

		parse();
	}

	protected void parseItem(Element el) throws InvalidConfigException {
			String title = el.getAttribute("title");
			LoyaltyCardType type = LoyaltyCardType.valueOf(el.getAttribute("type"));
			if (type == null)
				throw new InvalidConfigException(String.format("%s has invalid loyalty type", title));
			String text = el.getTextContent();
			
			LOGGER.info("Found card {}", title);
			cards.add(new LoyaltyCard(title, type, text, expansion));
	}
	
	public List<LoyaltyCard> getCards() {
		return new ArrayList<LoyaltyCard>(cards);
	}
	
	protected String getTagName() {
		return "loyaltycard";
	}
}
