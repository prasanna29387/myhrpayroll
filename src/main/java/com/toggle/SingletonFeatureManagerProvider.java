package com.toggle;

import org.togglz.core.manager.FeatureManager;
import org.togglz.core.manager.FeatureManagerBuilder;
import org.togglz.core.spi.FeatureManagerProvider;

public class SingletonFeatureManagerProvider implements FeatureManagerProvider {

	private static FeatureManager featureManager;

	@Override
	public int priority() {
		return 30;
	}

	@Override
	public synchronized FeatureManager getFeatureManager() {
		if (featureManager == null)
			featureManager = new FeatureManagerBuilder().togglzConfig(new TogglesConfig()).build();
		return featureManager;
	}

}