package com.bsg.damage;

public class DamageToken {

	public String name;
	public boolean discardWhenPlayed;
	
	public DamageToken(String name, boolean discardWhenPlayed) {
		this.name = name;
		this.discardWhenPlayed = discardWhenPlayed;
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
}
