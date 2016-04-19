package com.martinellis.rest.sample;

import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.INDEX_BACKEND_KEY;
import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.STORAGE_DIRECTORY_KEY;

import java.io.File;
import java.util.Calendar;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.joda.time.LocalDate;

import com.martinellis.rest.api.type.RelationshipToken;
import com.martinellis.rest.api.type.UserToken;
import com.thinkaurelius.titan.core.Mapping;
import com.thinkaurelius.titan.core.Order;
import com.thinkaurelius.titan.core.Parameter;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanKey;
import com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.ElementHelper;


/**
 * Example Graph factory that creates a {@link TitanGraph} based on roman mythology.
 * Used in the documentation examples and tutorials.
 *
 * @author
 */
public class TalentGraphFactory {

    public static final String INDEX_NAME = "search";


    public static void main(String [] args) {
        if (args.length > 0) {
            String directory = args[0];
            TalentGraphFactory.create(directory);
        }
    }
    
    public static TitanGraph create(String directory) {
        BaseConfiguration config = new BaseConfiguration();
        config.setProperty("autotype", "none");
        Configuration storage = config.subset(GraphDatabaseConfiguration.STORAGE_NAMESPACE);
        // configuring local backend
        if (directory != null) {
            storage.setProperty(GraphDatabaseConfiguration.STORAGE_BACKEND_KEY, "local");
            storage.setProperty(GraphDatabaseConfiguration.STORAGE_DIRECTORY_KEY, directory);
        } else {
            storage.setProperty(GraphDatabaseConfiguration.STORAGE_BACKEND_KEY, "inmemory");
        }
        // configuring elastic search index
        Configuration index = storage.subset(GraphDatabaseConfiguration.INDEX_NAMESPACE).subset(INDEX_NAME);
        index.setProperty(INDEX_BACKEND_KEY, "elasticsearch");
        index.setProperty("local-mode", true);
        index.setProperty("client-only", false);
        if (directory == null) {
            directory = "/tmp";
        }
        index.setProperty(STORAGE_DIRECTORY_KEY, directory + File.separator + "es");

        TitanGraph graph = TitanFactory.open(config);
        load(graph);
        return graph;
    }

