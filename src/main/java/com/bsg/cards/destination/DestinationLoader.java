package com.bsg.cards.destination;

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

public class DestinationLoader extends Loader {

	private static final Logger LOGGER = LoggerFactory.getLogger(DestinationLoader.class);

	private List<DestinationCard> cards;

	public DestinationLoader(File xmlFile) throws SAXException, IOException, ParserConfigurationException, InvalidConfigException {
		super(xmlFile);

		cards = new ArrayList<DestinationCard>();
		parse();
	}

	@Override
	protected void parseItem(Element el) throws InvalidConfigException {
		String name = getTextValue(el, "name");
		String distance = getTextValue(el, "distance");
		String fuelLoss = getTextValue(el, "fuelloss");
		String special = getTextValue(el, "special");

		DestinationCard dc = new DestinationCard(name,
				Integer.parseInt(distance), Integer.parseInt(fuelLoss),
				special, expansion);

		LOGGER.info("Read destination {}", name);
		cards.add(dc);
	}

	public List<DestinationCard> getCards() {
		return new ArrayList<DestinationCard>(cards);
	}

	@Override
	protected String getTagName() {
		return "destination";
	}
}
