package com.martinellis.rest.api.type;

import java.util.ArrayList;
import java.util.List;

public class CommentResponse {
	
    private List<Reply> result = new ArrayList<Reply>();

    public List<Reply> getResult() {
        return result;
    }

    public void setResult(List<Reply> result) {
        this.result = result;
    }
    
}
