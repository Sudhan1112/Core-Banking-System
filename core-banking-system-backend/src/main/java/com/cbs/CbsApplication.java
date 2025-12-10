package com.cbs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CbsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CbsApplication.class, args);
    }

    @Autowired
    private Environment env;

    @PostConstruct
    public void init() {
        System.out.println("=== Environment Check ===");
        String url = null;
        String user = null;
        // Prefer process environment so we avoid Spring placeholder resolution
        // exceptions
        try {
            url = System.getenv("SPRING_DATASOURCE_URL");
            user = System.getenv("SPRING_DATASOURCE_USERNAME");
        } catch (Exception e) {
            // ignore
        }

        // Fallback to Spring Environment safely (catch any placeholder resolution
        // problems)
        if (url == null) {
            try {
                url = env.getProperty("spring.datasource.url");
            } catch (Exception e) {
                url = null;
            }
        }
        if (user == null) {
            try {
                user = env.getProperty("spring.datasource.username");
            } catch (Exception e) {
                user = null;
            }
        }

        System.out.println("Database URL: " + (url != null ? url : "(not set)"));
        System.out.println("Database Username: " + (user != null ? user : "(not set)"));
        System.out.println("========================");
    }
}