package com.martinellis.rest.server;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martinellis.rest.api.exceptions.CustomErrorException;
import com.martinellis.rest.api.exceptions.CustomErrorExceptionBuilder;
import com.martinellis.rest.api.token.RequestModeEnum;
import com.martinellis.rest.api.type.AddProjectRequest;
import com.martinellis.rest.api.type.AddProjectResponse;
import com.martinellis.rest.api.type.CreateUserResponse;
import com.martinellis.rest.api.type.DeleteUserSettingsRequest;
import com.martinellis.rest.api.type.CreateUserRequest;
import com.martinellis.rest.api.type.GetProjectsResponse;
import com.martinellis.rest.api.type.Project;
import com.martinellis.rest.api.type.UserDataResponse;
import com.martinellis.rest.api.type.UserInfo;
import com.martinellis.rest.api.type.UserProject;
import com.martinellis.rest.api.type.UserProjectsResponse;
import com.martinellis.rest.api.type.UserTalent;
import com.martinellis.rest.api.type.UserTalentsResponse;
import com.martinellis.rest.service.UserService;
import com.martinellis.rest.utils.config.ServiceLocator;
import com.sun.jersey.api.representation.Form;

/**
 * UserServiceEndPoint REST implementation
 * Responsible for creating new user
 */

@Path("/user")
@Produces({MediaType.APPLICATION_JSON + ";charset=utf-8"})
public class UserServiceEndPoint {
    
    private Logger logger = LoggerFactory.getLogger(UserServiceEndPoint.class);
    
    private UserService service = ServiceLocator.get().lookup(UserService.class);
    
    /*
     * Get user
     */
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public UserDataResponse getUser (@PathParam("id") final String id) throws Exception {

        List<UserInfo> result = service.getUser(id);
        
        //RexsterClient client = RexsterClientFactory.open("localhost", 8184, "mygraph");
        
        //List<Vertex> list = client.execute("g.V(\"userId\", 102).next()");
        		
        //client.close();
        
        UserDataResponse response = new UserDataResponse();
        response.setResult(result);
        return response;
    }
    
    /*
     * Get user talents
     */
    @GET
    @Path("/{id}/talents")
    @Produces({MediaType.APPLICATION_JSON})
    public UserTalentsResponse getTalents (@PathParam("id") final String id) throws Exception {

        List<UserTalent> result = service.getTalents(id);
        
        UserTalentsResponse response = new UserTalentsResponse();
        response.setResult(result);
        return response;
    }
    
    /*
     * Get list of friends of friends
     */
    @GET
    @Path("/{id}/friendsOfFriends")
    @Produces({MediaType.APPLICATION_JSON})
    public UserDataResponse getFriendsOfFriends (@PathParam("id") final String id, @Context UriInfo uriInfo) throws Exception {

        List<UserInfo> result = service.getFriendsOfFriends(id);
        
        UserDataResponse response = new UserDataResponse();
        response.setResult(result);
        return response;
    }
    
    /*
     * Get list of first degree friends
     */
    @GET
    @Path("/{id}/friends")
    @Produces({MediaType.APPLICATION_JSON})
    public UserDataResponse getUserFriends (@PathParam("id") final String id, @Context UriInfo uriInfo) throws Exception {

        List<UserInfo> result = service.getUserFriends(id);
        
        UserDataResponse response = new UserDataResponse();
        response.setResult(result);
        return response;
    }
    
    /*
     * Get friends' talents
     */
    @GET
    @Path("/{id}/friends/talents")
    @Produces({MediaType.APPLICATION_JSON})
    public UserTalentsResponse getFriendsTalent (@PathParam("id") final String id, @Context UriInfo uriInfo) throws Exception {

        List<UserTalent> result = service.getFriendsTalents(id);
        
        UserTalentsResponse response = new UserTalentsResponse();
        response.setResult(result);
        return response;
    }
    
    /*
     * Register new user
     * Input: Name, email, password
     */
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public CreateUserResponse createUser (Form form) throws Exception {

        CreateUserRequest request = new CreateUserRequest(form);
        UserInfo user = service.createUser(request);
        CreateUserResponse response = new CreateUserResponse();
        response.setResult(user);
        return response;
    }
    
