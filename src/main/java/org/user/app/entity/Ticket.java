package org.user.app.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    // Primary key with sequence generator
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Ticket_seq")
    @SequenceGenerator(name = "Ticket_seq", sequenceName = "Ticket_sequence", allocationSize = 1)
    private Long Id; // Unique identifier for the ticket

    // The user who booked the ticket
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Reference to the user who owns this ticket
    
    // The bus associated with this ticket
    @ManyToOne
    private Bus bus; // Reference to the bus for this ticket
    
    // Total price for the ticket
    private int Price; // Cost of the ticket

    // List of passengers associated with this ticket
    @OneToMany(cascade=CascadeType.ALL)
    private List<Passenger> passengers = new ArrayList<>(); // Passengers on this ticket

    // Seats booked for this ticket
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
        name = "ticket_seat",
        joinColumns = @JoinColumn(name = "ticket_id"),
        inverseJoinColumns = @JoinColumn(name = "seat_id")
    )
    private Set<Seat> seats = new HashSet<>(); // Seats booked for the journey

    // Date of the journey for this ticket
    @Column(name = "journey_date")
    private LocalDate journeyDate; // Journey date associated with this ticket

    // Status of the ticket (e.g., booked, cancelled)
    private String status; // Current status of the ticket
}
