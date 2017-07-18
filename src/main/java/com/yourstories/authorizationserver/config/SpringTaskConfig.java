package com.yourstories.authorizationserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ComponentScan({ "com.yourstories.authorizationserver.task" })
public class SpringTaskConfig {

}
