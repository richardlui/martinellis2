package com.martinellis.rest.utils.generator;


public class RandomIdGenerator implements IdGenerator {

    public long getUserId() {
         return (long) (Math.random() * 10000);
    }
    
    public long getProjectId() {
        return (long) (Math.random() * 10000);
    }
}
