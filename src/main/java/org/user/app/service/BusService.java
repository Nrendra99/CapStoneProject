package org.user.app.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.user.app.dto.BusDetailsDto;
import org.user.app.dto.SeatDto;
import org.user.app.entity.Bus;
import org.user.app.entity.BusAvailability;
import org.user.app.entity.BusSchedule;
import org.user.app.entity.Seat;
import org.user.app.entity.SeatAvailability;
import org.user.app.exceptions.BusNotFoundException;
import org.user.app.exceptions.NoAvailableBusesException;
import org.user.app.exceptions.ResourceNotFoundException;
import org.user.app.exceptions.ScheduleNotFoundException;
import org.user.app.exceptions.SeatNotFoundException;
import org.user.app.repository.BusAvailabilityRepository;
import org.user.app.repository.BusRepository;
import org.user.app.repository.BusScheduleRepository;
import org.user.app.repository.SeatAvailabilityRepository;
import org.user.app.repository.SeatRepository;

import jakarta.transaction.Transactional;

@Service
public class BusService {

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private BusScheduleRepository busScheduleRepository;

    @Autowired
    private BusAvailabilityRepository busAvailabilityRepository;

    @Autowired
    private SeatAvailabilityRepository seatAvailabilityRepository;

    public BusDetailsDto getBusDetailsWithAvailability(Long busId, LocalDate date) {
        // Fetch bus and associated schedule, checking for existence
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new BusNotFoundException("Bus not found"));
        BusSchedule busSchedule = bus.getBusSchedule();
        if (busSchedule == null) {
            throw new ScheduleNotFoundException("No Routes found for this bus");
        }

        // Check seat availability for the bus on the specified date
        Set<Seat> seats = bus.getSeats();
        List<SeatDto> seatDtos = new ArrayList<>();

        for (Seat seat : seats) {
            SeatAvailability seatAvailability = seatAvailabilityRepository.findBySeatAndAvailabilityDate(seat, date)
                    .orElseThrow(() -> new ResourceNotFoundException("Seat availability not found for seat ID: " + seat.getId() + " on the requested date"));
            seatDtos.add(new SeatDto(seat.getId(), seat.getNumber(), seat.getPrice(), seatAvailability.isAvailable()));
        }

