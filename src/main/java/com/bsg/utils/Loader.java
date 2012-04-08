package com.bsg.utils;

import java.io.File;
import java.io.IOException;

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

public abstract class Loader {

	private static final Logger LOGGER = LoggerFactory.getLogger(Loader.class);
	
	protected File xmlFile;
	protected Document doc;
	protected Expansion expansion;
	
	
	public Loader(File xmlFile) throws ParserConfigurationException, SAXException, IOException, InvalidConfigException {
		this.xmlFile = xmlFile;
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		
		doc = db.parse(xmlFile);
		LOGGER.info("Loaded file {}", xmlFile.getAbsolutePath());
	}
	
	protected void parse() throws InvalidConfigException {
		Element root = doc.getDocumentElement();
		String expansion_str = root.getAttribute("expansion");
		try {
			expansion = Expansion.valueOf(expansion_str);
		} catch (Exception e) {
			throw new InvalidConfigException(String.format("%s: Expansion \"%s\" is not valid", xmlFile.getName(), expansion_str));
		}
		
		NodeList nl = root.getElementsByTagName(getTagName());
		if (nl == null)
			return;
		
		for (int i = 0; i < nl.getLength(); ++i) {
			Element el = (Element) nl.item(i);
			parseItem(el);
		}
		
		
	}
	
	protected String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}
	
	protected abstract String getTagName();
	protected abstract void parseItem(Element el) throws InvalidConfigException;
	
	
}
