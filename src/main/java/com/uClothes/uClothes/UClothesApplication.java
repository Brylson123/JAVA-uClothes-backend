package com.uClothes.uClothes;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class UClothesApplication {

    public static void main(String[] args) {
        String profile = System.getenv("SPRING_PROFILES_ACTIVE");

        if (profile == null || profile.equals("local")) {
            System.out.println("Running in local mode: loading .env file.");
            try {
                Dotenv dotenv = Dotenv.load();
                System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
                System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
                System.setProperty("DB_URL", dotenv.get("DB_URL"));
                System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
                System.setProperty("ADMIN_PASSWORD", dotenv.get("ADMIN_PASSWORD"));
                System.setProperty("ADMIN_EMAIL", dotenv.get("ADMIN_EMAIL"));
                System.setProperty("EMAIL_USERNAME", dotenv.get("EMAIL_USERNAME"));
                System.setProperty("EMAIL_PASSWORD", dotenv.get("EMAIL_PASSWORD"));
                System.setProperty("STRIPE_API_SECRET", dotenv.get("STRIPE_API_SECRET"));
                System.setProperty("STRIPE_WEBHOOK_SECRET", dotenv.get("STRIPE_WEBHOOK_SECRET"));
                System.setProperty("WEB_URL", dotenv.get("WEB_URL"));
                System.setProperty("SPRING_PROFILES_ACTIVE", dotenv.get("SPRING_PROFILES_ACTIVE", "local"));
            } catch (Exception e) {
                System.err.println("Failed to load .env file for local development. " +
                        "Ensure the .env file exists and is properly configured.");
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            System.out.println("Running in production mode: environment variables are used.");
        }
        SpringApplication.run(UClothesApplication.class, args);
    }
}
