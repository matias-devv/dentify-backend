package com.floss.odontologia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OdontologiaApplication {

	public static void main(String[] args) {
		SpringApplication.run(OdontologiaApplication.class, args);
	}

}
