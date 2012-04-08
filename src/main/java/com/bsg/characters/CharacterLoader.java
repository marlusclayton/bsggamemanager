package com.bsg.characters;

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

public class CharacterLoader extends Loader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CharacterLoader.class);
	
	private List<Character> characters;
	
	
	public CharacterLoader(File xmlFile) throws SAXException, IOException, ParserConfigurationException, InvalidConfigException {
		super(xmlFile);
		characters = new ArrayList<Character>();
		parse();
	}
	
	@Override
	protected
	void parseItem(Element el) throws InvalidConfigException {

		String name = getTextValue(el, "name");
		String shortname = getTextValue(el, "shortname");
		String type = getTextValue(el, "type");
		String draw = getTextValue(el, "draw");
		String setup = getTextValue(el, "setup");

		if (StringUtils.isEmpty(name))
			throw new InvalidConfigException(String.format(
					"Error in %s: Character's name was empty",
					xmlFile.getName()));
		if (StringUtils.isEmpty(shortname))
			throw new InvalidConfigException(String.format(
					"Error in %s: %s short name was empty", xmlFile.getName(),
					name));
		if (StringUtils.isEmpty(type))
			throw new InvalidConfigException(String.format(
					"Error in %s: %s type was empty", xmlFile.getName(), name));
		if (StringUtils.isEmpty(draw))
			throw new InvalidConfigException(String.format(
					"Error in %s: %s draw was empty", xmlFile.getName(), name));
		if (StringUtils.isEmpty(setup))
			throw new InvalidConfigException(String.format(
					"Error in %s: %s setup was empty", xmlFile.getName(), name));

		Character c = new Character(name, shortname, type, draw, setup,
				expansion);
		characters.add(c);
		LOGGER.info("Added character {} ({})", name, shortname);
	}
	
	public List<Character> getCharacters() {
		return new ArrayList<Character>(characters);
	}

	@Override
	protected String getTagName() {
		return "character";
	}
}
