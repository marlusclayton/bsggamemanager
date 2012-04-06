package com.bsg.cards.loyalty;


public enum LoyaltyType {

	HUMAN("Human"),
	CYLON("Hidden Cylon"),
	REVEALED_CYLON("Revealed Cylon"),
	CYLON_LEADER("Cylon Leader");
	
	private String description;
	
	private LoyaltyType(String desc) {
		this.description = desc;
	}
	
	public String getDescription() {
		return description;
	}
}
