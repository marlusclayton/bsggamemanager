package com.bsg.characters;

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

public class CharacterLoader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CharacterLoader.class);
	
	private List<Character> characters;
	private Document doc;
	private File xmlFile;
	
	
	public CharacterLoader(File xmlFile) throws SAXException, IOException, ParserConfigurationException, InvalidConfigException {
		this.xmlFile = xmlFile;
		
		characters = new ArrayList<Character>();
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
		
		NodeList nl = root.getElementsByTagName("character");
		if (nl == null)
			return;
		
		LOGGER.info("Read {} characters", nl.getLength());

		
		for (int i = 0; i < nl.getLength(); ++i) {
			Element el = (Element) nl.item(i);
			
			String name = getTextValue(el, "name");
			String shortname = getTextValue(el, "shortname");
			String type = getTextValue(el, "type");
			String draw = getTextValue(el, "draw");
			String setup = getTextValue(el, "setup");
			
			if (StringUtils.isEmpty(name))
				throw new InvalidConfigException(String.format("Error in %s: Character's name was empty", xmlFile.getName()));
			if (StringUtils.isEmpty(shortname))
				throw new InvalidConfigException(String.format("Error in %s: %s short name was empty", xmlFile.getName(), name));
			if (StringUtils.isEmpty(type))
				throw new InvalidConfigException(String.format("Error in %s: %s type was empty", xmlFile.getName(), name));
			if (StringUtils.isEmpty(draw))
				throw new InvalidConfigException(String.format("Error in %s: %s draw was empty", xmlFile.getName(), name));
			if (StringUtils.isEmpty(setup))
				throw new InvalidConfigException(String.format("Error in %s: %s setup was empty", xmlFile.getName(), name));
			
			Character c = new Character(name, shortname, type, draw, setup, expansion);
			characters.add(c);
			LOGGER.info("Added character {} ({})", name, shortname);
		}
	}
	
	public List<Character> getCharacters() {
		return new ArrayList<Character>(characters);
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
