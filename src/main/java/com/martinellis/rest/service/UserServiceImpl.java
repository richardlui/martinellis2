package com.martinellis.rest.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.elasticsearch.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.martinellis.rest.api.exceptions.CustomErrorException;
import com.martinellis.rest.api.exceptions.CustomErrorExceptionBuilder;
import com.martinellis.rest.api.token.RequestStatusEnum;
import com.martinellis.rest.api.type.AddProjectRequest;
import com.martinellis.rest.api.type.CreateUserRequest;
import com.martinellis.rest.api.type.DeleteUserSettingsRequest;
import com.martinellis.rest.api.type.Project;
import com.martinellis.rest.api.type.RelationshipToken;
import com.martinellis.rest.api.type.Talent;
import com.martinellis.rest.api.type.UserInfo;
import com.martinellis.rest.api.type.UserProject;
import com.martinellis.rest.api.type.UserTalent;
import com.martinellis.rest.api.type.UserToken;
import com.martinellis.rest.dao.TalentDao;
import com.martinellis.rest.utils.config.ServiceLocator;
import com.martinellis.rest.utils.generator.IdGenerator;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.attribute.Text;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.VertexQuery;
import com.tinkerpop.blueprints.util.ElementHelper;

public class UserServiceImpl implements UserService {
    
    private TalentDao dao;
    
    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    private IdGenerator idGenerator;
    
    @Override
    public List<UserInfo> getUser(String id) {
        
    	TitanGraph graph = ServiceLocator.get().lookup(TitanGraph.class);
        Iterable <Vertex> list = graph.getVertices("userId", id);
        
        List<UserInfo> result = new ArrayList<UserInfo>();
        
        for (Vertex v : list) {
        	UserInfo user = new UserInfo(v);
        	result.add(user);
        }
        graph.commit();
    	return result;
    }
    
    @Override
    public List<UserInfo> getUserFriends(String id) {
    	List<UserInfo> result = new ArrayList<UserInfo>();
    	TitanGraph graph = ServiceLocator.get().lookup(TitanGraph.class);
    	
        Iterable <Vertex> list = graph.getVertices(UserToken.USER_ID, id);
        Vertex user = Iterables.getOnlyElement(list);
        if (user != null) {
            Iterable <Vertex> friends = user.query().labels(RelationshipToken.FRIEND).direction(Direction.BOTH).vertices();
            for (Vertex friend : friends) {
        		UserInfo friendInfo = new UserInfo(friend);
        		result.add(friendInfo);
        	}
        }
        graph.commit();
        return result;
    }
    
    @Override
    public List<UserInfo> getFriendsOfFriends(String id) {
        HashSet<UserInfo> set1 = new HashSet<UserInfo>(128);
        HashSet<UserInfo> set2 = new HashSet<UserInfo>(256);
        
        TitanGraph graph = ServiceLocator.get().lookup(TitanGraph.class);
        Iterable <Vertex> list = graph.getVertices(UserToken.USER_ID, id);
        Vertex user = Iterables.getOnlyElement(list);
        if (user != null) {
            // Add user himself to the set to exclude himself from the result
            UserInfo self = new UserInfo(user);
            set1.add(self);
            Iterable <Vertex> friends = user.query().labels(RelationshipToken.FRIEND).direction(Direction.BOTH).vertices();
            for (Vertex friend : friends) {
                UserInfo friendInfo = new UserInfo(friend);
                set1.add(friendInfo);
                
                // for each friend, find his/her friends, limit to 10
                Iterable <Vertex> friendsOfFriends = friend.query().labels(RelationshipToken.FRIEND).direction(Direction.BOTH).vertices();
                for (Vertex friendsOfFriend : friendsOfFriends) {
                    UserInfo fofInfo = new UserInfo(friendsOfFriend);
                    set2.add(fofInfo);
                }
            }
        }
        graph.commit();
        Set<UserInfo> result = Sets.difference(set2,  set1);
        return new ArrayList<UserInfo>(result);
    }
    
    /*
     * For demo purpose, automatically link with user 100
     */
    @Override
    public UserInfo createUser(CreateUserRequest request) {
        final long superuserId = 100L;
        
        TitanGraph graph = ServiceLocator.get().lookup(TitanGraph.class);
        Vertex person = graph.addVertex(null);
        //long userId = idGenerator.getUserId();
        
        long userId = dao.createUser(request.getFirstName(),  request.getLastName(),  request.getEmail());
        logger.info("Creating userId: {}" , userId);
        
        long seconds = new Date().getTime() / 1000;
        ElementHelper.setProperties(person, UserToken.FIRST_NAME, request.getFirstName(), 
                UserToken.USER_ID, userId,
                UserToken.EMAIL, request.getEmail(),
                UserToken.TYPE, request.getType(),
                UserToken.TIME, seconds);
        
        // link the new user to superuser to get something going
        Iterable <Vertex> list = graph.getVertices(UserToken.USER_ID, superuserId);
        Vertex superuser = list.iterator().next();
        Edge e1 = person.addEdge(RelationshipToken.FRIEND, superuser);
        ElementHelper.setProperties(e1, "time", seconds);
        graph.commit();
        
        UserInfo user = new UserInfo (request);
        user.setId((Long)person.getId());
        user.setUserId(userId);
        return user;
    }
    
