package com.martinellis.rest.api.type;

import java.util.ArrayList;
import java.util.List;

public class GetProjectsResponse {
	
    private List<Project> result = new ArrayList<Project>();

	public List<Project> getResult() {
		return result;
	}

	public void setResult(List<Project> result) {
		this.result = result;
	}
    
}
