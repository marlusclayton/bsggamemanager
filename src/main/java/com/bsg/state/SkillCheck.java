package com.bsg.state;

import static ch.lambdaj.Lambda.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.hamcrest.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.lambdaj.function.matcher.Predicate;

import com.bsg.cards.skill.SkillCard;
import com.bsg.cards.skill.SkillCardType;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.base.Joiner;

public class SkillCheck {
	
	static {
		MustacheFactory mf = new DefaultMustacheFactory();
		SKILL_CHECK_MUSTACHE = mf.compile("config/templates/skillcheck.template");
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SkillCheck.class);
	private static final Mustache SKILL_CHECK_MUSTACHE;
	
	private List<SkillCard> cards;
	private int runningTotal;
	private boolean triggerConsequences;
	private boolean isReckless;
	private int strength;
	private int partial;
	
	private Matcher<SkillCard> positiveMatcher;
	private Matcher<SkillCard> negativeMatcher;
	
	private final Set<SkillCardType> positives;
	
		
	public SkillCheck(int strength, int partial, Set<SkillCardType> positives) {
		LOGGER.info("Initializing skill check....");
		this.positives = EnumSet.copyOf(positives);
		this.strength = strength;
		this.partial = partial;
		
		initializeMatchers();
		
		
		cards = new ArrayList<SkillCard>();
		runningTotal = 0;
		triggerConsequences = false;
		LOGGER.info("Skill check initialized!");
	}
	
	private void initializeMatchers() {
		positiveMatcher = new Predicate<SkillCard>() {

			@Override
			public boolean apply(SkillCard sc) {
				return positives.contains(sc.getType());
			}
		};
		
		negativeMatcher = new Predicate<SkillCard>() {

			@Override
			public boolean apply(SkillCard sc) {
				return !positives.contains(sc.getType());
			}
		};
		
	}
	
	public int getPositivesTotal() {
		return sum(filter(positiveMatcher, cards), on(SkillCard.class).getStrength());
	}
	
	public int getNegativesTotal() {
		return sum(filter(negativeMatcher, cards), on(SkillCard.class).getStrength());
	}

	public String getResult() {
		if (runningTotal >= strength) {
			return "PASS";
		} else if (partial != 0 && runningTotal >= partial) {
			return "PARTIAL";
		} else {
			return "FAIL";
		}
	}
	
	public void addCard(SkillCard card) {
		LOGGER.info("Adding card {}", card);
		if (positives.contains(card.getType())) {
			LOGGER.info("Card is positive");
			cards.add(card);
			runningTotal += card.getStrength();
		} else {
			LOGGER.info("Card is negative");
			cards.add(card);
			runningTotal -= card.getStrength();
		}
		if (card.hasSkillCheckAbility())
			triggerConsequences = true;
		
		LOGGER.info("Running total is {}", runningTotal);
	}
	
	public int getTotal() {
		return runningTotal;
	}
	
	public boolean shouldTriggerConsequences() {
		return triggerConsequences;
	}
	
	public boolean isReckless() {
		return isReckless;
	}
	
	public void doubleSkillTypeValue(SkillCardType type) {
		if (positives.contains(type)) {
			for (SkillCard sc : cards) 
				if (sc.getType() == type)
					runningTotal += sc.getStrength();
		} else {
			for (SkillCard sc : cards)
				if (sc.getType() == type)
					runningTotal -= sc.getStrength();
		}
	}
	
	public List<SkillCard> getPositiveCards() {
		List<SkillCard> positiveCards = filter(positiveMatcher, cards);
		Collections.sort(positiveCards);
		return positiveCards;
	}
	
	public List<SkillCard> getNegativeCards() {
		List<SkillCard> negativeCards = filter(negativeMatcher, cards);
		Collections.sort(negativeCards);
		return negativeCards;
	}
	
	public Object clone() {
		SkillCheck sc = new SkillCheck(strength, partial, positives);
		return sc;
	}
	
	public String getTypes() {
		List<String> positiveShortNames = new ArrayList<String>();
		for (SkillCardType t : positives)
			positiveShortNames.add(t.getShortName());
		
		return join(positiveShortNames, "/");	
	}
	
	public int getStrength() {
		return strength;
	}
	
	public Integer getPartial() {
		return partial != 0 ? partial : null;
	}
	
	public String getFullText() throws IOException {
		StringWriter sw = new StringWriter();
		SKILL_CHECK_MUSTACHE.execute(sw, this).flush();
		return sw.toString();
	}
	
	
}
