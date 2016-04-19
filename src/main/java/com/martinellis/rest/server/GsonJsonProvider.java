package com.martinellis.rest.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

@Provider
@Consumes({MediaType.APPLICATION_JSON, "text/json"})
@Produces({MediaType.APPLICATION_JSON, "text/json"})
public class GsonJsonProvider implements MessageBodyReader<Object>, MessageBodyWriter<Object> {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean isReadable(Class arg0, Type arg1, Annotation[] arg2, MediaType arg3) {
        return true;
    }

    @Override
    public Object readFrom(Class arg0, Type type, Annotation[] arg2, MediaType arg3, MultivaluedMap arg4, InputStream inp) throws IOException, WebApplicationException {
        return new Gson().fromJson(new InputStreamReader(inp), type);
    }

    @Override
    public boolean isWriteable(Class<?> type, Type type1, Annotation[] antns, MediaType mt) {
        return true;
    }

    @Override
    public long getSize(Object t, Class<?> type, Type type1, Annotation[] antns, MediaType mt) {
        return -1;
    }

    @Override
    public void writeTo(Object t, Class<?> type, Type type1, Annotation[] antns, MediaType mt, MultivaluedMap<String, Object> mm, OutputStream out) throws IOException, WebApplicationException {
        Gson gson = newGson();

        final Writer w = new OutputStreamWriter(out, "UTF-8");
        final JsonWriter jsw = new JsonWriter(w);
        gson.toJson(t, type, jsw);
        jsw.close();
    }

    private Gson newGson() {
        Gson gson = new GsonBuilder()
                .generateNonExecutableJson()
                .serializeSpecialFloatingPointValues()
                .setDateFormat("yyyy-MM-dd")
                .setPrettyPrinting()
                .create();
        return gson;
    }
}
