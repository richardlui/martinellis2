package com.martinellis.rest.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.junit.Test;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.martinellis.rest.api.type.UserInfo;

public class LibraryCodeTest  {
    
    /*
     * Test guava Sets.difference, used to find out all friends of friends who are not already a friend
     */
    @Test
    public void testSetDiff() {
        
        // first set of users
        UserInfo user1 = new UserInfo();
        user1.setUserId(100L);
        
        UserInfo user2 = new UserInfo();
        user2.setUserId(200L);
        
        UserInfo user3 = new UserInfo();
        user3.setUserId(300L);
        
        HashSet<UserInfo> set1 = new HashSet<UserInfo>();
        set1.add(user1);
        set1.add(user2);
        set1.add(user3);
        
        assertEquals(3, set1.size());
        
        // second set of users
        UserInfo user4 = new UserInfo();
        user4.setUserId(400L);
        
        UserInfo user5 = new UserInfo();
        user5.setUserId(500L);
        
        UserInfo user6 = new UserInfo();
        user6.setUserId(600L);
        
        UserInfo user7 = new UserInfo();
        user7.setUserId(100L);
        
        UserInfo user8 = new UserInfo();
        user8.setUserId(300L);
        
        HashSet<UserInfo> set2 = new HashSet<UserInfo>();
        set2.add(user4);
        set2.add(user5);
        set2.add(user6);
        set2.add(user7);
        set2.add(user8);
        
        SetView<UserInfo> diffSet = Sets.difference(set2, set1);
        
        assertEquals(3, diffSet.size());
        
        assertTrue (diffSet.contains(user4));
        assertTrue (diffSet.contains(user5));
        assertTrue (diffSet.contains(user6));        
    }
    
}
