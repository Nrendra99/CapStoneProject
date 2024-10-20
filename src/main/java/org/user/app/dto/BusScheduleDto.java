package org.user.app.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
/**
 * DTO class for transferring bus schedule information, including route details, start and end times, and formatted journey time.
 */
public class BusScheduleDto {
    private Long id;
    private String startingCity;
    private String destination;
    private String startTime; 
    private String endTime; 
    private String formattedJourneyTime; 
}