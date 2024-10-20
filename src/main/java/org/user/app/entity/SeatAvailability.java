package org.user.app.entity;


import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class SeatAvailability {

    // Primary key with sequence generator
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SeatAvailability_seq")
    @SequenceGenerator(name = "SeatAvailability_seq", sequenceName = "SeatAvailability_sequence", allocationSize = 1)
    private Long id; // Unique identifier for the seat availability record

    // Indicates if the seat is available for booking
    private boolean isAvailable; // Availability status of the seat

    // Each seat availability record is associated with a specific seat
    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = false) // Foreign key referencing the Seat
    private Seat seat; // The seat associated with this availability record

    // Date for which the seat availability is applicable
    @Column(name = "availability_date")
    private LocalDate availabilityDate; // Date of availability for the seat
}