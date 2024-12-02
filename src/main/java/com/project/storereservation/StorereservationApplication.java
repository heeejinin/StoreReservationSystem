package com.project.storereservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class StorereservationApplication {

	public static void main(String[] args) {
		SpringApplication.run(StorereservationApplication.class, args);
	}

}
