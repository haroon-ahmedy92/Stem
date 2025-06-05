package com.stemapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableJpaRepositories("com.stemapplication.Repository") // Explicitly scan your repository package
@EnableScheduling
public class StemapplicationApplication {

	public static void main(String[] args) {
		SpringApplication.run(StemapplicationApplication.class, args);
	}
}