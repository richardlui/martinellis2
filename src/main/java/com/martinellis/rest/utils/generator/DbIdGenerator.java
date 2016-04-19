package com.martinellis.rest.utils.generator;

import com.martinellis.rest.dao.TalentDao;


public class DbIdGenerator implements IdGenerator {

    private TalentDao dao;
    
    public DbIdGenerator (TalentDao dao) {
        this.dao = dao;
    }
    
    public long getUserId() {
         return dao.getUserId();
    }
    
    public long getProjectId() {
        return dao.getUserId();
    }
}
