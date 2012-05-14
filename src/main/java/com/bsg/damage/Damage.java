package com.bsg.damage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.bsg.Expansion;

public class Damage {

	List<DamageToken> damageGalacticaAvailable;
	List<DamageToken> damagePegasusAvailable;
	
	Set<DamageToken> activeTokens;
	Set<DamageToken> removedTokens;
	
	public Damage() {
		damageGalacticaAvailable = new ArrayList<DamageToken>();
		damagePegasusAvailable = new ArrayList<DamageToken>();
		activeTokens = new TreeSet<DamageToken>();
		removedTokens = new HashSet<DamageToken>();
		
		damageGalacticaAvailable.add(new DamageToken("Hangar Deck", false, Expansion.BASE));
		damageGalacticaAvailable.add(new DamageToken("Admiral's Quarters", false, Expansion.BASE));
		damageGalacticaAvailable.add(new DamageToken("Armory", false, Expansion.BASE));
		damageGalacticaAvailable.add(new DamageToken("FTL Control", false, Expansion.BASE));
		damageGalacticaAvailable.add(new DamageToken("Resource - Food", true, Expansion.BASE));
		damageGalacticaAvailable.add(new DamageToken("Command", false, Expansion.BASE));
		damageGalacticaAvailable.add(new DamageToken("Weapons Control", false, Expansion.BASE));
		damageGalacticaAvailable.add(new DamageToken("Resource - Fuel", true, Expansion.BASE));
		
		damagePegasusAvailable.add(new DamageToken("Engine Room", false, Expansion.PEGASUS));
		damagePegasusAvailable.add(new DamageToken("Airlock", false, Expansion.PEGASUS));
		damagePegasusAvailable.add(new DamageToken("Pegasus CIC", false, Expansion.PEGASUS));
		damagePegasusAvailable.add(new DamageToken("Main Batteries", false, Expansion.PEGASUS));
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
	
	public Set<DamageToken> getRemovedTokens() {
		return new HashSet<DamageToken>(removedTokens);
	}
	
	public Set<DamageToken> getActiveTokens() {
		return new TreeSet<DamageToken>(activeTokens);
	}
	
	public void activateToken(DamageToken dt) {
		
		boolean removed = false;
		removed |= damageGalacticaAvailable.remove(dt);
		removed |= damagePegasusAvailable.remove(dt);
		
		if (!removed) {
			throw new IllegalStateException("Trying to remove damage token that doesn't exist");
		}
		
		(dt.discardWhenPlayed() ? removedTokens : activeTokens).add(dt);

	}
	
	public void deactivateToken(DamageToken dt) {
		if (!activeTokens.contains(dt))
			throw new IllegalStateException("Trying to deactivate inactive token");
		
		activeTokens.remove(dt);
		
		if (dt.getExpansion() == Expansion.PEGASUS)
			damagePegasusAvailable.add(dt);
		else
			damageGalacticaAvailable.add(dt);
	}
}
