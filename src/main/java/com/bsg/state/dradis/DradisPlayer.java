package com.bsg.state.dradis;

import com.bsg.Player;

public class DradisPlayer {

	String player_name;
	String titles;
	String character_name;
	String character_short_name;
	String character_name_with_titles;
	String character_short_name_with_titles;
	String current_location;
	String draw;
	int hand_size;
	boolean is_president;
	boolean is_admiral;
	boolean is_cag;
	boolean has_opg;
	boolean is_current_turn;
	
	public DradisPlayer(Player p, boolean isCurrentTurn) {
		player_name = p.getPlayerName();
		character_name = p.getCharacter().getName();
		character_short_name = p.getCharacter().getShortName();
		titles = p.getTitles();
		character_name_with_titles = titles + " " + character_name;
		character_short_name_with_titles = titles + " " + character_short_name;
		current_location = p.getLocation();
		draw = p.getDrawString();
		hand_size = p.getHand().size();
		is_admiral = p.isAdmiral();
		is_president = p.isPresident();
		is_cag = p.isCAG();
		has_opg = p.hasOPG();
		is_current_turn = isCurrentTurn;
	}
	
	
}
