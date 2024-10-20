package org.user.app.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
	 
    // Unique identifier for each user
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "User_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "User_sequence", allocationSize = 1)
    private Long Id;  // User's unique ID

    // User's first name (mandatory)
    @NotBlank(message = "Name is mandatory")
    @Column(name = "first_name")
    private String firstName;  // User's first name
    
    // User's last name (optional)
    @Column(name = "last_name")
    private String lastName;  // User's last name
    
    // User's age with validation
    @Min(value = 1, message = "Age must be at least 1")
    @Max(value = 125, message = "Age must be less than or equal to 125")
    @Column(name = "age")
    private int age;  // User's age
    
    // User's gender (mandatory)
    @NotBlank(message = "Please specify gender")
    @Column(name = "gender")
    private String gender;  // User's gender
    
    // User's email address (mandatory and unique)
    @NotBlank(message = "Email is mandatory")
    @Column(name = "email", unique = true)
    @Email(message = "Incorrect email format")
    private String email;  // User's email address
    
    // User's phone number with validation
    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    private String phoneNo;  // User's phone number

    // User's password with various validation rules
    @Column(nullable = false)
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters long")
    @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter")
    @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter")
    @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit")
    @Pattern(regexp = ".*[!@#$%^&*(),.?\":{}|<>].*", message = "Password must contain at least one special character")
    @Pattern(regexp = "\\S+", message = "Password cannot contain spaces")
    private String password;  // User's password with validation rules
        
    // Tickets associated with the user
    @OneToMany
    private Set<Ticket> tickets = new HashSet<>();  // User's tickets
    
    // Role fixed as "USER"
    final String role = "USER";  // User's role
}