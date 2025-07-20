package com.devgrowth.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DevGrowthApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevGrowthApplication.class, args);
	}

}
