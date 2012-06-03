package com.bsg.state;

import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sumFrom;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

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
import com.bsg.cards.loyalty.LoyaltyType;
import com.bsg.cards.quorum.QuorumCard;
import com.bsg.cards.quorum.QuorumState;
import com.bsg.cards.quorum.QuorumType;
import com.bsg.cards.skill.DestinyDeck;
import com.bsg.cards.skill.SkillCard;
import com.bsg.cards.skill.SkillCardInitializer;
import com.bsg.cards.skill.SkillCardType;
import com.bsg.characters.Character;
import com.bsg.damage.Damage;
import com.bsg.damage.DamageToken;
import com.bsg.locations.Location;

public class GameState {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GameState.class);

	private static final Matcher<LoyaltyCard> CYLON_MATCHER = new Predicate<LoyaltyCard>() {
		@Override
		public boolean apply(LoyaltyCard lc) {
			return lc.getType() == LoyaltyCardType.CYLON;
		}
	};
	
	private static final Pattern RESOURCE_DAMAGE_PATTERN = Pattern.compile("Resource - (\\w+)");
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
	List<QuorumCard> availableQuorumCards;
	
	List<DestinationCard> destinationsTravelledTo;
	
	LoyaltyDeck loyaltyDeck;
	Deck<CrisisCard> crisisDeck;
	Deck<DestinationCard> destinationDeck;
	QuorumState quorum;
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
	
	int nukes;
	
	static final int halfFood = 4;
	static final int halfFuel = 4;
	static final int halfMorale = 5;
	static final int halfPopulation = 6;
	
	int turn;
	int subturn;
	
	int[] boarding = new int[5];

	private Damage damage;
	
	public GameState(List<Character> availableCharacters,
			List<LoyaltyCard> availableLoyalty, 
			List<CrisisCard> availableCrisisCards, 
			List<Location> availableLocations,
			List<DestinationCard> availableDestinaions,
			List<QuorumCard> availableQuorumCards) {
		players = new ArrayList<Player>();
		this.availableLoyalty = availableLoyalty;
		this.availableCharacters = availableCharacters;
		this.availableCrisisCards = availableCrisisCards;
		this.availableLocations = availableLocations;
		this.availableDestinations = availableDestinaions;
		this.availableQuorumCards = availableQuorumCards;
		
		usedCharacters = new HashSet<Character>();
		jumpTrack = new JumpTrack();
		destinationsTravelledTo = new ArrayList<DestinationCard>();
		
		skillCardDecks = new HashMap<SkillCardType, Deck<SkillCard>>();
		
		
		fuel = 8;
		food = 8;
		morale = 10;
		population = 12;
		
		nukes = 2;
		
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
		availableQuorumCards = filter(expansionFilter, availableQuorumCards);
		
		skillCardDecks.put(SkillCardType.POLITICS, new Deck<SkillCard>(SkillCardInitializer.getPoliticsCards(expansions)));
		skillCardDecks.put(SkillCardType.LEADERSHIP, new Deck<SkillCard>(SkillCardInitializer.getLeadershipCards(expansions)));
		skillCardDecks.put(SkillCardType.TACTICS, new Deck<SkillCard>(SkillCardInitializer.getTacticsCards(expansions)));
		skillCardDecks.put(SkillCardType.PILOTING, new Deck<SkillCard>(SkillCardInitializer.getPilotingCards(expansions)));
		skillCardDecks.put(SkillCardType.ENGINEERING, new Deck<SkillCard>(SkillCardInitializer.getEngineeringCards(expansions)));
		skillCardDecks.put(SkillCardType.TRECHERY, new Deck<SkillCard>(SkillCardInitializer.getTrecheryCards(expansions)));
		
		damage = new Damage(expansions);
		
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
		int playerSize = players.size();
		
		for (Player p : players)
			if (p.getLoyalty() == LoyaltyType.CYLON_LEADER)
				playerSize--;
		
		List<LoyaltyCard> loyaltyCards = filter(expansionFilter, availableLoyalty);
		loyaltyDeck = new LoyaltyDeck(playerSize, loyaltyCards, expansions.contains(Expansion.EXODUS), useFinalFive, usePersonalGoals, 0);
	}
	
	public void removeCylonAttackCrisisCards() {
		availableCrisisCards = filter(new CrisisCard.NormalCrisisCardMatcher(), availableCrisisCards);
	}

	public void dealLoyalty() {
		for (Player p : players) {
			if (p.getLoyalty() == LoyaltyType.CYLON_LEADER)
				continue; 
			
			p.giveLoyaltyCard(loyaltyDeck.dealTopCard());
		}
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
		quorum = new QuorumState(availableQuorumCards);
		quorum.dealQuorumCard();
	}
	
	public QuorumState getQuorumDeck() {
		return quorum;
	}
	
	public Deck<CrisisCard> getCrisisDeck() {
		return crisisDeck;
	}
	
	public Deck<DestinationCard> getDestinationDeck() {
		return destinationDeck;
	}

	public void jumpToTopCard() {
		DestinationCard dc = destinationDeck.deal();
		destinationsTravelledTo.add(dc);
		try {
			destinationDeck.discard(dc);
		} catch (DoesNotBelongInDeckException e) {
			JOptionPane.showMessageDialog(null /*TODO: fix */, e.toString(), "Error jumping", JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	public List<DestinationCard> getTravelledList() {
		return new ArrayList<DestinationCard>(destinationsTravelledTo);
	}
	
	public int getDistance() {
		return sumFrom(destinationsTravelledTo, DestinationCard.class).getDistance();
	}
	
	public LoyaltyDeck getLoyaltyDeck() {
		return loyaltyDeck;
	}
	
	public void dealSleeper() {
		for (Player p : getPlayers()) {
			if (p.getLoyalty() == LoyaltyType.CYLON_LEADER)
				continue;
			p.giveLoyaltyCard(loyaltyDeck.dealTopCard());
		}
	}
	
	public void legendaryDiscovery() {
		destinationsTravelledTo.add(new DestinationCard("Legendary Discovery", 1, 0, null, Expansion.BASE));
	}

	public void playQuorumCard(QuorumCard qc) throws DoesNotBelongInDeckException {
		QuorumType type = qc.getType();
		
		switch (type) {
		case DISCARD:
			quorum.discardQuorumCard(qc);
			break;
		case GAME:
		case PLAYER:
			quorum.activateQuorumCard(qc);
			break;
		case REMOVE:
			quorum.removeQuorumCard(qc);
			break;
		}
		
		
	}
	
	public int getNukeCount() {
		return nukes;
	}
	
	public void useNuke() {
		nukes--;
	}
	
	public void buildNuke() {
		nukes++;
	}
	
	public Damage getDamage() {
		return damage;
	}
	
	public void resolveDamage(DamageToken dt) {
		
		
		if (dt.getName().startsWith("Resource")) {
			java.util.regex.Matcher m = RESOURCE_DAMAGE_PATTERN.matcher(dt.getName());
			if (!m.matches()) {
				throw new IllegalStateException("DamageToken does not match resource pattern.");
			}

			String resource = m.group(1);
			
			if ("Food".equals(resource))
				food--;
			if ("Fuel".equals(resource))
				fuel--;
			
		}
		
		for (Location curr : availableLocations) {
			//O(n) oh well
			if (dt.getName().equals(curr.getName())) {
				curr.setDamaged(true);
				LOGGER.info("{} has been damaged!", curr.getName());
				break;
			}
		}
		
		damage.activateToken(dt);
		
	}
}
