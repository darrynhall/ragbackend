package org.aero;

import org.springframework.ai.autoconfigure.chat.client.ChatClientAutoConfiguration;
import org.springframework.ai.vectorstore.azure.autoconfigure.AzureVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
@EnableAutoConfiguration(exclude = { ChatClientAutoConfiguration.class,
		AzureVectorStoreAutoConfiguration.class })
public class RagbackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(RagbackendApplication.class, args);
	}
}
