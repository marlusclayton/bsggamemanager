package com.bsg;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bsg.cards.loyalty.LoyaltyCard;
import com.bsg.cards.loyalty.LoyaltyType;
import com.bsg.cards.skill.SkillCard;
import com.bsg.characters.Character;

public class Player {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Player.class);

	private String playerName;
	private String currLocation;
	private Character character;
	private boolean hasOPG;
	private boolean isPresident;
	private boolean isAdmiral;
	private boolean isCAG;
	private boolean isRevealed;
	
	
	private Set<LoyaltyCard> loyaltyCards;
	private SortedSet<SkillCard> hand;
	
	public Player(String playerName, Character character) {
		this.playerName = playerName;
		this.character = character;
		hasOPG = true;
		
		hand = new TreeSet<SkillCard>();
		loyaltyCards = new HashSet<LoyaltyCard>();
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	public Character getCharacter() {
		return character;
	}
	
	public boolean hasOPG() {
		return hasOPG;
	}
	
	public void setHasOPG(boolean hasOPG) {
		this.hasOPG = hasOPG;
	}
	
	public void giveSkillCard(SkillCard card) {
		hand.add(card);
		LOGGER.info("Player {} got got {}", playerName, card);
	}
	
	public boolean discardSkillCard(SkillCard card) {
		boolean success = hand.remove(card);
		if (!success)
			LOGGER.warn("Tried to remove non-existant card {} from {}", card, playerName);
		LOGGER.info("Player {} discarded {}", playerName, card);
		
		return success;
	}
	
	public SortedSet<SkillCard> getHand() {
		return new TreeSet<SkillCard>(hand);
	}
	
	public Set<LoyaltyCard> getLoyaltyCards() {
		return new HashSet<LoyaltyCard>(loyaltyCards);
	}
	
	
	/*for mustache parsing*/
	public Set<LoyaltyCard> loyalty_cards() {
		return getLoyaltyCards();
	}
	
	public void giveLoyaltyCard(LoyaltyCard lc) {
		LOGGER.info("Player {} got {} loyalty card", playerName, lc.getTitle());
		loyaltyCards.add(lc);
	}
	
	public void removeLoyaltyCard(LoyaltyCard lc) {
		LOGGER.info("Player {} is discarding {}", playerName, lc.getTitle());
		if (!loyaltyCards.remove(lc)) {
			LOGGER.warn("Unable to remove loyalty card from hand");
		}
	}
	
	public LoyaltyType getLoyalty() {
		if ("Cylon Leader".equals(getCharacter().getType())) 
			return LoyaltyType.CYLON_LEADER;
		
		boolean isCylon = false;
		for (LoyaltyCard c : loyaltyCards)
			if (c.getType().getLoyalty() == LoyaltyType.CYLON)
				isCylon = true;
		
		if (!isCylon)
			return LoyaltyType.HUMAN;
		
		return isRevealed ? LoyaltyType.REVEALED_CYLON : LoyaltyType.CYLON;
		
	}
	
	public boolean isPresident() {
		return isPresident;
	}
	
	public boolean isAdmiral() {
		return isAdmiral;
	}
	
	public boolean isCAG() {
		return isCAG;
	}
	
	public void setPresident(boolean isPresident) {
		this.isPresident = isPresident;
	}
	
	public void setAdmiral(boolean isAdmiral) {
		this.isAdmiral = isAdmiral;
	}
	
	public void setCAG(boolean isCAG) {
		this.isCAG = isCAG;
	}
	
	@Override
	public String toString() {
		return String.format("%s (%s)", playerName, character.getShortName());
	}
	
	public String getTitles() {
		return String.format("%s%s%s", isPresident ? "President " : "", isAdmiral ? "Admiral " : "", isCAG ? "CAG " : "");
	}

	public void setLocation(String location) {
		currLocation = location;
	}
	
	public String getLocation() {
		return currLocation;
	}

	public boolean isRevealed() {
		return isRevealed;
	}

	public void setRevealed(boolean isRevealed) {
		this.isRevealed = isRevealed;
	}
	
	public String getDrawString() {
		return isRevealed ? "**Two Cards**" : character.getDraw();
	}
}
