package com.spotify__server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SpringBootJdbcApplication {
	public static void main(String[] args) {
            SpringApplication.run(SpringBootJdbcApplication.class, args);
	}
}