package com.martinellis.rest.sample;

import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.INDEX_BACKEND_KEY;
import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.STORAGE_DIRECTORY_KEY;

import java.io.File;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

import com.martinellis.rest.api.type.RelationshipToken;
import com.thinkaurelius.titan.core.Mapping;
import com.thinkaurelius.titan.core.Order;
import com.thinkaurelius.titan.core.Parameter;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanType;
import com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration;
import com.tinkerpop.blueprints.Vertex;


public class UpgradeSchema {

    public static final String INDEX_NAME = "search";


    public static void main(String [] args) {
        if (args.length > 0) {
            String directory = args[0];
            
            BaseConfiguration config = new BaseConfiguration();
            config.setProperty("autotype", "none");
            Configuration storage = config.subset(GraphDatabaseConfiguration.STORAGE_NAMESPACE);
            // configuring local backend
            storage.setProperty(GraphDatabaseConfiguration.STORAGE_BACKEND_KEY, "local");
            storage.setProperty(GraphDatabaseConfiguration.STORAGE_DIRECTORY_KEY, directory);
            // configuring elastic search index
            Configuration index = storage.subset(GraphDatabaseConfiguration.INDEX_NAMESPACE).subset("search");
            index.setProperty(INDEX_BACKEND_KEY, "elasticsearch");
            index.setProperty("local-mode", true);
            index.setProperty("client-only", false);
            index.setProperty(STORAGE_DIRECTORY_KEY, "/tmp/talent" + File.separator + "es");

            TitanGraph graph = TitanFactory.open(config);
            
            /* 2014-02-22
            graph.makeKey("requestStatus").dataType(String.class).indexed(INDEX_NAME, Vertex.class, Parameter.of(Mapping.MAPPING_PREFIX,Mapping.STRING)).make();
            TitanType time = graph.getType("time");
            graph.makeLabel(RelationshipToken.REQUEST).sortKey(time).sortOrder(Order.DESC).manyToMany().make();
             */
            graph.commit();
        } else {
            System.out.println("Usage: UpgradeSchema <directory>");
        }
        
    }
}
