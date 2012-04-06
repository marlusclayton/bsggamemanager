package com.bsg.cards.crisis;

import ch.lambdaj.function.matcher.Predicate;

import com.bsg.Expansion;
import com.bsg.Item;
import com.bsg.cards.Card;

public class CrisisCard extends Card implements Item {

	public static class NormalCrisisCardMatcher extends Predicate<CrisisCard> {

		@Override
		public boolean apply(CrisisCard cc) {
			return cc.getType() != CrisisType.ATTACK;
		}
		
	}
	
	private String name;
	private CrisisType type;
	private String description;
	private Expansion expansion;
	
	public CrisisCard(String name, CrisisType type, String description, Expansion expansion) {
		this.name = name;
		this.type = type;
		this.description = description;
		this.expansion = expansion;
	}
	
	public String getName() {
		return name;
	}
	
	public CrisisType getType() {
		return type;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Expansion getExpansion() {
		return expansion;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
