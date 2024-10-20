package org.user.app.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.user.app.entity.Bus;
import org.user.app.entity.Passenger;
import org.user.app.entity.Seat;
import org.user.app.entity.SeatAvailability;
import org.user.app.entity.Ticket;
import org.user.app.entity.User;
import org.user.app.exceptions.ResourceNotFoundException;
import org.user.app.exceptions.SeatAlreadyBookedException;
import org.user.app.repository.PassengerRepository;
import org.user.app.repository.SeatAvailabilityRepository;
import org.user.app.repository.TicketRepository;


import jakarta.transaction.Transactional;

@Service
public class TicketService {

	@Autowired
	private TicketRepository ticketRepository;
	

	@Autowired
	private PassengerRepository passengerRepository;
	
	
    @Autowired
    private SeatAvailabilityRepository seatAvailabilityRepository;
    
    @Transactional
    public Ticket bookTicket(User user, Bus bus, int price, 
                             List<Passenger> passengers, Set<Seat> seats, 
                             LocalDate journeyDate) {

        // Create a new Ticket instance
        Ticket ticket = new Ticket();
        ticket.setUser(user);
        user.getTickets().add(ticket);
        ticket.setBus(bus); 
        ticket.setJourneyDate(journeyDate);
        ticket.setStatus("booked");
        ticket.setPrice(price);

        // Save the ticket first to ensure it's persisted
        ticketRepository.save(ticket);

        // Add passengers to the ticket
        List<Passenger> savedPassengers = new ArrayList<>();
        for (Passenger passenger : passengers) {
            passengerRepository.save(passenger); // Save the passenger before adding to the ticket
            passenger.setTicket(ticket); // Set the ticket in each passenger after saving
            savedPassengers.add(passenger); // Add the passenger to a temporary list
        }

        // Now set the list of saved passengers to the ticket and save it again
        ticket.setPassengers(savedPassengers); // Set passengers list in the ticket
        ticketRepository.save(ticket); // Save the ticket with passengers

        // Update seat availability for the selected seats
        for (Seat seat : seats) {
            SeatAvailability seatAvailability = seatAvailabilityRepository.findBySeatAndAvailabilityDate(seat, journeyDate)
                    .orElseThrow(() -> new ResourceNotFoundException("Seat availability not found for seat: " + seat.getId()));
            if (seatAvailability.isAvailable()) {
                seatAvailability.setAvailable(false); // Mark seat as unavailable
                seatAvailabilityRepository.save(seatAvailability); // Save updated availability
                ticket.getSeats().add(seat);
            } else {
                throw new SeatAlreadyBookedException("Seat " + seat.getId() + " is already booked for this date.");
            }
        }

        // Save the final ticket with passengers and seats
        return ticketRepository.save(ticket);
    }
	
}
