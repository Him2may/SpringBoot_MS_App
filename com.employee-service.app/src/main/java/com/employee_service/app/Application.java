package com.employee_service.app;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.employee_service.app.serivce")
public class Application {

	private final Environment environment;

	public Application(Environment environment) {
		this.environment = environment;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@PostConstruct
	public void init(){
		System.out.println("Active profile: " +
				Arrays.toString(environment.getActiveProfiles()));}
}
