package com.bsg.damage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.bsg.Expansion;

public class Damage {

	List<DamageToken> damageAvailable;
	
	Set<DamageToken> activeTokens;
	Set<DamageToken> removedTokens;
	
	public Damage(Set<Expansion> expansion) {
		damageAvailable = new ArrayList<DamageToken>();
		activeTokens = new TreeSet<DamageToken>();
		removedTokens = new HashSet<DamageToken>();
		
		if (expansion.contains(Expansion.BASE)) {
			damageAvailable.add(new DamageToken("Hangar Deck", false, Expansion.BASE));
			damageAvailable.add(new DamageToken("Admiral's Quarters", false, Expansion.BASE));
			damageAvailable.add(new DamageToken("Armory", false, Expansion.BASE));
			damageAvailable.add(new DamageToken("FTL Control", false, Expansion.BASE));
			damageAvailable.add(new DamageToken("Resource - Food", true, Expansion.BASE));
			damageAvailable.add(new DamageToken("Command", false, Expansion.BASE));
			damageAvailable.add(new DamageToken("Weapons Control", false, Expansion.BASE));
			damageAvailable.add(new DamageToken("Resource - Fuel", true, Expansion.BASE));
		}
		
		if (expansion.contains(Expansion.PEGASUS)) {
			damageAvailable.add(new DamageToken("Engine Room", false, Expansion.PEGASUS));
			damageAvailable.add(new DamageToken("Airlock", false, Expansion.PEGASUS));
			damageAvailable.add(new DamageToken("Pegasus CIC", false, Expansion.PEGASUS));
			damageAvailable.add(new DamageToken("Main Batteries", false, Expansion.PEGASUS));
		}
	}
	
	public void shuffleDamageTokens() {
		Collections.shuffle(damageAvailable);
	}
	
	public List<DamageToken> getAvailableGalacticaTokens() {
		return new ArrayList<DamageToken>(damageAvailable);
	}
	
	public Set<DamageToken> getRemovedTokens() {
		return new HashSet<DamageToken>(removedTokens);
	}
	
	public Set<DamageToken> getActiveTokens() {
		return new TreeSet<DamageToken>(activeTokens);
	}
	
	public void activateToken(DamageToken dt) {
		
		boolean removed = damageAvailable.remove(dt);
		
		if (!removed) {
			throw new IllegalStateException("Trying to remove damage token that doesn't exist");
		}
		
		(dt.discardWhenPlayed() ? removedTokens : activeTokens).add(dt);

	}
	
	public void deactivateToken(DamageToken dt) {
		if (!activeTokens.contains(dt))
			throw new IllegalStateException("Trying to deactivate inactive token");
		
		activeTokens.remove(dt);
		damageAvailable.add(dt);
	}
}
