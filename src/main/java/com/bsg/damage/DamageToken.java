package com.bsg.damage;

import com.bsg.Expansion;
import com.bsg.Item;

public class DamageToken implements Item {
	
	private String name;
	private boolean discardWhenPlayed;
	private Expansion expansion;
	
	public DamageToken(String name, boolean discardWhenPlayed, Expansion expansion) {
		this.name = name;
		this.discardWhenPlayed = discardWhenPlayed;
		this.expansion = expansion;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean discardWhenPlayed() {
		return discardWhenPlayed;
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public Expansion getExpansion() {
		return expansion;
	}
}
