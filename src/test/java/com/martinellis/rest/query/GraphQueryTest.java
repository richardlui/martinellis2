package com.martinellis.rest.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDateTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martinellis.rest.api.type.RelationshipToken;
import com.martinellis.rest.api.type.UserToken;
import com.martinellis.rest.sample.TalentGraphFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanVertex;
import com.thinkaurelius.titan.core.attribute.Text;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.ElementHelper;

public class GraphQueryTest  {
    
    private final Long USERID_0 = 100l;
    private final Long USERID_1 = 101l;
    private final Long USERID_2 = 102l;
    private final Long USERID_3 = 103l;
    private final Long UNKNOWN_USERID = 999l;
    
    private final Long PLUMBING_CAT_ID = 200L;
    private final Long COOKING_CAT_ID = 201L;
    private final Long COMPUTER_CAT_ID = 202L;
    
    private static TitanGraph graph;
    
    private Logger logger = LoggerFactory.getLogger(GraphQueryTest.class);
    
    @BeforeClass
    public static void setup() {
        graph = TalentGraphFactory.create(null);
    }

    @Test
    public void testGetUserByUserId() {
        Vertex v = getOneElement(graph, "userId", USERID_1);
        Long userid = v.getProperty(UserToken.USER_ID);
        assertEquals (userid, USERID_1);
        String name = v.getProperty(UserToken.FIRST_NAME);
        assertEquals (name, "Goretti");
        String lastName = v.getProperty(UserToken.LAST_NAME);
        assertEquals (lastName, "Chun");
    }
    
    @Test
    public void testDate() {
        Calendar now = Calendar.getInstance();
        long millis = now.getTimeInMillis();
        
        LocalDateTime date = new LocalDateTime(millis);
        
        long millis2 = new Date().getTime();
    }
    
    @Test
    public void testGetUserByUnknownUserId() {
        Iterable <Vertex> list = graph.getVertices("userId", UNKNOWN_USERID);
        assertFalse(list.iterator().hasNext());
        
    }
    
    @Test
    public void testGetFriends() {
        Vertex user = getOneElement(graph, "userId", USERID_0);
        
        // Check user 100 has 3 friends
        long count = user.query().direction(Direction.BOTH).labels(RelationshipToken.FRIEND).count();
        assertEquals (3, count);
        
        // Check all friends of user 101
        Iterable<Vertex> vertices = user.query().direction(Direction.BOTH).labels(RelationshipToken.FRIEND).vertices();
        boolean user1Found = false;
        boolean user2Found = false;
        boolean user3Found = false;
        for (Vertex v : vertices) {
            if ((Long)v.getProperty("userId") == USERID_1) {
                user1Found = true;
            } else if ((Long)v.getProperty("userId") == USERID_2) {
                user2Found = true;
            } else if ((Long)v.getProperty("userId") == USERID_3) {
                user3Found = true;
            }
        }
        assertTrue(user1Found);
        assertTrue(user2Found);
        assertTrue(user3Found);
        
        // Check if user 101 has a friend 100
        user = getOneElement(graph, "userId", USERID_1);
        count = user.query().direction(Direction.BOTH).labels(RelationshipToken.FRIEND).count();
        assertEquals (1, count);
        
        vertices = user.query().direction(Direction.BOTH).labels(RelationshipToken.FRIEND).vertices();
        boolean user0Found = false;
        for (Vertex v : vertices) {
            if ((Long)v.getProperty("userId") == USERID_0) {
                user0Found = true;
            }
        }
        assertTrue(user0Found);        
    }
    
    /*
     * Retrieve talent of the specified user
     */
    @Test
    public void testGetSelfTalent() {
        TitanVertex user =  (TitanVertex) graph.getVertices("userId", USERID_0).iterator().next();
        
        // find out talents of the given user
        Iterable<Vertex> talentVertices = user.query().labels("knows").direction(Direction.OUT).vertices();
        List<Long> categoryIds = new ArrayList<Long>();
        for (Vertex talent : talentVertices) {
            logger.info("friend " + user.getProperty(UserToken.FIRST_NAME) + " knows " + talent.getProperty("categoryId"));
            categoryIds.add((Long)talent.getProperty("categoryId"));
        }
        assertTrue(categoryIds.contains(200L));
        assertTrue(categoryIds.contains(202L));
    }
    
