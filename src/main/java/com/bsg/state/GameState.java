package com.bsg.state;

import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.hamcrest.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.lambdaj.function.matcher.Predicate;

import com.bsg.Expansion;
import com.bsg.Item;
import com.bsg.Player;
import com.bsg.cards.Deck;
import com.bsg.cards.DoesNotBelongInDeckException;
import com.bsg.cards.crisis.CrisisCard;
import com.bsg.cards.destination.DestinationCard;
import com.bsg.cards.loyalty.LoyaltyCard;
import com.bsg.cards.loyalty.LoyaltyCardType;
import com.bsg.cards.loyalty.LoyaltyDeck;
import com.bsg.cards.skill.DestinyDeck;
import com.bsg.cards.skill.SkillCard;
import com.bsg.cards.skill.SkillCardInitializer;
import com.bsg.cards.skill.SkillCardType;
import com.bsg.characters.Character;
import com.bsg.locations.Location;

public class GameState {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GameState.class);

	private static final Matcher<LoyaltyCard> CYLON_MATCHER = new Predicate<LoyaltyCard>() {
		@Override
		public boolean apply(LoyaltyCard lc) {
			return lc.getType() == LoyaltyCardType.CYLON;
		}
	};
	
	private static final Random RNG = new Random();
	
	Matcher<? extends Item> expansionFilter;
	Set<Expansion> expansions;
	
	List<Player> players;
	Set<Character> usedCharacters;
	List<Character> availableCharacters;
	List<LoyaltyCard> availableLoyalty;
	List<CrisisCard> availableCrisisCards;
	List<Location> availableLocations;
	List<DestinationCard> availableDestinations;
	
	List<DestinationCard> destinationsTravelledTo;
	
	LoyaltyDeck loyaltyDeck;
	Deck<CrisisCard> crisisDeck;
	Deck<DestinationCard> destinationDeck;
	Map<SkillCardType, Deck<SkillCard>> skillCardDecks;
	
	Deck<SkillCard> polDeck;
	Deck<SkillCard> leaDeck;
	Deck<SkillCard> tacDeck;
	Deck<SkillCard> pilDeck;
	Deck<SkillCard> engDeck;
	Deck<SkillCard> treDeck;
	
	DestinyDeck destiny;
	SkillCheck currentSkillCheck;
	
	JumpTrack jumpTrack;
	
	int fuel;
	int food;
	int morale;
	int population;
	
	int distance;
	
	static final int halfFood = 4;
	static final int halfFuel = 4;
	static final int halfMorale = 5;
	static final int halfPopulation = 6;
	
	int turn;
	int subturn;
	
	int[] boarding = new int[5];
	
	public GameState(List<Character> availableCharacters,
			List<LoyaltyCard> availableLoyalty, 
			List<CrisisCard> availableCrisisCards, 
			List<Location> availableLocations,
			List<DestinationCard> availableDestinaions) {
		players = new ArrayList<Player>();
		this.availableLoyalty = availableLoyalty;
		this.availableCharacters = availableCharacters;
		this.availableCrisisCards = availableCrisisCards;
		this.availableLocations = availableLocations;
		this.availableDestinations = availableDestinaions;
		
		usedCharacters = new HashSet<Character>();
		jumpTrack = new JumpTrack();
		destinationsTravelledTo = new ArrayList<DestinationCard>();
		
		skillCardDecks = new HashMap<SkillCardType, Deck<SkillCard>>();
		
		
		fuel = 8;
		food = 8;
		morale = 10;
		population = 12;
		
		turn = 1;
		subturn = 1;
		
	}
	
	public void addBoardingParty() {
		boarding[0]++;
	}
	
	public void advanceBoardingParty() {
		boarding[4] = boarding[3];
		boarding[3] = boarding[2];
		boarding[2] = boarding[1];
		boarding[1] = boarding[0];
		boarding[0] = 0;
	}
	
	public void destroyBoarderAt(int loc) {
		boarding[loc] = Math.max(0, --boarding[loc]);
	}
	
	public int getNumBoardersAt(int loc) {
		return boarding[loc];
	}
	
	public void setExpansionsAndInitLists(Set<Expansion> ex) {
		this.expansions = EnumSet.copyOf(ex);
		expansionFilter = new Predicate<Item>() {

			@Override
			public boolean apply(Item e) {
				return expansions.contains(e.getExpansion());
			}
		};
		
		availableCharacters = filter(expansionFilter, availableCharacters);
		availableCrisisCards = filter(expansionFilter, availableCrisisCards);
		availableLocations = filter(expansionFilter, availableLocations);
		availableDestinations = filter(expansionFilter, availableDestinations);
		
		skillCardDecks.put(SkillCardType.POLITICS, new Deck<SkillCard>(SkillCardInitializer.getPoliticsCards(expansions)));
		skillCardDecks.put(SkillCardType.LEADERSHIP, new Deck<SkillCard>(SkillCardInitializer.getLeadershipCards(expansions)));
		skillCardDecks.put(SkillCardType.TACTICS, new Deck<SkillCard>(SkillCardInitializer.getTacticsCards(expansions)));
		skillCardDecks.put(SkillCardType.PILOTING, new Deck<SkillCard>(SkillCardInitializer.getPilotingCards(expansions)));
		skillCardDecks.put(SkillCardType.ENGINEERING, new Deck<SkillCard>(SkillCardInitializer.getEngineeringCards(expansions)));
		skillCardDecks.put(SkillCardType.TRECHERY, new Deck<SkillCard>(SkillCardInitializer.getTrecheryCards(expansions)));
		
		initDestiny();
	}
	
	public void advanceTurn() {
		++subturn;
		if (subturn > players.size()) {
			++turn;
			subturn = 1;
		}
	}
	
	public List<Location> getLocations() {
		return new ArrayList<Location>(availableLocations);
	}
	
	public Player getPresident() {
		List<Player> pres = filter(having(on(Player.class).isPresident()), players);
		return pres.size() != 0 ? pres.get(0) : null;
	}
	
	public Player getAdmiral() {
		List<Player> pres = filter(having(on(Player.class).isAdmiral()), players);
		return pres.size() != 0 ? pres.get(0) : null;
	}
	
	public Player getCAG() {
		List<Player> pres = filter(having(on(Player.class).isCAG()), players);
		return pres.size() != 0 ? pres.get(0) : null;
	}
	
	public void makePresident(Player p) {
		Player oldPres = getPresident();
		if (oldPres != null)
			oldPres.setPresident(false);
		p.setPresident(true);
	}
	
	public void makeAdmiral(Player p) {
		Player oldAdm = getAdmiral();
		if (oldAdm != null)
			oldAdm.setAdmiral(false);
		p.setAdmiral(true);
	}
	
	public void makeCAG(Player p) {
		Player oldCAG = getCAG();
		if (oldCAG != null)
			oldCAG.setCAG(false);
		p.setCAG(true);
	}
	
	public static int rollD8() {
		return RNG.nextInt(8) + 1;
	}
	
	public void buildLoyaltyDeck(boolean usePersonalGoals, boolean useFinalFive) {
		List<LoyaltyCard> loyaltyCards = filter(expansionFilter, availableLoyalty);
		loyaltyDeck = new LoyaltyDeck(players.size(), loyaltyCards, expansions.contains(Expansion.EXODUS), useFinalFive, usePersonalGoals, 0);
	}
	
	public void removeCylonAttackCrisisCards() {
		availableCrisisCards = filter(new CrisisCard.NormalCrisisCardMatcher(), availableCrisisCards);
	}

	public void dealLoyalty() {
		for (Player p : players)
			p.giveLoyaltyCard(loyaltyDeck.dealTopCard());
	}
	
	public void addSympathizer(LoyaltyCardType type) {
		if (type == LoyaltyCardType.SYMPATHETIC_CYLON)
			loyaltyDeck.addSympatheticCylon();
		else if (type == LoyaltyCardType.SYMPATHIZER)
			loyaltyDeck.addSympathizer();
	}
	
	public JumpTrack getJumpTrack() {
		return jumpTrack;
	}
	
	public void setupPlayerLocations() {
		for (Player p : players) {
			//this below is terrible as all hell
			if (availableLocations.contains(new Location(p.getCharacter().getSetup(), "", Expansion.BASE)))
				p.setLocation(p.getCharacter().getSetup());
		}
	}
	
	private void initDestiny() {
		destiny = new DestinyDeck();
		
		Set<SkillCard> destinyCards = new HashSet<SkillCard>();
		for (SkillCardType type : SkillCardType.values()) {
			if (type == SkillCardType.TRECHERY && !expansions.contains(Expansion.PEGASUS))
				continue;
			destinyCards.add(skillCardDecks.get(type).deal());
			destinyCards.add(skillCardDecks.get(type).deal());
		}
		
		destiny.initNewDeck(destinyCards);
		
	}
	
	public Set<SkillCard> getTwoDestiny() {
		Set<SkillCard> cards = new HashSet<SkillCard>(2);
		if (destiny.getSize() == 0) {
			initDestiny();
		}
		
		cards.add(destiny.getTop());
		cards.add(destiny.getTop());
		
		return cards;
	}

	public void addPlayer(Player p) throws DuplicateCharacterException {
		if (usedCharacters.contains(p.getCharacter()))
			throw new DuplicateCharacterException(String.format("%s is already claimed as a character!", p.getCharacter().getName()));
		LOGGER.info("Adding player {}", p.getPlayerName());
		players.add(p);
		usedCharacters.add(p.getCharacter());
	}
	
	public List<Player> getPlayers() {
		return new ArrayList<Player>(players);
	}
	
	public Deck<SkillCard> getSkillCardDeck(SkillCardType type) {
		return skillCardDecks.get(type);
	}
	
	public DestinyDeck getDestinyDeck() {
		return destiny;
	}
	
	public void startSkillCheck(SkillCheck sc) {
		currentSkillCheck = sc;
	}
	
	public SkillCheck getSkillCheck() {
		return currentSkillCheck;
	}
	
	public void discardSkillCard(SkillCard sc) {
		try {
			skillCardDecks.get(sc.getType()).discard(sc);
		} catch (DoesNotBelongInDeckException e) {
			LOGGER.error("Error putting {} card back to {}", sc, sc.getType());
		}
	}

	/**
	 * @return the fuel
	 */
	public int getFuel() {
		return fuel;
	}

	/**
	 * @param fuel the fuel to set
	 */
	public void setFuel(int fuel) {
		this.fuel = fuel;
	}

	/**
	 * @return the food
	 */
	public int getFood() {
		return food;
	}

	/**
	 * @param food the food to set
	 */
	public void setFood(int food) {
		this.food = food;
	}

	/**
	 * @return the morale
	 */
	public int getMorale() {
		return morale;
	}

	/**
	 * @param morale the morale to set
	 */
	public void setMorale(int morale) {
		this.morale = morale;
	}

	/**
	 * @return the population
	 */
	public int getPopulation() {
		return population;
	}

	/**
	 * @param population the population to set
	 */
	public void setPopulation(int population) {
		this.population = population;
	}
	
	public int getTurn() {
		return turn;
	}
	
	public int getSubturn() {
		return subturn;
	}
	
	public List<Character> getAvailableCharacters() {
		return new ArrayList<Character>(availableCharacters);
	}

	public void createDeck() {
		crisisDeck = new Deck<CrisisCard>(availableCrisisCards);
		destinationDeck = new Deck<DestinationCard>(availableDestinations);
	}
	
	public Deck<CrisisCard> getCrisisDeck() {
		return crisisDeck;
	}
	
	public Deck<DestinationCard> getDestinationDeck() {
		return destinationDeck;
	}

	public void jumpToTopCard() {
		DestinationCard dc = destinationDeck.deal();
		distance += dc.getDistance();
		destinationsTravelledTo.add(dc);
		try {
			destinationDeck.discard(dc);
		} catch (DoesNotBelongInDeckException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public List<DestinationCard> getTravelledList() {
		return new ArrayList<DestinationCard>(destinationsTravelledTo);
	}
	
	public int getDistance() {
		return distance;
	}
	
	public LoyaltyDeck getLoyaltyDeck() {
		return loyaltyDeck;
	}
}
