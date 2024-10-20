package org.user.app.repository;

import java.time.LocalDate;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.user.app.entity.Bus;
import org.user.app.entity.BusAvailability;

import jakarta.transaction.Transactional;

@Repository
public interface BusAvailabilityRepository extends JpaRepository<BusAvailability, Long> {

    @Modifying // Indicates that this query modifies data
    @Transactional // Ensures the method runs within a transaction
    @Query("DELETE FROM BusAvailability ba WHERE ba.bus = :bus AND ba.availabilityDate > :effectiveDate")
    void deleteByBusAndDateAfter(Bus bus, LocalDate effectiveDate); // Deletes BusAvailability records for a specific bus after a given date

    boolean existsByBusAndAvailabilityDate(Bus bus, LocalDate availabilityDate); // Checks if a BusAvailability exists for the specified bus and date
}
