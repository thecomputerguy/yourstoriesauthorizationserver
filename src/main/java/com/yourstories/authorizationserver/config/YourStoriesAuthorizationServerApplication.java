package com.yourstories.authorizationserver.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.context.request.RequestContextListener;

@ComponentScan(basePackages={"com.yourstories"})
@EnableMongoRepositories(basePackages={"com.yourstories.repositories"})
@SpringBootApplication
public class YourStoriesAuthorizationServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(YourStoriesAuthorizationServerApplication.class, args);
	}

	@Bean
	public RequestContextListener requestContextListener() {
		return new RequestContextListener();
	}
}
