package com.martinellis.rest.utils.config;

import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.INDEX_BACKEND_KEY;
import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.STORAGE_DIRECTORY_KEY;

import java.io.File;
import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.martinellis.rest.dao.TalentDaoImpl;
import com.martinellis.rest.service.TopicService;
import com.martinellis.rest.service.TopicServiceImpl;
import com.martinellis.rest.service.UserService;
import com.martinellis.rest.service.UserServiceImpl;
import com.martinellis.rest.utils.build.BuildInfo;
import com.martinellis.rest.utils.build.BuildInfoFactory;
import com.martinellis.rest.utils.datasource.DatasourceConnection;
import com.martinellis.rest.utils.datasource.SchemaSelector;
import com.martinellis.rest.utils.generator.DbIdGenerator;
import com.martinellis.rest.utils.generator.IdGenerator;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration;

public class ServicesInitializeListener implements ServletContextListener {
    Logger logger = LoggerFactory.getLogger(getClass());
    ServiceLocator locator = ServiceLocator.get();
    
    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        logger.info("Wiring services");
        ServiceLocator locator = ServiceLocator.get();
        
        wireEnvironment();
        
        BuildInfo buildInfo = new BuildInfoFactory(
                locator.lookup(Environment.class)).getBuildInfo();
        
        try {
        	Environment env = locator.lookup(Environment.class);
        
        	logger.info("**************************************");
        	logger.info("ServicesInitializeListener running {}",
        	        buildInfo.toLogString());
        	logger.info("ServicesInitializeListener using main schema '{}'",
        	        env.getString(SchemaSelector.KEY_SCHEMA));
        	logger.info("**************************************");
        
        	initializeGraph();
        	
        	initializeService();
        	
        } catch (Exception ex) {
            logger.error("ERROR in wiring", ex);
        }
        
        locator.logWiring();
    }

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

	private void wireEnvironment() {
	    locator.wire(Environment.class, new EnvironmentFactory().newEnvironment());
	}
	
	private void initializeService() throws SQLException {
	    DataSource ds = DatasourceConnection.getDatasource();
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(ds);
        
        TalentDaoImpl dao = new TalentDaoImpl();
        dao.setJdbcTemplate(jdbcTemplate);
        
        IdGenerator dbIdGenerator = new DbIdGenerator(dao);
        UserServiceImpl service = new UserServiceImpl();
        service.setIdGenerator(dbIdGenerator);
        service.setDao(dao);
        locator.wire(UserService.class, service);
        
        TopicServiceImpl topicService = new TopicServiceImpl();
        topicService.setDao(dao);
        locator.wire(TopicService.class, topicService);
        
        
	}
	
	private void initializeGraph() throws SQLException {
	    
        BaseConfiguration config = new BaseConfiguration();
        config.setProperty("autotype", "none");
        Configuration storage = config.subset(GraphDatabaseConfiguration.STORAGE_NAMESPACE);
        // configuring local backend
        storage.setProperty(GraphDatabaseConfiguration.STORAGE_BACKEND_KEY, "local");
        storage.setProperty(GraphDatabaseConfiguration.STORAGE_DIRECTORY_KEY, "/tmp/talent");
        // configuring elastic search index
        Configuration index = storage.subset(GraphDatabaseConfiguration.INDEX_NAMESPACE).subset("search");
        index.setProperty(INDEX_BACKEND_KEY, "elasticsearch");
        index.setProperty("local-mode", true);
        index.setProperty("client-only", false);
        index.setProperty(STORAGE_DIRECTORY_KEY, "/tmp/talent" + File.separator + "es");
        
        //TitanGraph graph = TitanFactory.open("graph.properties");
        TitanGraph graph = TitanFactory.open(config);
        locator.wire(TitanGraph.class, graph);	
	}
}
