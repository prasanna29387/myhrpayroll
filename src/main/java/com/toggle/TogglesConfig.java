package com.toggle;

import com.config.Config;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.togglz.core.Feature;
import org.togglz.core.manager.TogglzConfig;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.repository.cache.CachingStateRepository;
import org.togglz.core.repository.file.FileBasedStateRepository;
import org.togglz.core.repository.mem.InMemoryStateRepository;
import org.togglz.core.user.SimpleFeatureUser;
import org.togglz.core.user.UserProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class TogglesConfig implements TogglzConfig {
	public static final String TOGGLES_FILE_DESTINATION_FOLDER = "toggles.destination.folder";
	public static final String TOGGLES_FILE_CONFIG_KEY = "toggles.source.file";
	public static final String TOGGLES_FILE_READ_INTERVAL_SECONDS = "toggles.file.read.interval.seconds";
	public static final String TOGGLES_CACHE_REFRESH_INTERVAL_SECONDS = "toggles.cache.refresh.interval.seconds";
	public static final String DEFAULT_INTERVAL_STRING = "120";
	private static final String ADMIN_ID = "admin";
	public static final String TCE_API = "tce-api";

	@Override
	public Class<? extends Feature> getFeatureClass() {
		return Features.class;
	}

	@Override
	public StateRepository getStateRepository() {
		String fileName = Config.getProperty(TOGGLES_FILE_CONFIG_KEY);
		try (InputStream stream = getClass().getResourceAsStream("/" + fileName)) {
			return createCachingStateRepository(fileName, stream);
		} catch (Exception e) {
			log.warn(
					"Toggles properties file {} could not be located, using In-Memory option. Please configure Toggles using Console. Toggles will not be persisted!",
					fileName, e);
			return new InMemoryStateRepository();
		}
	}

	@Override
	public UserProvider getUserProvider() {
		return () -> new SimpleFeatureUser(ADMIN_ID, true);
	}

	@NonNull
	protected FileBasedStateRepository getFileBasedStateRepository(final File toggleFile) {
		return new FileBasedStateRepository(toggleFile, getFileReadIntervalMillis());
	}

	protected int getFileReadIntervalMillis() {
		return Integer.parseInt(Config.getProperty(TOGGLES_FILE_READ_INTERVAL_SECONDS, DEFAULT_INTERVAL_STRING)) * 1000;
	}

	protected long getCacheRefreshIntervalMillis() {
		return Integer.parseInt(Config.getProperty(TOGGLES_CACHE_REFRESH_INTERVAL_SECONDS, DEFAULT_INTERVAL_STRING))
				* 1000L;
	}

	private StateRepository createCachingStateRepository(final String fileName, final InputStream stream)
			throws IOException {
		File destination = getDestinationTogglesFile(fileName);
		if (!destination.exists()) {
			boolean result = destination.createNewFile();
			log.debug("Toggle config file created: {}", result);
		}
		modifyTogglzConfigFileOnlyForTceApi(stream, destination);
		return new CachingStateRepository(getFileBasedStateRepository(destination), getCacheRefreshIntervalMillis());
	}

	private void modifyTogglzConfigFileOnlyForTceApi(InputStream stream, File destination) throws IOException {
		if (Config.getCurrentModuleName().equalsIgnoreCase(TCE_API)) {
			log.debug("Updating {} config file", destination.getName());
			copyNewContentToTheExistingTogglzFile(stream, destination);
		}
	}

	protected void copyNewContentToTheExistingTogglzFile(InputStream stream, File destination) throws IOException {
		FileUtils.copyInputStreamToFile(stream, destination);
	}

	private File getDestinationTogglesFile(final String fileName) {
		String parentDir = Config.getProperty(TOGGLES_FILE_DESTINATION_FOLDER, "./");
		parentDir = "".equals(parentDir) ? System.getProperty("user.home") : parentDir;
		return new File(parentDir, fileName);
	}
}
