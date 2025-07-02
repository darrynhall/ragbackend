package com.example;

import org.springframework.ai.autoconfigure.chat.client.ChatClientAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
@EnableAutoConfiguration(exclude = ChatClientAutoConfiguration.class)
public class RagbackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(RagbackendApplication.class, args);
    }
}