    public static void load(final TitanGraph graph) {

        // Generic property
        graph.makeKey("name").dataType(String.class).indexed(INDEX_NAME, Vertex.class, Parameter.of(Mapping.MAPPING_PREFIX,Mapping.TEXT)).make();
        
        // A person property
        graph.makeKey("firstName").dataType(String.class).indexed(INDEX_NAME, Vertex.class, Parameter.of(Mapping.MAPPING_PREFIX,Mapping.TEXT)).make();
        graph.makeKey("lastName").dataType(String.class).indexed(INDEX_NAME, Vertex.class, Parameter.of(Mapping.MAPPING_PREFIX,Mapping.TEXT)).make();
        
        graph.makeKey("email").dataType(String.class).indexed(INDEX_NAME, Vertex.class, Parameter.of(Mapping.MAPPING_PREFIX,Mapping.STRING)).make();
        graph.makeKey("userId").dataType(Long.class).indexed(Vertex.class).indexed(Edge.class).unique().make();
        graph.makeKey("type").dataType(String.class).indexed(Vertex.class).make();
        // A service category
        graph.makeKey("categoryId").dataType(Long.class).indexed(Vertex.class).unique().make();
        
        // A project category
        graph.makeKey("proj-catId").dataType(Long.class).indexed(Vertex.class).make();
        // Friend request property
        graph.makeKey("requestStatus").dataType(String.class).indexed(INDEX_NAME, Vertex.class, Parameter.of(Mapping.MAPPING_PREFIX,Mapping.STRING)).make();
        
        // Summary for projects, service, etc
        graph.makeKey("summary").dataType(String.class).indexed(INDEX_NAME, Vertex.class, Parameter.of(Mapping.MAPPING_PREFIX,Mapping.TEXT)).make();
        graph.makeKey("projectId").dataType(Long.class).indexed(Vertex.class).unique().make();
        
        // A person relationship
        final TitanKey time = graph.makeKey("time").dataType(Long.class).make();
        graph.makeLabel(RelationshipToken.FRIEND).sortKey(time).sortOrder(Order.DESC).manyToMany().make();  
        graph.makeLabel(RelationshipToken.KNOWS).sortKey(time).sortOrder(Order.DESC).manyToMany().make();
        graph.makeLabel(RelationshipToken.SHOWS).sortKey(time).sortOrder(Order.DESC).manyToMany().make();
        graph.makeLabel(RelationshipToken.REQUEST).sortKey(time).sortOrder(Order.DESC).manyToMany().make();

        graph.commit();

        Calendar now = Calendar.getInstance();
        long nowMillis = now.getTimeInMillis();
        
        // vertices
        // Create five people, Richard, Goretti, Charlotte, Tom, Jeffrey
        Vertex rich = graph.addVertex(null);
        ElementHelper.setProperties(rich, UserToken.FIRST_NAME, "Richard", UserToken.LAST_NAME, "Lui", UserToken.USER_ID, 100, UserToken.TYPE, "person");
        
        Vertex gchun = graph.addVertex(null);
        ElementHelper.setProperties(gchun, UserToken.FIRST_NAME, "Goretti", UserToken.LAST_NAME, "Chun", UserToken.USER_ID, 101, UserToken.TYPE, "person");
        
        Vertex charlotte = graph.addVertex(null);
        ElementHelper.setProperties(charlotte, UserToken.FIRST_NAME, "Charlotte", UserToken.LAST_NAME, "Chun", UserToken.USER_ID, 102, UserToken.TYPE, "person");
        
        Vertex tom = graph.addVertex(null);
        ElementHelper.setProperties(tom, UserToken.FIRST_NAME, "Tom", UserToken.LAST_NAME, "Lui", UserToken.USER_ID, 103, UserToken.TYPE, "person");
        
        Vertex jeff = graph.addVertex(null);
        ElementHelper.setProperties(jeff, UserToken.FIRST_NAME, "Jeffrey", UserToken.LAST_NAME, "Wu", UserToken.USER_ID, 104, UserToken.TYPE, "person");
        
        
        // Create three category
        Vertex plumbing = graph.addVertex(null);
        ElementHelper.setProperties(plumbing, "name", "Plumbing", "categoryId", 200, "type", "category");
        
        Vertex cooking = graph.addVertex(null);
        ElementHelper.setProperties(cooking, "name", "Cooking", "categoryId", 201, "type", "category");
        
        Vertex computer = graph.addVertex(null);
        ElementHelper.setProperties(computer, "name", "Computer", "categoryId", 202, "type", "category");
        
        // friend relationship
        Edge e1 = rich.addEdge("friend", gchun);
        ElementHelper.setProperties(e1, "time", nowMillis);
        
        Edge e3 = rich.addEdge("friend", charlotte);
        ElementHelper.setProperties(e3, "time", nowMillis);
        
        Edge e5 = rich.addEdge("friend", tom);
        ElementHelper.setProperties(e5, "time", nowMillis);
        
        Edge e6 = charlotte.addEdge("friend", jeff);
        ElementHelper.setProperties(e6, "time", nowMillis);
        
        
        // associate with talent
        rich.addEdge("knows", plumbing);
        rich.addEdge("knows", computer);
        gchun.addEdge("knows", plumbing);
        gchun.addEdge("knows", cooking);
        charlotte.addEdge("knows",  plumbing);
        charlotte.addEdge("knows", cooking);
        tom.addEdge("knows",  computer);
        jeff.addEdge("knows", computer);
        
        // commit the transaction to disk
        graph.commit();
        
    }
}
