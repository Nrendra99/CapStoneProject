package org.user.app.entity;

import java.util.HashSet;
import java.util.Set;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
public class Seat {

    // Primary key with sequence generator
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Seat_seq")
    @SequenceGenerator(name = "Seat_seq", sequenceName = "Seat_sequence", allocationSize = 1)
    private Long id; // Unique identifier for the seat

    // Seat number
    private int number; // The seat number in the bus

    // Price of the seat
    private int price; // The price of the seat

    // Each seat belongs to a bus
    @ManyToOne
    @JoinColumn(name = "bus_id", nullable = false) // Foreign key referencing the Bus
    private Bus bus; // The bus to which this seat belongs

    // Track availability per schedule for this seat
    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL)
    private Set<SeatAvailability> seatAvailabilities = new HashSet<>(); // Availability records for the seat
}