package com.uClothes.uClothes.bootstrap;

import com.uClothes.uClothes.domain.User;
import com.uClothes.uClothes.domain.UserRole;
import com.uClothes.uClothes.repositories.UserRepository;
import com.uClothes.uClothes.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {
    private final UserService userService;

    private final UserRepository userRepository;

    @Value("${ADMIN_EMAIL}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    public AdminInitializer(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public void run(String... args) {
        if (this.userRepository.findByEmail(this.adminEmail) == null) {
            User admin = new User();
            admin.setPassword(this.adminPassword);
            admin.setEmail(this.adminEmail);
            admin.setRole(UserRole.ADMIN);
            this.userService.registerUser(admin);
            System.out.println("Admin user created.");
        } else {
            System.out.println("Admin user is already registered");
        }
    }
}
