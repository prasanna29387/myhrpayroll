package com.config;

import com.util.RuleRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@ComponentScan("com")
public class ContextConfig {

    @Bean
    @Lazy
    public RuleRunner getRuleRunner() {
        return new RuleRunner();
    }

    @Bean
    public DataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/mkyongjava");
        dataSource.setUsername("admin");
        dataSource.setPassword("admin001");
        return dataSource;
    }

}
