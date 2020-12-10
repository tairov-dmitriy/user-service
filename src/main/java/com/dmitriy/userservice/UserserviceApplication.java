package com.dmitriy.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class UserserviceApplication {

	@PostConstruct
	void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Etc/UTC"));
	}

	public static void main(String[] args) {
		SpringApplication.run(UserserviceApplication.class, args);
	}

}