    private Vertex getOneElement(TitanGraph graph, String key, String value) {
        Iterable<Vertex> users = graph.getVertices(key, value);
        if (users.iterator().hasNext()) {
            return users.iterator().next();
        } else {
            return null;
        }
    }
    
    
    /*
     * Projects related api
     * 
     */
    @Override
    public List<Project> getProjects(String userId) {
        TitanGraph graph = ServiceLocator.get().lookup(TitanGraph.class);
        Vertex user = getOneElement(graph, UserToken.USER_ID, userId);
        List<Project> userProjects = new ArrayList<Project>();
        
        Iterable<Vertex> projects = user.query().labels(RelationshipToken.SHOWS).direction(Direction.OUT).vertices();
        
        for (Vertex project : projects) {
            Project proj = new Project(project);
            Edge projectEdge = project.getEdges(Direction.IN, RelationshipToken.SHOWS).iterator().next();
            proj.setSummary((String)projectEdge.getProperty("summary"));
            proj.setCid((Long)projectEdge.getProperty("proj-catId"));
            proj.setTime((Long)projectEdge.getProperty("time"));
            userProjects.add(proj);
        }
        graph.commit();
        return userProjects;
    }
    
    @Override
    public Project addProject(AddProjectRequest request) {
        TitanGraph graph = ServiceLocator.get().lookup(TitanGraph.class);
        Vertex user = getOneElement(graph, UserToken.USER_ID, request.getUserId());
        if (user == null) {
            logger.error("Invalid user {}", request.getUserId());
            // TODO: throw exception
            return new Project();
        }
        
        // Check if category id is valid and retrieve name
        Vertex service = getOneElement(graph, "categoryId", request.getCid());
        if (service == null) {
            logger.error("Invalid service category {}", request.getCid());
            return new Project();
        }
        
        long topicId = dao.addTopic(Long.valueOf(request.getUserId()));
        logger.info("Creating topic: {}" , topicId);
        
        // create new project node
        Vertex projectNode = graph.addVertex(null);
        ElementHelper.setProperties(projectNode, "name", service.getProperty("name"), "projectId", topicId, "type", "project");
        
        // establish the link
        long seconds = new Date().getTime() / 1000;
        Edge e1 = user.addEdge("shows", projectNode);
        ElementHelper.setProperties(e1, "proj-catId", request.getCid(), "time", seconds, "summary", request.getSummary());
        graph.commit();
        
        Project proj = new Project(projectNode);
        proj.setName((String)service.getProperty("name"));
        proj.setSummary(request.getSummary());
        proj.setTime(seconds);
        return proj;
    }
    
    /*
     * Returns friends projects that matches keywords
     * If keywords is null, return 5 most recent projects from each friend
     */
    @Override
    public List<UserProject> getFriendsProjects(String userId, String keywords) {
        TitanGraph graph = ServiceLocator.get().lookup(TitanGraph.class);
        Vertex user = getOneElement(graph, UserToken.USER_ID, userId);
        List<UserProject> userProjects = new ArrayList<UserProject>();
        
        // retrieve all friends
        if (user != null) {
            Iterable <Vertex> friends = user.query().labels(RelationshipToken.FRIEND).direction(Direction.BOTH).vertices();
            for (Vertex friend : friends) {
                
                // find out projects from each friend
                VertexQuery query = friend.query().labels("shows").direction(Direction.OUT).limit(5);
                if (keywords != null && !keywords.equals("")) {
                    query = query.has("summary", Text.CONTAINS, keywords);
                }
                Iterable<Vertex> projVertices = query.vertices();
                
                if (projVertices.iterator().hasNext()) {
                    
                    for (Vertex pv : projVertices) {
                        UserProject userProject = new UserProject();
                        UserInfo userInfo = new UserInfo(friend);
                        userProject.setUser(userInfo);
                        Project proj = new Project(pv);
                        Edge projectEdge = pv.getEdges(Direction.IN, RelationshipToken.SHOWS).iterator().next();
                        proj.setSummary((String)projectEdge.getProperty("summary"));
                        proj.setCid((Long)projectEdge.getProperty("proj-catId"));
                        proj.setTime((Long)projectEdge.getProperty("time"));
                        userProject.setProject(proj);
                        userProjects.add(userProject);
                    }
                    
                }
            }
        }
        graph.commit();
        Collections.sort(userProjects);
        return userProjects;
    }
    
