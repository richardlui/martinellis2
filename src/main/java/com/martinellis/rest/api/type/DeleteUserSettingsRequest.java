package com.martinellis.rest.api.type;


public class DeleteUserSettingsRequest extends AbstractRequest{
	
	private String userId;
    private String app;
    private String key;  // one or more key
    
    public DeleteUserSettingsRequest(String userId, String app, String key) {
    	this.userId = userId;
        this.app = app;
        this.key = key;
	}

	public String getApp() {
        return app;
    }
    
    public void setApp(String app) {
        this.app = app;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
