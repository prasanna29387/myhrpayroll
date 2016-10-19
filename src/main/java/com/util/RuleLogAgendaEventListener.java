package com.util;

import lombok.extern.slf4j.Slf4j;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;

@Slf4j
public class RuleLogAgendaEventListener extends DefaultAgendaEventListener {

	@Override
	public void afterMatchFired(AfterMatchFiredEvent event) {
		Rule rule = event.getMatch().getRule();
		log.debug("Rule {} in {} matched", rule.getName(), rule.getPackageName());
	}
}
