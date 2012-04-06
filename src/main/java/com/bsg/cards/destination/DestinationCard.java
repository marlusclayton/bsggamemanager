package com.bsg.cards.destination;

import com.bsg.Expansion;
import com.bsg.Item;
import com.bsg.cards.Card;

public class DestinationCard extends Card implements Item {

	private final String name;
	private final int distance;
	private final int fuelLoss;
	private final String other;
	private final String special;
	private final Expansion expansion;
	
	public DestinationCard(String name, int distance, int fuelLoss, String other, String special, Expansion expansion) {
		this.name = name;
		this.distance = distance;
		this.fuelLoss = fuelLoss;
		this.other = other;
		this.special = special;
		this.expansion = expansion;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the distance
	 */
	public int getDistance() {
		return distance;
	}

	/**
	 * @return the fuelLoss
	 */
	public int getFuelLoss() {
		return fuelLoss;
	}

	/**
	 * @return the other
	 */
	public String getOther() {
		return other;
	}

	/**
	 * @return the special
	 */
	public String getSpecial() {
		return special;
	}

	@Override
	public Expansion getExpansion() {
		return expansion;
	}
	
	@Override
	public String toString() {
		return String.format("%s (%d)", name, distance);
	}
	
	
}
