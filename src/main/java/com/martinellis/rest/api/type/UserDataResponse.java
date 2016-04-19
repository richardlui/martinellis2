package com.martinellis.rest.api.type;

import java.util.ArrayList;
import java.util.List;

public class UserDataResponse {
	
    private List<UserInfo> result = new ArrayList<UserInfo>();

	public List<UserInfo> getResult() {
		return result;
	}

	public void setResult(List<UserInfo> result) {
		this.result = result;
	}

    
}
