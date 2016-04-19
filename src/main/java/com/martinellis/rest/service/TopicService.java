package com.martinellis.rest.service;

import java.util.List;

import com.martinellis.rest.api.type.AddReplyRequest;
import com.martinellis.rest.api.type.Reply;

public interface TopicService {
    
    List<Reply> getComment(Long topicId);
    
    Reply updateComment(String userId, Long topicId, String comment);
    
    Reply addComment(AddReplyRequest request);
    
}
