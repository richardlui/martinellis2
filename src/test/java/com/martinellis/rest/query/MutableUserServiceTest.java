package com.martinellis.rest.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.martinellis.rest.api.type.CreateUserRequest;
import com.martinellis.rest.api.type.UserInfo;
import com.martinellis.rest.dao.MockTalentDaoImpl;
import com.martinellis.rest.sample.TalentGraphFactory;
import com.martinellis.rest.service.UserServiceImpl;
import com.martinellis.rest.utils.config.ServiceLocator;
import com.martinellis.rest.utils.generator.IdGenerator;
import com.martinellis.rest.utils.generator.RandomIdGenerator;
import com.thinkaurelius.titan.core.TitanGraph;

public class MutableUserServiceTest  {
    
    private final Long USERID_0 = 100l;
    private final Long USERID_1 = 101l;
    private final Long USERID_3 = 103l;
    
    private static UserServiceImpl service = new UserServiceImpl();
    private static TitanGraph graph;
    
    @BeforeClass
    public static void setup() {
        graph = TalentGraphFactory.create(null);
        ServiceLocator locator = ServiceLocator.get();
        locator.wire(TitanGraph.class, graph);
        IdGenerator idGenerator = new RandomIdGenerator();
        service.setIdGenerator(idGenerator);
        MockTalentDaoImpl mockDao = new MockTalentDaoImpl();
        service.setDao(mockDao);
    }

    @Test
    public void testLinkExistingUser() {
        
        UserInfo user = service.acceptFriend(String.valueOf(USERID_0), String.valueOf(USERID_1));
        assertEquals(user.getUserId(), USERID_1);
        
        user = service.acceptFriend(String.valueOf(USERID_1), String.valueOf(USERID_0));
        assertEquals(user.getUserId(), USERID_0);        
    }
    
    /*
     * Be care that this test create and link to USERID_3
     * It might create side effect and false failure for other tests
     * if the tests are testing number of friends USERID_3 has
     */
    @Test
    public void testCreateAndLinkUser() {

        // Test createUser api
        CreateUserRequest cur = new CreateUserRequest();
        cur.setFirstName("New User");
        cur.setEmail("newUser@test.com");
        UserInfo user = service.createUser(cur);
        
        assertEquals("New User", user.getFirstName());
        assertEquals("newUser@test.com", user.getEmail());
  
        // Link this new user to USERID_3
        UserInfo friend = service.acceptFriend(String.valueOf(user.getUserId()), String.valueOf(USERID_3));
        assertEquals(USERID_3, friend.getUserId());
        
        // Check if the new user has a friend USERID_3
        // By default, new user also automatically link to USERID_0
        List<UserInfo> friends = service.getUserFriends(String.valueOf(user.getUserId()));
        assertEquals(2, friends.size());
        //assertEquals(USERID_3, friends.get(0).getUserId());
        
        // Check if USERID_3 is aware of the new friend
        friends = service.getUserFriends(String.valueOf(USERID_3));
        boolean found = false;
        for (UserInfo f : friends) {
            if (user.getUserId().equals(f.getUserId())) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }
    
}
