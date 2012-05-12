/*
 * Created by JFormDesigner on Sat Mar 17 11:13:24 PDT 2012
 */

package com.bsg.view;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.StringWriter;
import java.util.EnumSet;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bsg.Expansion;
import com.bsg.Player;
import com.bsg.cards.DoesNotBelongInDeckException;
import com.bsg.cards.crisis.CrisisCard;
import com.bsg.cards.destination.DestinationCard;
import com.bsg.cards.loyalty.LoyaltyCard;
import com.bsg.cards.loyalty.LoyaltyCardType;
import com.bsg.cards.loyalty.LoyaltyType;
import com.bsg.cards.quorum.QuorumCard;
import com.bsg.cards.quorum.QuorumHandMustacheObject;
import com.bsg.cards.quorum.QuorumType;
import com.bsg.cards.skill.SkillCard;
import com.bsg.cards.skill.SkillCardType;
import com.bsg.characters.Character;
import com.bsg.locations.Location;
import com.bsg.state.DuplicateCharacterException;
import com.bsg.state.GameState;
import com.bsg.state.SkillCheck;
import com.bsg.state.dradis.Dradis;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Benjamin Schwartz
 */
public class GamePanel extends JFrame {
	
	private static final long serialVersionUID = 4024893435917416831L;
	private static final Logger LOGGER = LoggerFactory.getLogger(GamePanel.class);
	
	private GameState gs;
	
	private Mustache handMustache;
	private Mustache dradisMustache;
	private Mustache crisisMustache;
	private Mustache destinationMustache;
	private Mustache quorumMustache;
	private Mustache quorumHandMustache;
	
	private GamePanel self;
	
	public GamePanel(GameState gs) {
		this.gs = gs;
		this.self = this;
		initComponents();
		
		playersTable.setModel(new PlayerTableModel(gs, self));
		for (int i = 0; i < playersTable.getModel().getColumnCount(); ++i) {
			if (i == 0)
				playersTable.getColumnModel().getColumn(i).setPreferredWidth(20);
			else
				playersTable.getColumnModel().getColumn(i).setPreferredWidth(50);
		}
		
		int tabs = mainTabbedPane.getTabCount();
		for (int i = 1; i < tabs; ++i)
			mainTabbedPane.setEnabledAt(i, false);
		
		
		MustacheFactory mf = new DefaultMustacheFactory();
		handMustache = mf.compile("config/templates/hand.template");
		dradisMustache = mf.compile("config/templates/dradis.template");
		crisisMustache = mf.compile("config/templates/crisis.template");
		destinationMustache = mf.compile("config/templates/destination.template");
		quorumMustache = mf.compile("config/templates/quorum.template");
		quorumHandMustache = mf.compile("config/templates/quorumhand.template");
		
		refreshDisplay();
	}
	