        // Sort seat DTOs by seat number and return bus details
        seatDtos.sort(Comparator.comparingInt(SeatDto::getSeatNumber));
        return new BusDetailsDto(bus.getId(), bus.getNumber(), seatDtos);
    }

    public List<Bus> findAvailableBusesWithSeats(LocalDate date, String startingCity, String destination) {
        // Retrieve bus schedules matching the specified cities
        List<BusSchedule> schedules = busScheduleRepository.findByStartingCityAndDestination(startingCity, destination);
        List<Bus> availableBuses = new ArrayList<>();

        // Iterate schedules to find buses with available seats
        for (BusSchedule schedule : schedules) {
            Set<Bus> buses = schedule.getBuses();
            for (Bus bus : buses) {
                if (busAvailabilityRepository.existsByBusAndAvailabilityDate(bus, date)) {
                    // Check if any seat is available on the specified date
                    boolean hasAvailableSeats = bus.getSeats().stream()
                            .map(seat -> seatAvailabilityRepository.findBySeatAndAvailabilityDate(seat, date).orElse(null))
                            .filter(Objects::nonNull)
                            .anyMatch(SeatAvailability::isAvailable);
                    if (hasAvailableSeats) {
                        availableBuses.add(bus);
                    }
                }
            }
        }

        // Check if no available buses were found
        if (availableBuses.isEmpty()) {
            throw new NoAvailableBusesException("No available buses for the route from " + startingCity + " to " + destination + " on " + date);
        }

        return availableBuses; // Return the list of available buses
    }

    @Transactional
    public Bus createBus(String number, String driverName, String driverPhone, int seatCount, int price) {
        // Create and save a new bus entity
        Bus bus = new Bus();
        bus.setNumber(number);
        bus.setDriverName(driverName);
        bus.setDriverPhone(driverPhone);
        Bus savedBus = busRepository.save(bus);

        // Create and associate seats with the bus
        for (int i = 1; i <= seatCount; i++) {
            Seat seat = new Seat();
            seat.setNumber(i);
            seat.setPrice(price);
            seat.setBus(savedBus); // Link seat to the saved bus
            savedBus.getSeats().add(seat); // Add seat to bus's collection
        }
        return busRepository.save(savedBus); // Persist bus and seats
    }

    // Retrieve all buses
    public List<Bus> getAllBuses() {
        return busRepository.findAll(); // Fetch all buses from the database
    }

    // Update a bus by ID
    public Bus updateBus(Long id, Bus updatedBus) {
        Bus existingBus = busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with id: " + id));
        // Update bus fields
        existingBus.setNumber(updatedBus.getNumber());
        existingBus.setDriverName(updatedBus.getDriverName());
        existingBus.setDriverPhone(updatedBus.getDriverPhone());
        return busRepository.save(existingBus); // Save updated bus
    }

    @Transactional
    public void addScheduleToBus(Long busId, Long scheduleId, LocalDate startDate, LocalDate endDate) {
        // Link a bus with a schedule and generate availability
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new BusNotFoundException("Bus not found with id: " + busId));
        BusSchedule schedule = busScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("Bus Schedule not found with id: " + scheduleId));
        bus.setBusSchedule(schedule);
        busRepository.save(bus); // Save bus with schedule
        generateBusAvailability(bus, startDate, endDate);
        generateSeatAvailability(bus, startDate, endDate);
    }

    private void generateBusAvailability(Bus bus, LocalDate startDate, LocalDate endDate) {
        Set<BusAvailability> busAvailabilitySet = new HashSet<>();
        LocalDate date = startDate;
        while (!date.isAfter(endDate)) {
            BusAvailability busAvailability = new BusAvailability();
            busAvailability.setBus(bus);
            busAvailability.setAvailabilityDate(date);
            busAvailability.setAvailable(true); // Set as available
            busAvailabilitySet.add(busAvailability);
            date = date.plusDays(1); // Increment to the next day
        }
        busAvailabilityRepository.saveAll(busAvailabilitySet); // Persist availability records
    }

    private void generateSeatAvailability(Bus bus, LocalDate startDate, LocalDate endDate) {
        Set<Seat> seats = bus.getSeats();
        Set<SeatAvailability> seatAvailabilitySet = new HashSet<>();
        for (Seat seat : seats) {
            LocalDate date = startDate;
            while (!date.isAfter(endDate)) {
                SeatAvailability seatAvailability = new SeatAvailability();
                seatAvailability.setSeat(seat);
                seatAvailability.setAvailabilityDate(date);
                seatAvailability.setAvailable(true); // Set as available
                seatAvailabilitySet.add(seatAvailability);
                date = date.plusDays(1); // Increment to the next day
            }
        }
        seatAvailabilityRepository.saveAll(seatAvailabilitySet); // Persist availability records
    }

    @Transactional
    public void removeScheduleFromBus(Long busId, Long busScheduleId) {
        // Unlink a bus from its schedule and remove availability
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new BusNotFoundException("Bus not found with id: " + busId));
        BusSchedule busSchedule = busScheduleRepository.findById(busScheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("Bus Schedule not found with id: " + busScheduleId));

        // Determine effective date for availability deletion
        LocalDate effectiveDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        if (currentTime.isAfter(busSchedule.getStartTime())) {
            effectiveDate = effectiveDate.plusDays(1);
        }

        // Delete availability records for bus and seats
        busAvailabilityRepository.deleteByBusAndDateAfter(bus, effectiveDate);
        for (Seat seat : bus.getSeats()) {
            seatAvailabilityRepository.deleteBySeatAndDateAfter(seat, effectiveDate);
        }
        bus.setBusSchedule(null); // Remove the schedule from the bus
        busRepository.save(bus); // Save changes to the bus
    }

    public BusSchedule getCurrentScheduleForBus(Long busId) {
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new BusNotFoundException("Bus not found with id: " + busId));
        return bus.getBusSchedule(); // Return the bus's current schedule
    }

    public Bus addSeatToBus(Long busId, Seat seat) {
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new BusNotFoundException("Bus not found with id: " + busId));
        bus.getSeats().add(seat); // Add seat to bus's collection
        seat.setBus(bus); // Link seat to bus
        return busRepository.save(bus); // Persist bus with new seat
    }

    public Seat getSeatById(Long seatId) {
        return seatRepository.findById(seatId)
                .orElseThrow(() -> new SeatNotFoundException("Seat not found with id " + seatId));
    }

    public List<Seat> viewSeats(Bus bus) {
        return bus.getSeats().stream()
                .sorted(Comparator.comparingInt(Seat::getNumber)) // Sort seats by number
                .collect(Collectors.toList()); // Return sorted list of seats
    }

    @Transactional
    public Bus deleteSeat(Bus bus, Long seatId) {
        bus.getSeats().removeIf(seat -> seat.getId().equals(seatId)); // Remove seat by ID
        return busRepository.save(bus); // Persist bus changes
    }

    public Bus getBusById(Long busId) {
        return busRepository.findById(busId)
                .orElseThrow(() -> new BusNotFoundException("Bus not found with id " + busId));
    }
}
