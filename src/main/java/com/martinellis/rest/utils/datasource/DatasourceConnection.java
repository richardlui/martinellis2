package com.martinellis.rest.utils.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import com.martinellis.rest.utils.datasource.DatasourceMap;

public class DatasourceConnection {
	private static DatasourceMap map = null;
    static Logger logger = LoggerFactory.getLogger(DatasourceConnection.class);
    
    DatasourceConnection() {
    }

    public static Connection getConnection() throws SQLException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        if (DatasourceConnection.map == null) {
            DatasourceConnection.initDatasource();
        }
        Connection connection = DatasourceConnection.map.getConnection();
        connection.setAutoCommit(true);
        stopWatch.stop();
        logger.debug("acquiring connection took {} ms", stopWatch.getLastTaskTimeMillis());
        return connection;
    }
    
    public static void initDatasource() {
        logger.info("initializing data source");
        DatasourceConnection.map = new DatasourceMap();
    }

    public static void closeDatasource() {
        if (map != null) {
            DatasourceConnection.map.close();
        }
    }
    
    public static DataSource getDatasource() throws SQLException {
        if (DatasourceConnection.map == null) {
            DatasourceConnection.initDatasource();
        }
        return map.getDataSource();
    }
    
}
