package com.martinellis.rest.utils.config;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martinellis.rest.api.exceptions.CustomErrorExceptionBuilder;

public class PropertiesFileProcessor {
	private Logger logger = LoggerFactory.getLogger(PropertiesFileProcessor.class);

    public void loadPropertyFile(Properties settings, String propertyFileLocation) {
        InputStream stream = null;
        try {
            stream = getPathAsStream(propertyFileLocation);
            if (stream == null) {
                logger.error("loadPropertyFile() - could not find property file " + propertyFileLocation);
                throw new CustomErrorExceptionBuilder().internalError("failed to load environment variables from "+propertyFileLocation).build();
            }
            settings.load(stream);
        } catch (IOException e) {
            logger.error("could not read property file " + propertyFileLocation, e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    logger.error("loadPropertyFile() - could not close property file " + propertyFileLocation, e);
                }
            }
        }
    }

    @SuppressWarnings("resource")
    private InputStream getPathAsStream(String propertyFileLocation) throws FileNotFoundException {
        InputStream stream;
        File file = new File(propertyFileLocation);
        if (file.exists()) {
            logger.debug("getPathAsStream() - loading {} from file system", propertyFileLocation);
            stream = new BufferedInputStream(new FileInputStream(file));
        } else {
            logger.debug("getPathAsStream() - loading {} from classpath", propertyFileLocation);
            stream = getClass().getResourceAsStream(propertyFileLocation);
        }
        return stream;
    }
}
