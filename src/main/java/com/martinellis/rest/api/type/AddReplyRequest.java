package com.martinellis.rest.api.type;

import javax.ws.rs.core.MultivaluedMap;


public class AddReplyRequest extends AbstractRequest {
	
    private String userId;
    private Long topicId;
    private String comment;
    
    public AddReplyRequest() {
        
    }
    
    public AddReplyRequest(MultivaluedMap<String, String> parameters) {
        super(parameters);
        this.userId = super.getString(UserToken.USER_ID);
        this.comment = super.getString("comment");
        super.throwValidationErrors();
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    
}
