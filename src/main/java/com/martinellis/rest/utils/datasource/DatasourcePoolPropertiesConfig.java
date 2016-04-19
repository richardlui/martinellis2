package com.martinellis.rest.utils.datasource;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martinellis.rest.utils.config.Environment;
import com.martinellis.rest.utils.datasource.DatasourceConfigKeys;

public class DatasourcePoolPropertiesConfig {
	private static final String PRIVATE_KEY_LOCATION = "db.private.key.location";
    static Logger logger = LoggerFactory.getLogger(DatasourcePoolPropertiesConfig.class);
    private Environment environment;

    public DatasourcePoolPropertiesConfig() {
        this(new Environment());
    }

    public DatasourcePoolPropertiesConfig(Environment environment) {
        this.environment = environment == null ? new Environment() : environment;
    }

    public void configure(PoolProperties p, String credentialsLocation) throws Exception {
        this.configure(p, credentialsLocation, 80, 10, 3);
    }
    
    public void configure(PoolProperties p, String credentialsLocation, int maxActive, int maxIdle, int maxInitialSize) throws Exception {
    	DatasourceConfigKeys dck = new DatasourceConfigKeys();
    	
        dck.decryptKeys(environment.getString(PRIVATE_KEY_LOCATION), credentialsLocation);
        p.setUrl(dck.getUrl());
        p.setDriverClassName(dck.getJdbcDriver());
        p.setUsername(dck.getUser());
        p.setPassword(dck.getPassword());
        p.setJmxEnabled(true);
        p.setTestWhileIdle(false);
        p.setTestOnBorrow(true);
        p.setValidationQuery("SELECT 1");
        p.setTestOnReturn(false);
        p.setValidationInterval(5000);
        p.setTimeBetweenEvictionRunsMillis(5000);

        p.setMaxActive(maxActive);
        p.setMaxIdle(maxIdle);
        p.setInitialSize(maxInitialSize);
        p.setMaxWait(4000);
        p.setRemoveAbandonedTimeout(600);
        p.setMinEvictableIdleTimeMillis(5000);

        p.setLogAbandoned(true);
        p.setRemoveAbandoned(true);

        p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
                + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer;");
    }
}
