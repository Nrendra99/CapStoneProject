package org.user.app.dto;

import java.util.List;

import org.user.app.entity.Passenger;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

/**
 * DTO class for booking requests containing bus ID, selected date, seat IDs, and passenger details.
 */
public class BookingRequest {
    private Long busId;
    private String selectedDate;
    private String seatIds;
    private List<Passenger> passengers;

}