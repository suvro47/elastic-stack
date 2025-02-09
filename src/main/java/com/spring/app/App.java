package com.spring.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class App {

	public static void main(String[] args) {

//		ElasticApmAttacher.attach();
		SpringApplication.run(App.class, args);

		log.info("******************************************************");
		log.info("-----------SPRING BOOT APPLICATION RUNNING------------");
		log.info("******************************************************");

	}
}
