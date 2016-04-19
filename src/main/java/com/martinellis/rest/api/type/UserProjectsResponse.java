package com.martinellis.rest.api.type;

import java.util.ArrayList;
import java.util.List;

public class UserProjectsResponse {
	
    private List<UserProject> result = new ArrayList<UserProject>();

	public List<UserProject> getResult() {
		return result;
	}

	public void setResult(List<UserProject> result) {
		this.result = result;
	}
    
}
