package com.martinellis.rest.utils.config;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceLocator {
	private static ServiceLocator instance = new ServiceLocator();

	private HashMap<Object, Object> serviceRegistry = new HashMap<Object, Object>();

	private ServiceLocator() {
	}

	public <C> C lookup(Class<C> type) {
		return (C) serviceRegistry.get(type);
	}

	public <C> C wire(Class<C> type, C instance) {
		return (C) serviceRegistry.put(type, instance);
	}

	public static ServiceLocator get() {
		return instance;
	}

	public void clear() {
		serviceRegistry.clear();
	}

	public void logWiring() {
	   Logger logger = LoggerFactory.getLogger(getClass());
	   logger.info("com.acxiom.user.common.utils.config.ServiceLocator - logWiring - wired services:");
	   for (Map.Entry<Object, Object> entry : serviceRegistry.entrySet()) {
	       logger.info("  {} -> {}", entry.getKey(), entry.getValue());
	   }
	}
}
