package com.martinellis.rest.utils.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martinellis.rest.utils.config.Environment;
import com.martinellis.rest.utils.config.ServiceLocator;

public class DatasourceMap {
	static Logger logger = LoggerFactory.getLogger(DatasourceMap.class);
    private static final String CREDENTIALS_LIST_PROP = "db.credentials.location";
    private Map<Integer, DataSource> datasourceMap = new HashMap<Integer, DataSource>();
    private DataSource dbDatasource;
    
    /**
     * @return the datasourceMap
     */
    public Map<Integer, DataSource> getDatasourceMap() {
        return datasourceMap;
    }

    /**
     * 
     */
    DatasourceMap() {
        Environment environment = ServiceLocator.get().lookup(Environment.class);
        String dbCredentials = environment.getString(CREDENTIALS_LIST_PROP);
        dbDatasource = createDatasource(dbCredentials);
        datasourceMap.put(1, dbDatasource); 
        
    }
    
    private DataSource createDatasource(String credentialsLocation) {
        PoolProperties p = new PoolProperties();
        Environment env = ServiceLocator.get().lookup(Environment.class);
        try {
            new DatasourcePoolPropertiesConfig(env).configure(p, credentialsLocation);
        } catch (Exception e) {
            logger.error("Failed to create a new datasource.", e);
        }
        DataSource datasource = new DataSource();
        datasource.setPoolProperties(p);
        if (p.getUrl() == null) {
            logger.error ("Database url is null");
        } else if (p.getUsername() == null) {
            logger.error ("Database username is null");
        } else {
            logger.info("using database url {}", p.getUrl());
            logger.info("using database username {}", p.getUsername());
        }
        return datasource;
    }

    public void close() {
        Iterator<DataSource> iterator = datasourceMap.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().close();
        }
    }

    public Connection getConnection() throws NumberFormatException, SQLException {
        DataSource dataSource = getDataSource();
        return dataSource.getConnection();
    }

    public DataSource getDataSource() throws SQLException {
        return datasourceMap.get(1);
    }
}
