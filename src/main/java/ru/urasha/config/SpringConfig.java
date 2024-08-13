package ru.urasha.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:configuration.properties")
@PropertySource("classpath:messages.properties")
@ComponentScan("ru.urasha")
public class SpringConfig {
}
