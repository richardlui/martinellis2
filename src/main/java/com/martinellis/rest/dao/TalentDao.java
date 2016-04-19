package com.martinellis.rest.dao;

import java.util.List;

import com.martinellis.rest.api.type.Reply;

public interface TalentDao {

    List<Reply> findReplyByTopicId (Long threadId);
    
    int delete(String userId, String namespace, String keys);
    
    Long getUserId();
    
    Long createUser(String firstName, String lastName, String email);
    
    Long addTopic(Long userId);
        
    Long addReply(Long userId, Long topicId, String comment);
}
