package com.stemapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EnableJpaRepositories("com.stemapplication.Repository") // Explicitly scan your repository package
public class StemapplicationApplication {

	public static void main(String[] args) {
		SpringApplication.run(StemapplicationApplication.class, args);
	}
}