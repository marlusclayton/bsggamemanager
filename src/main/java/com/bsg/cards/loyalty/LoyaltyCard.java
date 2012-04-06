package com.bsg.cards.loyalty;

import com.bsg.Expansion;
import com.bsg.Item;

public class LoyaltyCard implements Item {

	private String title;
	private LoyaltyCardType type;
	private String text;
	private Expansion expansion;
	
	public LoyaltyCard(LoyaltyCard other) {
		this.title = other.title;
		this.type = other.type;
		this.text = other.text;
		this.expansion = other.expansion;
	}
	
	public LoyaltyCard(String title, LoyaltyCardType type, String text, Expansion expansion) {
		this.title = title;
		this.type = type;
		this.text = text;
		this.expansion = expansion;
	}
	
	public String getTitle() {
		return title;
	}
	
	public LoyaltyCardType getType() {
		return type;
	}
	
	public String getText() {
		return text;
	}
	
	public Expansion getExpansion() {
		return expansion;
	}
	
	@Override
	public String toString() {
		return title;
	}
}
