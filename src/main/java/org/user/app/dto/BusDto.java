package org.user.app.dto;


import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
/**
 * DTO class for transferring bus information, including the bus number, driver's name and phone, seat count, and price.
 */
public class BusDto {
    @NotBlank(message = "Number is mandatory")
    @Column(name = "number")
    private String number;

    @NotBlank(message = "Name is mandatory")
    @Column(name = "name")
    private String driverName;

    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    @Column(name = "driver_phone")
    private String driverPhone;

    private int seatCount;

    private int price;


}