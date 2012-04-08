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
import com.bsg.utils.Loader;

public class CrisisLoader extends Loader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CrisisLoader.class);
	
	private List<CrisisCard> cards;
	private Document doc;
	private File xmlFile;
	
	
	public CrisisLoader(File xmlFile) throws SAXException, IOException, ParserConfigurationException, InvalidConfigException {
		super(xmlFile);
		cards = new ArrayList<CrisisCard>();
		parse();
	}
	
	@Override
	protected void parseItem(Element el) throws InvalidConfigException {
			
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
	
	public List<CrisisCard> getCards() {
		return new ArrayList<CrisisCard>(cards);
	}

	@Override
	protected String getTagName() {
		return "crisiscard";
	}
	
}
