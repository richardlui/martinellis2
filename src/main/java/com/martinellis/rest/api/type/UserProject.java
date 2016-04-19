package com.martinellis.rest.api.type;


public class UserProject implements Comparable<UserProject>{
	
	private UserInfo user;
	private Project project;

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
    
    @Override
    public int compareTo(UserProject other) {
        
        if (other == null || this == other) {
            return 0;
        }
        
        
        UserProject otherProject = (UserProject) other; 
 
        if (this.project.getTime() == null) {
            return -1;
        }
        
        if (otherProject.getProject().getTime() == null) {
            return 1;
        }
        
        return (int) (otherProject.getProject().getTime() - this.project.getTime());
 
    }

}