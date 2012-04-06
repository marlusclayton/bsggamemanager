package com.bsg.cards.skill;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DestinyDeck {

	
	private List<SkillCard> deck;
	
	public DestinyDeck() {
		deck = new ArrayList<SkillCard>();
	}
	
	public void initNewDeck(Collection<SkillCard> cards) {
		assert deck.isEmpty();
		deck.addAll(cards);
		
		Collections.shuffle(deck);
	}
	
	public SkillCard peek() {
		return deck.get(0);
	}
	
	public SkillCard getTop() {
		SkillCard ret = deck.get(0);
		deck.remove(0);
		return ret;
	}
	
	public void placeCardOnTop(SkillCard c) {
		deck.add(0, c);
	}
	
	public int getSize() {
		return deck.size();
	}
}
