package org.user.app.service;


import java.time.LocalTime;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.user.app.entity.BusSchedule;
import org.user.app.exceptions.ScheduleNotFoundException;
import org.user.app.repository.BusScheduleRepository;


import jakarta.transaction.Transactional;

@Service
public class BusScheduleService {

    @Autowired
    private BusScheduleRepository busScheduleRepository;

    @Transactional
    public BusSchedule createNewSchedule(String startingCity, LocalTime startTime, String destination, LocalTime endTime) {
        // Create and save a new bus schedule
        BusSchedule busSchedule = new BusSchedule();
        busSchedule.setStartingCity(startingCity);
        busSchedule.setStartTime(startTime);
        busSchedule.setDestination(destination);
        busSchedule.setEndTime(endTime);
        return busScheduleRepository.save(busSchedule);
    }

    @Transactional
    public BusSchedule getBusScheduleById(Long id) {
        // Retrieve a bus schedule by ID
        return busScheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleNotFoundException("Bus Schedule not found with id: " + id));
    }

    @Transactional
    public BusSchedule updateBusSchedule(Long id, BusSchedule updatedSchedule) {
        // Update an existing bus schedule
        BusSchedule existingSchedule = busScheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleNotFoundException("Bus Schedule not found with id: " + id));
        existingSchedule.setStartingCity(updatedSchedule.getStartingCity());
        existingSchedule.setStartTime(updatedSchedule.getStartTime());
        existingSchedule.setDestination(updatedSchedule.getDestination());
        existingSchedule.setEndTime(updatedSchedule.getEndTime());
        return busScheduleRepository.save(existingSchedule);
    }

    public List<BusSchedule> getAllBusSchedules() {
        // Retrieve all bus schedules
        return busScheduleRepository.findAll();
    }

}