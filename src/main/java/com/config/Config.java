package com.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

@Slf4j
public class Config {
	public static final String TCE_PUBLISH_FLAG_FILE_KEY = "tce.publish.active.flag.file";
	public static final String RUNTIME_ENV_KEY = "app.env";
	protected static final String RUNTIME_ENV_PRODUCTION = "Production";
	protected static final Properties properties = new Properties();
	public static final String APPLICATION = "application";
	private static volatile File monitor;
	private static volatile ClassPathXmlApplicationContext springContext = null;

	public static synchronized void kickOffConfig() {
		try {
			loadPropertiesFromFile(getRuntimeEnv());
			createFoldersFromPropertyFile();
			copyConfigFileFromResources();
		} catch (Exception e) {
			log.error("Problems initializing Configuration", e);
		}
	}

	private static void copyConfigFileFromResources() {
		try {
			InputStream inputStream = Config.class.getClassLoader().getResourceAsStream("config/BulkUploadConfigMapping.xlsx");

			File file = new File(Config.getProperty("upload.config.file.folder")+"/"+"BulkUploadConfigMapping.xlsx");
			FileUtils.copyInputStreamToFile(inputStream,file);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void createFoldersFromPropertyFile() {
		Config.getPropertyNames();
		for(String keys : Config.getPropertyNames())
		{
			if(keys.contains(".folder"))
			{
				String folderName = Config.getProperty(keys);
				File file = new File(folderName);
				if(!file.exists())
				{
					log.info("Creating a new directory in location {}",file.getAbsolutePath());
					file.mkdirs();
				}
				else
				{
					log.info("Folder already exists in location {}",file.getAbsolutePath());
				}

			}
		}

	}

	private static String getRuntimeEnv() {
		String currentEnv = System.getProperty(RUNTIME_ENV_KEY);
		return StringUtils.isNotEmpty(currentEnv) ? currentEnv : "Local";
	}

	private Config() {
	}

	public static String getProperty(String key) {
		return properties.getProperty(key);
	}

	public static String getProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	public static Set<String> getPropertyNames() {
		return properties.stringPropertyNames();
	}

	public static String getRuntimeEnvironment() {
		return getRuntimeEnv();
	}

	public static boolean isPublishingEnabled() {
		if (monitor == null) {
			monitor = new File(properties.getProperty(TCE_PUBLISH_FLAG_FILE_KEY, ""));
		}
		return monitor.exists();
	}

	public static boolean isProductionMode() {
		return RUNTIME_ENV_PRODUCTION.equalsIgnoreCase(getRuntimeEnvironment());
	}

	public static ApplicationContext getSpringContext() {
		return springContext;
	}

	public static void setProperty(String key, String value) {
		if (value == null) {
			properties.remove(key);
		} else {
			properties.setProperty(key, value);
		}
	}

	public static void loadPropertiesFromFile(String environment) {
		properties.clear();
		String propertiesFileName = APPLICATION + "-" + environment + ".properties";
		try {
			Configuration configuration = new Configurations().properties(propertiesFileName);
			for (Iterator<String> itor = configuration.getKeys(); itor.hasNext();) {
				String key = itor.next();
				String value = configuration.getString(key);
				String systemValue = System.getProperty(key);
				if (!StringUtils.isEmpty(systemValue) && (!systemValue.equals(value))) {
					log.warn("Overriding app property {}={} with system value:{}", key, value, systemValue);
					value = systemValue;
				}
				properties.put(key, value);
			}
		} catch (ConfigurationException e) {
			throw new IllegalStateException(
					"Unable to load properties for environment " + environment + ": " + e.getMessage(), e);
		}
	}
}