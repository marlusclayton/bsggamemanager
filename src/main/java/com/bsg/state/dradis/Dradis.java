package com.bsg.state.dradis;

import java.util.ArrayList;
import java.util.List;

import com.bsg.Player;
import com.bsg.state.GameState;

public class Dradis {

	int turn;
	int subturn;
	
	int food;
	int fuel;
	int morale;
	int population;
	
	String jp_start;
	String jp_red1;
	String jp_red2;
	String jp_risk3;
	String jp_risk1;
	String jp_autojump;
	
	List<DradisPlayer> players;
	
	int boarders_0;
	int boarders_1;
	int boarders_2;
	int boarders_3;
	int boarders_lose;
	
	public Dradis(GameState gs) {
		setTurn(gs);
		setResources(gs);
		setJumpTrack(gs);
		setPlayers(gs);
		setBoarders(gs);
	}
	
	private void setBoarders(GameState gs) {
		boarders_0 = gs.getNumBoardersAt(0);
		boarders_1 = gs.getNumBoardersAt(1);
		boarders_2 = gs.getNumBoardersAt(2);
		boarders_3 = gs.getNumBoardersAt(3);
		boarders_lose = gs.getNumBoardersAt(4);
	}
	
	private void setTurn(GameState gs) {
		turn = gs.getTurn();
		subturn = gs.getSubturn();
	}
	
	private void setResources(GameState gs) {
		food = gs.getFood();
		fuel = gs.getFuel();
		morale = gs.getMorale();
		population = gs.getPopulation();
	}
	
	private void setPlayers(GameState gs) {
		List<Player> p = gs.getPlayers();
		players = new ArrayList<DradisPlayer>();
		
		for (int i = 0; i < p.size(); ++i) {
			players.add(new DradisPlayer(p.get(i), i == subturn - 1));
		}
	}
	
	private void setJumpTrack(GameState gs) {
		switch(gs.getJumpTrack().getStatus()) {
		case START:
			jp_start = "X";
			break;
		case RED1:
			jp_red1 = "X";
			break;
		case RED2:
			jp_red2 = "X";
			break;
		case RISK3:
			jp_risk3 = "X";
			break;
		case RISK1:
			jp_risk1 = "X";
			break;
		case AUTOJUMP:
			jp_autojump = "X";
			break;
		}
	}
}
