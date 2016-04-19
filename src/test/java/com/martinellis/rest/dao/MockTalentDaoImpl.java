package com.martinellis.rest.dao;

import java.util.Collections;
import java.util.List;

import com.martinellis.rest.api.type.Reply;

public class MockTalentDaoImpl implements TalentDao {
    
    public List<Reply> findReplyByTopicId (Long threadId) {
        return Collections.emptyList();
    }
    
    public int delete(String userId, String namespace, String keys) {
        return 0;
    }
    
    public Long getUserId() {
        return 1L;
    }
    
    public Long createUser(String firstName, String lastName, String email) {
        return 1L;
    }
    
    public Long addReply(Long userId, Long topicId, String comment) {
        return 1L;
    }
    
    public Long addTopic(Long userId) {
        return (long) (Math.random() * 10000);
    }
}
