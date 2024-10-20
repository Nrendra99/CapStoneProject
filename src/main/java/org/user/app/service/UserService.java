package org.user.app.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.user.app.entity.Passenger;
import org.user.app.entity.Ticket;
import org.user.app.entity.User;
import org.user.app.exceptions.ResourceNotFoundException;
import org.user.app.exceptions.UserNotFoundException;
import org.user.app.repository.TicketRepository;
import org.user.app.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository; // Repository for User entity
    
    @Autowired
    private PasswordEncoder passwordEncoder; // Password encoder for hashing passwords
    
    @Autowired
    private TicketRepository ticketRepository; // Repository for Ticket entity

    // Create a new user
    public User createUser(User user) {
        String encryptedPassword = passwordEncoder.encode(user.getPassword()); // Encrypt the password
        user.setPassword(encryptedPassword); // Set the encrypted password
        return userRepository.save(user); // Save the user to the repository
    }

    // Retrieve a user by ID
    public User getUserById(Long userId) throws UserNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }

    // Retrieve a user by email (optional method)
    public User getUserByEmail(String email) throws Exception {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    // Update an existing user
    public User updateUser(Long userId, User updatedUser)  {
        User existingUser = getUserById(userId); // Get existing user

        // Update user details
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setAge(updatedUser.getAge());
        existingUser.setGender(updatedUser.getGender());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhoneNo(updatedUser.getPhoneNo());

        // Optionally, update password only if needed
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            String encryptedPassword = passwordEncoder.encode(updatedUser.getPassword()); // Encrypt new password
            existingUser.setPassword(encryptedPassword); // Set the new encrypted password
        }

        return userRepository.save(existingUser); // Save the updated user
    }

    // Delete a user by ID
    @Transactional
    public void deleteUser(Long userId) throws UserNotFoundException {
        User user = getUserById(userId); // Retrieve user
        userRepository.delete(user); // Delete the user
    }

    // Get all users (optional method for listing users)
    public List<User> getAllUsers() {
        return userRepository.findAll(); // Return all users
    }
    
    // View tickets associated with the user
    public Set<Ticket> viewTickets(User user) {
        return user.getTickets(); // Return user's tickets
    }
    
    // Retrieve a ticket by ID
    public Ticket getTicketById(Long ticketId) {
        return ticketRepository.findById(ticketId)
                 .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with Id: " + ticketId)); // Retrieve ticket
    }
    
    // View passengers associated with a ticket
    public List<Passenger> viewPassengers(Long ticketId) {
        Ticket ticket = getTicketById(ticketId); // Get the ticket
        return ticket.getPassengers(); // Return passengers of the ticket
    }
}