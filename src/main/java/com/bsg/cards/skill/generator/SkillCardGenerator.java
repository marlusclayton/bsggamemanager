package com.bsg.cards.skill.generator;

import java.util.HashSet;
import java.util.Set;

import com.bsg.cards.skill.SkillCard;

public class SkillCardGenerator {

	public static class InternalGenerator {
		private int qty;
		
		InternalGenerator(int qty) {
			this.qty = qty;
		}
		
		public Set<SkillCard> of(SkillCard card) {
			Set<SkillCard> ret = new HashSet<SkillCard>();
			
			for (int i = 0; i < qty; ++i) {
				ret.add(new SkillCard(card.getName(), card.getStrength(), card.getType(), card.hasSkillCheckAbility()));
			}
			
			return ret;
		}
	}
	
	public static InternalGenerator generate(int num) {
		return new InternalGenerator(num);
	}
}
