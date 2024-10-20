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
public class BusAvailability {

    // Primary key with sequence generator
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Availability_seq")
    @SequenceGenerator(name = "Availability_seq", sequenceName = "Availability_sequence", allocationSize = 1)
    private Long id; // Unique identifier for the availability record

    // Many-to-one relationship with the bus
    @ManyToOne
    @JoinColumn(name = "bus_id")
    private Bus bus; // The bus associated with this availability record

    // Date for which the availability is being recorded
    @Column(name = "availability_date")
    private LocalDate availabilityDate; // The date of availability

    // Flag indicating whether the bus is available on the given date
    private boolean isAvailable; // Availability status of the bus on the specified date

}