    /*
     * ------------------------------------------------
     * ------------  Projects related api -------------
     * ------------------------------------------------
     */
    @GET
    @Path("/{id}/projects")
    @Produces({MediaType.APPLICATION_JSON})
    public GetProjectsResponse getProject (@PathParam("id") final String id) throws Exception {
        List<Project> projects = service.getProjects(id);
        GetProjectsResponse response = new GetProjectsResponse();
        response.setResult(projects);
        return response;
    }
    
    @GET
    @Path("/{id}/friends/projects")
    @Produces({MediaType.APPLICATION_JSON})
    public UserProjectsResponse getFriendsProject (@PathParam("id") final String id, @QueryParam("keywords") final String keywords) throws Exception {
        List<UserProject> result = service.getFriendsProjects(id, keywords);
        UserProjectsResponse response = new UserProjectsResponse();
        response.setResult(result);
        return response;
    }
    
    @POST
    @Path("/{id}/project")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public AddProjectResponse addProject (@PathParam("id") final String id, Form form) throws Exception {
        AddProjectRequest request = new AddProjectRequest(form);
        request.setUserId(id);
        Project project = service.addProject(request);
        AddProjectResponse response = new AddProjectResponse();
        response.setResult(project);
        return response;
    }
    
    /*
     * ------------------------------------------------
     * ------------   Friends request api -------------
     * ------------------------------------------------
     */
    /*
     * Request friend
     */
    @POST
    @Path("/{id}/request/{fid}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public CreateUserResponse requestFriend (@PathParam("id") final String id, @PathParam("fid") final String fid) throws Exception {
        logger.debug ("Add friend");
        UserInfo user = service.addFriend(id, fid);
        CreateUserResponse response = new CreateUserResponse();
        response.setResult(user);
        return response;
    }
    
    /*
     * Accept friend by linking user1 to user2
     */
    @POST
    @Path("/{id}/friend/{fid}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public CreateUserResponse acceptFriend (@PathParam("id") final String id, @PathParam("fid") final String fid, Form form) throws Exception {
        logger.debug("Link user");
        UserInfo user = service.acceptFriend(id, fid);
        CreateUserResponse response = new CreateUserResponse();
        response.setResult(user);
        return response;
    }
    
    /*
     * mode = pending - return list of pending request issued
     * mode = confirm - return list of pending request to accept
     */
    @GET
    @Path("/{id}/request")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public UserDataResponse getPendingFriendRequests (@PathParam("id") final String id, @QueryParam("mode") final String mode) throws Exception {
        if (RequestModeEnum.PENDING.name().toLowerCase().equals(mode)) {
            List <UserInfo> result = service.getPendingFriendRequests(id);
            UserDataResponse response = new UserDataResponse();
            response.setResult(result);
            return response;
        } else {
            List <UserInfo> result = service.getConfirmFriendRequests(id);
            UserDataResponse response = new UserDataResponse();
            response.setResult(result);
            return response;
        }
    }
    
    @DELETE
    @Path("/data")
    public Response deleteSettingsData(@QueryParam("user_id") String userId, @QueryParam("app") String app,
            @QueryParam("keys") String keys) throws Exception {
        if (userId == null || userId.isEmpty()) {
            CustomErrorExceptionBuilder exceptionBuilder = new CustomErrorExceptionBuilder();
            CustomErrorException exception = exceptionBuilder.validationError("user_id", "is not optional and missing").build();
            return exception.getResponse();
        } else if (app == null || app.isEmpty()){
            CustomErrorExceptionBuilder exceptionBuilder = new CustomErrorExceptionBuilder();
            CustomErrorException exception = exceptionBuilder.validationError("app", "is not optional and missing").build();
            return exception.getResponse();
        } else if (keys == null || keys.isEmpty()){
            CustomErrorExceptionBuilder exceptionBuilder = new CustomErrorExceptionBuilder();
            CustomErrorException exception = exceptionBuilder.validationError("keys", "is not optional and missing").build();
            return exception.getResponse();
        } 
        logger.info("Deleting user settings for user {} and app {} and key {}", userId, app, keys);
        DeleteUserSettingsRequest request = new DeleteUserSettingsRequest(userId, app, keys);
        service.deleteSettingsData(request);
        return Response.status(200).build();
    }
}
