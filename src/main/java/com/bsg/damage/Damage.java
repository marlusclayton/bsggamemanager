package com.bsg.damage;

import java.util.ArrayList;
import java.util.List;

public class Damage {

	List<String> damageGalacticaAvailable;
	List<String> damagePegasusAvailable;
	
	public Damage() {
		damageGalacticaAvailable = new ArrayList<String>();
		damagePegasusAvailable = new ArrayList<String>();
		
		damageGalacticaAvailable.add("Hangar Deck");
		damageGalacticaAvailable.add("Admiral's Quarters");
		damageGalacticaAvailable.add("Armory");
		damageGalacticaAvailable.add("FTL Control");
		damageGalacticaAvailable.add("Food - Resource");
		damageGalacticaAvailable.add("Command");
		damageGalacticaAvailable.add("Weapons Control");
		damageGalacticaAvailable.add("Fuel - Resource");
		
		damagePegasusAvailable.add("Engine Room");
		damagePegasusAvailable.add("Airlock");
		damagePegasusAvailable.add("Pegasus CIC");
		damagePegasusAvailable.add("Main Batteries");
	}
	
	public String damageRandomGalactica() {
		return "";
	}
}
