package com.saber.springbatchtest;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBatchTestApplication {
	public static void main(String[] args) {
		SpringApplication.run(SpringBatchTestApplication.class, args);
	}

}
