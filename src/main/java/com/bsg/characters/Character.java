package com.bsg.characters;

import com.bsg.Expansion;
import com.bsg.Item;

public class Character implements Comparable<Character>, Item {

	private final String name;
	private final String shortName;
	private final String type;
	private final String draw;
	private final String setup;
	private final Expansion expansion;
	
	public Character(String name, String shortName, String type, String draw, String setup, Expansion expansion) {
		this.name = name;
		this.shortName = shortName;
		this.type = type;
		this.draw = draw;
		this.setup = setup;
		this.expansion = expansion;
	}
	
	
	@Override
	public String toString() {
		return shortName;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @return the shortName
	 */
	public String getShortName() {
		return shortName;
	}


	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}


	/**
	 * @return the draw
	 */
	public String getDraw() {
		return draw;
	}


	/**
	 * @return the setup
	 */
	public String getSetup() {
		return setup;
	}


	/**
	 * @return the expansion
	 */
	public Expansion getExpansion() {
		return expansion;
	}


	@Override
	public int compareTo(Character o) {
		if (this.expansion != o.expansion)
			return this.expansion.compareTo(o.expansion);
		
		if (this.type.equals(o.type))
			return this.shortName.compareTo(o.shortName);
		
		return this.type.compareTo(o.type);
	}
	
	
}
