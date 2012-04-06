package com.bsg.cards.loyalty.generator;

import java.util.HashSet;
import java.util.Set;

import com.bsg.Expansion;
import com.bsg.cards.loyalty.LoyaltyCard;
import com.bsg.cards.loyalty.LoyaltyCardType;

public class YouAreNotACylonCardGenerator {

	private static final LoyaltyCard YANAC_CARD = new LoyaltyCard("You Are Not A Cylon", LoyaltyCardType.NOTACYLON, "You are not a cylon...yet", Expansion.BASE);

	
	public static Set<LoyaltyCard> generateYANAC(int qty) {
		Set<LoyaltyCard> ret = new HashSet<LoyaltyCard>();
		
		for (int i = 0; i < qty; ++i)
			ret.add(new LoyaltyCard(YANAC_CARD));
		
		return ret;
	}
}
