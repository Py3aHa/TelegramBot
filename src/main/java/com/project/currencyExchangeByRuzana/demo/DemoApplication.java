package com.project.currencyExchangeByRuzana.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.ApiContextInitializer;

@EnableScheduling
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		ApiContextInitializer.init();
		SpringApplication.run(DemoApplication.class, args);
	}

}
