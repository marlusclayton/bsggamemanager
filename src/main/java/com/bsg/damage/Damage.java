package com.bsg.damage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Damage {

	List<DamageToken> damageGalacticaAvailable;
	List<DamageToken> damagePegasusAvailable;
	
	public Damage() {
		damageGalacticaAvailable = new ArrayList<DamageToken>();
		damagePegasusAvailable = new ArrayList<DamageToken>();
		
		damageGalacticaAvailable.add(new DamageToken("Hangar Deck", false));
		damageGalacticaAvailable.add(new DamageToken("Admiral's Quarters", false));
		damageGalacticaAvailable.add(new DamageToken("Armory", false));
		damageGalacticaAvailable.add(new DamageToken("FTL Control", false));
		damageGalacticaAvailable.add(new DamageToken("Food - Resource", true));
		damageGalacticaAvailable.add(new DamageToken("Command", false));
		damageGalacticaAvailable.add(new DamageToken("Weapons Control", false));
		damageGalacticaAvailable.add(new DamageToken("Fuel - Resource", true));
		
		damagePegasusAvailable.add(new DamageToken("Engine Room", false));
		damagePegasusAvailable.add(new DamageToken("Airlock", false));
		damagePegasusAvailable.add(new DamageToken("Pegasus CIC", false));
		damagePegasusAvailable.add(new DamageToken("Main Batteries", false));
	}
	
	public void shuffleDamageTokens() {
		Collections.shuffle(damageGalacticaAvailable);
		Collections.shuffle(damagePegasusAvailable);
	}
	
	public List<DamageToken> getAvailableGalacticaTokens() {
		return new ArrayList<DamageToken>(damageGalacticaAvailable);
	}
	
	public List<DamageToken> getAvailablePegasusTokens() {
		return new ArrayList<DamageToken>(damagePegasusAvailable);
	}
}
