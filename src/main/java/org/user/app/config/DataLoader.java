package org.user.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.user.app.entity.Admin;
import org.user.app.service.AdminService;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private AdminService adminService;

    @Override
    public void run(String... args) throws Exception {
        // Create a new admin instance with default credentials
        Admin admin = new Admin();
        admin.setEmail("admin@demo.com");
        admin.setPassword("Demo0@00");

        // Check if the admin already exists in the system to avoid duplicates
        if (!adminService.findByEmail(admin.getEmail()).isPresent()) {
            adminService.createAdmin(admin); // Create admin user if not found
            System.out.println("Admin user created: " + admin.getEmail());
        } else {
            System.out.println("Admin user already exists."); // Inform if the admin already exists
        }
    }
}