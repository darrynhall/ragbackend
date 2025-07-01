package com.example.ingestion.config;

import org.apache.activemq.broker.BrokerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration that starts an embedded ActiveMQ broker so the application can
 * run without requiring a separate broker installation.
 */
@Configuration
public class ActiveMQConfig {

    /**
     * Embedded ActiveMQ broker listening on {@code tcp://localhost:61616}. The
     * broker is non-persistent and JMX is disabled.
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    BrokerService brokerService() throws Exception {
        BrokerService broker = new BrokerService();
        broker.addConnector("tcp://localhost:61616");
        broker.setPersistent(false);
        broker.setUseJmx(false);
        return broker;
    }
}
