package com.bsg.cards.quorum;

public enum QuorumType {

	DISCARD("Discard after use"),
	REMOVE("Remove after use"),
	PLAYER("Assign to player"),
	GAME("Stays in effect");
	
	private String description;
	
	private QuorumType(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	@Override
	public String toString() {
		return description;
	}
}
