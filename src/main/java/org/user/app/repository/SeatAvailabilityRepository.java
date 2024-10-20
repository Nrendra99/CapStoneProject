package org.user.app.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.user.app.entity.Seat;
import org.user.app.entity.SeatAvailability;

import jakarta.transaction.Transactional;

@Repository 
public interface SeatAvailabilityRepository extends JpaRepository<SeatAvailability, Long> {

    // Deletes SeatAvailability records for a specific seat where the availability date is after the effective date
    @Modifying
    @Transactional
    @Query("DELETE FROM SeatAvailability sa WHERE sa.seat = :seat AND sa.availabilityDate > :effectiveDate")
    void deleteBySeatAndDateAfter(Seat seat, LocalDate effectiveDate);

    // Retrieves an Optional SeatAvailability for a specific seat and journey date
    Optional<SeatAvailability> findBySeatAndAvailabilityDate(Seat seat, LocalDate journeyDate);
}
