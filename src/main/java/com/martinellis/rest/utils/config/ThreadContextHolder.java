package com.martinellis.rest.utils.config;

import com.martinellis.rest.utils.config.ThreadContext;

public class ThreadContextHolder {
	private static ThreadLocal<ThreadContext> context = new ThreadLocal<ThreadContext>();

    public static ThreadContext get() {
        if (context.get() == null)
            context.set(new ThreadContext());
        return context.get();
    }

    public static void clear() {
        context.set(null);
    }
}
