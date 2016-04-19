package com.martinellis.rest.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martinellis.rest.api.type.AddReplyRequest;
import com.martinellis.rest.api.type.Reply;
import com.martinellis.rest.dao.TalentDao;

public class TopicServiceImpl implements TopicService {
    
    private TalentDao dao;
    
    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    @Override
    public List<Reply> getComment(Long topicId) {
        return dao.findReplyByTopicId(topicId);
    }
    
    @Override
    public Reply updateComment(String userId, Long topicId, String comment) {
        return new Reply();
    }
    
    @Override
    public Reply addComment(AddReplyRequest request) {
        long replyId = dao.addReply(Long.valueOf(request.getUserId()), request.getTopicId(), request.getComment());
        Reply reply = new Reply();
        reply.setId(replyId);
        reply.setUserId(Long.valueOf(request.getUserId()));
        reply.setComment(request.getComment());
        return reply;
    }

    public TalentDao getDao() {
        return dao;
    }

    public void setDao(TalentDao dao) {
        this.dao = dao;
    }
    
}