    /*
     * Talents related api
     * 
     */
    @Override
    public Talent addTalent(String userId, String categoryId) {
        TitanGraph graph = ServiceLocator.get().lookup(TitanGraph.class);
        Vertex user = getOneElement(graph, UserToken.USER_ID, userId);
        if (user == null) {
            // TODO, throws user not found
            
        }
        
        // Get the talent category
        Vertex category = getOneElement(graph, "categoryId", categoryId);
        user.addEdge(RelationshipToken.KNOWS, category);
        
        graph.commit();
        return new Talent(category);
    }
    
    @Override
    public List<UserTalent> getFriendsTalents(String userId) {
        TitanGraph graph = ServiceLocator.get().lookup(TitanGraph.class);
        Vertex user = getOneElement(graph, UserToken.USER_ID, userId);
        List<UserTalent> userTalents = new ArrayList<UserTalent>();
        
        // retrieve all friends
        if (user != null) {    
            Iterable <Vertex> friends = user.query().labels(RelationshipToken.FRIEND).direction(Direction.BOTH).vertices();
            for (Vertex friend : friends) {
                
                // find out talents from each friend
                Iterable<Vertex> talentVertices = friend.query().direction(Direction.OUT).labels(RelationshipToken.KNOWS).vertices();
                if (talentVertices.iterator().hasNext()) {
                    UserTalent userTalent = new UserTalent();
                    UserInfo userInfo = new UserInfo(friend);
                    userTalent.setUser(userInfo);
                    for (Vertex tv : talentVertices) {
                        Talent talent = new Talent(tv);
                        userTalent.getTalents().add(talent);
                    }
                    userTalents.add(userTalent);
                }
            }
        }
        graph.commit();
        return userTalents;
    }
    
    @Override
    public List<UserTalent> getTalents(String userId) {
        TitanGraph graph = ServiceLocator.get().lookup(TitanGraph.class);
        Iterable<Vertex> users = graph.getVertices(UserToken.USER_ID, userId);
        List<UserTalent> userTalents = new ArrayList<UserTalent>();
        
        if (users.iterator().hasNext()) {    
            Vertex user = users.iterator().next();
            UserTalent userTalent = new UserTalent();
            UserInfo userInfo = new UserInfo(user);
            userTalent.setUser(userInfo);
            Iterable<Vertex> talentVertices = user.query().labels(RelationshipToken.KNOWS).direction(Direction.OUT).vertices();
            
            for (Vertex tv : talentVertices) {
                Talent talent = new Talent(tv);
                userTalent.getTalents().add(talent);
            }
            userTalents.add(userTalent);
        }
        graph.commit();
        return userTalents;
    }
    
    @Override
    public UserInfo addFriend(String userId, String newFriendId) {
        TitanGraph graph = ServiceLocator.get().lookup(TitanGraph.class);
        Vertex user = getOneElement(graph, UserToken.USER_ID, userId);
        Vertex newFriend = getOneElement(graph, UserToken.USER_ID, newFriendId);
        
        // Check if user ids are valid
        if (user == null) {
            logger.error("Invalid ids: userId: {}", userId);
            CustomErrorException exception = 
                    new CustomErrorExceptionBuilder().notFound("User " + userId).build();
            throw exception;
        }
        if (newFriend == null) {
            logger.error("Invalid ids: new friend userId: {}", newFriend);
            CustomErrorException exception = 
                    new CustomErrorExceptionBuilder().notFound("Friend User " + userId).build();
            throw exception;
        }
        
        // Check if friend request already pending
        Iterable<Vertex> friends = user.query().labels(RelationshipToken.REQUEST).direction(Direction.OUT).has("requestStatus", RequestStatusEnum.PENDING.getStatus()).vertices();
        for (Vertex v : friends) {
            if (Long.valueOf(newFriendId).equals((Long)v.getProperty("userId"))) {
                logger.warn ("{} has already request friend to {}", userId, newFriendId);
                return new UserInfo(newFriend);
            }
        }
        
        long seconds = new Date().getTime() / 1000;
        Edge e1 = user.addEdge(RelationshipToken.REQUEST, newFriend);
        ElementHelper.setProperties(e1, "time", seconds);
        ElementHelper.setProperties(e1, "requestStatus", RequestStatusEnum.PENDING.getStatus());
        graph.commit();
        
        return new UserInfo(newFriend);
    }
    
