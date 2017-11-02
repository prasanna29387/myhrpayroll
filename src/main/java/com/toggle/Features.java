package com.toggle;


import com.toggle.annotations.NonClientsToggleGroup;
import org.togglz.core.Feature;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

public enum Features implements Feature {

	@Label("Auto Replay Document Stuck in Workflow")
	@NonClientsToggleGroup
	REPLAY_STUCK_DOCUMENT;

	public boolean isActive() {
		return FeatureContext.getFeatureManager().isActive(this);
	}
}
