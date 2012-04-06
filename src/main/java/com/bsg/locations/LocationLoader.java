package com.bsg.locations;

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

public class LocationLoader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LocationLoader.class);
	
	private List<Location> locations;
	private Document doc;
	private File xmlFile;
	
	
	public LocationLoader(File xmlFile) throws SAXException, IOException, ParserConfigurationException, InvalidConfigException {
		this.xmlFile = xmlFile;
		
		locations = new ArrayList<Location>();
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
		
		NodeList nl = root.getElementsByTagName("area");
		if (nl == null)
			return;
		
		LOGGER.info("Read {} areas", nl.getLength());

		
		for (int i = 0; i < nl.getLength(); ++i) {
			Element el = (Element) nl.item(i);
			
			String areaName = el.getAttribute("name");
			if (StringUtils.isEmpty(areaName))
				throw new InvalidConfigException(String.format("Error in %s: Invalid area name", xmlFile.getName()));
			
			NodeList locationNodes = el.getElementsByTagName("location");
			for (int j = 0; j < locationNodes.getLength(); ++j) {
				Element loc = (Element) locationNodes.item(j);
				String locName = loc.getAttribute("name");
				
				if (StringUtils.isEmpty(locName))
					throw new InvalidConfigException(String.format("Error in %s: Area %s has an invalid location", xmlFile.getName(), areaName));
				
				Location l = new Location(locName, areaName, expansion);
				locations.add(l);
				LOGGER.info("Found location {} in {}", locName, areaName);
				
			}
		}
	}
	
	public List<Location> getLocations() {
		return new ArrayList<Location>(locations);
	}
	
}
