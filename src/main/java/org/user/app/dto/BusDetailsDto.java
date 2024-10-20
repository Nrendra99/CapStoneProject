package org.user.app.dto;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
/**
 * DTO class for transferring bus details, including bus ID, bus number, and associated seat information.
 */
public class BusDetailsDto {
    private Long busId;
    private String busNumber; 
    private List<SeatDto> seats;

}