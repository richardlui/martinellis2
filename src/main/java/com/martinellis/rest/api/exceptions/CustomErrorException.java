package com.martinellis.rest.api.exceptions;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomErrorException extends WebApplicationException{
	private Logger logger = LoggerFactory.getLogger(getClass());
    private List<CustomError> errors = new ArrayList<CustomError>();
    private boolean errorLogged = false;

    public CustomErrorException() {
    }

    public List<CustomError> getErrors() {
        return errors;
    }

    public void setErrors(List<CustomError> errors) {
        this.errors = errors;
    }

    @Override
    public String getMessage() {
        return this.errors.toString();
    }

    @Override
    public Response getResponse() {
        Errors errors = new Errors(this.errors);
        int code = errors.getErrors().get(0).getCode();
        if (!this.errorLogged) {
            this.logError();
            this.errorLogged = true;
        }
        return Response.status(code).
                entity(errors).type(MediaType.APPLICATION_JSON).build();
    }

    private void logError() {
        for (CustomError e : this.errors) {
            logger.error(" -> HTTP Status Code: {} - Body: {}", e.getCode(), e.getMessage());
        }
    }
}
