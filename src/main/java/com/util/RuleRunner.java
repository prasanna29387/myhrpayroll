package com.util;

import lombok.extern.slf4j.Slf4j;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RuleRunner {
	protected KieSession kieSession;
	@Autowired
	private static final String KIE_SESSION_NAME = "client-rules";

	public void initKieSession(String kSessionName) {
		KieServices kServices = KieServices.Factory.get();
		KieContainer kContainer = kServices.getKieClasspathContainer();
		kieSession = kContainer.newKieSession(kSessionName);
		kieSession.addEventListener(new RuleLogAgendaEventListener());
		setGlobalVariables();
	}

	public void runRules(Object fact) {
		initKieSession(KIE_SESSION_NAME);
		kieSession.insert(fact);
		kieSession.fireAllRules();
	}

	protected void setGlobalVariables() {
		kieSession.setGlobal("log", log);
	}
}
