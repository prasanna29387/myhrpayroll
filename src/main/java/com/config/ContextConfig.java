package com.config;

import com.util.RuleRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@ComponentScan("com")
public class ContextConfig {

    @Bean
    @Lazy
    public RuleRunner getRuleRunner() {
        return new RuleRunner();
    }

}
