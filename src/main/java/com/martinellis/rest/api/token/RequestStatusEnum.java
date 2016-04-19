package com.martinellis.rest.api.token;

public enum RequestStatusEnum {
    
    PENDING("pending"), COMPLETE("complete");
    
    private String status;
    
    private RequestStatusEnum(String s) {
        this.status = s;
    }
    
    public String getStatus () {
        return this.status;
    }
}
