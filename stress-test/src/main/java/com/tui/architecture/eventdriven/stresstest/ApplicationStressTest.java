package com.tui.architecture.eventdriven.stresstest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 *
 */
@SpringBootApplication
@EnableFeignClients
public class ApplicationStressTest {

	public static void main(String[] args) {
		SpringApplication.run(ApplicationStressTest.class, args);
	}

}
