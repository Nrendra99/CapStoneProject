package org.user.app.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.user.app.dto.BookingRequest;
import org.user.app.dto.BusDetailsDto;
import org.user.app.entity.Bus;
import org.user.app.entity.Passenger;
import org.user.app.entity.Seat;
import org.user.app.entity.User;
import org.user.app.exceptions.BusNotFoundException;
import org.user.app.exceptions.NoAvailableBusesException;
import org.user.app.exceptions.ResourceNotFoundException;
import org.user.app.exceptions.ScheduleNotFoundException;
import org.user.app.exceptions.SeatAlreadyBookedException;
import org.user.app.exceptions.SeatNotFoundException;
import org.user.app.exceptions.UserNotFoundException;
import org.user.app.repository.BusRepository;
import org.user.app.repository.SeatRepository;
import org.user.app.service.BusService;
import org.user.app.service.TicketService;
import org.user.app.service.UserService;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;



@Controller
@RequestMapping("/tickets")
@Tag(name = "Ticket Management", description = "Endpoints for managing tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BusService busService;

    @GetMapping("/available")
    @Operation(summary = "Find available buses with seats")
    public String findAvailableBusesWithSeats(
            @Param(value = "Date for the journey") @RequestParam LocalDate date,
            @Param(value = "Starting city") @RequestParam String startingCity,
            @Param(value = "Destination city") @RequestParam String destination,
            Model model) {

        try {
            List<Bus> buses = busService.findAvailableBusesWithSeats(date, startingCity, destination);
            model.addAttribute("buses", buses);
        } catch (NoAvailableBusesException e) {
            model.addAttribute("ErrorMessage", e.getMessage());
        }

        return "UserHome"; // Return the same view (UserHome) with the buses or error message
    }

    @GetMapping("/details/{busId}")
    @Operation(summary = "Get bus details with availability")
    public String getBusDetails(
            @Param(value = "ID of the bus") @PathVariable Long busId,
            @Param(value = "Date for the journey") @RequestParam LocalDate date,
            Model model) {

        try {
            BusDetailsDto busDetails = busService.getBusDetailsWithAvailability(busId, date);
            model.addAttribute("busDetails", busDetails);
            model.addAttribute("busId", busId);
            model.addAttribute("selectedDate", date);
            model.addAttribute("passengers", new ArrayList<Passenger>());

            return "BookingForm"; // Return the Thymeleaf template name
        } catch (ResourceNotFoundException | BusNotFoundException | ScheduleNotFoundException e) {
            model.addAttribute("ErrorMessage", e.getMessage());
            return "BookingForm"; // Return an error page if bus or seat availability is not found
        }
    }

    @PostMapping("/book")
    @Operation(summary = "Book a ticket for a bus journey")
    public String bookTicket(
            Authentication authentication,
            @ModelAttribute BookingRequest bookingRequest,
            Model model) throws Exception {

        Long busId = bookingRequest.getBusId();
        String selectedDate = bookingRequest.getSelectedDate();
        String seatIds = bookingRequest.getSeatIds();
        List<Passenger> passengers = bookingRequest.getPassengers();

        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new BusNotFoundException("Bus not found"));

        // Convert seat IDs to Seat entities
        Set<Seat> seats = Arrays.stream(seatIds.split(","))
                .map(Long::parseLong)
                .map(seatId -> seatRepository.findById(seatId)
                        .orElseThrow(() -> new SeatNotFoundException("Seat not found: " + seatId)))
                .collect(Collectors.toSet());

        // Calculate total price from selected seats
        int totalPrice = seats.stream().mapToInt(Seat::getPrice).sum();

        try {
            // Book the ticket
            ticketService.bookTicket(user, bus, totalPrice, passengers, seats, LocalDate.parse(selectedDate));
        } catch (SeatAlreadyBookedException | BusNotFoundException | SeatNotFoundException |UserNotFoundException e) {
            model.addAttribute("ErrorMessage", e.getMessage());
            model.addAttribute("busDetails", busService.getBusDetailsWithAvailability(busId, LocalDate.parse(selectedDate)));
            model.addAttribute("busId", busId);
            model.addAttribute("selectedDate", selectedDate);
            model.addAttribute("passengers", passengers);
            return "BookingForm"; // Return to the booking form with the error message
        }

        // Re-populate model for successful booking
        model.addAttribute("busDetails", busService.getBusDetailsWithAvailability(busId, LocalDate.parse(selectedDate)));
        model.addAttribute("busId", busId);
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("passengers", passengers);

        return "redirect:/users/tickets"; // Redirect to the tickets page after booking
    }
}