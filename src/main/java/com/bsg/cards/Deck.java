package com.bsg.cards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Deck<T extends Card> {
	
	private final int maxDeckSize;
	
	private List<T> deck;
	private Set<T> outstanding;
	private List<T> discards;
	
	
	public Deck(Collection<T> cards) {
		maxDeckSize = cards.size();
		
		deck = new ArrayList<T>(maxDeckSize);
		discards = new ArrayList<T>(maxDeckSize);  
		outstanding = new HashSet<T>(maxDeckSize * 2); //multiply by two so you don't exceed load factor
		
		
		deck.addAll(cards);
		Collections.shuffle(deck);
	}
	
	public List<T> getDeckContents() {
		return new ArrayList<T>(deck);
	}
	
	public List<T> getDiscardContents() {
		return new ArrayList<T>(discards);
	}
	
	public T deal() {
		T ret =  deck.get(0);
		deck.remove(0);
		outstanding.add(ret);
		if (deck.isEmpty())
			reshuffle();
		return ret;
	}
	
	public void play(int idx) {
		T card = deck.get(idx);
		deck.remove(idx);
		discards.add(0, card);
	}
	
	public T peekAtTop() {
		return deck.get(0);
	}
	
	public T peekAtBottom() {
		return deck.get(deck.size() - 1);
	}

	private void reshuffle() {
		assert deck.isEmpty();
		
		deck.addAll(discards);
		discards = new ArrayList<T>(maxDeckSize * 2);
		Collections.shuffle(deck);
	}
	
	public void discard(T card) throws DoesNotBelongInDeckException {
		if (!outstanding.contains(card))
			throw new DoesNotBelongInDeckException();
		outstanding.remove(card);
		discards.add(0, card);
	}
	
	public void bury(int idx) {
		T top = deck.get(idx);
		deck.remove(idx);
		deck.add(top);
	}
	
	public void bury() {
		bury(0);
	}
	
	public void shuffleInPlace() {
		Collections.shuffle(deck);
	}
	
	public int getDeckSize() {
		return deck.size();
	}
	
	public int getDiscardSize() {
		return discards.size();
	}
	
	
}
