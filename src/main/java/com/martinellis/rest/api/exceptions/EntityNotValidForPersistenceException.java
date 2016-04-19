package com.martinellis.rest.api.exceptions;

public class EntityNotValidForPersistenceException extends RuntimeException {
	private final Object entity;

    public EntityNotValidForPersistenceException(Object entity) {
        super("tried to persist entity which wasn't valid for persistence");
        this.entity = entity;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "\nEntity: "+entity.toString();
    }
}
