package com.cbs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;

@SpringBootApplication 
public class CbsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CbsApplication.class, args);
    }
    
    @Autowired
    private Environment env;
    
    @PostConstruct
    public void init() {
        System.out.println("=== Environment Check ===");
        System.out.println("Database URL: " + env.getProperty("spring.datasource.url"));
        System.out.println("Database Username: " + env.getProperty("spring.datasource.username"));
        System.out.println("========================");
    }
}