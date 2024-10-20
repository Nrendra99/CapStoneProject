package org.user.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
/**
 * Data Transfer Object representing seat details, including availability and price.
 */
public class SeatDto {
    private Long seatId; 
    private int seatNumber;
    private int price; 
    private boolean isAvailable; 


}
