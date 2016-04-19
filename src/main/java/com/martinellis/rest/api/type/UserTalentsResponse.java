package com.martinellis.rest.api.type;

import java.util.ArrayList;
import java.util.List;

public class UserTalentsResponse {
	
    private List<UserTalent> result = new ArrayList<UserTalent>();

	public List<UserTalent> getResult() {
		return result;
	}

	public void setResult(List<UserTalent> result) {
		this.result = result;
	}
    
}
