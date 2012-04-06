package com.bsg.cards.loyalty;

public enum LoyaltyCardType {

	NOTACYLON(LoyaltyType.HUMAN),
	CYLON(LoyaltyType.CYLON),
	SYMPATHIZER(LoyaltyType.HUMAN),  //TODO: change me
	GOAL(LoyaltyType.HUMAN),
	FINALFIVE(LoyaltyType.HUMAN), 
	SYMPATHETIC_CYLON(LoyaltyType.CYLON);
	
	private LoyaltyType type;
	
	private LoyaltyCardType(LoyaltyType lt) {
		type = lt;
	}
	
	public LoyaltyType getLoyalty() {
		return type;
	}
	
}
