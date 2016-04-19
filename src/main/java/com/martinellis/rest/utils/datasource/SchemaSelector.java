package com.martinellis.rest.utils.datasource;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martinellis.rest.utils.StringUtil;
import com.martinellis.rest.utils.config.Environment;
import com.martinellis.rest.utils.config.ThreadContextHolder;

public class SchemaSelector {
private static Logger LOGGER = LoggerFactory.getLogger(SchemaSelector.class);
    
    public static final String KEY_SCHEMA = "db.schema";
    private Environment environment;
    private String schemaConfig;
    private Map<String, String> schemaMap;

    public SchemaSelector(Environment environment) {
        this.environment = environment;
        if (environment != null) {
            schemaConfig = environment.getString(KEY_SCHEMA);
            schemaMap = StringUtil.splitIntoMap(schemaConfig);
        }
    }
    
    public String get() {
        return readConfigValue(KEY_SCHEMA, null);
    }

    private String readConfigValue(String key, String defaultValue) {
        String clientId = ThreadContextHolder.get().getClientId();
        if (clientId == null) {
            LOGGER.error("client id is missing in threadlocal.");
            // Some data is loaded at server startup time that is generic to any client
            // We take the first schema in this case
            Collection<String> list = schemaMap.values();
            for (String schema: list) {
                return schema;
            }
        }
        
        if (environment == null) {
            return defaultValue;
        }
        if (schemaMap.isEmpty()) {
            return schemaConfig;
        }
        
        return schemaMap.get(clientId);
        
    }
}
