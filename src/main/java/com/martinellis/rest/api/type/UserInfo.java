package com.martinellis.rest.api.type;

import com.tinkerpop.blueprints.Vertex;

public class UserInfo {
	
	private Long id;
	private Long userId;
	private String firstName;
	private String lastName;
	private String type;
	private String email;
	// Depending on the context, time can indicate user creation time, friend request timestamp
	private Long time;   // in seconds
	
	public UserInfo() {
	    
	}
	
	public UserInfo(Vertex v) {
		this.id = (Long) v.getId();
		this.userId = (Long) v.getProperty(UserToken.USER_ID);
		this.firstName = (String)v.getProperty(UserToken.FIRST_NAME);
		this.lastName = (String)v.getProperty(UserToken.LAST_NAME);
    	this.type = (String)v.getProperty(UserToken.TYPE);
    	this.email = (String)v.getProperty(UserToken.EMAIL);
	}
	
	public UserInfo(Vertex v, long time) {
        this(v);
        this.time = time;
    }
	
	public UserInfo(CreateUserRequest r) {
        this.firstName = r.getFirstName();
        this.lastName = r.getLastName();
        this.type = r.getType();
        this.email = r.getEmail();
    }
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String name) {
		this.firstName = name;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
	    return "First Name: " + this.getFirstName() + ", last Name: " + this.getLastName() + ", userid: " + this.getUserId();
	}
	
	@Override
	public boolean equals(Object other) {
	    if (other == null) {
	        return false;
	    }
	    if (getClass() != other.getClass()) {
	        return false;
	    }
	    final UserInfo otherUser = (UserInfo) other;
	    if (this.getUserId() == null && otherUser.getUserId() == null) {
	        return true;
	    }
	    if (this.getUserId() == null || otherUser.getUserId() == null) {
            return false;
	    }
	    boolean value = this.getUserId().longValue() == otherUser.getUserId().longValue();
	    return value;
	}
	
	@Override
	public int hashCode() {
	    if (this.userId == null) {
	        return -1;
	    } else {
	        int hash = this.getUserId().hashCode();
	        return this.getUserId().hashCode();
	    }
	}

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
