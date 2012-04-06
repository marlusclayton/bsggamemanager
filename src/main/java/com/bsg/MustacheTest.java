package com.bsg;

import java.io.IOException;
import java.io.PrintWriter;

import com.bsg.cards.skill.SkillCard;
import com.bsg.cards.skill.SkillCardType;
import com.bsg.characters.Character;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public class MustacheTest {
	

	public static void main(String[] args) throws IOException {
		MustacheFactory mf = new DefaultMustacheFactory();
		Mustache m = mf.compile("config/templates/hand.template");
		
		Player p = new Player("Ben", new Character("William Adama", "Adama", "Military", "", "", Expansion.BASE));
		p.giveSkillCard(new SkillCard("Launch Scout", 2, SkillCardType.TACTICS, false));
		p.giveSkillCard(new SkillCard("Strategic Planning", 3, SkillCardType.TACTICS, false));
		p.giveSkillCard(new SkillCard("Repair", 1, SkillCardType.ENGINEERING, false));
		
		
		m.execute(new PrintWriter(System.out), p).flush();
	}
}
