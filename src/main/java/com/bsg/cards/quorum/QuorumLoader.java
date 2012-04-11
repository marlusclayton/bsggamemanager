package com.bsg.cards.quorum;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.bsg.InvalidConfigException;
import com.bsg.utils.Loader;

public class QuorumLoader extends Loader {

	
	
	public QuorumLoader(File xmlFile) throws ParserConfigurationException,
			SAXException, IOException, InvalidConfigException {
		super(xmlFile);
		
		parse();
	}

	@Override
	protected String getTagName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void parseItem(Element el) throws InvalidConfigException {
		// TODO Auto-generated method stub

	}

}
