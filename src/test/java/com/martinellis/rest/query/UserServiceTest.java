package com.martinellis.rest.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Iterables;
import com.martinellis.rest.api.type.AddProjectRequest;
import com.martinellis.rest.api.type.Project;
import com.martinellis.rest.api.type.RelationshipToken;
import com.martinellis.rest.api.type.UserInfo;
import com.martinellis.rest.api.type.UserProject;
import com.martinellis.rest.api.type.UserTalent;
import com.martinellis.rest.api.type.UserToken;
import com.martinellis.rest.dao.MockTalentDaoImpl;
import com.martinellis.rest.sample.TalentGraphFactory;
import com.martinellis.rest.service.UserServiceImpl;
import com.martinellis.rest.utils.config.Environment;
import com.martinellis.rest.utils.config.EnvironmentFactory;
import com.martinellis.rest.utils.config.ServiceLocator;
import com.martinellis.rest.utils.generator.IdGenerator;
import com.martinellis.rest.utils.generator.RandomIdGenerator;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;

public class UserServiceTest  {
    
    private final Long USERID_0 = 100l;
    private final Long USERID_1 = 101l;
    private final Long USERID_2 = 102l;
    private final Long USERID_3 = 103l;
    private final Long USERID_4 = 104l;
    private final Long UNKNOWN_USERID = 999l;
    
    private final Long PLUMBING_CAT_ID = 200L;
    private final Long COOKING_CAT_ID = 201L;
    private final Long COMPUTER_CAT_ID = 202L;
    
    private static final UserServiceImpl service = new UserServiceImpl();
    private static TitanGraph graph;
    
    @BeforeClass
    public static void setup() {
        graph = TalentGraphFactory.create(null);
        ServiceLocator locator = ServiceLocator.get();
        locator.wire(TitanGraph.class, graph);
        IdGenerator idGenerator = new RandomIdGenerator();
        service.setIdGenerator(idGenerator);
        locator.wire(Environment.class, new EnvironmentFactory().testEnvironment());
        MockTalentDaoImpl mockDao = new MockTalentDaoImpl();
        service.setDao(mockDao);
    }

    @Test
    public void testGetUserByUserId() {
        
        List<UserInfo> users = service.getUser(String.valueOf(USERID_1));
        assertEquals(1, users.size());
        UserInfo user = users.get(0);
        assertEquals("Goretti", user.getFirstName());
        
    }
    
    @Test
    public void testGetFriendsOfFriends() {
        
        // Check USERID_0 should have 1 friends of friends, user 104
        List<UserInfo> users = service.getFriendsOfFriends(String.valueOf(USERID_0));
        assertEquals(1, users.size());
        assertEquals(USERID_4, users.get(0).getUserId());
    }
    
    @Test
    public void testGetUserFriends() {
        
        // Check USERID_0 has 3 friends
        List<UserInfo> users = service.getUserFriends(String.valueOf(USERID_0));
        assertEquals(3, users.size());
        
        // Check if USERID_1 is a friend of USERID_0
        users = service.getUserFriends(String.valueOf(USERID_1));
        assertEquals(1, users.size());
        UserInfo user = users.get(0);
        assertEquals(USERID_0, user.getUserId());  
    }
    
    @Test 
    public void testGetTalent() {
        List <UserTalent> talents = service.getFriendsTalents(String.valueOf(USERID_0));
        assertEquals(3, talents.size());
        
        talents = service.getFriendsTalents(String.valueOf(USERID_1));
        assertEquals(1, talents.size());
    }
    
    @Test
    public void testUserProjectOrder() {
        UserProject up1 = new UserProject();
        Project proj1 = new Project();
        proj1.setTime(17823L);
        up1.setProject(proj1);
        
        UserProject up2 = new UserProject();
        Project proj2 = new Project();
        proj2.setTime(123L);
        up2.setProject(proj2);
        
        UserProject up3 = new UserProject();
        Project proj3 = new Project();
        proj3.setTime(2637L);
        up3.setProject(proj3);
        
        List<UserProject> list = new ArrayList<UserProject>(3);
        list.add(up1);
        list.add(up2);
        list.add(up3);
        
        Collections.sort(list);
        
        // The bigger the number, the more the recent is the project
        // Most recent should bubble to the top of the list
        assertEquals(Long.valueOf(17823), list.get(0).getProject().getTime());
        assertEquals(Long.valueOf(2637), list.get(1).getProject().getTime());
        assertEquals(Long.valueOf(123), list.get(2).getProject().getTime());
    }
    
