package com.example.ingestion.config;

import org.apache.activemq.artemis.jms.server.embedded.EmbeddedJMS;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration that starts an embedded Artemis broker so the application can
 * run without requiring a separate broker installation.
 */
@Configuration
public class ArtemisConfig {

    /**
     * Embedded Artemis broker listening on {@code tcp://localhost:61616}.
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    EmbeddedJMS artemisServer() {
        return new EmbeddedJMS();
    }
}
