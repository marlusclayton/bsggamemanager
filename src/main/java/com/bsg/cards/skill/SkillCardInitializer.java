package com.bsg.cards.skill;

import java.util.HashSet;
import java.util.Set;

import com.bsg.Expansion;
import com.bsg.cards.skill.generator.SkillCardGenerator;

public class SkillCardInitializer {

	public static Set<SkillCard> getPoliticsCards(Set<Expansion> expansions) {
		Set<SkillCard> ret = new HashSet<SkillCard>();
		
		//consolidate power
		ret.addAll(SkillCardGenerator.generate(8).of(new SkillCard("Consolidate Power", 1, SkillCardType.POLITICS, false)));
		ret.addAll(SkillCardGenerator.generate(6).of(new SkillCard("Consolidate Power", 2, SkillCardType.POLITICS, false)));
		
		//investigative committee
		ret.addAll(SkillCardGenerator.generate(4).of(new SkillCard("Investigative Committee", 3, SkillCardType.POLITICS, false)));
		ret.addAll(SkillCardGenerator.generate(2).of(new SkillCard("Investigative Committee", 4, SkillCardType.POLITICS, false)));
		ret.addAll(SkillCardGenerator.generate(1).of(new SkillCard("Investigative Committee", 5, SkillCardType.POLITICS, false)));
		
		
		if (expansions.contains(Expansion.PEGASUS)) {
			//support of the people (pegasus)
			ret.add(new SkillCard("Support of the People", 1, SkillCardType.POLITICS, false));
			ret.add(new SkillCard("Support of the People", 2, SkillCardType.POLITICS, false));
			
			//Preventative policy (pegasus)
			for (int i = 3; i < 6; ++i) {
				ret.add(new SkillCard("Preventative Policy", i, SkillCardType.POLITICS, false));
			}
		}
		
		if (expansions.contains(Expansion.EXODUS)) {
		
			//red tape (exodus)
			ret.addAll(SkillCardGenerator.generate(4).of(new SkillCard("Red Tape", 0, SkillCardType.POLITICS, true)));
			
			//political prowess
			ret.add(new SkillCard("Political Prowess", 6, SkillCardType.POLITICS, false));
		}
		
		return ret;
	}
	
	public static Set<SkillCard> getLeadershipCards(Set<Expansion> expansions) {
		Set<SkillCard> ret = new HashSet<SkillCard>();
		
		//consolidate power
		ret.addAll(SkillCardGenerator.generate(8).of(new SkillCard("Executive Order", 1, SkillCardType.LEADERSHIP, false)));
		ret.addAll(SkillCardGenerator.generate(6).of(new SkillCard("Executive Order", 2, SkillCardType.LEADERSHIP, false)));
		
		//investigative committee
		ret.addAll(SkillCardGenerator.generate(4).of(new SkillCard("Declare Emergency", 3, SkillCardType.LEADERSHIP, false)));
		ret.addAll(SkillCardGenerator.generate(2).of(new SkillCard("Declare Emergency", 4, SkillCardType.LEADERSHIP, false)));
		ret.addAll(SkillCardGenerator.generate(1).of(new SkillCard("Declare Emergency", 5, SkillCardType.LEADERSHIP, false)));
		
		if (expansions.contains(Expansion.PEGASUS)) {
			//support of the people (pegasus)
			ret.add(new SkillCard("Major Victory", 1, SkillCardType.LEADERSHIP,
					false));
			ret.add(new SkillCard("Major Victory", 2, SkillCardType.LEADERSHIP,
					false));
			//Preventative policy (pegasus)
			for (int i = 3; i < 6; ++i) {
				ret.add(new SkillCard("At Any Cost", i,
						SkillCardType.LEADERSHIP, false));
			}
		}
		
		
		if (expansions.contains(Expansion.EXODUS)) {
			//red tape (exodus)
			ret.addAll(SkillCardGenerator.generate(4).of(
					new SkillCard("Iron Will", 0, SkillCardType.LEADERSHIP,
							true)));
			//political prowess
			ret.add(new SkillCard("State of Emergency", 6,
					SkillCardType.LEADERSHIP, false));
		}
		return ret;
	}
	
	public static Set<SkillCard> getTacticsCards(Set<Expansion> expansions) {
		Set<SkillCard> ret = new HashSet<SkillCard>();
		
		//consolidate power
		ret.addAll(SkillCardGenerator.generate(8).of(new SkillCard("Launch Scout", 1, SkillCardType.TACTICS, false)));
		ret.addAll(SkillCardGenerator.generate(6).of(new SkillCard("Launch Scout", 2, SkillCardType.TACTICS, false)));
		
		//investigative committee
		ret.addAll(SkillCardGenerator.generate(4).of(new SkillCard("Strategic Planning", 3, SkillCardType.TACTICS, false)));
		ret.addAll(SkillCardGenerator.generate(2).of(new SkillCard("Strategic Planning", 4, SkillCardType.TACTICS, false)));
		ret.addAll(SkillCardGenerator.generate(1).of(new SkillCard("Strategic Planning", 5, SkillCardType.TACTICS, false)));
		
		if (expansions.contains(Expansion.PEGASUS)) {
			//support of the people (pegasus)
			ret.add(new SkillCard("Guts and Initative", 1,
					SkillCardType.TACTICS, false));
			ret.add(new SkillCard("Guts and Initative", 2,
					SkillCardType.TACTICS, false));
			//Preventative policy (pegasus)
			for (int i = 3; i < 6; ++i) {
				ret.add(new SkillCard("Critical Suituation", i,
						SkillCardType.TACTICS, false));
			}
		}
		
		if (expansions.contains(Expansion.EXODUS)) {
			//red tape (exodus)
			ret.addAll(SkillCardGenerator.generate(4).of(
					new SkillCard("Trust Instincts", 0, SkillCardType.TACTICS,
							true)));
			//political prowess
			ret.add(new SkillCard("Scouting for Fuel", 6,
					SkillCardType.TACTICS, false));
		}
		return ret;
	}
	
