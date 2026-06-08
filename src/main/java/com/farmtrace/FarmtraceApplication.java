package com.farmtrace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FarmtraceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FarmtraceApplication.class, args);
        System.out.println("========================================");
        System.out.println("🌾 FARMATRACE BACKEND STARTED!");
        System.out.println("📍 http://localhost:8080");
        System.out.println("========================================");
    }
}