	private void addCharacterButtonActionPerformed(ActionEvent e) {
		Player p = new Player(playerNameField.getText(), (Character)characterComboBox.getSelectedItem());
		try {
			gs.addPlayer(p);
		} catch (DuplicateCharacterException e1) {
			JOptionPane.showMessageDialog(this, e1.getMessage(), "Error adding Player", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		playerNameField.setText("");
		characterComboBox.removeItem(p.getCharacter());
		
		refreshDisplay();
	}
	
	void refreshDisplay() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {				
				//save skill card list selection
				int skillCardCharSelectIdx = skillCardCharacterList.getSelectedIndex();
				int loyaltyCardCharSelectIdx = loyaltyCardCharacterList.getSelectedIndex();
				
				//refresh character lists
				characterList.setListData(gs.getPlayers().toArray());
				skillCardCharacterList.setListData(gs.getPlayers().toArray());
				handList.setListData(skillCardCharacterList.getSelectedValue() == null ? new Object[0] : ((Player)skillCardCharacterList.getSelectedValue()).getHand().toArray());
				playersTable.setModel(new PlayerTableModel(gs, self )); //TODO: change me
				loyaltyCardCharacterList.setListData(gs.getPlayers().toArray());
				
				
				//set location thing
				TableColumn tc = playersTable.getColumnModel().getColumn(PlayerTableModel.LOCATION_COLUMN);
				JComboBox jcb = new JComboBox();
				for (Location l : gs.getLocations())
					jcb.addItem(l.getName());
				tc.setCellEditor(new DefaultCellEditor(jcb));
				
				playersTable.getColumnModel().getColumn(PlayerTableModel.SKILL_CARD_HAND_SIZE_COLUMN).setPreferredWidth(40);
				playersTable.getColumnModel().getColumn(PlayerTableModel.IS_ADMIRAL_COLUMN).setPreferredWidth(20);
				playersTable.getColumnModel().getColumn(PlayerTableModel.IS_PRESIDENT_COLUMN).setPreferredWidth(20);
				playersTable.getColumnModel().getColumn(PlayerTableModel.IS_CAG_COLUMN).setPreferredWidth(20);
				playersTable.getColumnModel().getColumn(PlayerTableModel.CURRENT_PLAYER_COLUMN).setPreferredWidth(5);

				
				//skillcheckstuff
				if (gs.getSkillCheck() != null) {
					positiveCardList.setListData(gs.getSkillCheck().getPositiveCards().toArray());
					negativeCardList.setListData(gs.getSkillCheck().getNegativeCards().toArray());
					runningTotalLabel.setText(String.valueOf(gs.getSkillCheck().getTotal()));
					skillCheckResult.setText(gs.getSkillCheck().getResult());
				} else {
					positiveCardList.setListData(new Object[0]);
					negativeCardList.setListData(new Object[0]);
					runningTotalLabel.setText("");
					skillCheckResult.setText("");
				}
				
				//game state
				turnLabel.setText(String.format("%d.%d", gs.getTurn(), gs.getSubturn()));
				
				//refresh deck sizes
				if (gs.getSkillCardDeck(SkillCardType.POLITICS) != null) {
					politicsQty.setText(String.valueOf(gs.getSkillCardDeck(SkillCardType.POLITICS).getDeckSize()));
					leadershipQty.setText(String.valueOf(gs.getSkillCardDeck(SkillCardType.LEADERSHIP).getDeckSize()));
					tacticsQty.setText(String.valueOf(gs.getSkillCardDeck(SkillCardType.TACTICS).getDeckSize()));
					pilotingQty.setText(String.valueOf(gs.getSkillCardDeck(SkillCardType.PILOTING).getDeckSize()));
					engineeringQty.setText(String.valueOf(gs.getSkillCardDeck(SkillCardType.ENGINEERING).getDeckSize()));
					trecheryQty.setText(String.valueOf(gs.getSkillCardDeck(SkillCardType.TRECHERY).getDeckSize()));
					destinyQty.setText(String.valueOf(gs.getDestinyDeck().getSize()));
				}
				
				if (gs.getCrisisDeck() != null) {
					crisisDeckList.setListData(gs.getCrisisDeck().getDeckContents().toArray());
					crisisDiscardList.setListData(gs.getCrisisDeck().getDiscardContents().toArray());
				}
				
				//resources
				fuelProgress.setValue(gs.getFuel());
				fuelProgress.setString(String.format("%d/%d", gs.getFuel(), 15));
				foodProgress.setValue(gs.getFood());
				foodProgress.setString(String.format("%d/%d", gs.getFood(), 15));
				moraleProgress.setValue(gs.getMorale());
				moraleProgress.setString(String.format("%d/%d", gs.getMorale(), 15));
				populationProgress.setValue(gs.getPopulation());
				populationProgress.setString(String.format("%d/%d", gs.getPopulation(), 15));
				
				nukeCountLabel.setText(String.valueOf(gs.getNukeCount()));
				
				//jumptrack
				jumpTrackStartBox.setSelected(false);
				jumpTrackRed1Box.setSelected(false);
				jumpTrackRed2Box.setSelected(false);
				jumpTrackRisk1Box.setSelected(false);
				jumpTrackRisk3Box.setSelected(false);
				jumpTrackAutojumpBox.setSelected(false);
				
				switch(gs.getJumpTrack().getStatus()) {
				case START:
					jumpTrackStartBox.setSelected(true);
					break;
				case RED1:
					jumpTrackRed1Box.setSelected(true);
					break;
				case RED2:
					jumpTrackRed2Box.setSelected(true);
					break;
				case RISK1:
					jumpTrackRisk1Box.setSelected(true);
					break;
				case RISK3:
					jumpTrackRisk3Box.setSelected(true);
					break;
				case AUTOJUMP:
					jumpTrackAutojumpBox.setSelected(true);
					break;
				}
				
				//loyalty
				if (gs.getLoyaltyDeck() != null)
					loyaltyDeckList.setListData(gs.getLoyaltyDeck().getDeckContents().toArray());
				
				//quorum
				if (gs.getQuorumDeck() != null) {
					quorumCardDeckList.setListData(gs.getQuorumDeck().getDeck().getDeckContents().toArray());
					quorumCardHandList.setListData(gs.getQuorumDeck().getHand().toArray());
					quorumCardActiveList.setListData(gs.getQuorumDeck().getActive().toArray());
					quorumCardRemovedList.setListData(gs.getQuorumDeck().getRemoved().toArray());
					quorumCardDiscardedList.setListData(gs.getQuorumDeck().getDeck().getDiscardContents().toArray());
				}
				
				//destinations
				if (gs.getDestinationDeck() != null)
					destinationList.setListData(gs.getDestinationDeck().getDeckContents().toArray());
				distanceTraveledLabelVal.setText(String.valueOf(gs.getDistance()));
				travelledList.setListData(gs.getTravelledList().toArray());
				
				//boarders
				boarders_0.setText(String.valueOf(gs.getNumBoardersAt(0)));
				boarders_1.setText(String.valueOf(gs.getNumBoardersAt(1)));
				boarders_2.setText(String.valueOf(gs.getNumBoardersAt(2)));
				boarders_3.setText(String.valueOf(gs.getNumBoardersAt(3)));
				boarders_lose.setText(String.valueOf(gs.getNumBoardersAt(4)));
				
				//damage stuff
				galacticaDamageList.setListData(gs.getDamage().getAvailableGalacticaTokens().toArray());
				
				//re-highlight
				skillCardCharacterList.setSelectedIndex(skillCardCharSelectIdx);
				loyaltyCardCharacterList.setSelectedIndex(loyaltyCardCharSelectIdx);
			}
			
		});
	}
	
	private void generateHandEmailButtonActionPerformed(ActionEvent e) {
		Object o = skillCardCharacterList.getSelectedValue();
		if (o == null)
			return;
		
		Player p = (Player)o;
		StringWriter sw = new StringWriter();
		try {
			handMustache.execute(sw, p).flush();
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(this, e1.toString(), "Uncaught Exception", JOptionPane.ERROR_MESSAGE);
		}
		new TextWindow(sw.toString()).setVisible(true);
		
	}

	private void dealPolticsButtonActionPerformed(ActionEvent e) {
		Object o = skillCardCharacterList.getSelectedValue();
		if (o == null)
			return;
		
		Player p = (Player)o;
		p.giveSkillCard(gs.getSkillCardDeck(SkillCardType.POLITICS).deal());
		
		refreshDisplay();
	}

	private void dealLeadershipButtonActionPerformed(ActionEvent e) {
		Object o = skillCardCharacterList.getSelectedValue();
		if (o == null)
			return;
		
		Player p = (Player)o;
		p.giveSkillCard(gs.getSkillCardDeck(SkillCardType.LEADERSHIP).deal());
		
		refreshDisplay();
	}

	private void dealTacticsButtonActionPerformed(ActionEvent e) {
		Object o = skillCardCharacterList.getSelectedValue();
		if (o == null)
			return;
		
		Player p = (Player)o;
		p.giveSkillCard(gs.getSkillCardDeck(SkillCardType.TACTICS).deal());
		
		refreshDisplay();
	}

	private void dealPilotingButtonActionPerformed(ActionEvent e) {
		Object o = skillCardCharacterList.getSelectedValue();
		if (o == null)
			return;
		
		Player p = (Player)o;
		p.giveSkillCard(gs.getSkillCardDeck(SkillCardType.PILOTING).deal());
		
		refreshDisplay();
	}

	private void dealEngineeringButtonActionPerformed(ActionEvent e) {
		Object o = skillCardCharacterList.getSelectedValue();
		if (o == null)
			return;
		
		Player p = (Player)o;
		p.giveSkillCard(gs.getSkillCardDeck(SkillCardType.ENGINEERING).deal());
		
		refreshDisplay();
	}

	private void dealTrecheryButtonActionPerformed(ActionEvent e) {
		Object o = skillCardCharacterList.getSelectedValue();
		if (o == null)
			return;
		
		Player p = (Player)o;
		p.giveSkillCard(gs.getSkillCardDeck(SkillCardType.TRECHERY).deal());
		
		refreshDisplay();
	}

	private void skillCardCharacterListValueChanged(ListSelectionEvent e) {
		if (!skillCardCharacterList.getValueIsAdjusting()) {
			Object o = skillCardCharacterList.getSelectedValue();
			if (o == null)
				return;
			Player p = (Player)o;
			handList.setListData(p.getHand().toArray());
			
			playerDrawString.setText(p.getDrawString());
		}
	}

	private void discardSkillCardButtonActionPerformed(ActionEvent e) {
		Object o = skillCardCharacterList.getSelectedValue();
		if (o == null)
			return;
		

		Player p = (Player)o;
		
		for (Object m : handList.getSelectedValues()) {
			SkillCard c = (SkillCard)m;
			
			if (p.discardSkillCard(c)) {
				try {
					gs.getSkillCardDeck(c.getType()).discard(c);
				} catch (DoesNotBelongInDeckException e1) {
					JOptionPane.showMessageDialog(this, e1.toString(), "Uncaught Exception", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		
		refreshDisplay();
		
	}

	private void startSkillCheckButtonActionPerformed(ActionEvent e) {
		//first, figure out what things are positive
		Set<SkillCardType> types = EnumSet.noneOf(SkillCardType.class);
		if (politicsCheckbox.isSelected())
			types.add(SkillCardType.POLITICS);
		if (leadershipCheckbox.isSelected())
			types.add(SkillCardType.LEADERSHIP);
		if (tacticsCheckbox.isSelected())
			types.add(SkillCardType.TACTICS);
		if (pilotingCheckbox.isSelected())
			types.add(SkillCardType.PILOTING);
		if (engineeringCheckbox.isSelected())
			types.add(SkillCardType.ENGINEERING);
		if (trecheryCheckbox.isSelected())
			types.add(SkillCardType.TRECHERY);
		
		//now, get strength and partial
		int strength = (Integer) difficultySpinner.getValue();
		int partial = (Integer) partialSpinner.getValue();
		
		if (partial >= strength) {
			//TODO: display error
		}
		
		//disable our buttons
		politicsCheckbox.setEnabled(false);
		leadershipCheckbox.setEnabled(false);
		tacticsCheckbox.setEnabled(false);
		pilotingCheckbox.setEnabled(false);
		engineeringCheckbox.setEnabled(false);
		trecheryCheckbox.setEnabled(false);
		difficultySpinner.setEnabled(false);
		partialSpinner.setEnabled(false);
		hasConsequencesCheckbox.setEnabled(false);
		startSkillCheckButton.setEnabled(false);
		
		//enable the others
		playIntoSkillCheckButton.setEnabled(true);
		addDestinyButton.setEnabled(true);
		doubleEngineeringButton.setEnabled(true);
		discardAllBCardsButton.setEnabled(true);
		
		//now init the skillcheck object
		SkillCheck sc = new SkillCheck(strength, partial, types);
		gs.startSkillCheck(sc);
		
		refreshDisplay();
	}

	private void playIntoSkillCheckButtonActionPerformed(ActionEvent e) {
		if (gs.getSkillCheck() == null)
			return;
		
		Object o = skillCardCharacterList.getSelectedValue();
		if (o == null)
			return;
		Player p = (Player)o;
		
		for (Object r : handList.getSelectedValues()) {
			SkillCard s = (SkillCard)r;
			
			p.discardSkillCard(s);
			gs.getSkillCheck().addCard(s);
		}
		
		refreshDisplay();
	}

	private void addDestinyButtonActionPerformed(ActionEvent e) {
		if (gs.getSkillCheck() == null)
			return;
		
		for (SkillCard curr : gs.getTwoDestiny()) {
			gs.getSkillCheck().addCard(curr);
		}
		
		refreshDisplay();
	}

	private void discardAllBCardsButtonActionPerformed(ActionEvent e) {
		if (gs.getSkillCheck() == null)
			return;
		
		SkillCheck sc = gs.getSkillCheck();
		for (SkillCard curr : sc.getPositiveCards())
			gs.discardSkillCard(curr);
		for (SkillCard curr : sc.getNegativeCards())
			gs.discardSkillCard(curr);
		
		//enable our buttons
		politicsCheckbox.setEnabled(true);
		leadershipCheckbox.setEnabled(true);
		tacticsCheckbox.setEnabled(true);
		pilotingCheckbox.setEnabled(true);
		engineeringCheckbox.setEnabled(true);
		trecheryCheckbox.setEnabled(true);
		difficultySpinner.setEnabled(true);
		partialSpinner.setEnabled(true);
		hasConsequencesCheckbox.setEnabled(true);
		startSkillCheckButton.setEnabled(true);
		
		//disable the others
		playIntoSkillCheckButton.setEnabled(false);
		addDestinyButton.setEnabled(false);
		doubleEngineeringButton.setEnabled(false);
		discardAllBCardsButton.setEnabled(false);
		
		politicsCheckbox.setSelected(false);
		leadershipCheckbox.setSelected(false);
		tacticsCheckbox.setSelected(false);
		pilotingCheckbox.setSelected(false);
		engineeringCheckbox.setSelected(false);
		trecheryCheckbox.setSelected(false);
		difficultySpinner.setValue(0);
		partialSpinner.setValue(0);
		hasConsequencesCheckbox.setSelected(false);
		
		gs.startSkillCheck(null);
		
		refreshDisplay();
		
		
	}

	private void doubleEngineeringButtonActionPerformed(ActionEvent e) {
		if (gs.getSkillCheck() == null)
			return;
		
		gs.getSkillCheck().doubleSkillTypeValue(SkillCardType.ENGINEERING);
		
		refreshDisplay();
	}

	private void exportSkillCheckButtonActionPerformed(ActionEvent e) {
		if (gs.getSkillCheck() == null)
			return;
		
		try {
			TextWindow tw = new TextWindow(gs.getSkillCheck().getFullText());
			tw.setVisible(true);
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(this, e1.toString(), "Uncaught Exception", JOptionPane.ERROR_MESSAGE);
		}
	}


	private void resourceButtonActionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		if (source == decreaseFoodButton) {
			gs.setFood(gs.getFood() - 1);
		} else if (source == decreaseFuelButton) {
			gs.setFuel(gs.getFuel() - 1);
		} else if (source == decreaseMoraleButton) {
			gs.setMorale(gs.getMorale() - 1);
		} else if (source == decreasePopulationButton) {
			gs.setPopulation(gs.getPopulation() - 1);
		} else if (source == increaseFoodButton) {
			gs.setFood(gs.getFood() + 1);
		} else if (source == increaseFuelButton) {
			gs.setFuel(gs.getFuel() + 1);
		} else if (source == increaseMoraleButton) {
			gs.setMorale(gs.getMorale() + 1);
		} else if (source == increasePopulationButton) {
			gs.setPopulation(gs.getPopulation() + 1);
		}
		
		refreshDisplay();
	}


	private void decreaseJumpTrackButtonActionPerformed(ActionEvent e) {
		gs.getJumpTrack().decrementJumpTrack();
		refreshDisplay();
	}

	private void increaseJumpTrackButtonActionPerformed(ActionEvent e) {
		gs.getJumpTrack().incrementJumpTrack();
		refreshDisplay();
	}

	private void finishedStep1ButtonActionPerformed(ActionEvent e) {
		Set<Expansion> exp = EnumSet.noneOf(Expansion.class);
		exp.add(Expansion.BASE);
		
		if (pegasusCheckbox.isSelected())
			exp.add(Expansion.PEGASUS);
		if (exodusCheckbox.isSelected())
			exp.add(Expansion.EXODUS);
		
		gs.setExpansionsAndInitLists(exp);
		characterComboBox.removeAllItems();
		for (Character c : gs.getAvailableCharacters()) {
			characterComboBox.addItem(c);
		}
		
		
		playerNameField.setEnabled(true);
		characterComboBox.setEnabled(true);
		addCharacterButton.setEnabled(true);
		deleteCharacterButton.setEnabled(true);
		stepTwoDoneButton.setEnabled(true);
		
		pegasusCheckbox.setEnabled(false);
		exodusCheckbox.setEnabled(false);
		customCheckbox.setEnabled(false);
		useComplexDestinyCheckbox.setEnabled(false);
		finishedStep1Button.setEnabled(false);
		loadGameButton.setEnabled(false);
		
		refreshDisplay();
	}

	private void stepTwoDoneButtonActionPerformed(ActionEvent e) {
		int numPlayers = gs.getPlayers().size();
		if (numPlayers < 3 || numPlayers > 7) {
			JOptionPane.showMessageDialog(this, "You must have between 3 and 7 players to begin.", "Invalid number of players", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		playerNameField.setEnabled(false);
		characterComboBox.setEnabled(false);
		addCharacterButton.setEnabled(false);
		deleteCharacterButton.setEnabled(false);
		stepTwoDoneButton.setEnabled(false);
		
		if (numPlayers == 4 || numPlayers == 6) {
			if (pegasusCheckbox.isSelected())
				sympatheticCylonRadio.setEnabled(true);
			noSympathizerRadio.setEnabled(true);
			sympathizerRadio.setEnabled(true);
		}
		
		if (exodusCheckbox.isSelected()) {
			usePersonalGoalsCheckbox.setEnabled(true);
			useFinalFiveCheckbox.setEnabled(true);
			useCylonFleetCheckbox.setEnabled(true);
		}
		
		giveLoyaltyCardsComboBox.removeAllItems();
		for (Player p : gs.getPlayers()) 
			giveLoyaltyCardsComboBox.addItem(p);
		
		startGameButton.setEnabled(true);
		
	}

	private void startGameButtonActionPerformed(ActionEvent e) {
		gs.buildLoyaltyDeck(usePersonalGoalsCheckbox.isSelected(), useFinalFiveCheckbox.isSelected()); 
		gs.dealLoyalty();
		
		if (sympathizerRadio.isSelected())
			gs.addSympathizer(LoyaltyCardType.SYMPATHIZER);
		
		if (sympatheticCylonRadio.isSelected())
			gs.addSympathizer(LoyaltyCardType.SYMPATHETIC_CYLON);
		
		if (noSympathizerRadio.isSelected() && (gs.getPlayers().size() == 4 || gs.getPlayers().size() == 6)) {
			//setup official nosympth variant
		}
		
		if (useCylonFleetCheckbox.isSelected()) {
			gs.removeCylonAttackCrisisCards();
		}
		
		gs.createDeck();
		gs.setupPlayerLocations();
		
		noSympathizerRadio.setEnabled(false);
		sympatheticCylonRadio.setEnabled(false);
		sympathizerRadio.setEnabled(false);
		
		usePersonalGoalsCheckbox.setEnabled(false);
		useFinalFiveCheckbox.setEnabled(false);
		useCylonFleetCheckbox.setEnabled(false);

		int tabs = mainTabbedPane.getTabCount();
		for (int i = 0; i < tabs; ++i)
			mainTabbedPane.setEnabledAt(i, true);
		
		startGameButton.setEnabled(false);
		mainTabbedPane.setSelectedIndex(1); // main game state pane
		
		refreshDisplay();
	}

	private void rollD8ButtonActionPerformed(ActionEvent e) {
		d8Label.setText(String.valueOf(GameState.rollD8()));
	}

	private void advanceTurnButtonActionPerformed(ActionEvent e) {
		gs.advanceTurn();
		
		refreshDisplay();
	}

	private void crisisDeckListValueChanged(ListSelectionEvent e) {
		
	}

	private void playCrisisCardButtonActionPerformed(ActionEvent e) {
		int idx = crisisDeckList.getSelectedIndex();
		if (idx == -1)
			idx = 0;
		
		CrisisCard cc = (CrisisCard) crisisDeckList.getSelectedValue();
		
		if (idx != 0) {
			int res = JOptionPane.showConfirmDialog(this, String.format("Are you sure you want to play '%s'?", cc.getName()), "Play Card?", JOptionPane.YES_NO_OPTION);
			if (res != JOptionPane.YES_OPTION)
				return;
		}
		
		gs.getCrisisDeck().play(idx);
		refreshDisplay();
	}

	private void buryCrisisCardButtonActionPerformed(ActionEvent e) {
		int idx = crisisDeckList.getSelectedIndex();
		if (idx == -1)
			idx = 0;
		
		CrisisCard cc = (CrisisCard) crisisDeckList.getSelectedValue();
		
		if (idx != 0) {
			int res = JOptionPane.showConfirmDialog(this, String.format("Are you sure you want to bury '%s'?", cc.getName()), "Bury Card?", JOptionPane.YES_NO_OPTION);
			if (res != JOptionPane.YES_OPTION)
				return;
		}
		
		gs.getCrisisDeck().bury(idx);
		refreshDisplay();
	}

	private void makePresidentActionPerformed(ActionEvent e) {
		int idx = playersTable.getSelectedRow();
		if (idx == -1)
			return;
		
		gs.makePresident(gs.getPlayers().get(idx));
		refreshDisplay();
	}

	private void makeAdmiralButtonActionPerformed(ActionEvent e) {
		int idx = playersTable.getSelectedRow();
		if (idx == -1)
			return;
		
		gs.makeAdmiral(gs.getPlayers().get(idx));
		refreshDisplay();
	}

	private void makeCAGButtonActionPerformed(ActionEvent e) {
		int idx = playersTable.getSelectedRow();
		if (idx == -1)
			return;
		
		gs.makeCAG(gs.getPlayers().get(idx));
		refreshDisplay();
	}

	private void revealCylonButtonActionPerformed(ActionEvent e) {
		int idx = playersTable.getSelectedRow();
		if (idx == -1)
			return;
		
		Player p = gs.getPlayers().get(idx);
		if (p.getLoyalty() != LoyaltyType.CYLON) {
			JOptionPane.showMessageDialog(this, "You can not reveal this player.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		int res = JOptionPane.showConfirmDialog(this, "Are you sure you want to reveal this player as a cylon?", "Are you sure?", JOptionPane.YES_NO_OPTION);
		if (res != JOptionPane.YES_OPTION)
			return;
		
		p.setRevealed(true);
		p.setLocation("Resurrection Ship");
		JOptionPane.showMessageDialog(this, "Remember, this player must discard down to 3 cards", "Discard Cards", JOptionPane.INFORMATION_MESSAGE);
		//TODO: there's more here, right?
		
		
		refreshDisplay();
	}

	private void createDRADISButtonActionPerformed(ActionEvent e) {
		StringWriter sw = new StringWriter();
		try {
			dradisMustache.execute(sw, new Dradis(gs)).flush();
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(this, e1.toString(), "Uncaught Exception", JOptionPane.ERROR_MESSAGE);
		}
		new TextWindow(sw.toString()).setVisible(true);
		
	}

	private void addBoarderButtonActionPerformed(ActionEvent e) {
		gs.addBoardingParty();
		refreshDisplay();
	}

	private void advanceBoardersButtonActionPerformed(ActionEvent e) {
		gs.advanceBoardingParty();
		refreshDisplay();
	}

	private void jumpButtonActionPerformed(ActionEvent e) {
		gs.jumpToTopCard();
		refreshDisplay();
	}

	private void crisisDeckMouseClicked(MouseEvent e) {
		JList list = (JList) e.getSource();
		if (e.getClickCount() == 2) {
			CrisisCard cc = (CrisisCard) list.getSelectedValue();
			StringWriter sw = new StringWriter();
			try {
				crisisMustache.execute(sw, cc).flush();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(this, e1.toString(), "Uncaught Exception", JOptionPane.ERROR_MESSAGE);
			}
			new TextWindow(sw.toString()).setVisible(true);
			
		} 
	}

	private void reshuffleCrisisDeckButtonActionPerformed(ActionEvent e) {
		gs.getCrisisDeck().shuffleInPlace();
		
		refreshDisplay();
	}

	private void destinationListMouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			JList source = (JList) e.getSource();
			
			DestinationCard dc = (DestinationCard) source.getSelectedValue();
			
			StringWriter sw = new StringWriter();
			try {
				destinationMustache.execute(sw, dc).flush();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(this, e1.toString(), "Uncaught Exception", JOptionPane.ERROR_MESSAGE);
			}
			new TextWindow(sw.toString()).setVisible(true);
		}
	}

	private void buryButtonActionPerformed(ActionEvent e) {
		int idx = destinationList.getSelectedIndex();
		
		
		if (idx != 0) {
			int res = JOptionPane.showConfirmDialog(this, String.format("Are you sure you want to bury '%s'?", ((DestinationCard) destinationList.getSelectedValue()).getName()), "Bury Card?", JOptionPane.YES_NO_OPTION);
			if (res != JOptionPane.YES_OPTION)
				return;
		}
		
		gs.getDestinationDeck().bury(idx);
		
		refreshDisplay();
		
		
	}

	private void loyaltyCardCharacterListValueChanged(ListSelectionEvent e) {
		if (!loyaltyCardCharacterList.getValueIsAdjusting()) {
			Object o = loyaltyCardCharacterList.getSelectedValue();
			if (o == null)
				return;
			Player p = (Player)o;
			loyaltyPanelHandList.setListData(p.getLoyaltyCards().toArray());
		}
	}

	private void giveLoyaltyCardsButtonActionPerformed(ActionEvent e) {
		Object o = loyaltyCardCharacterList.getSelectedValue();
		if (o == null)
			return;
		
		Player sourcePlayer = (Player)o;
		Player targetPlayer = (Player)giveLoyaltyCardsComboBox.getSelectedItem();
		
		Object[] cards = loyaltyPanelHandList.getSelectedValues();
		if (cards == null || cards.length == 0)
			return;
		
		for (Object c : cards) {
			LoyaltyCard lc = (LoyaltyCard) c;
			sourcePlayer.removeLoyaltyCard(lc);
			targetPlayer.giveLoyaltyCard(lc);
		}
		
		refreshDisplay();
	}

	private void dealSleeperButtonActionPerformed(ActionEvent e) {
		gs.dealSleeper();
		refreshDisplay();
	}

	private void legendaryDiscoveryActionPerformed(ActionEvent e) {
		gs.legendaryDiscovery();
		refreshDisplay();
		
	}

	private void dealQuorumCardButtonActionPerformed(ActionEvent e) {
		gs.getQuorumDeck().dealQuorumCard();
		
		refreshDisplay();
	}

	private void playQuorumCardButtonActionPerformed(ActionEvent e) {
		QuorumCard qc = (QuorumCard) quorumCardHandList.getSelectedValue();
		if (qc == null)
			return;
		
		try {
			gs.playQuorumCard(qc);
		} catch (DoesNotBelongInDeckException e1) {
			JOptionPane.showMessageDialog(this, e1.toString(), "Uncaught Exception", JOptionPane.ERROR_MESSAGE);
		}
		
		refreshDisplay();
		
	}

	private void discardQuorumCardButtonActionPerformed(ActionEvent e) {
		QuorumCard qc = (QuorumCard) quorumCardHandList.getSelectedValue();
		if (qc == null)
			return;
		
		try {
			gs.getQuorumDeck().discardQuorumCard(qc);
		} catch (DoesNotBelongInDeckException e1) {
			e1.printStackTrace();
		}
		
		refreshDisplay();
	}

	private void discardActiveQuorumCardButtonActionPerformed(ActionEvent e) {
		QuorumCard qc = (QuorumCard) quorumCardActiveList.getSelectedValue();
		if (qc == null)
			return;
		
		try {
			gs.getQuorumDeck().deactivateQuorumCard(qc);
		} catch (DoesNotBelongInDeckException e1) {
			JOptionPane.showMessageDialog(this, e1.toString(), "Uncaught Exception", JOptionPane.ERROR_MESSAGE);
		}
		
		refreshDisplay();
	}

	private void quorumCardListMouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			JList source = (JList) e.getSource();
			QuorumCard qc = (QuorumCard) source.getSelectedValue();
			
			StringWriter sw = new StringWriter();
			try {
				quorumMustache.execute(sw, qc).flush();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(this, e1.toString(), "Uncaught Exception", JOptionPane.ERROR_MESSAGE);
			}
			new TextWindow(sw.toString()).setVisible(true);
		}
	}

	private void generateQuorumHandEmailButtonActionPerformed(ActionEvent e) {
		QuorumHandMustacheObject qhmo = new QuorumHandMustacheObject(gs.getQuorumDeck().getHand());
		
		StringWriter sw = new StringWriter();
		try {
			quorumHandMustache.execute(sw, qhmo).flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		new TextWindow(sw.toString()).setVisible(true);
	}

	private void useNukeButtonActionPerformed(ActionEvent e) {
		gs.useNuke();
		
		refreshDisplay();
	}

	private void addNukeButtonActionPerformed(ActionEvent e) {
		gs.buildNuke();
		
		refreshDisplay();
	}

	




	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		mainTabbedPane = new JTabbedPane();
		playerPanel = new JPanel();
		gameSetupPanel = new JPanel();
		JLabel expansionsLabel = new JLabel();
		pegasusCheckbox = new JCheckBox();
		exodusCheckbox = new JCheckBox();
		customCheckbox = new JCheckBox();
		finishedStep1Button = new JButton();
		loadGameButton = new JButton();
		useComplexDestinyCheckbox = new JCheckBox();
		playerConfigPanel = new JPanel();
		addPlayerPanel = new JPanel();
		JLabel playerNameLabel = new JLabel();
		playerNameField = new JTextField();
		JLabel characterNameLabel = new JLabel();
		characterComboBox = new JComboBox(gs.getAvailableCharacters().toArray());
		addCharacterButton = new JButton();
		deleteCharacterButton = new JButton();
		characterListScrollPane = new JScrollPane();
		characterList = new JList();
		stepTwoDoneButton = new JButton();
		finalOptionsPanel = new JPanel();
		JLabel sympathizerTypeLabel = new JLabel();
		noSympathizerRadio = new JRadioButton();
		sympathizerRadio = new JRadioButton();
		sympatheticCylonRadio = new JRadioButton();
		JLabel exodusOptionsLabel = new JLabel();
		usePersonalGoalsCheckbox = new JCheckBox();
		useFinalFiveCheckbox = new JCheckBox();
		useCylonFleetCheckbox = new JCheckBox();
		startGameButton = new JButton();
		gameStatePanel = new JPanel();
		mainStatePanel = new JPanel();
		JLabel turnTitleLabel = new JLabel();
		turnLabel = new JLabel();
		advanceTurnButton = new JButton();
		rollD8Button = new JButton();
		d8Label = new JLabel();
		createDRADISButton = new JButton();
		resoucesPanel = new JPanel();
		JLabel fuelLabel = new JLabel();
		decreaseFuelButton = new JButton();
		fuelProgress = new JProgressBar();
		increaseFuelButton = new JButton();
		JLabel foodLabel = new JLabel();
		decreaseFoodButton = new JButton();
		foodProgress = new JProgressBar();
		increaseFoodButton = new JButton();
		JLabel moraleLabel = new JLabel();
		decreaseMoraleButton = new JButton();
		moraleProgress = new JProgressBar();
		increaseMoraleButton = new JButton();
		JLabel populationLabel = new JLabel();
		decreasePopulationButton = new JButton();
		populationProgress = new JProgressBar();
		increasePopulationButton = new JButton();
		jumpTrackPanel = new JPanel();
		jumpTrackStartBox = new JCheckBox();
		jumpTrackRed1Box = new JCheckBox();
		jumpTrackRed2Box = new JCheckBox();
		jumpTrackRisk3Box = new JCheckBox();
		jumpTrackRisk1Box = new JCheckBox();
		jumpTrackAutojumpBox = new JCheckBox();
		decreaseJumpTrackButton = new JButton();
		increaseJumpTrackButton = new JButton();
		distancePanel = new JPanel();
		distanceTraveledLabel = new JLabel();
		distanceTraveledLabelVal = new JLabel();
		travelListScrollPane = new JScrollPane();
		destinationList = new JList();
		scrollPane4 = new JScrollPane();
		travelledList = new JList();
		jumpButton = new JButton();
		buryButton = new JButton();
		legendaryDiscovery = new JButton();
		playerTablePanel = new JPanel();
		scrollPane1 = new JScrollPane();
		playersTable = new JTable();
		makePresident = new JButton();
		revealCylonButton = new JButton();
		makeAdmiralButton = new JButton();
		executeButton = new JButton();
		makeCAGButton = new JButton();
		useOPGButton = new JButton();
		boardingPartyPanel = new JPanel();
		boarders_0 = new JTextField();
		boarders_1 = new JTextField();
		boarders_2 = new JTextField();
		boarders_3 = new JTextField();
		boarders_lose = new JTextField();
		addBoarderButton = new JButton();
		advanceBoardersButton = new JButton();
		nukesPanel = new JPanel();
		JLabel nukeCountTitleLabel = new JLabel();
		nukeCountLabel = new JLabel();
		useNukeButton = new JButton();
		addNukeButton = new JButton();
		damagePanel = new JPanel();
		JLabel galacticaDamageLabel = new JLabel();
		JLabel pegasusDamageLabel = new JLabel();
		scrollPane13 = new JScrollPane();
		galacticaDamageList = new JList();
		skillCardPanel = new JPanel();
		skillDeckQtyPanel = new JPanel();
		JLabel politicsLabel = new JLabel();
		politicsQty = new JLabel();
		JLabel leadershipLabel = new JLabel();
		leadershipQty = new JLabel();
		JLabel tacticsLabel = new JLabel();
		tacticsQty = new JLabel();
		JLabel pilotingLabel = new JLabel();
		pilotingQty = new JLabel();
		JLabel engineeringLabel = new JLabel();
		engineeringQty = new JLabel();
		JLabel trecheryLabel = new JLabel();
		trecheryQty = new JLabel();
		JLabel destinyDeckLabel = new JLabel();
		destinyQty = new JLabel();
		viewDestinyDeckButton = new JButton();
		dealCardsPanel = new JPanel();
		skillCardCharacterListScrollPanel = new JScrollPane();
		skillCardCharacterList = new JList();
		dealPolticsButton = new JButton();
		handScrollPanel = new JScrollPane();
		handList = new JList();
		dealLeadershipButton = new JButton();
		dealTacticsButton = new JButton();
		dealPilotingButton = new JButton();
		dealEngineeringButton = new JButton();
		dealTrecheryButton = new JButton();
		playerDrawString = new JLabel();
		discardSkillCardButton = new JButton();
		generateHandEmailButton = new JButton();
		giveToPlayerButton = new JButton();
		playIntoSkillCheckButton = new JButton();
		skillCheckPanel = new JPanel();
		difficultySpinner = new JSpinner();
		JLabel difficultyLabel = new JLabel();
		JLabel positiveCardsLabel = new JLabel();
		JLabel negativeCardsLabel = new JLabel();
		addDestinyButton = new JButton();
		partialSpinner = new JSpinner();
		JLabel partialLabel = new JLabel();
		positiveCardListScrollPane = new JScrollPane();
		positiveCardList = new JList();
		negativeCardsListScrollPane = new JScrollPane();
		negativeCardList = new JList();
		discardAllBCardsButton = new JButton();
		hasConsequencesCheckbox = new JCheckBox();
		doubleEngineeringButton = new JButton();
		politicsCheckbox = new JCheckBox();
		leadershipCheckbox = new JCheckBox();
		tacticsCheckbox = new JCheckBox();
		pilotingCheckbox = new JCheckBox();
		engineeringCheckbox = new JCheckBox();
		JLabel skillCheckResultLabel = new JLabel();
		skillCheckResult = new JLabel();
		trecheryCheckbox = new JCheckBox();
		JLabel skillCheckTotalLabel = new JLabel();
		runningTotalLabel = new JLabel();
		startSkillCheckButton = new JButton();
		exportSkillCheckButton = new JButton();
		crisisQuorumDeckPanel = new JPanel();
		crisisDeckPanel = new JPanel();
		JLabel crisisCardDeckLabel = new JLabel();
		JLabel crisisCardDiscardLabel = new JLabel();
		scrollPane2 = new JScrollPane();
		crisisDeckList = new JList();
		scrollPane3 = new JScrollPane();
		crisisDiscardList = new JList();
		playCrisisCardButton = new JButton();
		buryCrisisCardButton = new JButton();
		reshuffleCrisisDeckButton = new JButton();
		quorumCardPanel = new JPanel();
		JLabel quorumDeckLabel = new JLabel();
		JLabel quorumHandLabel = new JLabel();
		JLabel quorumCardActiveLabel = new JLabel();
		JLabel quorumCardRemovedLabel = new JLabel();
		scrollPane8 = new JScrollPane();
		quorumCardDeckList = new JList();
		scrollPane9 = new JScrollPane();
		quorumCardHandList = new JList();
		scrollPane10 = new JScrollPane();
		quorumCardActiveList = new JList();
		scrollPane11 = new JScrollPane();
		quorumCardRemovedList = new JList();
		JLabel discardedQuorumLabel = new JLabel();
		scrollPane12 = new JScrollPane();
		quorumCardDiscardedList = new JList();
		dealQuorumCardButton = new JButton();
		playQuorumCardButton = new JButton();
		discardActiveQuorumCardButton = new JButton();
		discardQuorumCardButton = new JButton();
		generateQuorumHandEmailButton = new JButton();
		loyaltyCardPanel = new JPanel();
		loyaltyCardInnerPanel = new JPanel();
		scrollPane5 = new JScrollPane();
		loyaltyCardCharacterList = new JList();
		scrollPane6 = new JScrollPane();
		loyaltyPanelHandList = new JList();
		giveLoyaltyCardsButton = new JButton();
		giveLoyaltyCardsComboBox = new JComboBox();
		loyaltyDeckPanel = new JPanel();
		scrollPane7 = new JScrollPane();
		loyaltyDeckList = new JList();
		dealSleeperButton = new JButton();
		shipsPanel = new JPanel();
		CellConstraints cc = new CellConstraints();

		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"392dlu",
			"530dlu"));

		//======== mainTabbedPane ========
		{

			//======== playerPanel ========
			{
				playerPanel.setLayout(new FormLayout(
					"378dlu, $lcgap, default",
					"default, $lgap, 53dlu, $lgap, 126dlu, $lgap, 116dlu, $lgap, default"));

				//======== gameSetupPanel ========
				{
					gameSetupPanel.setBorder(new TitledBorder("Step 1: Game Setup"));
					gameSetupPanel.setLayout(new FormLayout(
						"default, $lcgap, 44dlu, 2*($lcgap, default), $lcgap, 73dlu, 2*($lcgap, 56dlu)",
						"2*(default, $lgap), 13dlu"));

					//---- expansionsLabel ----
					expansionsLabel.setText("Expansions:");
					gameSetupPanel.add(expansionsLabel, cc.xy(3, 1));

					//---- pegasusCheckbox ----
					pegasusCheckbox.setText("Use Pegasus");
					gameSetupPanel.add(pegasusCheckbox, cc.xy(5, 1));

					//---- exodusCheckbox ----
					exodusCheckbox.setText("Use Exodus");
					gameSetupPanel.add(exodusCheckbox, cc.xy(7, 1));

					//---- customCheckbox ----
					customCheckbox.setText("Use Custom");
					gameSetupPanel.add(customCheckbox, cc.xy(9, 1));

					//---- finishedStep1Button ----
					finishedStep1Button.setText("Finished Step 1");
					finishedStep1Button.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							finishedStep1ButtonActionPerformed(e);
						}
					});
					gameSetupPanel.add(finishedStep1Button, cc.xywh(11, 1, 1, 4));

					//---- loadGameButton ----
					loadGameButton.setText("Load a Game");
					gameSetupPanel.add(loadGameButton, cc.xywh(13, 1, 1, 4));

					//---- useComplexDestinyCheckbox ----
					useComplexDestinyCheckbox.setText("Use Complex Destiny");
					useComplexDestinyCheckbox.setToolTipText("The destiny deck will be created with 3 cards from each skill deck instead of 2.  When it is 1/3 full (5 cards in base, 6 in Pegasus), it will be refilled and reshuffled.");
					gameSetupPanel.add(useComplexDestinyCheckbox, cc.xywh(5, 3, 3, 1));
				}
				playerPanel.add(gameSetupPanel, cc.xy(1, 3, CellConstraints.FILL, CellConstraints.FILL));

				//======== playerConfigPanel ========
				{
					playerConfigPanel.setBorder(new TitledBorder("Step 2: Player Setup"));
					playerConfigPanel.setLayout(new FormLayout(
						"default, $lcgap, 161dlu",
						"10dlu, $lgap, 78dlu, $lgap, 14dlu"));

					//======== addPlayerPanel ========
					{
						addPlayerPanel.setLayout(new FormLayout(
							"87dlu, $lcgap, 106dlu",
							"4*(default, $lgap), default"));

						//---- playerNameLabel ----
						playerNameLabel.setText("Player name:");
						addPlayerPanel.add(playerNameLabel, cc.xy(1, 1));

						//---- playerNameField ----
						playerNameField.setEnabled(false);
						addPlayerPanel.add(playerNameField, cc.xy(3, 1));

						//---- characterNameLabel ----
						characterNameLabel.setText("Character:");
						addPlayerPanel.add(characterNameLabel, cc.xy(1, 3));

						//---- characterComboBox ----
						characterComboBox.setEnabled(false);
						addPlayerPanel.add(characterComboBox, cc.xy(3, 3));

						//---- addCharacterButton ----
						addCharacterButton.setText("Add Character");
						addCharacterButton.setEnabled(false);
						addCharacterButton.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								addCharacterButtonActionPerformed(e);
							}
						});
						addPlayerPanel.add(addCharacterButton, cc.xywh(1, 5, 3, 1));

						//---- deleteCharacterButton ----
						deleteCharacterButton.setText("Delete Selected Character");
						deleteCharacterButton.setEnabled(false);
						addPlayerPanel.add(deleteCharacterButton, cc.xywh(1, 7, 3, 1));
					}
					playerConfigPanel.add(addPlayerPanel, cc.xy(1, 3));

					//======== characterListScrollPane ========
					{

						//---- characterList ----
						characterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						characterListScrollPane.setViewportView(characterList);
					}
					playerConfigPanel.add(characterListScrollPane, cc.xywh(3, 1, 1, 5));

					//---- stepTwoDoneButton ----
					stepTwoDoneButton.setText("Done");
					stepTwoDoneButton.setEnabled(false);
					stepTwoDoneButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							stepTwoDoneButtonActionPerformed(e);
						}
					});
					playerConfigPanel.add(stepTwoDoneButton, cc.xy(1, 5));
				}
				playerPanel.add(playerConfigPanel, cc.xywh(1, 5, 1, 2));

				//======== finalOptionsPanel ========
				{
					finalOptionsPanel.setBorder(new TitledBorder("Step 3: Final Options"));
					finalOptionsPanel.setLayout(new FormLayout(
						"4*(default, $lcgap), 75dlu",
						"5*(default, $lgap), default"));

					//---- sympathizerTypeLabel ----
					sympathizerTypeLabel.setText("Sympathizer Type:");
					finalOptionsPanel.add(sympathizerTypeLabel, cc.xy(1, 3));

					//---- noSympathizerRadio ----
					noSympathizerRadio.setText("None");
					noSympathizerRadio.setEnabled(false);
					noSympathizerRadio.setSelected(true);
					finalOptionsPanel.add(noSympathizerRadio, cc.xy(3, 3));

					//---- sympathizerRadio ----
					sympathizerRadio.setText("Sympathizer");
					sympathizerRadio.setEnabled(false);
					finalOptionsPanel.add(sympathizerRadio, cc.xy(5, 3));

					//---- sympatheticCylonRadio ----
					sympatheticCylonRadio.setText("Sympathetic Cylon");
					sympatheticCylonRadio.setEnabled(false);
					finalOptionsPanel.add(sympatheticCylonRadio, cc.xy(7, 3));

					//---- exodusOptionsLabel ----
					exodusOptionsLabel.setText("Exodus Options:");
					finalOptionsPanel.add(exodusOptionsLabel, cc.xy(1, 5));

					//---- usePersonalGoalsCheckbox ----
					usePersonalGoalsCheckbox.setText("Personal Goals");
					usePersonalGoalsCheckbox.setEnabled(false);
					finalOptionsPanel.add(usePersonalGoalsCheckbox, cc.xy(3, 5));

					//---- useFinalFiveCheckbox ----
					useFinalFiveCheckbox.setText("Final Five");
					useFinalFiveCheckbox.setEnabled(false);
					finalOptionsPanel.add(useFinalFiveCheckbox, cc.xy(5, 5));

					//---- useCylonFleetCheckbox ----
					useCylonFleetCheckbox.setText("Cylon Fleet Board");
					useCylonFleetCheckbox.setEnabled(false);
					finalOptionsPanel.add(useCylonFleetCheckbox, cc.xywh(3, 7, 3, 1));

					//---- startGameButton ----
					startGameButton.setText("Start the Game!");
					startGameButton.setEnabled(false);
					startGameButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							startGameButtonActionPerformed(e);
						}
					});
					finalOptionsPanel.add(startGameButton, cc.xywh(7, 7, 3, 5));
				}
				playerPanel.add(finalOptionsPanel, cc.xy(1, 7, CellConstraints.FILL, CellConstraints.FILL));
			}
			mainTabbedPane.addTab("Game Setup", playerPanel);


			//======== gameStatePanel ========
			{
				gameStatePanel.setLayout(new FormLayout(
					"92dlu, $lcgap, 52dlu, $lcgap, 231dlu",
					"38dlu, $lgap, 52dlu, $lgap, 98dlu, $lgap, 215dlu, $lgap, 79dlu"));

				//======== mainStatePanel ========
				{
					mainStatePanel.setBorder(new TitledBorder("Game"));
					mainStatePanel.setLayout(new FormLayout(
						"23dlu, 2*($lcgap, default), $lcgap, 77dlu, 3*($lcgap, default), $lcgap, 154dlu",
						"default"));

					//---- turnTitleLabel ----
					turnTitleLabel.setText("Turn:");
					mainStatePanel.add(turnTitleLabel, cc.xywh(1, 1, 3, 1));

					//---- turnLabel ----
					turnLabel.setText("1.1");
					mainStatePanel.add(turnLabel, cc.xy(5, 1));

					//---- advanceTurnButton ----
					advanceTurnButton.setText("Advance Turn");
					advanceTurnButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							advanceTurnButtonActionPerformed(e);
						}
					});
					mainStatePanel.add(advanceTurnButton, cc.xy(7, 1));

					//---- rollD8Button ----
					rollD8Button.setText("Roll D8");
					rollD8Button.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							rollD8ButtonActionPerformed(e);
						}
					});
					mainStatePanel.add(rollD8Button, cc.xy(11, 1));
					mainStatePanel.add(d8Label, cc.xy(13, 1));

					//---- createDRADISButton ----
					createDRADISButton.setText("Create DRADIS");
					createDRADISButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							createDRADISButtonActionPerformed(e);
						}
					});
					mainStatePanel.add(createDRADISButton, cc.xy(15, 1));
				}
				gameStatePanel.add(mainStatePanel, cc.xywh(1, 1, 5, 1, CellConstraints.FILL, CellConstraints.FILL));

				//======== resoucesPanel ========
				{
					resoucesPanel.setBorder(new TitledBorder("Resources"));
					resoucesPanel.setLayout(new FormLayout(
						"default, $lcgap, 58dlu, $lcgap, 38dlu",
						"7*(default, $lgap), default"));

					//---- fuelLabel ----
					fuelLabel.setText("Fuel");
					resoucesPanel.add(fuelLabel, cc.xy(3, 1));

					//---- decreaseFuelButton ----
					decreaseFuelButton.setText("<-");
					decreaseFuelButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							resourceButtonActionPerformed(e);
						}
					});
					resoucesPanel.add(decreaseFuelButton, cc.xy(1, 3));

					//---- fuelProgress ----
					fuelProgress.setMaximum(15);
					fuelProgress.setStringPainted(true);
					resoucesPanel.add(fuelProgress, cc.xy(3, 3));

					//---- increaseFuelButton ----
					increaseFuelButton.setText("->");
					increaseFuelButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							resourceButtonActionPerformed(e);
						}
					});
					resoucesPanel.add(increaseFuelButton, cc.xy(5, 3));

					//---- foodLabel ----
					foodLabel.setText("Food");
					resoucesPanel.add(foodLabel, cc.xy(3, 5));

					//---- decreaseFoodButton ----
					decreaseFoodButton.setText("<-");
					decreaseFoodButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							resourceButtonActionPerformed(e);
						}
					});
					resoucesPanel.add(decreaseFoodButton, cc.xy(1, 7));

					//---- foodProgress ----
					foodProgress.setMaximum(15);
					foodProgress.setStringPainted(true);
					resoucesPanel.add(foodProgress, cc.xy(3, 7));

					//---- increaseFoodButton ----
					increaseFoodButton.setText("->");
					increaseFoodButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							resourceButtonActionPerformed(e);
						}
					});
					resoucesPanel.add(increaseFoodButton, cc.xy(5, 7));

					//---- moraleLabel ----
					moraleLabel.setText("Morale");
					resoucesPanel.add(moraleLabel, cc.xy(3, 9));

					//---- decreaseMoraleButton ----
					decreaseMoraleButton.setText("<-");
					decreaseMoraleButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							resourceButtonActionPerformed(e);
						}
					});
					resoucesPanel.add(decreaseMoraleButton, cc.xy(1, 11));

					//---- moraleProgress ----
					moraleProgress.setMaximum(15);
					moraleProgress.setStringPainted(true);
					resoucesPanel.add(moraleProgress, cc.xy(3, 11));

					//---- increaseMoraleButton ----
					increaseMoraleButton.setText("->");
					increaseMoraleButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							resourceButtonActionPerformed(e);
						}
					});
					resoucesPanel.add(increaseMoraleButton, cc.xy(5, 11));

					//---- populationLabel ----
					populationLabel.setText("Population");
					resoucesPanel.add(populationLabel, cc.xy(3, 13));

					//---- decreasePopulationButton ----
					decreasePopulationButton.setText("<-");
					decreasePopulationButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							resourceButtonActionPerformed(e);
						}
					});
					resoucesPanel.add(decreasePopulationButton, cc.xy(1, 15));

					//---- populationProgress ----
					populationProgress.setMaximum(15);
					populationProgress.setStringPainted(true);
					resoucesPanel.add(populationProgress, cc.xy(3, 15));

					//---- increasePopulationButton ----
					increasePopulationButton.setText("->");
					increasePopulationButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							resourceButtonActionPerformed(e);
						}
					});
					resoucesPanel.add(increasePopulationButton, cc.xy(5, 15));
				}
				gameStatePanel.add(resoucesPanel, cc.xywh(1, 3, 3, 3, CellConstraints.FILL, CellConstraints.FILL));

				//======== jumpTrackPanel ========
				{
					jumpTrackPanel.setBorder(new TitledBorder("Jump Track"));
					jumpTrackPanel.setLayout(new FormLayout(
						"7*(default, $lcgap), default",
						"2*(default, $lgap), default"));

					//---- jumpTrackStartBox ----
					jumpTrackStartBox.setText("Start");
					jumpTrackStartBox.setEnabled(false);
					jumpTrackPanel.add(jumpTrackStartBox, cc.xy(1, 1));

					//---- jumpTrackRed1Box ----
					jumpTrackRed1Box.setText("Red 1");
					jumpTrackRed1Box.setEnabled(false);
					jumpTrackPanel.add(jumpTrackRed1Box, cc.xy(3, 1));

					//---- jumpTrackRed2Box ----
					jumpTrackRed2Box.setText("Red 2");
					jumpTrackRed2Box.setEnabled(false);
					jumpTrackPanel.add(jumpTrackRed2Box, cc.xy(5, 1));

					//---- jumpTrackRisk3Box ----
					jumpTrackRisk3Box.setText("Risk 3");
					jumpTrackRisk3Box.setEnabled(false);
					jumpTrackPanel.add(jumpTrackRisk3Box, cc.xy(7, 1));

					//---- jumpTrackRisk1Box ----
					jumpTrackRisk1Box.setText("Risk 1");
					jumpTrackRisk1Box.setEnabled(false);
					jumpTrackPanel.add(jumpTrackRisk1Box, cc.xy(9, 1));

					//---- jumpTrackAutojumpBox ----
					jumpTrackAutojumpBox.setText("Jump");
					jumpTrackAutojumpBox.setEnabled(false);
					jumpTrackPanel.add(jumpTrackAutojumpBox, cc.xy(11, 1));

					//---- decreaseJumpTrackButton ----
					decreaseJumpTrackButton.setText("Decrease Jump Track");
					decreaseJumpTrackButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							decreaseJumpTrackButtonActionPerformed(e);
						}
					});
					jumpTrackPanel.add(decreaseJumpTrackButton, cc.xywh(1, 3, 5, 1));

					//---- increaseJumpTrackButton ----
					increaseJumpTrackButton.setText("Increase Jump Track");
					increaseJumpTrackButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							increaseJumpTrackButtonActionPerformed(e);
						}
					});
					jumpTrackPanel.add(increaseJumpTrackButton, cc.xywh(7, 3, 5, 1));
				}
				gameStatePanel.add(jumpTrackPanel, cc.xy(5, 3, CellConstraints.FILL, CellConstraints.FILL));

				//======== distancePanel ========
				{
					distancePanel.setBorder(new TitledBorder("Jumps and Distance"));
					distancePanel.setLayout(new FormLayout(
						"65dlu, $lcgap, default, $lcgap, 81dlu, $lcgap, 62dlu",
						"2*(default, $lgap), 2*(15dlu, $lgap), 9dlu"));

					//---- distanceTraveledLabel ----
					distanceTraveledLabel.setText("Distance Traveled");
					distancePanel.add(distanceTraveledLabel, cc.xy(1, 1));

					//---- distanceTraveledLabelVal ----
					distanceTraveledLabelVal.setText("0");
					distancePanel.add(distanceTraveledLabelVal, cc.xy(3, 1));

					//======== travelListScrollPane ========
					{

						//---- destinationList ----
						destinationList.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								destinationListMouseClicked(e);
							}
						});
						travelListScrollPane.setViewportView(destinationList);
					}
					distancePanel.add(travelListScrollPane, cc.xywh(5, 1, 1, 9));

					//======== scrollPane4 ========
					{

						//---- travelledList ----
						travelledList.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								destinationListMouseClicked(e);
							}
						});
						scrollPane4.setViewportView(travelledList);
					}
					distancePanel.add(scrollPane4, cc.xywh(1, 3, 2, 7));

					//---- jumpButton ----
					jumpButton.setText("Jump");
					jumpButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							jumpButtonActionPerformed(e);
						}
					});
					distancePanel.add(jumpButton, cc.xy(7, 3));

					//---- buryButton ----
					buryButton.setText("Bury");
					buryButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							buryButtonActionPerformed(e);
						}
					});
					distancePanel.add(buryButton, cc.xy(7, 5));

					//---- legendaryDiscovery ----
					legendaryDiscovery.setText("L. Discovery");
					legendaryDiscovery.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							legendaryDiscoveryActionPerformed(e);
						}
					});
					distancePanel.add(legendaryDiscovery, cc.xy(7, 7));
				}
				gameStatePanel.add(distancePanel, cc.xy(5, 5, CellConstraints.FILL, CellConstraints.FILL));

				//======== playerTablePanel ========
				{
					playerTablePanel.setBorder(new TitledBorder("Players"));
					playerTablePanel.setLayout(new FormLayout(
						"39dlu, 2*($lcgap, 90dlu), $lcgap, 143dlu",
						"136dlu, 3*($lgap, default)"));

					//======== scrollPane1 ========
					{

						//---- playersTable ----
						playersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						scrollPane1.setViewportView(playersTable);
					}
					playerTablePanel.add(scrollPane1, cc.xywh(1, 1, 7, 1));

					//---- makePresident ----
					makePresident.setText("Make President");
					makePresident.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							makePresidentActionPerformed(e);
						}
					});
					playerTablePanel.add(makePresident, cc.xy(3, 3));

					//---- revealCylonButton ----
					revealCylonButton.setText("Reveal Cylon");
					revealCylonButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							revealCylonButtonActionPerformed(e);
						}
					});
					playerTablePanel.add(revealCylonButton, cc.xy(5, 3));

					//---- makeAdmiralButton ----
					makeAdmiralButton.setText("Make Admiral");
					makeAdmiralButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							makeAdmiralButtonActionPerformed(e);
						}
					});
					playerTablePanel.add(makeAdmiralButton, cc.xy(3, 5));

					//---- executeButton ----
					executeButton.setText("Execute");
					playerTablePanel.add(executeButton, cc.xy(5, 5));

					//---- makeCAGButton ----
					makeCAGButton.setText("Make CAG");
					makeCAGButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							makeCAGButtonActionPerformed(e);
						}
					});
					playerTablePanel.add(makeCAGButton, cc.xy(3, 7));

					//---- useOPGButton ----
					useOPGButton.setText("Use OPG");
					playerTablePanel.add(useOPGButton, cc.xy(5, 7));
				}
				gameStatePanel.add(playerTablePanel, cc.xywh(1, 7, 5, 1, CellConstraints.FILL, CellConstraints.FILL));

				//======== boardingPartyPanel ========
				{
					boardingPartyPanel.setBorder(new TitledBorder("Boarding Party"));
					boardingPartyPanel.setLayout(new FormLayout(
						"3*(15dlu, $lcgap), 11dlu, $lcgap, 11dlu",
						"2*(default, $lgap), default"));

					//---- boarders_0 ----
					boarders_0.setEditable(false);
					boarders_0.setEnabled(false);
					boardingPartyPanel.add(boarders_0, cc.xy(1, 1));

					//---- boarders_1 ----
					boarders_1.setEditable(false);
					boarders_1.setEnabled(false);
					boardingPartyPanel.add(boarders_1, cc.xy(3, 1));

					//---- boarders_2 ----
					boarders_2.setEditable(false);
					boarders_2.setEnabled(false);
					boardingPartyPanel.add(boarders_2, cc.xy(5, 1));

					//---- boarders_3 ----
					boarders_3.setEditable(false);
					boarders_3.setEnabled(false);
					boardingPartyPanel.add(boarders_3, cc.xy(7, 1));

					//---- boarders_lose ----
					boarders_lose.setEditable(false);
					boarders_lose.setEnabled(false);
					boardingPartyPanel.add(boarders_lose, cc.xy(9, 1));

					//---- addBoarderButton ----
					addBoarderButton.setText("Add Boarder");
					addBoarderButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							addBoarderButtonActionPerformed(e);
						}
					});
					boardingPartyPanel.add(addBoarderButton, cc.xywh(1, 3, 9, 1));

					//---- advanceBoardersButton ----
					advanceBoardersButton.setText("Advance Boarders");
					advanceBoardersButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							advanceBoardersButtonActionPerformed(e);
						}
					});
					boardingPartyPanel.add(advanceBoardersButton, cc.xywh(1, 5, 9, 1));
				}
				gameStatePanel.add(boardingPartyPanel, cc.xy(1, 9, CellConstraints.FILL, CellConstraints.FILL));

				//======== nukesPanel ========
				{
					nukesPanel.setBorder(new TitledBorder("Nukes"));
					nukesPanel.setLayout(new FormLayout(
						"default, $lcgap, 21dlu",
						"2*(default, $lgap), default"));

					//---- nukeCountTitleLabel ----
					nukeCountTitleLabel.setText("Count:");
					nukesPanel.add(nukeCountTitleLabel, cc.xy(1, 1));

					//---- nukeCountLabel ----
					nukeCountLabel.setText("2");
					nukesPanel.add(nukeCountLabel, cc.xy(3, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));

					//---- useNukeButton ----
					useNukeButton.setText("Use");
					useNukeButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							useNukeButtonActionPerformed(e);
						}
					});
					nukesPanel.add(useNukeButton, cc.xywh(1, 3, 3, 1));

					//---- addNukeButton ----
					addNukeButton.setText("Add");
					addNukeButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							addNukeButtonActionPerformed(e);
						}
					});
					nukesPanel.add(addNukeButton, cc.xywh(1, 5, 3, 1));
				}
				gameStatePanel.add(nukesPanel, cc.xy(3, 9, CellConstraints.DEFAULT, CellConstraints.FILL));

				//======== damagePanel ========
				{
					damagePanel.setBorder(new TitledBorder("Damage"));
					damagePanel.setLayout(new FormLayout(
						"53dlu, $lcgap, 61dlu",
						"default, $lgap, 25dlu, $lgap, 18dlu"));

					//---- galacticaDamageLabel ----
					galacticaDamageLabel.setText("Galactica");
					damagePanel.add(galacticaDamageLabel, cc.xy(1, 1));

					//---- pegasusDamageLabel ----
					pegasusDamageLabel.setText("Pegasus");
					damagePanel.add(pegasusDamageLabel, cc.xy(3, 1));

					//======== scrollPane13 ========
					{
						scrollPane13.setViewportView(galacticaDamageList);
					}
					damagePanel.add(scrollPane13, cc.xywh(1, 3, 1, 3));
				}
				gameStatePanel.add(damagePanel, cc.xy(5, 9, CellConstraints.DEFAULT, CellConstraints.FILL));
			}
			mainTabbedPane.addTab("Game State", gameStatePanel);


			//======== skillCardPanel ========
			{
				skillCardPanel.setLayout(new FormLayout(
					"92dlu, $lcgap, 278dlu",
					"233dlu, $lgap, 214dlu"));

				//======== skillDeckQtyPanel ========
				{
					skillDeckQtyPanel.setBorder(new TitledBorder("Skill Decks"));
					skillDeckQtyPanel.setLayout(new FormLayout(
						"74dlu, $lcgap, 9dlu",
						"8*(default, $lgap), default"));

					//---- politicsLabel ----
					politicsLabel.setText("Politics");
					skillDeckQtyPanel.add(politicsLabel, cc.xy(1, 1));

					//---- politicsQty ----
					politicsQty.setText("0");
					skillDeckQtyPanel.add(politicsQty, cc.xy(3, 1));

					//---- leadershipLabel ----
					leadershipLabel.setText("Leadership");
					skillDeckQtyPanel.add(leadershipLabel, cc.xy(1, 3));

					//---- leadershipQty ----
					leadershipQty.setText("0");
					skillDeckQtyPanel.add(leadershipQty, cc.xy(3, 3));

					//---- tacticsLabel ----
					tacticsLabel.setText("Tactics");
					skillDeckQtyPanel.add(tacticsLabel, cc.xy(1, 5));

					//---- tacticsQty ----
					tacticsQty.setText("0");
					skillDeckQtyPanel.add(tacticsQty, cc.xy(3, 5));

					//---- pilotingLabel ----
					pilotingLabel.setText("Piloting");
					skillDeckQtyPanel.add(pilotingLabel, cc.xy(1, 7));

					//---- pilotingQty ----
					pilotingQty.setText("0");
					skillDeckQtyPanel.add(pilotingQty, cc.xy(3, 7));

					//---- engineeringLabel ----
					engineeringLabel.setText("Engineering");
					skillDeckQtyPanel.add(engineeringLabel, cc.xy(1, 9));

					//---- engineeringQty ----
					engineeringQty.setText("0");
					skillDeckQtyPanel.add(engineeringQty, cc.xy(3, 9));

					//---- trecheryLabel ----
					trecheryLabel.setText("Trechery");
					skillDeckQtyPanel.add(trecheryLabel, cc.xy(1, 11));

					//---- trecheryQty ----
					trecheryQty.setText("0");
					skillDeckQtyPanel.add(trecheryQty, cc.xy(3, 11));

					//---- destinyDeckLabel ----
					destinyDeckLabel.setText("Destiny Deck Size");
					skillDeckQtyPanel.add(destinyDeckLabel, cc.xy(1, 15));

					//---- destinyQty ----
					destinyQty.setText("0");
					skillDeckQtyPanel.add(destinyQty, cc.xy(3, 15));

					//---- viewDestinyDeckButton ----
					viewDestinyDeckButton.setText("View Destiny Deck");
					skillDeckQtyPanel.add(viewDestinyDeckButton, cc.xywh(1, 17, 3, 1, CellConstraints.FILL, CellConstraints.FILL));
				}
				skillCardPanel.add(skillDeckQtyPanel, cc.xy(1, 1, CellConstraints.DEFAULT, CellConstraints.FILL));

				//======== dealCardsPanel ========
				{
					dealCardsPanel.setBorder(new TitledBorder("Player Skill Cards"));
					dealCardsPanel.setLayout(new FormLayout(
						"91dlu, $lcgap, default, 2*($lcgap, 66dlu)",
						"10*(default, $lgap), default"));

					//======== skillCardCharacterListScrollPanel ========
					{

						//---- skillCardCharacterList ----
						skillCardCharacterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						skillCardCharacterList.addListSelectionListener(new ListSelectionListener() {
							@Override
							public void valueChanged(ListSelectionEvent e) {
								skillCardCharacterListValueChanged(e);
							}
						});
						skillCardCharacterListScrollPanel.setViewportView(skillCardCharacterList);
					}
					dealCardsPanel.add(skillCardCharacterListScrollPanel, cc.xywh(1, 1, 1, 13));

					//---- dealPolticsButton ----
					dealPolticsButton.setText("POL");
					dealPolticsButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							dealPolticsButtonActionPerformed(e);
						}
					});
					dealCardsPanel.add(dealPolticsButton, cc.xy(3, 1));

					//======== handScrollPanel ========
					{
						handScrollPanel.setViewportView(handList);
					}
					dealCardsPanel.add(handScrollPanel, cc.xywh(5, 1, 3, 13));

					//---- dealLeadershipButton ----
					dealLeadershipButton.setText("LEA");
					dealLeadershipButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							dealLeadershipButtonActionPerformed(e);
						}
					});
					dealCardsPanel.add(dealLeadershipButton, cc.xy(3, 3));

					//---- dealTacticsButton ----
					dealTacticsButton.setText("TAC");
					dealTacticsButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							dealTacticsButtonActionPerformed(e);
						}
					});
					dealCardsPanel.add(dealTacticsButton, cc.xy(3, 5));

					//---- dealPilotingButton ----
					dealPilotingButton.setText("PIL");
					dealPilotingButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							dealPilotingButtonActionPerformed(e);
						}
					});
					dealCardsPanel.add(dealPilotingButton, cc.xy(3, 7));

					//---- dealEngineeringButton ----
					dealEngineeringButton.setText("ENG");
					dealEngineeringButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							dealEngineeringButtonActionPerformed(e);
						}
					});
					dealCardsPanel.add(dealEngineeringButton, cc.xy(3, 9));

					//---- dealTrecheryButton ----
					dealTrecheryButton.setText("TRE");
					dealTrecheryButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							dealTrecheryButtonActionPerformed(e);
						}
					});
					dealCardsPanel.add(dealTrecheryButton, cc.xy(3, 11));
					dealCardsPanel.add(playerDrawString, cc.xy(1, 15));

					//---- discardSkillCardButton ----
					discardSkillCardButton.setText("Discard Card");
					discardSkillCardButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							discardSkillCardButtonActionPerformed(e);
						}
					});
					dealCardsPanel.add(discardSkillCardButton, cc.xywh(5, 15, 3, 1));

					//---- generateHandEmailButton ----
					generateHandEmailButton.setText("Generate Hand Email");
					generateHandEmailButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							generateHandEmailButtonActionPerformed(e);
						}
					});
					dealCardsPanel.add(generateHandEmailButton, cc.xy(1, 17));

					//---- giveToPlayerButton ----
					giveToPlayerButton.setText("Give to Another Player");
					dealCardsPanel.add(giveToPlayerButton, cc.xywh(5, 17, 3, 1));

					//---- playIntoSkillCheckButton ----
					playIntoSkillCheckButton.setText("Play in to Skill Check");
					playIntoSkillCheckButton.setEnabled(false);
					playIntoSkillCheckButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							playIntoSkillCheckButtonActionPerformed(e);
						}
					});
					dealCardsPanel.add(playIntoSkillCheckButton, cc.xywh(5, 19, 3, 1));
				}
				skillCardPanel.add(dealCardsPanel, cc.xy(3, 1, CellConstraints.DEFAULT, CellConstraints.FILL));

				//======== skillCheckPanel ========
				{
					skillCheckPanel.setBorder(new TitledBorder("Skill Check"));
					skillCheckPanel.setLayout(new FormLayout(
						"28dlu, $lcgap, 47dlu, $lcgap, 6dlu, 2*($lcgap, 100dlu), 2*($lcgap, 34dlu), $lcgap, default",
						"9*(default, $lgap), default"));

					//---- difficultySpinner ----
					difficultySpinner.setModel(new SpinnerNumberModel(0, 0, null, 1));
					skillCheckPanel.add(difficultySpinner, cc.xy(1, 1));

					//---- difficultyLabel ----
					difficultyLabel.setText("Difficulty");
					skillCheckPanel.add(difficultyLabel, cc.xywh(3, 1, 2, 1));

					//---- positiveCardsLabel ----
					positiveCardsLabel.setText("Positive Cards");
					skillCheckPanel.add(positiveCardsLabel, cc.xy(7, 1));

					//---- negativeCardsLabel ----
					negativeCardsLabel.setText("Negative Cards");
					skillCheckPanel.add(negativeCardsLabel, cc.xy(9, 1));

					//---- addDestinyButton ----
					addDestinyButton.setText("Add Destiny");
					addDestinyButton.setEnabled(false);
					addDestinyButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							addDestinyButtonActionPerformed(e);
						}
					});
					skillCheckPanel.add(addDestinyButton, cc.xywh(11, 1, 3, 1));

					//---- partialSpinner ----
					partialSpinner.setModel(new SpinnerNumberModel(0, 0, null, 1));
					skillCheckPanel.add(partialSpinner, cc.xy(1, 3));

					//---- partialLabel ----
					partialLabel.setText("Partial");
					skillCheckPanel.add(partialLabel, cc.xy(3, 3));

					//======== positiveCardListScrollPane ========
					{
						positiveCardListScrollPane.setViewportView(positiveCardList);
					}
					skillCheckPanel.add(positiveCardListScrollPane, cc.xywh(7, 3, 1, 17));

					//======== negativeCardsListScrollPane ========
					{
						negativeCardsListScrollPane.setViewportView(negativeCardList);
					}
					skillCheckPanel.add(negativeCardsListScrollPane, cc.xywh(9, 3, 1, 17));

					//---- discardAllBCardsButton ----
					discardAllBCardsButton.setText("Discard All Cards");
					discardAllBCardsButton.setEnabled(false);
					discardAllBCardsButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							discardAllBCardsButtonActionPerformed(e);
						}
					});
					skillCheckPanel.add(discardAllBCardsButton, cc.xywh(11, 3, 3, 1));

					//---- hasConsequencesCheckbox ----
					hasConsequencesCheckbox.setText("Has Consequences");
					skillCheckPanel.add(hasConsequencesCheckbox, cc.xywh(1, 5, 3, 1));

					//---- doubleEngineeringButton ----
					doubleEngineeringButton.setText("Double ENG");
					doubleEngineeringButton.setEnabled(false);
					doubleEngineeringButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							doubleEngineeringButtonActionPerformed(e);
						}
					});
					skillCheckPanel.add(doubleEngineeringButton, cc.xywh(11, 5, 3, 1));

					//---- politicsCheckbox ----
					politicsCheckbox.setText("Politics");
					skillCheckPanel.add(politicsCheckbox, cc.xywh(1, 7, 3, 1));

					//---- leadershipCheckbox ----
					leadershipCheckbox.setText("Leadership");
					skillCheckPanel.add(leadershipCheckbox, cc.xywh(1, 9, 3, 1));

					//---- tacticsCheckbox ----
					tacticsCheckbox.setText("Tactics");
					skillCheckPanel.add(tacticsCheckbox, cc.xywh(1, 11, 3, 1));

					//---- pilotingCheckbox ----
					pilotingCheckbox.setText("Piloting");
					skillCheckPanel.add(pilotingCheckbox, cc.xywh(1, 13, 3, 1));

					//---- engineeringCheckbox ----
					engineeringCheckbox.setText("Engineering");
					skillCheckPanel.add(engineeringCheckbox, cc.xywh(1, 15, 3, 1));

					//---- skillCheckResultLabel ----
					skillCheckResultLabel.setText("Result");
					skillCheckPanel.add(skillCheckResultLabel, cc.xy(11, 15));
					skillCheckPanel.add(skillCheckResult, cc.xy(13, 15));

					//---- trecheryCheckbox ----
					trecheryCheckbox.setText("Trechery");
					skillCheckPanel.add(trecheryCheckbox, cc.xywh(1, 17, 3, 1));

					//---- skillCheckTotalLabel ----
					skillCheckTotalLabel.setText("Total");
					skillCheckPanel.add(skillCheckTotalLabel, cc.xywh(11, 17, 2, 1));
					skillCheckPanel.add(runningTotalLabel, cc.xy(13, 17));

					//---- startSkillCheckButton ----
					startSkillCheckButton.setText("Start Skill Check");
					startSkillCheckButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							startSkillCheckButtonActionPerformed(e);
						}
					});
					skillCheckPanel.add(startSkillCheckButton, cc.xywh(1, 19, 3, 1));

					//---- exportSkillCheckButton ----
					exportSkillCheckButton.setText("Export Skill Check");
					exportSkillCheckButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							exportSkillCheckButtonActionPerformed(e);
						}
					});
					skillCheckPanel.add(exportSkillCheckButton, cc.xywh(11, 19, 3, 1));
				}
				skillCardPanel.add(skillCheckPanel, cc.xywh(1, 3, 3, 1, CellConstraints.FILL, CellConstraints.FILL));
			}
			mainTabbedPane.addTab("Skill Cards", skillCardPanel);


			//======== crisisQuorumDeckPanel ========
			{
				crisisQuorumDeckPanel.setBorder(null);
				crisisQuorumDeckPanel.setLayout(new FormLayout(
					"374dlu, $lcgap, default",
					"190dlu, $lgap, 250dlu, $lgap, default"));

				//======== crisisDeckPanel ========
				{
					crisisDeckPanel.setBorder(new TitledBorder("Crisis Cards"));
					crisisDeckPanel.setLayout(new FormLayout(
						"3*(89dlu, $lcgap), 89dlu",
						"15dlu, $lgap, 74dlu, $lgap, 59dlu, $lgap, default"));

					//---- crisisCardDeckLabel ----
					crisisCardDeckLabel.setText("Deck");
					crisisDeckPanel.add(crisisCardDeckLabel, cc.xywh(1, 1, 3, 1));

					//---- crisisCardDiscardLabel ----
					crisisCardDiscardLabel.setText("Discards");
					crisisDeckPanel.add(crisisCardDiscardLabel, cc.xywh(5, 1, 3, 1));

					//======== scrollPane2 ========
					{

						//---- crisisDeckList ----
						crisisDeckList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						crisisDeckList.addListSelectionListener(new ListSelectionListener() {
							@Override
							public void valueChanged(ListSelectionEvent e) {
								crisisDeckListValueChanged(e);
							}
						});
						crisisDeckList.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								crisisDeckMouseClicked(e);
							}
						});
						scrollPane2.setViewportView(crisisDeckList);
					}
					crisisDeckPanel.add(scrollPane2, cc.xywh(1, 3, 3, 3, CellConstraints.FILL, CellConstraints.FILL));

					//======== scrollPane3 ========
					{

						//---- crisisDiscardList ----
						crisisDiscardList.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								crisisDeckMouseClicked(e);
							}
						});
						scrollPane3.setViewportView(crisisDiscardList);
					}
					crisisDeckPanel.add(scrollPane3, cc.xywh(5, 3, 3, 3));

					//---- playCrisisCardButton ----
					playCrisisCardButton.setText("Play Crisis Card");
					playCrisisCardButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							playCrisisCardButtonActionPerformed(e);
						}
					});
					crisisDeckPanel.add(playCrisisCardButton, cc.xy(1, 7));

					//---- buryCrisisCardButton ----
					buryCrisisCardButton.setText("Bury Crisis Card");
					buryCrisisCardButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							buryCrisisCardButtonActionPerformed(e);
						}
					});
					crisisDeckPanel.add(buryCrisisCardButton, cc.xy(3, 7));

					//---- reshuffleCrisisDeckButton ----
					reshuffleCrisisDeckButton.setText("Reshuffle Crisis Card");
					reshuffleCrisisDeckButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							reshuffleCrisisDeckButtonActionPerformed(e);
						}
					});
					crisisDeckPanel.add(reshuffleCrisisDeckButton, cc.xy(5, 7));
				}
				crisisQuorumDeckPanel.add(crisisDeckPanel, cc.xy(1, 1, CellConstraints.FILL, CellConstraints.FILL));

				//======== quorumCardPanel ========
				{
					quorumCardPanel.setBorder(new TitledBorder("Quorum Cards"));
					quorumCardPanel.setLayout(new FormLayout(
						"3*(89dlu, $lcgap), 89dlu",
						"default, $lgap, 73dlu, $lgap, 9dlu, $lgap, 62dlu, 3*($lgap, default)"));

					//---- quorumDeckLabel ----
					quorumDeckLabel.setText("Deck");
					quorumCardPanel.add(quorumDeckLabel, cc.xy(1, 1));

					//---- quorumHandLabel ----
					quorumHandLabel.setText("Hand");
					quorumCardPanel.add(quorumHandLabel, cc.xy(3, 1));

					//---- quorumCardActiveLabel ----
					quorumCardActiveLabel.setText("Active");
					quorumCardPanel.add(quorumCardActiveLabel, cc.xy(5, 1));

					//---- quorumCardRemovedLabel ----
					quorumCardRemovedLabel.setText("Removed");
					quorumCardPanel.add(quorumCardRemovedLabel, cc.xy(7, 1));

					//======== scrollPane8 ========
					{

						//---- quorumCardDeckList ----
						quorumCardDeckList.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								quorumCardListMouseClicked(e);
							}
						});
						scrollPane8.setViewportView(quorumCardDeckList);
					}
					quorumCardPanel.add(scrollPane8, cc.xywh(1, 3, 1, 5, CellConstraints.DEFAULT, CellConstraints.FILL));

					//======== scrollPane9 ========
					{

						//---- quorumCardHandList ----
						quorumCardHandList.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								quorumCardListMouseClicked(e);
							}
						});
						scrollPane9.setViewportView(quorumCardHandList);
					}
					quorumCardPanel.add(scrollPane9, cc.xywh(3, 3, 1, 5, CellConstraints.DEFAULT, CellConstraints.FILL));

					//======== scrollPane10 ========
					{

						//---- quorumCardActiveList ----
						quorumCardActiveList.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								quorumCardListMouseClicked(e);
							}
						});
						scrollPane10.setViewportView(quorumCardActiveList);
					}
					quorumCardPanel.add(scrollPane10, cc.xywh(5, 3, 1, 5, CellConstraints.FILL, CellConstraints.FILL));

					//======== scrollPane11 ========
					{

						//---- quorumCardRemovedList ----
						quorumCardRemovedList.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								quorumCardListMouseClicked(e);
							}
						});
						scrollPane11.setViewportView(quorumCardRemovedList);
					}
					quorumCardPanel.add(scrollPane11, cc.xy(7, 3, CellConstraints.DEFAULT, CellConstraints.FILL));

					//---- discardedQuorumLabel ----
					discardedQuorumLabel.setText("Discarded");
					quorumCardPanel.add(discardedQuorumLabel, cc.xy(7, 5));

					//======== scrollPane12 ========
					{

						//---- quorumCardDiscardedList ----
						quorumCardDiscardedList.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								quorumCardListMouseClicked(e);
							}
						});
						scrollPane12.setViewportView(quorumCardDiscardedList);
					}
					quorumCardPanel.add(scrollPane12, cc.xy(7, 7));

					//---- dealQuorumCardButton ----
					dealQuorumCardButton.setText("Deal Quorum Card");
					dealQuorumCardButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							dealQuorumCardButtonActionPerformed(e);
						}
					});
					quorumCardPanel.add(dealQuorumCardButton, cc.xy(1, 9));

					//---- playQuorumCardButton ----
					playQuorumCardButton.setText("Play Quorum Card");
					playQuorumCardButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							playQuorumCardButtonActionPerformed(e);
						}
					});
					quorumCardPanel.add(playQuorumCardButton, cc.xy(3, 9));

					//---- discardActiveQuorumCardButton ----
					discardActiveQuorumCardButton.setText("Discard Active Card");
					discardActiveQuorumCardButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							discardActiveQuorumCardButtonActionPerformed(e);
						}
					});
					quorumCardPanel.add(discardActiveQuorumCardButton, cc.xy(5, 9));

					//---- discardQuorumCardButton ----
					discardQuorumCardButton.setText("Discard Quorum Card");
					discardQuorumCardButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							discardQuorumCardButtonActionPerformed(e);
						}
					});
					quorumCardPanel.add(discardQuorumCardButton, cc.xy(3, 11));

					//---- generateQuorumHandEmailButton ----
					generateQuorumHandEmailButton.setText("Generate Hand Email");
					generateQuorumHandEmailButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							generateQuorumHandEmailButtonActionPerformed(e);
						}
					});
					quorumCardPanel.add(generateQuorumHandEmailButton, cc.xy(3, 13));
				}
				crisisQuorumDeckPanel.add(quorumCardPanel, cc.xy(1, 3, CellConstraints.FILL, CellConstraints.FILL));
			}
			mainTabbedPane.addTab("Crisis/Quorum Cards", crisisQuorumDeckPanel);


			//======== loyaltyCardPanel ========
			{
				loyaltyCardPanel.setLayout(new FormLayout(
					"379dlu",
					"140dlu, $lgap, 129dlu"));

				//======== loyaltyCardInnerPanel ========
				{
					loyaltyCardInnerPanel.setBorder(new TitledBorder("Loyalty Card Hands"));
					loyaltyCardInnerPanel.setLayout(new FormLayout(
						"175dlu, 2*($lcgap, 95dlu)",
						"2*(default, $lgap), default"));

					//======== scrollPane5 ========
					{

						//---- loyaltyCardCharacterList ----
						loyaltyCardCharacterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						loyaltyCardCharacterList.addListSelectionListener(new ListSelectionListener() {
							@Override
							public void valueChanged(ListSelectionEvent e) {
								loyaltyCardCharacterListValueChanged(e);
							}
						});
						scrollPane5.setViewportView(loyaltyCardCharacterList);
					}
					loyaltyCardInnerPanel.add(scrollPane5, cc.xy(1, 1));

					//======== scrollPane6 ========
					{
						scrollPane6.setViewportView(loyaltyPanelHandList);
					}
					loyaltyCardInnerPanel.add(scrollPane6, cc.xywh(3, 1, 3, 1, CellConstraints.DEFAULT, CellConstraints.FILL));

					//---- giveLoyaltyCardsButton ----
					giveLoyaltyCardsButton.setText("Give Loyalty Cards To");
					giveLoyaltyCardsButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							giveLoyaltyCardsButtonActionPerformed(e);
						}
					});
					loyaltyCardInnerPanel.add(giveLoyaltyCardsButton, cc.xy(3, 3));
					loyaltyCardInnerPanel.add(giveLoyaltyCardsComboBox, cc.xy(5, 3));
				}
				loyaltyCardPanel.add(loyaltyCardInnerPanel, cc.xy(1, 1, CellConstraints.FILL, CellConstraints.FILL));

				//======== loyaltyDeckPanel ========
				{
					loyaltyDeckPanel.setBorder(new TitledBorder("Loyalty Deck"));
					loyaltyDeckPanel.setLayout(new FormLayout(
						"156dlu, $lcgap, 73dlu",
						"2*(default, $lgap), 71dlu"));

					//======== scrollPane7 ========
					{
						scrollPane7.setViewportView(loyaltyDeckList);
					}
					loyaltyDeckPanel.add(scrollPane7, cc.xywh(1, 1, 1, 5));

					//---- dealSleeperButton ----
					dealSleeperButton.setText("Deal Sleeper");
					dealSleeperButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							dealSleeperButtonActionPerformed(e);
						}
					});
					loyaltyDeckPanel.add(dealSleeperButton, cc.xy(3, 1));
				}
				loyaltyCardPanel.add(loyaltyDeckPanel, cc.xy(1, 3, CellConstraints.FILL, CellConstraints.FILL));
			}
			mainTabbedPane.addTab("Loyalty Cards", loyaltyCardPanel);


			//======== shipsPanel ========
			{
				shipsPanel.setLayout(new FormLayout(
					"default, $lcgap, default",
					"2*(default, $lgap), default"));
			}
			mainTabbedPane.addTab("Ships", shipsPanel);

		}
		contentPane.add(mainTabbedPane, cc.xy(1, 1, CellConstraints.FILL, CellConstraints.FILL));
		pack();
		setLocationRelativeTo(getOwner());

		//---- sympthizerButtonGroup ----
		ButtonGroup sympthizerButtonGroup = new ButtonGroup();
		sympthizerButtonGroup.add(noSympathizerRadio);
		sympthizerButtonGroup.add(sympathizerRadio);
		sympthizerButtonGroup.add(sympatheticCylonRadio);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JTabbedPane mainTabbedPane;
	private JPanel playerPanel;
	private JPanel gameSetupPanel;
	private JCheckBox pegasusCheckbox;
	private JCheckBox exodusCheckbox;
	private JCheckBox customCheckbox;
	private JButton finishedStep1Button;
	private JButton loadGameButton;
	private JCheckBox useComplexDestinyCheckbox;
	private JPanel playerConfigPanel;
	private JPanel addPlayerPanel;
	private JTextField playerNameField;
	private JComboBox characterComboBox;
	private JButton addCharacterButton;
	private JButton deleteCharacterButton;
	private JScrollPane characterListScrollPane;
	private JList characterList;
	private JButton stepTwoDoneButton;
	private JPanel finalOptionsPanel;
	private JRadioButton noSympathizerRadio;
	private JRadioButton sympathizerRadio;
	private JRadioButton sympatheticCylonRadio;
	private JCheckBox usePersonalGoalsCheckbox;
	private JCheckBox useFinalFiveCheckbox;
	private JCheckBox useCylonFleetCheckbox;
	private JButton startGameButton;
	private JPanel gameStatePanel;
	private JPanel mainStatePanel;
	private JLabel turnLabel;
	private JButton advanceTurnButton;
	private JButton rollD8Button;
	private JLabel d8Label;
	private JButton createDRADISButton;
	private JPanel resoucesPanel;
	private JButton decreaseFuelButton;
	private JProgressBar fuelProgress;
	private JButton increaseFuelButton;
	private JButton decreaseFoodButton;
	private JProgressBar foodProgress;
	private JButton increaseFoodButton;
	private JButton decreaseMoraleButton;
	private JProgressBar moraleProgress;
	private JButton increaseMoraleButton;
	private JButton decreasePopulationButton;
	private JProgressBar populationProgress;
	private JButton increasePopulationButton;
	private JPanel jumpTrackPanel;
	private JCheckBox jumpTrackStartBox;
	private JCheckBox jumpTrackRed1Box;
	private JCheckBox jumpTrackRed2Box;
	private JCheckBox jumpTrackRisk3Box;
	private JCheckBox jumpTrackRisk1Box;
	private JCheckBox jumpTrackAutojumpBox;
	private JButton decreaseJumpTrackButton;
	private JButton increaseJumpTrackButton;
	private JPanel distancePanel;
	private JLabel distanceTraveledLabel;
	private JLabel distanceTraveledLabelVal;
	private JScrollPane travelListScrollPane;
	private JList destinationList;
	private JScrollPane scrollPane4;
	private JList travelledList;
	private JButton jumpButton;
	private JButton buryButton;
	private JButton legendaryDiscovery;
	private JPanel playerTablePanel;
	private JScrollPane scrollPane1;
	private JTable playersTable;
	private JButton makePresident;
	private JButton revealCylonButton;
	private JButton makeAdmiralButton;
	private JButton executeButton;
	private JButton makeCAGButton;
	private JButton useOPGButton;
	private JPanel boardingPartyPanel;
	private JTextField boarders_0;
	private JTextField boarders_1;
	private JTextField boarders_2;
	private JTextField boarders_3;
	private JTextField boarders_lose;
	private JButton addBoarderButton;
	private JButton advanceBoardersButton;
	private JPanel nukesPanel;
	private JLabel nukeCountLabel;
	private JButton useNukeButton;
	private JButton addNukeButton;
	private JPanel damagePanel;
	private JScrollPane scrollPane13;
	private JList galacticaDamageList;
	private JPanel skillCardPanel;
	private JPanel skillDeckQtyPanel;
	private JLabel politicsQty;
	private JLabel leadershipQty;
	private JLabel tacticsQty;
	private JLabel pilotingQty;
	private JLabel engineeringQty;
	private JLabel trecheryQty;
	private JLabel destinyQty;
	private JButton viewDestinyDeckButton;
	private JPanel dealCardsPanel;
	private JScrollPane skillCardCharacterListScrollPanel;
	private JList skillCardCharacterList;
	private JButton dealPolticsButton;
	private JScrollPane handScrollPanel;
	private JList handList;
	private JButton dealLeadershipButton;
	private JButton dealTacticsButton;
	private JButton dealPilotingButton;
	private JButton dealEngineeringButton;
	private JButton dealTrecheryButton;
	private JLabel playerDrawString;
	private JButton discardSkillCardButton;
	private JButton generateHandEmailButton;
	private JButton giveToPlayerButton;
	private JButton playIntoSkillCheckButton;
	private JPanel skillCheckPanel;
	private JSpinner difficultySpinner;
	private JButton addDestinyButton;
	private JSpinner partialSpinner;
	private JScrollPane positiveCardListScrollPane;
	private JList positiveCardList;
	private JScrollPane negativeCardsListScrollPane;
	private JList negativeCardList;
	private JButton discardAllBCardsButton;
	private JCheckBox hasConsequencesCheckbox;
	private JButton doubleEngineeringButton;
	private JCheckBox politicsCheckbox;
	private JCheckBox leadershipCheckbox;
	private JCheckBox tacticsCheckbox;
	private JCheckBox pilotingCheckbox;
	private JCheckBox engineeringCheckbox;
	private JLabel skillCheckResult;
	private JCheckBox trecheryCheckbox;
	private JLabel runningTotalLabel;
	private JButton startSkillCheckButton;
	private JButton exportSkillCheckButton;
	private JPanel crisisQuorumDeckPanel;
	private JPanel crisisDeckPanel;
	private JScrollPane scrollPane2;
	private JList crisisDeckList;
	private JScrollPane scrollPane3;
	private JList crisisDiscardList;
	private JButton playCrisisCardButton;
	private JButton buryCrisisCardButton;
	private JButton reshuffleCrisisDeckButton;
	private JPanel quorumCardPanel;
	private JScrollPane scrollPane8;
	private JList quorumCardDeckList;
	private JScrollPane scrollPane9;
	private JList quorumCardHandList;
	private JScrollPane scrollPane10;
	private JList quorumCardActiveList;
	private JScrollPane scrollPane11;
	private JList quorumCardRemovedList;
	private JScrollPane scrollPane12;
	private JList quorumCardDiscardedList;
	private JButton dealQuorumCardButton;
	private JButton playQuorumCardButton;
	private JButton discardActiveQuorumCardButton;
	private JButton discardQuorumCardButton;
	private JButton generateQuorumHandEmailButton;
	private JPanel loyaltyCardPanel;
	private JPanel loyaltyCardInnerPanel;
	private JScrollPane scrollPane5;
	private JList loyaltyCardCharacterList;
	private JScrollPane scrollPane6;
	private JList loyaltyPanelHandList;
	private JButton giveLoyaltyCardsButton;
	private JComboBox giveLoyaltyCardsComboBox;
	private JPanel loyaltyDeckPanel;
	private JScrollPane scrollPane7;
	private JList loyaltyDeckList;
	private JButton dealSleeperButton;
	private JPanel shipsPanel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
