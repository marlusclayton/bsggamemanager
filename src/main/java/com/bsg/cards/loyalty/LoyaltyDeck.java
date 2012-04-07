package com.bsg.cards.loyalty;

import static ch.lambdaj.Lambda.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hamcrest.Matcher;

import ch.lambdaj.function.matcher.Predicate;

import com.bsg.cards.loyalty.generator.YouAreNotACylonCardGenerator;

public class LoyaltyDeck {
	
	private static final Matcher<LoyaltyCard> CYLON_FILTER = new Predicate<LoyaltyCard>() {

		@Override
		public boolean apply(LoyaltyCard lc) {
			return lc.getType() == LoyaltyCardType.CYLON;
		}
	};
	
	private static final Matcher<LoyaltyCard> FINALFILVE_FILTER = new Predicate<LoyaltyCard>() {

		@Override
		public boolean apply(LoyaltyCard lc) {
			return lc.getType() == LoyaltyCardType.FINALFIVE;
		}
	};
	
	private static final Matcher<LoyaltyCard> GOAL_FILTER = new Predicate<LoyaltyCard>() {

		@Override
		public boolean apply(LoyaltyCard lc) {
			return lc.getType() == LoyaltyCardType.GOAL;
		}
	};
	
	private static final Matcher<LoyaltyCard> SYMPTHAIZER_FILTER = new Predicate<LoyaltyCard>() {

		@Override
		public boolean apply(LoyaltyCard lc) {
			return lc.getType() == LoyaltyCardType.SYMPATHIZER;
		}
	};
	
	private static final Matcher<LoyaltyCard> SYMPATHETIC_CYLON_FILTER = new Predicate<LoyaltyCard>() {

		@Override
		public boolean apply(LoyaltyCard lc) {
			return lc.getType() == LoyaltyCardType.SYMPATHETIC_CYLON;
		}
	};
	
	private int numPlayers;
	private int addlCards;
	
	private boolean useExodusRules;
	
	private List<LoyaltyCard> deck;
	private List<LoyaltyCard> yanacDeck;
	private List<LoyaltyCard> cylonCards;
	private List<LoyaltyCard> personalGoalCards;
	private List<LoyaltyCard> finalFiveCards;
	private List<LoyaltyCard> sympathizerCard;
	private List<LoyaltyCard> sympatheticCylonCard;

	public LoyaltyDeck(int numPlayers, List<LoyaltyCard> loyaltyCards, boolean useExodusRules, boolean useFinalFive, boolean usePersonalGoals, int addlCards) {
		this.numPlayers = numPlayers;
		this.addlCards = addlCards;
		this.useExodusRules = useExodusRules;
		
		deck = new ArrayList<LoyaltyCard>();
		
		cylonCards = filter(CYLON_FILTER, loyaltyCards);
		personalGoalCards = filter(GOAL_FILTER, loyaltyCards);
		finalFiveCards = filter(FINALFILVE_FILTER, loyaltyCards);
		sympathizerCard = filter(SYMPTHAIZER_FILTER, loyaltyCards);
		sympatheticCylonCard = filter(SYMPATHETIC_CYLON_FILTER, loyaltyCards);
		
		Collections.shuffle(cylonCards);
		Collections.shuffle(personalGoalCards);
		Collections.shuffle(finalFiveCards);
		
		if (!useExodusRules) {
			createStandardLoyaltyDeck();
		} else {
			createExodusLoyaltyDeck(useFinalFive, usePersonalGoals);
		}
	}

	private void createExodusLoyaltyDeck(boolean useFinalFive, boolean usePersonalGoals) {
		int numCylonCards = -1;
		int numYanacCards = -1;
		
		List<LoyaltyCard> yanacDeck = new ArrayList<LoyaltyCard>();
		yanacDeck.addAll(YouAreNotACylonCardGenerator.generateYANAC(16));
		
		if (useFinalFive)
			yanacDeck.addAll(finalFiveCards);
		
		if (usePersonalGoals)
			yanacDeck.addAll(personalGoalCards);
		
		List<LoyaltyCard> cylonDeck = new ArrayList<LoyaltyCard>(cylonCards);
		
		Collections.shuffle(yanacDeck);
		Collections.shuffle(cylonDeck);
		
		if (numPlayers == 3) {
			numCylonCards = 1;
			numYanacCards = 6;
		} else if (numPlayers == 4) {
			numCylonCards = 1;
			numYanacCards = 7;
		} else if (numPlayers == 5) {
			numCylonCards = 2;
			numYanacCards = 9;
		} else if (numPlayers == 6) {
			numCylonCards = 2;
			numYanacCards = 10;
		} else if (numPlayers == 7) {
			numCylonCards = 2;
			numYanacCards = 11;
		}
		
		assert numCylonCards != -1;
		assert numYanacCards != -1;
		
		for (int i = 0; i < numCylonCards; ++i) {
			deck.add(cylonDeck.get(0));
			cylonDeck.remove(0);
		}
		
		for (int i = 0; i < numYanacCards; ++i) {
			deck.add(yanacDeck.get(0));
			yanacDeck.remove(0);
		}
		
		Collections.shuffle(deck);
	}

	private void createStandardLoyaltyDeck() {

		
		int numCylonCards = -1;
		int numYanacCards = -1;
		
		if (numPlayers == 3) {
			numCylonCards = 1;
			numYanacCards = 5;
		} else if (numPlayers == 4) {
			numCylonCards = 1;
			numYanacCards = 6;
		} else if (numPlayers == 5) {
			numCylonCards = 2;
			numYanacCards = 8;
		} else if (numPlayers == 6) {
			numCylonCards = 2;
			numYanacCards = 9;
		} else if (numPlayers == 7) {
			numCylonCards = 2;
			numYanacCards = 9;
		}
		
		assert numCylonCards != -1;
		assert numYanacCards != -1;
		
		numYanacCards += addlCards;
		
		for (int i = 0; i < numCylonCards; ++i)
			deck.add(cylonCards.get(i));
		
		deck.addAll(YouAreNotACylonCardGenerator.generateYANAC(numYanacCards));
		
		Collections.shuffle(deck);
	}
	
	public void addSympathizer() {
		if (sympathizerCard.size() == 0)
			throw new IllegalArgumentException();
		
		deck.add(sympathizerCard.get(0));
		Collections.shuffle(deck);
	}
	
	public void addSympatheticCylon() {
		if (sympatheticCylonCard.size() == 0)
			throw new IllegalArgumentException();
		
		deck.add(sympatheticCylonCard.get(0));
		Collections.shuffle(deck);
	}
	
	public LoyaltyCard dealTopCard() {
		if (useExodusRules && deck.size() == 0) {
			deck.add(yanacDeck.get(0));
			yanacDeck.remove(0);
			Collections.shuffle(deck);
		}
		LoyaltyCard lc = deck.get(0);
		deck.remove(0);
		return lc;
	}
	
	public void addCardAndReshuffle(LoyaltyCard lc) {
		deck.add(lc);
		Collections.shuffle(deck);
	}
	
	public List<LoyaltyCard> getDeckContents() {
		return new ArrayList<LoyaltyCard>(deck);
	}
}
