package com.martinellis.rest.api.type;

import javax.ws.rs.core.MultivaluedMap;


public class LinkUserRequest extends AbstractRequest {
	
    private String userId;
    private String type = RelationshipToken.FRIEND;
    
    public LinkUserRequest(MultivaluedMap<String, String> parameters) {
        super(parameters);
        super.throwValidationErrors();
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    

    public String getType() {
        return type;
    }

}
