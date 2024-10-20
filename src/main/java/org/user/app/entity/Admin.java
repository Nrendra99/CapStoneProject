package org.user.app.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Admin {

    // Primary key with sequence generator
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "admin_seq")
    @SequenceGenerator(name = "admin_seq", sequenceName = "admin_sequence", allocationSize = 1)
    private Long id; // Unique identifier for the admin

    // Email field, must be unique and not blank with validation for proper format
    @NotBlank(message = "Email is mandatory")
    @Column(name = "email", unique = true)
    @Email(message = "Incorrect email format")
    private String email; // Admin's unique email address

    // Password field, cannot be null
    @Column(nullable = false)
    private String password; // Admin's password for authentication

    // Role is set to "ADMIN" and final (constant)
    final String role = "ADMIN"; // The role assigned to the admin, constant value
}