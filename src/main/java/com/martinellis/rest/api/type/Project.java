package com.martinellis.rest.api.type;

import com.tinkerpop.blueprints.Vertex;

public class Project extends Talent {
	
    private Long projectId;
	private String imageURL;
	private String desc;
	private Long time;  // in seconds
	
	public Project() {
	    
	}
	
	public Project (Vertex v) {
	    super(v);
	    this.projectId = (Long) v.getProperty("projectId");
	    this.time = (Long) v.getProperty("time");
	}
	
    public String getImageURL() {
        return imageURL;
    }
    
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
	
}