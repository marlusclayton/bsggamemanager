package com.bsg.cards.quorum;

import com.bsg.Expansion;
import com.bsg.Item;
import com.bsg.Player;
import com.bsg.cards.Card;

public class QuorumCard extends Card implements Item {

	private final String name;
	private final String text;
	private final QuorumType type;
	private final Expansion expansion;
	
	private Player activeOn;
	
	public QuorumCard(String name, String text, QuorumType type, Expansion expansion) {
		this.name = name;
		this.text = text;
		this.type = type;
		this.expansion = expansion;
	}
	
	public void setActiveOn(Player activeOn) {
		this.activeOn = activeOn;
	}
	
	public Player getActiveOn() {
		return activeOn;
	}
	
	public QuorumType getType() {
		return type;
	}
	
	public String getText() {
		return text;
	}
	
	@Override
	public String toString() {
		return String.format("%s%s", name, activeOn != null ? " (active on " + activeOn.getCharacter().getShortName() + ")" : "");
	}
	
	@Override
	public Expansion getExpansion() {
		return expansion;
	}
}
