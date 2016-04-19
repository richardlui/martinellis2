package com.martinellis.rest.utils.config;

import java.util.ArrayList;
import java.util.List;

import com.martinellis.rest.utils.config.Environment;

public class EnvironmentFactory {
	static final String DEFAULT_PROPERTIES_FILE = "/environment-defaults.properties";
    static final String TEST_PROPERTIES_FILE = "/environment-test.properties";
    private static final String SYSTEM_ENV_VARIABLE = "DMP_ADDITIONAL_SETTINGS";
    
    public Environment newEnvironment() {
        List<String> filesToLoad = new ArrayList<String>();
        filesToLoad.add(DEFAULT_PROPERTIES_FILE);
        filesToLoad.addAll(additionalPropertiesFiles());
        Environment env = buildEnvironmentLoading(filesToLoad);
        if (env != null) {
            env.setTestEnv(false);
        }
        return env;
    }
    
    public Environment testEnvironment() {
        List<String> filesToLoad = new ArrayList<String>();
        filesToLoad.add(TEST_PROPERTIES_FILE);
        Environment env = buildEnvironmentLoading(filesToLoad);
        if (env != null) {
            env.setTestEnv(true);
        }
        return env;
    }

    protected Environment buildEnvironmentLoading(List<String> propertiesFiles) {
        return new Environment(propertiesFiles.toArray(new String[propertiesFiles.size()]));
    }

    private List<String> additionalPropertiesFiles() {
        List<String> additions = new ArrayList<String>();
        String envVar = readAdditionalPropertiesFilesFromSystemEnv();
        if (envVar != null) {
            for (String addition : envVar.split(","))
                additions.add(addition.trim());
        }

        return additions;
    }

    protected String readAdditionalPropertiesFilesFromSystemEnv() {
        return System.getenv(SYSTEM_ENV_VARIABLE);
    }
}
