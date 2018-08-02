package com.mulesoft.mozart.recommender.tests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class AlexanderEnvironmentConfiguration {

    static Logger logger = LoggerFactory.getLogger(AlexanderEnvironmentConfiguration.class);

    static Properties properties;

    public static Properties getConfiguration() {
        if(Objects.isNull(properties)) {
            String env = System.getProperty("mozart.environment", "local");
            logger.info("Using env {} ", env);
            try {
                properties = new Properties();
                properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(env + ".properties"));
                return properties;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return properties;
    }

}
