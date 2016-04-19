package com.martinellis.rest.utils.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martinellis.rest.utils.config.PropertiesFileProcessor;

public class Environment {
	private Logger logger = LoggerFactory.getLogger(Environment.class);
    private Properties settings = new Properties();
    private String[] propertyFileLocations;
    private boolean testEnv;
    
    public Environment(String... propertyFileLocations) {
        this.propertyFileLocations = propertyFileLocations;
        for (String propertyFileLocation : propertyFileLocations) {
            loadPropertyFile(propertyFileLocation);
        }
    }

    private void loadPropertyFile(String propertyFileLocation) {
        logger.info("loading settings from property file " + propertyFileLocation);
        new PropertiesFileProcessor().loadPropertyFile(settings, propertyFileLocation);
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public String getString(String key, String defaultValue) {
        String value = defaultValue;
        if (keyIsKnown(key)) {
            value = getValue(key);
        }
        return value;
    }

    public Integer getInteger(String key) {
        return getInteger(key, null);
    }

    public Integer getInteger(String key, Integer defaultValue) {
        Integer value = defaultValue;
        if (keyIsKnown(key)) {
            value = Integer.parseInt(getValue(key));
        }
        return value;
    }

    public Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        Boolean value = defaultValue;
        if (keyIsKnown(key)) {
            value = Boolean.parseBoolean(getValue(key));
        }
        return value;
    }

    public void set(String key, Object value) {
        settings.put(key, value);
    }

    private boolean keyIsKnown(String key) {
        return (settings.containsKey(key) || keyIsInEnvironmentVariables(escapeForEnv(key)))
                && !getValue(key).startsWith("$");
    }

    private String getValue(String key) {
        String retval = getValueFromEnvironment(escapeForEnv(key));
        if (retval == null)
            retval = settings.getProperty(key);
        return retval;
    }

    protected boolean keyIsInEnvironmentVariables(String key) {
        return getValueFromEnvironment(key) != null;
    }

    private String escapeForEnv(String key) {
        String escaped = key.replaceAll("\\.", "_");
        String upperCase = escaped.toUpperCase();
        return upperCase;
    }

    protected String getValueFromEnvironment(String key) {
        return System.getenv(key);
    }

    public Set<String> keys() {
        HashSet<String> keys = new HashSet<String>();
        for (Object key : settings.keySet()) {
            if (keyIsKnown(key.toString()))
                keys.add(key.toString());
        }
        return keys;
    }

    public List<String> getPropertyFileLocations() {
        return Arrays.asList(propertyFileLocations);
    }

    @Override
    public String toString() {
        return "Environment [propertyFileLocations=" + Arrays.toString(propertyFileLocations) + "]";
    }

    public boolean isTestEnv() {
        return testEnv;
    }

    public void setTestEnv(boolean testEnv) {
        this.testEnv = testEnv;
    }
}
