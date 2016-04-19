package com.martinellis.rest.service;

import java.util.List;

import com.martinellis.rest.api.type.AddProjectRequest;
import com.martinellis.rest.api.type.CreateUserRequest;
import com.martinellis.rest.api.type.DeleteUserSettingsRequest;
import com.martinellis.rest.api.type.Project;
import com.martinellis.rest.api.type.Talent;
import com.martinellis.rest.api.type.UserInfo;
import com.martinellis.rest.api.type.UserProject;
import com.martinellis.rest.api.type.UserTalent;

public interface UserService {
    
    List<UserInfo> getUser(String id);
    
    List<UserInfo> getUserFriends(String id);
    
    List<UserInfo> getFriendsOfFriends(String id);
    
    UserInfo createUser(CreateUserRequest request);
    
    List<UserTalent> getFriendsTalents(String userId);
    
    List<UserTalent> getTalents(String userId);
    
    Talent addTalent(String userId, String categoryId);
    
    List<Project> getProjects(String userId);
    
    Project addProject(AddProjectRequest request);
    
    List<UserProject> getFriendsProjects(String userId, String keywords);
    
    UserInfo acceptFriend(String id, String fid);
    
    UserInfo addFriend(String id, String newFriendId);
    
    List<UserInfo> getPendingFriendRequests(String id);
    
    List<UserInfo> getConfirmFriendRequests(String id);
    
    // addSellingItem
    // addNeededItem
    // addHobby
    // addReferral
    
	void deleteSettingsData(DeleteUserSettingsRequest request);
}
