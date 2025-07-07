package org.aero.ingestion.config;

import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EtlPipelineConfig {

	@Bean
	TextSplitter textSplitter() {
		return new TokenTextSplitter();
	}

	@Autowired
	VectorStore vectorStore;
}