	public static Set<SkillCard> getPilotingCards(Set<Expansion> expansions) {
		Set<SkillCard> ret = new HashSet<SkillCard>();
		
		//consolidate power
		ret.addAll(SkillCardGenerator.generate(8).of(new SkillCard("Evasive Manuevers", 1, SkillCardType.PILOTING, false)));
		ret.addAll(SkillCardGenerator.generate(6).of(new SkillCard("Evasive Manuevers", 2, SkillCardType.PILOTING, false)));
		
		//investigative committee
		ret.addAll(SkillCardGenerator.generate(4).of(new SkillCard("Maximum Firepower", 3, SkillCardType.PILOTING, false)));
		ret.addAll(SkillCardGenerator.generate(2).of(new SkillCard("Maximum Firepower", 4, SkillCardType.PILOTING, false)));
		ret.addAll(SkillCardGenerator.generate(1).of(new SkillCard("Maximum Firepower", 5, SkillCardType.PILOTING, false)));
		
		if (expansions.contains(Expansion.PEGASUS)) {
			//support of the people (pegasus)
			ret.add(new SkillCard("Full Throttle", 1, SkillCardType.PILOTING,
					false));
			ret.add(new SkillCard("Full Throttle", 2, SkillCardType.PILOTING,
					false));
			//Preventative policy (pegasus)
			for (int i = 3; i < 6; ++i) {
				ret.add(new SkillCard("Run Interference", i,
						SkillCardType.PILOTING, false));
			}
		}
		
		if (expansions.contains(Expansion.EXODUS)) {
			//red tape (exodus)
			ret.addAll(SkillCardGenerator.generate(4).of(
					new SkillCard("Protect the Fleet", 0,
							SkillCardType.PILOTING, true)));
			//political prowess
			ret.add(new SkillCard("Best of the Best", 6,
					SkillCardType.PILOTING, false));
		}
		
		return ret;
	}
	
	public static Set<SkillCard> getEngineeringCards(Set<Expansion> expansions) {
		Set<SkillCard> ret = new HashSet<SkillCard>();
		
		//consolidate power
		ret.addAll(SkillCardGenerator.generate(8).of(new SkillCard("Repair", 1, SkillCardType.ENGINEERING, false)));
		ret.addAll(SkillCardGenerator.generate(6).of(new SkillCard("Repair", 2, SkillCardType.ENGINEERING, false)));
		
		//investigative committee
		ret.addAll(SkillCardGenerator.generate(4).of(new SkillCard("Scientific Research", 3, SkillCardType.ENGINEERING, false)));
		ret.addAll(SkillCardGenerator.generate(2).of(new SkillCard("Scientific Research", 4, SkillCardType.ENGINEERING, false)));
		ret.addAll(SkillCardGenerator.generate(1).of(new SkillCard("Scientific Research", 5, SkillCardType.ENGINEERING, false)));
		
		if (expansions.contains(Expansion.PEGASUS)) {
			//support of the people (pegasus)
			ret.add(new SkillCard("Jury Rigged", 1, SkillCardType.ENGINEERING,
					false));
			ret.add(new SkillCard("Jury Rigged", 2, SkillCardType.ENGINEERING,
					false));
			//Preventative policy (pegasus)
			for (int i = 3; i < 6; ++i) {
				ret.add(new SkillCard("Calculations", i,
						SkillCardType.ENGINEERING, false));
			}
		}
		
		if (expansions.contains(Expansion.EXODUS)) {
			//red tape (exodus)
			ret.addAll(SkillCardGenerator.generate(4).of(
					new SkillCard("Establish Network", 0,
							SkillCardType.ENGINEERING, true)));
			//political prowess
			ret.add(new SkillCard("Build Nuke", 6, SkillCardType.ENGINEERING,
					false));
		}
		return ret;
	}
	
	public static Set<SkillCard> getTrecheryCards(Set<Expansion> expansions) {
		Set<SkillCard> ret = new HashSet<SkillCard>();
		
		if (!expansions.contains(Expansion.PEGASUS))
			return ret;
		
		ret.addAll(SkillCardGenerator.generate(8).of(new SkillCard("Broadcast Location", 1, SkillCardType.TRECHERY, true)));
		ret.addAll(SkillCardGenerator.generate(4).of(new SkillCard("By Your Command", 1, SkillCardType.TRECHERY, true)));
		ret.addAll(SkillCardGenerator.generate(5).of(new SkillCard("Special Destiny", 2, SkillCardType.TRECHERY, true)));
		ret.addAll(SkillCardGenerator.generate(3).of(new SkillCard("God's Plan", 2, SkillCardType.TRECHERY, false)));
		ret.addAll(SkillCardGenerator.generate(4).of(new SkillCard("Sabotage", 3, SkillCardType.TRECHERY, false)));
		ret.addAll(SkillCardGenerator.generate(2).of(new SkillCard("Human Weakness", 3, SkillCardType.TRECHERY, false)));
		
		return ret;
	}
	
}
