package org.aero.ingestion.config;

 import static org.aero.ingestion.constants.AppConstants.FILE_UPLOADED_QUEUE;
 import static org.aero.ingestion.constants.AppConstants.FILE_DELETED_QUEUE; 

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JmsConfig {
    @Bean
    ActiveMQQueue fileUploadedQueue() {
        return new ActiveMQQueue(FILE_UPLOADED_QUEUE);
    }

    @Bean
    ActiveMQQueue fileDeletedQueue() {
        return new ActiveMQQueue(FILE_DELETED_QUEUE);
    }

    @Bean
    ActiveMQQueue textExtractedQueue() {
        return new ActiveMQQueue("text.extracted");
    }

    @Bean
    ActiveMQQueue textChunkedQueue() {
        return new ActiveMQQueue("text.chunked");
    }

    @Bean
    ActiveMQQueue embeddingGeneratedQueue() {
        return new ActiveMQQueue("embedding.generated");
    }
}
