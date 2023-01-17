package com.algamoney.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.algamoney.api.config.properties.AlgamoneyApiProperty;

@SpringBootApplication
@EnableConfigurationProperties(AlgamoneyApiProperty.class)
public class AlgamoneyApi1Application {

	public static void main(String[] args) {
		SpringApplication.run(AlgamoneyApi1Application.class, args);
	}

}
