package com.martinellis.rest.api.type;

import com.tinkerpop.blueprints.Vertex;


public class Talent {
	
	private String name;
	protected Long cid;
	private String summary;
	
	public Talent() {
	    
	}
	
	public Talent(Vertex v) {
	    this.name = v.getProperty("name");
	    this.cid = v.getProperty("categoryId");
	    this.summary = v.getProperty("summary");
	}
	
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Long getCid() {
        return cid;
    }
    
    public void setCid(Long cid) {
        this.cid = cid;
    }
    
    public String getSummary() {
        return summary;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }	
}