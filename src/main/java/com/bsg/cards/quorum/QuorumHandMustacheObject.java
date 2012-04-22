package com.bsg.cards.quorum;

import java.util.ArrayList;
import java.util.List;

public class QuorumHandMustacheObject {

	private final List<QuorumCard> hand;
	
	public QuorumHandMustacheObject(List<QuorumCard> hand) {
		this.hand = hand;
	}
	
	public List<QuorumCard> getHand() {
		return new ArrayList<QuorumCard>(hand);
	}
}
