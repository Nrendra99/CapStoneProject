package org.user.app.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.user.app.entity.BusSchedule;

@Repository 
public interface BusScheduleRepository extends JpaRepository<BusSchedule, Long> {

    // Retrieves a list of BusSchedules based on starting city and destination
    List<BusSchedule> findByStartingCityAndDestination(String startingCity, String destination);
}