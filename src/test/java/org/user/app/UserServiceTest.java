package org.user.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.user.app.entity.Passenger;
import org.user.app.entity.Ticket;
import org.user.app.entity.User;
import org.user.app.exceptions.UserNotFoundException;
import org.user.app.repository.TicketRepository;
import org.user.app.repository.UserRepository;
import org.user.app.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private User updatedUser;

    @BeforeEach
    public void setUp() {
        user = new User(); // Initialize user object
        updatedUser = new User(); // Initialize updated user object
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password");
    }

    @Test
    public void testCreateUser() {
        // Given
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encryptedPassword");
        when(userRepository.save(user)).thenReturn(user);

        // When
        User result = userService.createUser(user);

        // Then
        assertNotNull(result);
        assertEquals("encryptedPassword", result.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    public void testGetUserById() throws UserNotFoundException {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        User result = userService.getUserById(1L);

        // Then
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        verify(userRepository).findById(1L);
    }

    @Test
    public void testGetUserByEmail() throws Exception {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // When
        User result = userService.getUserByEmail("test@example.com");

        // Then
        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    public void testUpdateUser() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        updatedUser.setFirstName("Updated");
        updatedUser.setLastName("User");

        // When
        User result = userService.updateUser(1L, updatedUser);

        // Then
        assertEquals("Updated", result.getFirstName());
        verify(userRepository).save(user);
    }

    @Test
    public void testDeleteUser() throws UserNotFoundException {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository).delete(user);
    }

    @Test
    public void testGetAllUsers() {
        // Given
        when(userRepository.findAll()).thenReturn(List.of(user));

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertEquals(1, result.size());
        verify(userRepository).findAll();
    }

    @Test
    public void testViewTickets() {
        // Given
        Set<Ticket> tickets = Set.of(new Ticket());
        user.setTickets(tickets);

        // When
        Set<Ticket> result = userService.viewTickets(user);

        // Then
        assertEquals(tickets, result);
    }

    @Test
    public void testGetTicketById() {
        // Given
        Ticket ticket = new Ticket();
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        // When
        Ticket result = userService.getTicketById(1L);

        // Then
        assertNotNull(result);
        verify(ticketRepository).findById(1L);
    }

    @Test
    public void testViewPassengers() {
        // Given
        Ticket ticket = new Ticket();
        Passenger passenger = new Passenger();
        ticket.setPassengers(List.of(passenger));
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        // When
        List<Passenger> result = userService.viewPassengers(1L);

        // Then
        assertEquals(1, result.size());
        verify(ticketRepository).findById(1L);
    }
}