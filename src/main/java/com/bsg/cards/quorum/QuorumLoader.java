package com.bsg.cards.quorum;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.bsg.InvalidConfigException;
import com.bsg.utils.Loader;

public class QuorumLoader extends Loader {

	private static final Logger LOGGER = LoggerFactory.getLogger(QuorumLoader.class);
	
	private List<QuorumCard> cards;
	
	public QuorumLoader(File xmlFile) throws ParserConfigurationException,
			SAXException, IOException, InvalidConfigException {
		super(xmlFile);
		
		cards = new ArrayList<QuorumCard>();
		
		parse();
	}

	@Override
	protected String getTagName() {
		return "quorumcard";
	}

	@Override
	protected void parseItem(Element el) throws InvalidConfigException {
		String name = getTextValue(el, "name");
		String text = getTextValue(el, "text");
		QuorumType type;
		try {
			type = QuorumType.valueOf(getTextValue(el, "type"));
		} catch (IllegalArgumentException e) {
			throw new InvalidConfigException(String.format("Error in %s: Invalid type for %s", xmlFile.getName(), name));
		}
		
		
		if (StringUtils.isEmpty(name))
			throw new InvalidConfigException(String.format("Error in %s: Quorum Card name was empty", xmlFile.getName()));
		if (StringUtils.isEmpty(text))
			throw new InvalidConfigException(String.format("Error in %s: %s text was empty", xmlFile.getName(), name));
		
		QuorumCard qc = new QuorumCard(name, text, type, expansion);
		cards.add(qc);
		LOGGER.info("Added quorum card {}", name);
	}
	
	public List<QuorumCard> getQuorumCards() {
		return cards;
	}

}
