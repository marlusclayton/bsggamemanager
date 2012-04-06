package com.bsg.view;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bsg.Player;
import com.bsg.state.GameState;

public class PlayerTableModel implements TableModel {

	private static final Logger LOGGER = LoggerFactory.getLogger(PlayerTableModel.class);
	
	static final int CURRENT_PLAYER_COLUMN = 0;
	static final int PLAYER_NAME_COLUMN = 1;
	static final int CHARACTER_NAME_COLUMN = 2;
	static final int LOYALTY_COLUMN = 3;
	static final int SKILL_CARD_HAND_SIZE_COLUMN = 4;
	static final int LOCATION_COLUMN = 5;
	static final int IS_PRESIDENT_COLUMN = 6;
	static final int IS_ADMIRAL_COLUMN = 7;
	static final int IS_CAG_COLUMN = 8;

	private final GameState gs;
	private final GamePanel gp;
	
	public PlayerTableModel(GameState gs, GamePanel gp) {
		this.gs = gs;
		this.gp = gp;
	}
	
	@Override
	public void addTableModelListener(TableModelListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public Class<?> getColumnClass(int c) {
		if (c == IS_PRESIDENT_COLUMN || c == IS_ADMIRAL_COLUMN || c == IS_CAG_COLUMN)
			return Boolean.class;
		
		return String.class;
	}

	@Override
	public int getColumnCount() {
		return 9;
	}

	@Override
	public String getColumnName(int c) {
		//@formatter:off
		return c == CURRENT_PLAYER_COLUMN       ? ""            :
			   c == PLAYER_NAME_COLUMN          ? "Player Name" :
			   c == CHARACTER_NAME_COLUMN       ? "Character"   :
			   c == LOYALTY_COLUMN              ? "Loyalty"     :
			   c == SKILL_CARD_HAND_SIZE_COLUMN ? "Hand Size"   :
			   c == LOCATION_COLUMN             ? "Location"    :
			   c == IS_PRESIDENT_COLUMN         ? "President"   :
			   c == IS_ADMIRAL_COLUMN           ? "Admiral"     :
			   c == IS_CAG_COLUMN               ? "CAG"         :
				   "Unknown";
		//@formatter:on
	}

	@Override
	public int getRowCount() {
		return gs.getPlayers().size();
	}

	@Override
	public Object getValueAt(int x, int y) {
		Player p = gs.getPlayers().get(x);
		
		//@formatter:off
		return y == CURRENT_PLAYER_COLUMN       ? (x == gs.getSubturn() - 1 ? "*" : "")  :
			   y == PLAYER_NAME_COLUMN          ? p.getPlayerName()                      :
			   y == CHARACTER_NAME_COLUMN       ? p.getCharacter().getName()             :
			   y == LOYALTY_COLUMN              ? p.getLoyalty().getDescription()        :
			   y == SKILL_CARD_HAND_SIZE_COLUMN ? String.valueOf(p.getHand().size())     :
			   y == LOCATION_COLUMN             ? p.getLocation()                        :
			   y == IS_PRESIDENT_COLUMN         ? p.isPresident()                        : 
			   y == IS_ADMIRAL_COLUMN           ? p.isAdmiral()                          :
			   y == IS_CAG_COLUMN               ? p.isCAG()                              :
				   "";
		//@formatter:on
	}

	@Override
	public boolean isCellEditable(int x, int y) {
		return y == LOCATION_COLUMN || y == IS_PRESIDENT_COLUMN || y == IS_ADMIRAL_COLUMN || y == IS_CAG_COLUMN;
	}

	@Override
	public void removeTableModelListener(TableModelListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setValueAt(Object arg0, int x, int y) {
		if (y == LOCATION_COLUMN) {
			LOGGER.info("Setting location {} for {}", arg0, gs.getPlayers().get(x).getCharacter());
			gs.getPlayers().get(x).setLocation((String)arg0);
		}

		if (y == IS_CAG_COLUMN) {
			gs.makeCAG(gs.getPlayers().get(x));
		}
		
		if (y == IS_ADMIRAL_COLUMN) {
			gs.makeAdmiral(gs.getPlayers().get(x));
		}
		
		if (y == IS_PRESIDENT_COLUMN) {
			gs.makePresident(gs.getPlayers().get(x));
		}
		
		gp.refreshDisplay();
	}

}
