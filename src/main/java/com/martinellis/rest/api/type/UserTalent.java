package com.martinellis.rest.api.type;

import java.util.ArrayList;
import java.util.List;

public class UserTalent {
	
	private UserInfo user;
	private List<Talent> talents = new ArrayList<Talent>();

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public List<Talent> getTalents() {
        return talents;
    }

    public void setTalents(List<Talent> talents) {
        this.talents = talents;
    }
}