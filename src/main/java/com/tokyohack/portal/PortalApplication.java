package com.tokyohack.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@SpringBootApplication
@RestController
public class PortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortalApplication.class, args);
    }

    @GetMapping("/api/health")
    public Map<String, String> healthCheck() {
        return Map.of(
            "status", "UP",
            "message", "Tokyo Hack Group Portal API is running smoothly!",
            "javaVersion", System.getProperty("java.version")
        );
    }
}
