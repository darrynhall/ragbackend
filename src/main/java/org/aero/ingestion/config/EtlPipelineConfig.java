package org.aero.ingestion.config;

import java.util.List;

import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class EtlPipelineConfig implements WebMvcConfigurer {
	@Override
	public void configureMessageConverters(final @NonNull List<HttpMessageConverter<?>> converters) {
		// This is a hack to make swagger interface work with multipart form
		// data (json + file). Instead of sending the json part as content type
		// application/json, it sends it as application/octet-stream, so we need to
		// accept and convert that with Jackson.
		final MappingJackson2HttpMessageConverter customConverter = new MappingJackson2HttpMessageConverter();
		customConverter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM));
		converters.add(customConverter);
	}

	@Bean
	TextSplitter textSplitter() {
		int chunkSize = 750;
		 int minChunkSizeChars = 50;
		 int minChunkLengthToEmbed = 0;
		 int maxNumChunks = 5;
		 boolean keepSeparator = true;
		return new TokenTextSplitter(
                 
                 chunkSize,
                 minChunkSizeChars,
                 minChunkLengthToEmbed,
                 maxNumChunks,
                  keepSeparator);  
	}

	@Autowired
	VectorStore vectorStore;
}
