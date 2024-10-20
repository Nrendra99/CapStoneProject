package org.user.app.entity;


import java.util.HashSet;
import java.util.Set;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bus {

    // Primary key with sequence generator
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Bus_seq")
    @SequenceGenerator(name = "Bus_seq", sequenceName = "Bus_sequence", allocationSize = 1)
    private Long Id; // Unique identifier for the bus

    // Mandatory field for bus number
    @NotBlank(message = "Number is mandatory")
    @Column(name = "number")
    private String number; // The bus's identification number

    // Mandatory field for the driver's name
    @NotBlank(message = "Name is mandatory")
    @Column(name = "name")
    private String driverName; // The name of the bus driver

    // Phone number for the driver, must be exactly 10 digits
    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    @Column(name = "driver_phone")
    private String driverPhone; // Driver's contact number

    // Set of seats associated with the bus
    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Seat> seats = new HashSet<>(); // Collection of seats in the bus

    // Many-to-one relationship with bus schedule
    @ManyToOne
    @JoinColumn(name = "bus_schedule_id") // Many buses can share one schedule
    private BusSchedule busSchedule; // The schedule associated with this bus

    // Set of bus availabilities
    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL)
    private Set<BusAvailability> availabilities = new HashSet<>(); // Collection of availability records for the bus

}