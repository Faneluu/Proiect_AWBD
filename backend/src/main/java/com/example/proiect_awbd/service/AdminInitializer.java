package com.example.proiect_awbd.service;

import com.example.proiect_awbd.entity.Authority;
import com.example.proiect_awbd.entity.User;
import com.example.proiect_awbd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.email}")
    private String adminEmail;

    @Autowired
    public AdminInitializer(UserRepository userRepository,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        System.out.println("Initializing admin user...");

        if (userRepository.findById(adminUsername).isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername(adminUsername);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setEmail(adminEmail);
            adminUser.setEnabled(true);
            adminUser.setUserSecret(UUID.randomUUID().toString());
            adminUser.setTotalSpace(1024f);
            adminUser.setUsedSpace(0f);
            adminUser.setUserBucketId(UUID.randomUUID().toString());

            Authority adminAuthority = new Authority();
            adminAuthority.setAuthority("ADMIN");
            adminAuthority.setUser(adminUser);
            adminUser.getAuthorities().add(adminAuthority);

            userRepository.save(adminUser);
            System.out.println("Admin user created successfully.");
        } else {
            System.out.println("Admin user already exists.");
        }
    }
}
