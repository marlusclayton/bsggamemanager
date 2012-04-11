package com.bsg.cards.quorum;

import com.bsg.Expansion;
import com.bsg.Item;
import com.bsg.cards.Card;

public class QuorumCard extends Card implements Item {

	private final String name;
	private final String text;
	private final QuorumType type;
	private final Expansion expansion;
	
	public QuorumCard(String name, String text, QuorumType type, Expansion expansion) {
		this.name = name;
		this.text = text;
		this.type = type;
		this.expansion = expansion;
	}
	
	
	@Override
	public Expansion getExpansion() {
		return expansion;
	}
}
