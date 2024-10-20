package org.user.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.user.app.entity.Bus;
import org.user.app.entity.Passenger;
import org.user.app.entity.Seat;
import org.user.app.entity.SeatAvailability;
import org.user.app.entity.Ticket;
import org.user.app.entity.User;
import org.user.app.exceptions.ResourceNotFoundException;
import org.user.app.exceptions.SeatAlreadyBookedException;
import org.user.app.repository.BusRepository;
import org.user.app.repository.PassengerRepository;
import org.user.app.repository.SeatAvailabilityRepository;
import org.user.app.repository.TicketRepository;
import org.user.app.repository.UserRepository;
import org.user.app.service.TicketService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

class TicketServiceTest {

    @InjectMocks
    private TicketService ticketService;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private SeatAvailabilityRepository seatAvailabilityRepository;

    @Mock
    private BusRepository busRepository;

    private User user;
    private Bus bus;
    private List<Passenger> passengers;
    private Set<Seat> seats;
    private Seat seat1; // Declare seat1 as a class-level variable
    private LocalDate journeyDate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setTickets(new HashSet<>());

        bus = new Bus();
        bus.setId(1L);

        passengers = new ArrayList<>();
        Passenger passenger1 = new Passenger(null, "John Doe", 24, "male", null);
        passengers.add(passenger1);

        // Initialize seat1 and add it to the set of seats
        seat1 = new Seat(1L, 1, 100, bus, null);
        seats = new HashSet<>();
        seats.add(seat1);

        journeyDate = LocalDate.of(2024, 10, 20);
    }

    @Test
    void givenValidInputs_whenBookTicket_thenReturnTicket() {
        // Given
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
        when(seatAvailabilityRepository.findBySeatAndAvailabilityDate(seat1, journeyDate))
                .thenReturn(Optional.of(new SeatAvailability(null, true, null, journeyDate)));
        
        // When
        Ticket ticket = ticketService.bookTicket(user, bus, 100, passengers, seats, journeyDate);

        // Then
        assertNotNull(ticket);
        assertEquals("booked", ticket.getStatus());
        assertEquals(1, ticket.getPassengers().size());
        assertTrue(ticket.getSeats().contains(seat1));
    }

    @Test
    void givenSeatAvailabilityNotFound_whenBookTicket_thenThrowResourceNotFoundException() {
        // Given
        when(seatAvailabilityRepository.findBySeatAndAvailabilityDate(seat1, journeyDate))
                .thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                ticketService.bookTicket(user, bus, 100, passengers, seats, journeyDate));
        assertEquals("Seat availability not found for seat: 1", exception.getMessage());
    }

    @Test
    void givenSeatAlreadyBooked_whenBookTicket_thenThrowSeatAlreadyBookedException() {
        // Given
        when(seatAvailabilityRepository.findBySeatAndAvailabilityDate(seat1, journeyDate))
                .thenReturn(Optional.of(new SeatAvailability(null, false, null, journeyDate))); // Seat is already booked

        // When & Then
        SeatAlreadyBookedException exception = assertThrows(SeatAlreadyBookedException.class, () ->
                ticketService.bookTicket(user, bus, 100, passengers, seats, journeyDate));
        assertEquals("Seat 1 is already booked for this date.", exception.getMessage());
    }
}