    @Test
    public void testProjectApi() throws Exception {
        // Add a computer project
        AddProjectRequest request = new AddProjectRequest();
        request.setCid(String.valueOf(COMPUTER_CAT_ID));
        request.setUserId(String.valueOf(USERID_0));
        request.setSummary("Replaced laptop memory");        
        Project project1 = service.addProject(request);
        assertEquals("Computer", project1.getName());
        
        Thread.sleep(1500);
        
        // Add another computer project
        request = new AddProjectRequest();
        request.setCid(String.valueOf(COOKING_CAT_ID));
        request.setUserId(String.valueOf(USERID_0));
        request.setSummary("Italian dessert");
        Project project2 = service.addProject(request);
        assertEquals("Cooking", project2.getName());
        
        // Test getProjects api
        // Check if this user has two projects
        List<Project> projects = service.getProjects(String.valueOf(USERID_0));
        assertEquals(2, projects.size());
        
        // Most recent project appears first
        assertTrue(projects.get(0).getSummary().startsWith("Italian"));
        assertTrue(projects.get(1).getSummary().startsWith("Replaced"));
        assertTrue(projects.get(0).getTime() >= projects.get(1).getTime());
        
        // Check other user has no project
        projects = service.getProjects(String.valueOf(USERID_1));
        assertEquals(0, projects.size());
        
        // Test getFriendsProject api for USERID_0
        // USERID_0 friends has no projects
        List<UserProject> friendProjects = service.getFriendsProjects(String.valueOf(USERID_0), null);
        assertEquals(0, friendProjects.size());
        
        // USERID_1 should have one friend USERID_0 who has just added two projects
        // Test getFriendsProject api for USERID_1 with no keywords
        friendProjects = service.getFriendsProjects(String.valueOf(USERID_1), null);
        assertEquals(2, friendProjects.size());
        
        for (UserProject userProject : friendProjects) {
            assertEquals("Richard", userProject.getUser().getFirstName());
            assertTrue(userProject.getProject().getSummary().startsWith("Replaced") ||
                    userProject.getProject().getSummary().startsWith("Italian"));
        }
        
        // Test getFriendsProject api with wrong keywords
        friendProjects = service.getFriendsProjects(String.valueOf(USERID_1), "garage");
        assertEquals(0, friendProjects.size());
        
        // Test getFriendsProject api with correct keywords
        friendProjects = service.getFriendsProjects(String.valueOf(USERID_1), "LAPTOP");
        assertEquals(1, friendProjects.size());
        assertEquals(project1.getProjectId(), friendProjects.get(0).getProject().getProjectId());
        
    }
    
    /*
     * Test addFriend api
     * userid 2 makes a friend request to userid 3
     */
    @Test
    public void testAddFriend() {
        
        UserInfo user = service.addFriend(String.valueOf(USERID_2), String.valueOf(USERID_3));
        
        // Check if user3 has a pending request
        Iterable<Vertex> users = graph.getVertices("userId", USERID_3);
        Vertex user3 = Iterables.getOnlyElement(users);
        
        long count = user3.query().labels(RelationshipToken.REQUEST).direction(Direction.IN).count();
        assertEquals(1L, count);
        
        Iterable<Vertex> originators = user3.query().labels(RelationshipToken.REQUEST).direction(Direction.IN).vertices();
        Vertex originator = originators.iterator().next();
        assertEquals(USERID_2, (Long) originator.getProperty(UserToken.USER_ID));
        
        // Retrieve pending friend request of userid 2
        List<UserInfo> requests = service.getPendingFriendRequests(String.valueOf(USERID_2));
        assertEquals(1, requests.size());
        UserInfo returnedUser = requests.get(0);
        assertEquals(USERID_3, returnedUser.getUserId());
        
        // Make the same request, should be still successful, no exception
        user = service.addFriend(String.valueOf(USERID_2), String.valueOf(USERID_3));
        assertEquals(USERID_3, user.getUserId());
        
        // Get the pending list, should be still one request only
        requests = service.getPendingFriendRequests(String.valueOf(USERID_2));
        returnedUser = requests.get(0);
        assertEquals(1, requests.size());
        assertEquals(USERID_3, returnedUser.getUserId());
        assertTrue(returnedUser.getTime() != null);
        
        // Now retrieve confirm friend requests list
        requests = service.getConfirmFriendRequests(String.valueOf(USERID_3));
        returnedUser = requests.get(0);
        assertEquals(1, requests.size());
        assertEquals(USERID_2, returnedUser.getUserId()); 
        
        // TODO: Next call accept friend and make sure no more pending friend request
        
    }
    
}
