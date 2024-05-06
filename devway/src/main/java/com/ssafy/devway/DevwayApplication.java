package com.ssafy.devway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DevwayApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevwayApplication.class, args);
	}

}
