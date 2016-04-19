package com.martinellis.rest.api.type;

import javax.ws.rs.core.MultivaluedMap;


public class CreateUserRequest extends AbstractRequest {
	
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String type = "person";
    
    public CreateUserRequest() {
        
    }
    
    public CreateUserRequest(MultivaluedMap<String, String> parameters) {
        super(parameters);
        this.userId = super.getString(UserToken.USER_ID);
        this.firstName = super.getString(UserToken.FIRST_NAME);
        this.lastName = super.getString(UserToken.LAST_NAME);
        this.email = super.getString(UserToken.EMAIL);
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
