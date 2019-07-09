package com.revolut.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtil {

    private static Logger log = LogManager.getLogger(PropertyUtil.class);

    private static Properties properties = new Properties();

    private static final String configFileName = "application.properties";

    private PropertyUtil() {
        //preventing instance creation
    }

    static {
        initConfig();
    }

    private static void initConfig() {
        try {
            if (log.isInfoEnabled()) {
                log.info(String.format("Initializing config file: %s", configFileName));
            }
            final InputStream fis = Thread.currentThread().getContextClassLoader().getResourceAsStream(configFileName);
            properties.load(fis);
        } catch (FileNotFoundException fne) {
            log.warn("file name not found " + configFileName, fne);
        } catch (IOException ioe) {
            log.error(String.format("error when reading the config %s", configFileName), ioe);
        }
    }

    public static String getProperty(String key, String defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            value = System.getProperty(key);
        }
        return value == null ? defaultValue : value;
    }

    public static int getProperty(String key, Integer defaultValue) {
        String value = getProperty(key, defaultValue.toString());
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("cannot convert value %s into int", value));
            }
            return defaultValue;
        }
    }
}
