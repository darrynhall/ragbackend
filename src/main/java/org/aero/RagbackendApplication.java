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
		AzureVectorStoreAutoConfiguration.class /*
												 * , OpenAiAutoConfiguration.class, AzureOpenAiAutoConfiguration.class,
												 * ChatObservationAutoConfiguration.class
												 */ })
public class RagbackendApplication {

	public static void main(String[] args) {
		System.setProperty("javax.net.debug", "ssl,handshake");
		SpringApplication.run(RagbackendApplication.class, args);
	}
}
