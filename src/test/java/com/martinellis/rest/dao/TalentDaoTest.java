package com.martinellis.rest.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.martinellis.rest.api.type.Reply;
import com.martinellis.rest.utils.config.Environment;
import com.martinellis.rest.utils.config.EnvironmentFactory;
import com.martinellis.rest.utils.config.ServiceLocator;

@ContextConfiguration(locations = {"/dao-test.xml"})
public class TalentDaoTest extends AbstractJUnit4SpringContextTests {
    
    @Autowired
    protected TalentDao talentDao;
   
    @BeforeClass
    public static void setup() {
        ServiceLocator locator = ServiceLocator.get();
        locator.wire(Environment.class, new EnvironmentFactory().testEnvironment());
    }
    
    @Test
    public void testGetUserId() {
        
        Long id = talentDao.getUserId();
        assertTrue(id >= 0);
        Long id2 = talentDao.getUserId();
        assertTrue(id2 >= 0);
        
        // The two id must be different
        assertTrue(id.longValue() != id2.longValue());
    }
    
    @Test
    public void testCreateUser() {
        
        Long id = talentDao.createUser("Whitney", "Houston", "wh@gmail.com");
        assertTrue(id >= 0);
    }
    
    @Test
    public void testReplyApi() {
        long threadId = 111L;
        
        // First add a couple of replies
        Long replyId1 = talentDao.addReply(100L, threadId, "comment1");
        assertTrue(replyId1 >= 0);
        Long replyId2 = talentDao.addReply(101L, threadId, "comment2");
        assertTrue(replyId2 >= 0);
        assertTrue(replyId1.longValue() != replyId2.longValue());
        
        // Retrieve all comments by topic id
        List<Reply> list = talentDao.findReplyByTopicId(threadId);
        assertEquals(2, list.size());
    }
}
