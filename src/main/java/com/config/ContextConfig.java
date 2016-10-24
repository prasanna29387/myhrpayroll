package com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@ComponentScan("com")
public class ContextConfig {
    @Bean
    public JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(getDataSource());
    }


    @Bean
    @Lazy
    public DataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/dev");
        dataSource.setUsername("admin");
        dataSource.setPassword("admin001");
        return dataSource;
    }
}
