package com.bsg.cards.skill;

import org.apache.commons.lang3.text.WordUtils;

import com.bsg.cards.Card;

public class SkillCard extends Card implements Comparable<SkillCard> {

	private String name;
	private int strength;
	private SkillCardType type;
	private boolean hasSkillCheckAbility;
	
	public SkillCard(String name, int strength, SkillCardType type, boolean hasSkillCheckAbility) {
		this.name = name;
		this.strength = strength;
		this.type = type;
		this.hasSkillCheckAbility = hasSkillCheckAbility;
	}
	
	public SkillCard(String name, int strength, SkillCardType type) {
		this(name, strength, type, false);
	}
	
	public String getName() {
		return name;
	}
	
	public int getStrength() {
		return strength;
	}
	
	public SkillCardType getType() {
		return type;
	}
	
	public boolean hasSkillCheckAbility() {
		return hasSkillCheckAbility;
	}

	@Override
	public int compareTo(SkillCard o) {
		if (o.getType() != this.type) 
			return this.type.compareTo(o.type);
		if (o.getStrength() != this.getStrength())
			return this.strength - o.strength;
		if (!o.getName().equals(this.name))
			return this.name.compareTo(o.name);
		return this.identifier.compareTo(o.identifier);
		
	}
	
	@Override
	public String toString() {
		return String.format("%s-%d (%s)", WordUtils.capitalize(type.toString()), strength, name);
	}
	
}
