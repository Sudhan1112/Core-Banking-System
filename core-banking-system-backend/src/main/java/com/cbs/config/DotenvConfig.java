package com.cbs.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class DotenvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .systemProperties()
                    .directory("./")
                    .load();
            
            if (dotenv != null) {
                Map<String, Object> envMap = new HashMap<>();
                dotenv.entries().forEach(entry -> 
                    envMap.put(entry.getKey(), entry.getValue())
                );
                
                if (!envMap.isEmpty()) {
                    environment.getPropertySources()
                            .addFirst(new MapPropertySource("dotenvProperties", envMap));
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to load .env file: " + e.getMessage());
            System.out.println("Using system environment variables instead");
        }
    }
}