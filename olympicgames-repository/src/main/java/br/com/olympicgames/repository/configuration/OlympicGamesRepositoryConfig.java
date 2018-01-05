package br.com.olympicgames.repository.configuration;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = { "br.com.olympicgames.repository" })
@EnableTransactionManagement
public class OlympicGamesRepositoryConfig {

    @Bean
    public DataSource dataSource() {

	EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
	builder.setName("data/olympycgames");
	builder.setType(EmbeddedDatabaseType.HSQL);
	return builder.build();
    }

    @Bean
    public JdbcTemplate getJdbcTemplate() {
	return new JdbcTemplate(dataSource());
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {

	HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
	vendorAdapter.setGenerateDdl(false);

	LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
	factory.setJpaVendorAdapter(vendorAdapter);
	factory.setPackagesToScan("br.com.olympicgames.repository");
	factory.setDataSource(dataSource());
	factory.afterPropertiesSet();

	Properties properties = new Properties();
	properties.setProperty("spring.jpa.hibernate.ddl-auto", "none");
	factory.setJpaProperties(properties);

	return factory.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {

	JpaTransactionManager txManager = new JpaTransactionManager();
	txManager.setEntityManagerFactory(entityManagerFactory());
	return txManager;
    }
}
