package com.martinellis.rest.server;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martinellis.rest.api.type.AddReplyRequest;
import com.martinellis.rest.api.type.CreateUserRequest;
import com.martinellis.rest.api.type.CreateUserResponse;
import com.martinellis.rest.api.type.Reply;
import com.martinellis.rest.api.type.CommentResponse;
import com.martinellis.rest.service.TopicService;
import com.martinellis.rest.utils.config.ServiceLocator;
import com.sun.jersey.api.representation.Form;

/**
 * UserServiceEndPoint REST implementation
 * Responsible for creating new user
 */

@Path("/topic")
@Produces({MediaType.APPLICATION_JSON + ";charset=utf-8"})
public class TopicServiceEndPoint {
    
    private Logger logger = LoggerFactory.getLogger(TopicServiceEndPoint.class);
    
    private TopicService service = ServiceLocator.get().lookup(TopicService.class);
    
    /*
     * Get comments based on topic
     */
    @GET
    @Path("/{topicId}/reply")
    @Produces({MediaType.APPLICATION_JSON})
    public CommentResponse getReply (@PathParam("topicId") final String topicId) throws Exception {

        List<Reply> result = service.getComment(Long.valueOf(topicId));
        
        CommentResponse response = new CommentResponse();
        response.setResult(result);
        return response;
    }
    
    /*
     * Post comments based on topic
     */
    @POST
    @Path("/{topicId}/reply")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public CommentResponse addReply (@PathParam("topicId") final Long topicId, Form form) throws Exception {

        AddReplyRequest request = new AddReplyRequest(form);
        request.setTopicId(topicId);
        Reply result = service.addComment(request);
        List<Reply> list = new ArrayList<Reply>(1);
        list.add(result);
        
        CommentResponse response = new CommentResponse();
        response.setResult(list);
        return response;
    }
    
}
