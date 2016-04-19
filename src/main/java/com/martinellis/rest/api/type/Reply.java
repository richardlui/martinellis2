package com.martinellis.rest.api.type;

import java.util.Date;


public class Reply {
	
    private Long id;
    private Long userId;
	private String comment;
	private Date time;  // in seconds
	
	public Reply() {
	    
	}

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
	
	
}