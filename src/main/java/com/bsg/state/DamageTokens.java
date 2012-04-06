package com.bsg.state;

import java.util.ArrayList;
import java.util.List;

public class DamageTokens {

	List<String> damageGalactica;
	List<String> damagePegasus;
	
	public DamageTokens() {
		damageGalactica = new ArrayList<String>();
		damagePegasus = new ArrayList<String>();
		
		damageGalactica.add("Hangar Deck");
		damageGalactica.add("Admiral's Quarters");
		damageGalactica.add("Armory");
		damageGalactica.add("FTL Control");
		damageGalactica.add("Food - Resource");
		damageGalactica.add("Command");
		damageGalactica.add("Weapons Control");
		damageGalactica.add("Fuel - Resource");
		
		damagePegasus.add("Engine Room");
		damagePegasus.add("Airlock");
		damagePegasus.add("Pegasus CIC");
		damagePegasus.add("Main Batteries");
	}
	
	public String damageRandomGalactica() {
		return "";
	}
}
