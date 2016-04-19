package com.martinellis.rest.utils.config;

import java.util.HashMap;

public class ThreadContext extends HashMap<Object, Object> {

    private static final String REQUEST_URL = "requestUrl";
    private static final String REQUEST_PARAMS = "";
    private static final String ID = "ID";
    private static final String CLIENT_ID = "clientId";

    public void setId(long id) {
        put(ID, id);
    }

    public Long getId() {
        return (Long) get(ID);
    }

    public void setRequestUrl(String url) {
        if (url == null) {
            return;
        }
        put(REQUEST_URL, url);
    }

    public void setRequestParams(String queryString) {
        if (queryString == null) {
            return;
        }
        put(REQUEST_PARAMS, queryString);
    }

    public String getRequestUrl() {
        return (String) get(REQUEST_URL);
    }

    public String getRequestParams() {
        return (String) get(REQUEST_PARAMS);
    }
    
    public void setClientId(String id) {
        put(CLIENT_ID, id);
    }

    public String getClientId() {
        return (String) get(CLIENT_ID);
    }
}
