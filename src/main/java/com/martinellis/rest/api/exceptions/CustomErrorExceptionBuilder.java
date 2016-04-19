package com.martinellis.rest.api.exceptions;


public class CustomErrorExceptionBuilder {
	private CustomErrorException exception = new CustomErrorException();

    public CustomErrorExceptionBuilder() {
    }

    public CustomErrorExceptionBuilder notFound(String msg) {
        exception.getErrors().add(new CustomError(400, msg+" not found"));
        return this;
    }

    public CustomErrorExceptionBuilder clientError(String msg) {
        exception.getErrors().add(new CustomError(400, "Input Error: " + msg));
        return this;
    }

    public CustomErrorExceptionBuilder validationError(String field, String msg) {
        exception.getErrors().add(new CustomError(400, "Error Validating Input: '" + field + "': " + msg));
        return this;
    }

    public CustomErrorExceptionBuilder internalError(String msg) {
        exception.getErrors().add(new CustomError(500, "Internal Error: " + msg));
        return this;
    }

    public CustomErrorExceptionBuilder internalError(Exception e) {
        if (e.getCause() != null) {
            exception.getErrors().add(new CustomError(800, e.getMessage(), e.getCause().getMessage()));
        } else {
            exception.getErrors().add(new CustomError(500, e.getMessage()));
        }
        return this;
    }

    public CustomErrorExceptionBuilder noMetaDataError() {
        return noMetaDataError(null);
    }

    public CustomErrorExceptionBuilder noMetaDataError(String param) {
        String msg = "No metadata found for the requested parameter";
        if (param != null) {
            msg += "[" + param + "]";
        }
        exception.getErrors().add(new CustomError(404, msg));
        return this;
    }

    public CustomErrorExceptionBuilder alreadyExists(String msg) {
        exception.getErrors().add(new CustomError(400, "Item " + msg + " already exists in your target context."));
        return this;
    }

    public CustomErrorExceptionBuilder doesntExistOrNotAccessibleByYouError(String msg) {
        exception.getErrors().add(new CustomError(404,
                "Requested item (" + msg + ") does not exist, is not of the right type, or is not accessible by you"));
        return this;
    }

    public CustomErrorExceptionBuilder doesntExistOrNotAccessibleByYouError() {
        exception.getErrors().add(new CustomError(404,
                "Requested item does not exist, is not of the right type, or is not accessible by you"));
        return this;
    }

    public CustomErrorException build() {
        return exception;
    }

    public CustomErrorExceptionBuilder methodNotAllowd() {
        exception.getErrors().add(new CustomError(405, "Method Not Allowed"));
        return this;
    }
}