    /*
     * Retrieve all the talent of given user's friends
     */
    @Test
    public void testGetFriendsTalent() {
        TitanVertex user =  (TitanVertex) graph.getVertices("userId", USERID_0).iterator().next();
        // retrieve all friends
        Iterable <Vertex> friends = user.query().labels(RelationshipToken.FRIEND).direction(Direction.OUT).vertices();
        for (Vertex friend : friends) {
            logger.info((String)friend.getProperty(UserToken.FIRST_NAME));
            // find out talents from each friend
            Iterable<Vertex> talentVertices = friend.query().direction(Direction.OUT).labels("knows").vertices();
            for (Vertex talent : talentVertices) {
                logger.info("friend " + friend.getProperty(UserToken.FIRST_NAME) + " knows " + talent.getProperty("name"));
            }
        }
    }
    
    /*
     * Retrieve count of all talent 
     */
    @Test
    public void testGetTalentTotalCount() {
        Iterable <Vertex> talents =  graph.getVertices("type", "category");
        for (Vertex talent : talents) {
            long count = talent.query().direction(Direction.IN).count();
            logger.info(talent.getProperty("name") + " has " + count+ " people.");
        }
    }
    
    /*
     * Add user
     */
    @Test
    public void testAddUser() {
        
        Vertex person = graph.addVertex(null);
        long userId = (long) (Math.random() * 10000);
        logger.info("Creating userId: {}" , userId);
        ElementHelper.setProperties(person, UserToken.FIRST_NAME, "UserName10", UserToken.USER_ID, userId, UserToken.TYPE, "person");
        graph.commit();
        
        // create same user again should get error
        Vertex person2 = graph.addVertex(null);
        try {
            ElementHelper.setProperties(person2, UserToken.FIRST_NAME, "UserName11", UserToken.USER_ID, userId, UserToken.TYPE, "person");
        } catch (Exception e) {
            return;
        }
        finally {
            graph.commit();
        }
        fail("Expect to throw exception.");
    }
    
    /*
     * Add talent
     */
    @Test
    public void testAddTalent() {
        Vertex user = getOneElement(graph, "userId", USERID_0);
        
        // Get cooking category
        Vertex category = getOneElement(graph, "categoryId", COOKING_CAT_ID);
        user.addEdge("knows", category);
        graph.commit();
    }
    
    /*
     * Add project
     */
    @Test
    public void testAddProject() {
        Vertex user = getOneElement(graph, UserToken.USER_ID, USERID_0);
        
        // create new project node
        Vertex project = graph.addVertex(null);
        ElementHelper.setProperties(project, "projectId", 1000, "type", "project");
        Edge e1 = user.addEdge("shows", project);
        ElementHelper.setProperties(e1, "proj-catId", COMPUTER_CAT_ID, "type", "project", "summary", "Cleaned up computer virus.");
        graph.commit();
        
        // Add one more of the same project type should be allowed
        project = graph.addVertex(null);
        ElementHelper.setProperties(project, "projectId", 1001, "type", "project");
        Edge e2 = user.addEdge("shows", project);
        ElementHelper.setProperties(e2, "proj-catId", COMPUTER_CAT_ID, "summary", "Replaced laptop memory.");
        
        graph.commit();
        
        // Now query for this person's projects
        assertEquals (2, user.query().labels("shows").direction(Direction.OUT).count());
        
        // search for summary
        Iterable<Vertex> projects = user.query().labels("shows").direction(Direction.OUT).has("proj-catId", COMPUTER_CAT_ID).has("summary", Text.CONTAINS, "virus").vertices();
        Vertex matchingProject = projects.iterator().next();
        assertEquals(Long.valueOf(1000L), (Long)matchingProject.getProperty("projectId"));
    }
    
    private Vertex getOneElement(TitanGraph graph, String key, String value) {
        Iterable<Vertex> users = graph.getVertices(key, value);
        if (users.iterator().hasNext()) {
            return users.iterator().next();
        } else {
            return null;
        }
    }
    
    private Vertex getOneElement(TitanGraph graph, String key, Long value) {
        Iterable<Vertex> users = graph.getVertices(key, value);
        if (users.iterator().hasNext()) {
            return users.iterator().next();
        } else {
            return null;
        }
    }
    
    
}
