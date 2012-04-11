package com.bsg.cards.quorum;

import java.util.ArrayList;
import java.util.List;

import com.bsg.Player;
import com.bsg.cards.Deck;
import com.bsg.cards.DoesNotBelongInDeckException;

public class QuorumState {

	private Deck<QuorumCard> deck;
	private List<QuorumCard> hand;
	private List<QuorumCard> active;
	private List<QuorumCard> removed;
	
	public QuorumState(List<QuorumCard> cards) {
		deck = new Deck<QuorumCard>(cards);
		hand = new ArrayList<QuorumCard>();
		active = new ArrayList<QuorumCard>();
		removed = new ArrayList<QuorumCard>();

	}
	
	public void dealQuorumCard() {
		hand.add(deck.deal());
	}
	
	public void discardQuorumCard(QuorumCard qc) throws DoesNotBelongInDeckException {
		checkHand(qc);
		hand.remove(qc);
		deck.discard(qc);
		
	}
	
	public void activateQuorumCard(QuorumCard qc, Player p) {
		checkHand(qc);
		hand.remove(qc);
		
		qc.setActiveOn(p);
		active.add(qc);
	}
	
	public void deactivateQuorumCard(QuorumCard qc) throws DoesNotBelongInDeckException {
		if (!active.contains(qc))
			throw new IllegalStateException("Can not deactivate inactive Quorum Card");
		
		active.remove(qc);
		qc.setActiveOn(null);
		deck.discard(qc);
	}
	
	public Deck<QuorumCard> getDeck() {
		return deck;
	}
	
	public List<QuorumCard> getHand() {
		return new ArrayList<QuorumCard>(hand);
	}
	
	public void removeQuorumCard(QuorumCard qc) {
		checkHand(qc);
		removed.add(qc);
	}
	
	private void checkHand(QuorumCard qc) {
		if (!hand.contains(qc))
			throw new IllegalArgumentException("Can not discard a quorum card you don't have");
	}
}
