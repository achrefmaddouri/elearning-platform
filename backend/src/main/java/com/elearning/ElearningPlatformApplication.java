package com.elearning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ElearningPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElearningPlatformApplication.class, args);
	}

}
