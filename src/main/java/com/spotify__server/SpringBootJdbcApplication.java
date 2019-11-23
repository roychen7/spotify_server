package com.spotify__server;

import com.spotify__server.components.ThreadExecutor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableCaching
public class SpringBootJdbcApplication {
	public static void main(String[] args) {
            SpringApplication.run(SpringBootJdbcApplication.class, args);
	}
}