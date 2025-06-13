package com.example.emailMicroService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(exclude = {
		org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class
})
public class EmailMicroServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmailMicroServiceApplication.class, args);
	}

}
