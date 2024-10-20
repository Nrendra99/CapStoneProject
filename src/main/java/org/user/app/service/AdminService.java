package org.user.app.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.user.app.entity.Admin;
import org.user.app.repository.AdminRepository;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository; // Repository for Admin entity

    @Autowired
    private PasswordEncoder passwordEncoder; // Password encoder for hashing passwords

    // Method to create a new admin with an encrypted password
    public Admin createAdmin(Admin admin) {
        String encryptedPassword = passwordEncoder.encode(admin.getPassword()); // Encrypt the password
        admin.setPassword(encryptedPassword); // Set the encrypted password
        return adminRepository.save(admin); // Save the admin to the repository
    }

    // Method to find an admin by email, returns an Optional
    public Optional<Admin> findByEmail(String email) {
        return adminRepository.findByEmail(email); // Ensure this method returns Optional<Admin>
    }
}