package com.martinellis.rest.utils.datasource;

import com.martinellis.rest.utils.config.Environment;
import com.martinellis.rest.utils.config.ServiceLocator;
import com.martinellis.rest.utils.datasource.SchemaSelector;

public class DB {
	private final static SchemaSelector schemaName = new SchemaSelector(ServiceLocator.get().lookup(Environment.class));
    
    public static String getSchema() {
        return schemaName.get();
    }

    public static String getSchema(String table) {
        return getSchema() + "." + table;
    }

}
