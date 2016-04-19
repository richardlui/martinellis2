package com.martinellis.rest.api.type;

import javax.ws.rs.core.MultivaluedMap;


public class AddProjectRequest extends AbstractRequest {
	
    private String userId;
    private String cid;
    private String summary;
    private String type = "project";
    
    public AddProjectRequest() {
        
    }
    
    public AddProjectRequest(MultivaluedMap<String, String> parameters) {
        super(parameters);
        this.userId = super.getString(UserToken.USER_ID);
        this.cid = super.getString("categoryId");
        this.summary = super.getString("summary");
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

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

}
