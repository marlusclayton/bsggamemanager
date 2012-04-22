package com.bsg;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bsg.cards.crisis.CrisisCard;
import com.bsg.cards.crisis.CrisisLoader;
import com.bsg.cards.destination.DestinationCard;
import com.bsg.cards.destination.DestinationLoader;
import com.bsg.cards.loyalty.LoyaltyCard;
import com.bsg.cards.loyalty.LoyaltyCardLoader;
import com.bsg.cards.quorum.QuorumCard;
import com.bsg.cards.quorum.QuorumLoader;
import com.bsg.characters.Character;
import com.bsg.characters.CharacterLoader;
import com.bsg.locations.Location;
import com.bsg.locations.LocationLoader;
import com.bsg.state.GameState;
import com.bsg.view.GamePanel;


public class Game {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Game.class);

	private static final FilenameFilter XML_FILE_FILTER = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			return "xml".equals(FilenameUtils.getExtension(name).toLowerCase());
		}
	};
	
	private static final Map<String, String> TEMPLATES = new HashMap<String, String>() {{
		put("crisis.template", "Crisis Card template");
		put("dradis.template", "DRADIS template");
		put("hand.template", "Hand template");
		put("skillcheck.template", "Skill Check template");
		put("quorum.template", "Quorum Card template");
		put("quorumhand.template", "Quorum Hand template");
	}};
	
	private List<Character> characters;
	private List<LoyaltyCard> loyalty;
	private List<CrisisCard> crisisCards;
	private List<Location> locations;
	private List<DestinationCard> destinations;
	private List<QuorumCard> quorumCards;
	
	private Game(String[] args) throws Exception {
		setLookAndFeel();
		checkTemplates();
		loadConfig();
		
		
		GamePanel panel = new GamePanel(new GameState(characters, loyalty, crisisCards, locations, destinations, quorumCards));
		panel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel.setVisible(true);
	}
	
	private void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			//nop
		} catch (InstantiationException e) {
			//nop
		} catch (IllegalAccessException e) {
			//nop
		} catch (UnsupportedLookAndFeelException e) {
			//nop
		}
		
	}

	private void checkTemplates() throws InvalidConfigException {
		File templateDir = new File("config/templates");
		if (!templateDir.exists())
			throw new InvalidConfigException("Template directory does not exist!");
		
		for (Entry<String, String> curr : TEMPLATES.entrySet()) {
			File template = new File(templateDir, curr.getKey());
			if (!template.exists()) 
				throw new InvalidConfigException(String.format("%s does not exist", curr.getValue()));
		}
	}

	public static void main(String[] args) throws Exception {
		new Game(args);
	}
	
	private void loadConfig() throws Exception {
		long startTime = System.currentTimeMillis();
		loadCharacters();
		loadLoyalty();
		loadCrisisCards();
		loadLocations();
		loadDestinations();
		loadQuorum();
		long loadTime = System.currentTimeMillis() - startTime;
		
		LOGGER.info("Total load time: {}ms", loadTime);
	}
	
	private void loadQuorum() throws Exception {
		quorumCards = new ArrayList<QuorumCard>();
		
		File quorumConfigDirectory = new File("config/quorum");
		if (!quorumConfigDirectory.exists())
			throw new InvalidConfigException("Quorum configuration does not exist");
		
		for (File curr: quorumConfigDirectory.listFiles(XML_FILE_FILTER))
			quorumCards.addAll(new QuorumLoader(curr).getQuorumCards());
	}
	
	private void loadDestinations() throws Exception {
		destinations = new ArrayList<DestinationCard>();
		
		File destinationConfigDirectory = new File("config/destinations");
		if (!destinationConfigDirectory.exists())
			throw new InvalidConfigException("Destination configuration does not exist");
		
		for (File curr : destinationConfigDirectory.listFiles(XML_FILE_FILTER))
			destinations.addAll(new DestinationLoader(curr).getCards());
		
	}

	private void loadLoyalty() throws Exception {
		loyalty = new ArrayList<LoyaltyCard>();
		
		File loyaltyConfigDirectory = new File("config/loyalty");
		if (!loyaltyConfigDirectory.exists())
			throw new InvalidConfigException("Loyalty configuration does not exist");
		
		for (File curr: loyaltyConfigDirectory.listFiles(XML_FILE_FILTER))
			loyalty.addAll(new LoyaltyCardLoader(curr).getCards());
		
	}
	
	private void loadLocations() throws Exception {
		locations = new ArrayList<Location>();
		
		File locationConfigDirectory = new File("config/locations");
		if (!locationConfigDirectory.exists())
			throw new InvalidConfigException("Location configuration does not exist");
		
		for (File curr : locationConfigDirectory.listFiles(XML_FILE_FILTER))
			locations.addAll(new LocationLoader(curr).getLocations());
		
	}
	
	private void loadCrisisCards() throws Exception {
		crisisCards = new ArrayList<CrisisCard>();
		
		File charConfigDirectory = new File("config/crisis");
		if (!charConfigDirectory.exists())
			throw new InvalidConfigException("Crisis Card configuration does not exist");
		
		for (File curr : charConfigDirectory.listFiles(XML_FILE_FILTER)) {
			crisisCards.addAll(new CrisisLoader(curr).getCards());
		}
	}

	private void loadCharacters() throws Exception {
		characters = new ArrayList<Character>();
		
		File charConfigDirectory = new File("config/characters");
		if (!charConfigDirectory.exists())
			throw new InvalidConfigException("Character configuration does not exist");
		
		for (File curr : charConfigDirectory.listFiles(XML_FILE_FILTER)) {
			characters.addAll(new CharacterLoader(curr).getCharacters());
		}
		
		Collections.sort(characters);
	}

}
