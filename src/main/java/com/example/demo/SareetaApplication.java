package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableJpaRepositories("com.example.demo.model.persistence.repositories")
@EntityScan("com.example.demo.model.persistence")
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class}) //disable default security because we need to do JWT implementation
public class SareetaApplication {

	Logger logger = LoggerFactory.getLogger("splunk.logger");
	public static void main(String[] args) {
		SpringApplication.run(SareetaApplication.class, args);
	}


	@Bean
	BCryptPasswordEncoder getPasswordEncoder(){
		logger.info("This is a test event for Logback test");
		return new BCryptPasswordEncoder();
	}

}
