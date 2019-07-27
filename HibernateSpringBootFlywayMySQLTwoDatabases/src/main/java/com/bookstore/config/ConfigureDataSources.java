package com.bookstore.config;

import com.bookstore.flyway.propeties.FlywayAuthorProperties;
import com.bookstore.flyway.propeties.FlywayBookProperties;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

@Configuration
public class ConfigureDataSources {

    // first database, booksdb
    @Primary
    @Bean(name = "configBooksDb")
    @ConfigurationProperties("app.datasource.ds1")
    public DataSourceProperties firstDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "configFlywayBooksDb")
    public FlywayBookProperties firstFlywayProperties() {
        return new FlywayBookProperties();
    }

    @Primary
    @Bean(name = "dataSourceBooksDb")
    @ConfigurationProperties("app.datasource.ds1")
    public HikariDataSource firstDataSource(@Qualifier("configBooksDb") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().type(HikariDataSource.class)
                .build();
    }

    @Primary
    @FlywayDataSource
    @Bean(name = "flywayBooksDb", initMethod = "migrate")
    @DependsOn("dataSourceBooksDb")
    public Flyway firstFlyway(@Qualifier("configFlywayBooksDb") FlywayBookProperties properties) {
        return Flyway.configure()
                .dataSource(properties.getUrl(), properties.getUser(), properties.getPassword())
                .schemas(properties.getSchema())
                .locations(properties.getLocation())
                .load();
    }

    // second database, authorsdb
    @Bean(name = "configAuthorsDb")
    @ConfigurationProperties("app.datasource.ds2")
    public DataSourceProperties secondDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "configFlywayAuthorsDb")
    public FlywayAuthorProperties secondFlywayProperties() {
        return new FlywayAuthorProperties();
    }

    @Bean(name = "dataSourceAuthorsDb")
    @ConfigurationProperties("app.datasource.ds2")
    public HikariDataSource secondDataSource(@Qualifier("configAuthorsDb") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().type(HikariDataSource.class)
                .build();
    }

    @FlywayDataSource
    @Bean(name = "flywayAuthorsDb", initMethod = "migrate")
    @DependsOn("dataSourceAuthorsDb")
    public Flyway secondFlyway(@Qualifier("configFlywayAuthorsDb") FlywayAuthorProperties properties) {
        return Flyway.configure()
                .dataSource(properties.getUrl(), properties.getUser(), properties.getPassword())
                .schemas(properties.getSchema())
                .locations(properties.getLocation())
                .load();
    }
}
