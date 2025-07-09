package org.aero.ingestion.config;


import  org.aero.ingestion.constants.AppConstants;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
	basePackages = { 
		"org.aero.ingestion.repository"},
	entityManagerFactoryRef = 
		"entityManagerFactory", 
	transactionManagerRef = 
		"appTransactionManager")
public class AppDatasourceConfiguration {

	@Autowired
	private Environment env;

	@Bean
	@Primary
	@ConfigurationProperties("app.datasource.ragbackend")
	DataSourceProperties appDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	@Primary
	DataSource appDataSource() {
		return appDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}

	@Primary
	@Bean(name = "entityManagerFactory")
	LocalContainerEntityManagerFactoryBean entityManagerFactory(final EntityManagerFactoryBuilder builder) {
		final Map<String, Object> properties = new HashMap<>();
		properties.put("hibernate.physical_naming_strategy",
				"org.aero.ingestion.util.ImprovedNamingStrategy");
		properties.put("provider", "org.hibernate.jpa.HibernatePersistenceProvider");
		properties.put("hibernate.id.new_generator_mappings", "false");
		properties.put("hibernate.id.db_structure_naming_strategy","single");
		properties.put("hibernate.hbm2ddl.auto", env.getProperty("app.datasource.ingestion.hibernate.hbm2ddl.auto"));
	 
 		return builder.dataSource(appDataSource())
				.packages("org.aero.ingestion.entity",  "org.aero.common.person.domain").properties(properties)
				.build();
	}

	@SuppressWarnings("null")
	@Primary
	@Bean
	PlatformTransactionManager appTransactionManager(
			final @Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean appEntityManagerFactory) {
		return new JpaTransactionManager(appEntityManagerFactory.getObject());
	}

	@Bean
	@DependsOnDatabaseInitialization
	DataSourceInitializer securityDataSourceInitializer() {
		final DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
		dataSourceInitializer.setDataSource(appDataSource());
		final ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
		final String [] files = env.getProperty("spring.datasource.data", AppConstants.EMPTY_STRING).split(AppConstants.COMMA_DELIMITER);
		
		for(String file: files) {
			databasePopulator.addScript(new ClassPathResource(file.trim()));
		}
		dataSourceInitializer.setDatabasePopulator(databasePopulator);
		dataSourceInitializer.setEnabled(env.getProperty("app.datasource.ingestion.initialize", Boolean.class, false));
		return dataSourceInitializer;
	}
}
