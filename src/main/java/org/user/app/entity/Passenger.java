package org.user.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Passenger {

    // Primary key with sequence generator
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Passenger_seq")
    @SequenceGenerator(name = "Passenger_seq", sequenceName = "Passenger_sequence", allocationSize = 1)
    private Long id; // Unique identifier for the passenger

    // Name of the passenger (mandatory field)
    @NotBlank(message = "Name is mandatory")
    @Column(name = "name")
    private String name; // The name of the passenger

    // Age of the passenger (must be between 1 and 125)
    @Min(value = 1, message = "Age must be at least 1")
    @Max(value = 125, message = "Age must be less than or equal to 125")
    @Column(name = "age")
    private int age; // The age of the passenger

    // Gender of the passenger (mandatory field)
    @NotBlank(message = "Please specify gender")
    @Column(name = "gender")
    private String gender; // The gender of the passenger

    // Many passengers can be associated with one ticket
    @ManyToOne
    @JoinColumn(name = "ticket_id") // Reference to Ticket
    private Ticket ticket; // The ticket associated with this passenger
}
