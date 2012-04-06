package com.bsg.cards.skill;

public enum SkillCardType {

	POLITICS("POL"),
	LEADERSHIP("LEA"),
	TACTICS("TAC"),
	PILOTING("PIL"),
	ENGINEERING("ENG"),
	TRECHERY("TRE");
	
	private String shortName;
	
	SkillCardType(String shortName) {
		this.shortName = shortName;
	}
	
	public String getShortName() {
		return shortName;
	}
	
}