    @Override
    public UserInfo acceptFriend(String userId, String friendId) {
        TitanGraph graph = ServiceLocator.get().lookup(TitanGraph.class);
        
        Vertex user1 = null;
        Iterable <Vertex> list = graph.getVertices(UserToken.USER_ID, userId);        
        try {
            user1 = Iterables.getOnlyElement(list);
        } catch (Exception e) {
            CustomErrorException exception = 
                    new CustomErrorExceptionBuilder().notFound("User " + userId).build();
            throw exception;
        }
        
        Vertex user2 = null;
        Iterable <Vertex> list2 = graph.getVertices(UserToken.USER_ID, friendId);
        try {
            user2 = Iterables.getOnlyElement(list2);
        } catch (Exception e) {
            CustomErrorException exception = 
                    new CustomErrorExceptionBuilder().notFound("User " + friendId).build();
            throw exception;
        }
        
        // Check if both parties already friend
        Iterable<Vertex> friends = user1.query().labels(RelationshipToken.FRIEND).direction(Direction.BOTH).vertices();
        for (Vertex v : friends) {
            if (Long.valueOf(friendId).equals((Long)v.getProperty("userId"))) {
                // This should never happen
                // reset REQUEST relationship to COMPLETE
                logger.warn ("{} is already a friend of {}", friendId, userId);
                logger.warn ("Resetting REQUEST relationship property to CONFIRM");
                
                Iterable <Edge> requests = user1.query().labels(RelationshipToken.REQUEST).direction(Direction.IN).has("requestStatus", RequestStatusEnum.PENDING.getStatus()).edges();
                for (Edge request : requests) {
                    Vertex originator = request.getVertex(Direction.OUT);
                    if (Long.valueOf(friendId).equals((Long) originator.getProperty(UserToken.USER_ID))) {
                        ElementHelper.setProperties(request, "requestStatus", RequestStatusEnum.COMPLETE);
                        break;
                    }
                }
                // TODO: put in finally block
                // Add unit test.
                graph.commit();
                return new UserInfo(user2);
            }
        }
        
        // Check if there is a friend request relationship
        // Disallow link two user without having a REQUEST relationship first.
        // TODO:
        
        
        long seconds = new Date().getTime() / 1000;
        Edge e1 = user1.addEdge(RelationshipToken.FRIEND, user2);
        ElementHelper.setProperties(e1, "time", seconds);
        
        logger.info("Added {} as friend.", (String) user2.getProperty(UserToken.FIRST_NAME));
        
        // Update the request status
        Iterable <Edge> requests = user1.query().labels(RelationshipToken.REQUEST).direction(Direction.IN).has("requestStatus", RequestStatusEnum.PENDING.getStatus()).edges();
        logger.info("Checking for existing friend request.");
        boolean foundRequest = false;
        for (Edge request : requests) {
            Vertex originator = request.getVertex(Direction.OUT);
            if (Long.valueOf(friendId).equals((Long) originator.getProperty(UserToken.USER_ID))) {
                logger.info("found the corresponding friend request, updating from pending to complete");
                ElementHelper.setProperties(request, "requestStatus", RequestStatusEnum.COMPLETE);
                foundRequest = true;
                break;
            }
        }
        if (!foundRequest) {
            logger.warn("Adding {} without a friend request", (String) user2.getProperty(UserToken.FIRST_NAME));
        }
         
        graph.commit();
        
        UserInfo user = new UserInfo(user2);
        return user;
    }
    
    private List<UserInfo> getFriendRequests(String id, Direction direction) {
        TitanGraph graph = ServiceLocator.get().lookup(TitanGraph.class);
        Iterable <Vertex> list = graph.getVertices(UserToken.USER_ID, id);
        Vertex user = Iterables.getOnlyElement(list);
        List<UserInfo> result = new ArrayList<UserInfo>();
        
        if (user != null) {
            Iterable <Edge> requests = user.query().labels(RelationshipToken.REQUEST).direction(direction).has("requestStatus", RequestStatusEnum.PENDING.getStatus()).edges();
            for (Edge request : requests) {
                Vertex friend = request.getVertex(direction == Direction.OUT ? Direction.IN : Direction.OUT);
                long seconds = request.getProperty("time");
                UserInfo friendInfo = new UserInfo(friend, seconds);
                result.add(friendInfo);
            }
        }
        graph.commit();
        return result;
    }
    
    @Override
    public List<UserInfo> getPendingFriendRequests(String id) {
        return getFriendRequests(id, Direction.OUT);
    }
    
    @Override
    public List<UserInfo> getConfirmFriendRequests(String id) {
        return getFriendRequests(id, Direction.IN);
    }
    
    @Override
    public void deleteSettingsData(DeleteUserSettingsRequest request) {
        int count = dao.delete(request.getUserId(), request.getApp(), request.getKey());
        logger.info("Deleted {} rows of settings data.", count);
        if (count==0) {
        	CustomErrorException exception = 
                    new CustomErrorExceptionBuilder().notFound(
                            "No settings found for user " + request.getUserId()).build();
        	throw exception;
        }
    }
    
    public TalentDao getDao() {
        return dao;
    }

    public void setDao(TalentDao dao) {
        this.dao = dao;
    }

    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }
}
