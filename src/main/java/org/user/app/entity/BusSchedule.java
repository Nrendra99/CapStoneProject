package org.user.app.entity;

import java.time.Duration;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusSchedule {

    // Primary key with sequence generator
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Schedule_seq")
    @SequenceGenerator(name = "Schedule_seq", sequenceName = "Schedule_sequence", allocationSize = 1)
    private Long id; // Unique identifier for the bus schedule

    // One-to-many relationship with buses
    @OneToMany(mappedBy = "busSchedule")
    private Set<Bus> buses = new HashSet<>(); // Set of buses associated with this schedule

    // Starting city of the bus schedule
    @NotBlank(message = "Bus Schedule Should contain Starting city")
    @Column(name = "starting_city")
    private String startingCity; // The starting city for the bus route

    // Start time of the bus schedule
    @NotNull(message = "Bus Schedule Should contain Start time")
    @Column(name = "start_time")
    private LocalTime startTime; // The time when the bus schedule starts

    // Destination of the bus schedule
    @NotBlank(message = "Bus Schedule Should contain destination")
    @Column(name = "destination")
    private String destination; // The destination city for the bus route

    // End time of the bus schedule
    @NotNull(message = "Bus Schedule Should contain End time")
    @Column(name = "end_time")
    private LocalTime endTime; // The time when the bus schedule ends

    // Transient field for journey time calculation
    @Transient
    private Duration journeyTime; // Duration of the journey

    // Method to get formatted journey time as a string
    public String getFormattedJourneyTime() {
        if (startTime != null && endTime != null) {
            Duration journeyTime;

            // If end time is less than start time, it means it crosses midnight
            if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
                // Add a day (24 hours) to the end time
                journeyTime = Duration.between(startTime, endTime.plusHours(24));
            } else {
                journeyTime = Duration.between(startTime, endTime);
            }

            long hours = journeyTime.toHours(); // Get hours
            long minutes = journeyTime.toMinutes() % 60; // Get remaining minutes
            return String.format("%d hours, %d minutes", hours, minutes); // Format as desired
        }
        return "N/A"; // Return "N/A" if start or end time is not available
    }
}