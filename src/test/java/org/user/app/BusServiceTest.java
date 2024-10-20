package org.user.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import org.user.app.dto.BusDetailsDto;
import org.user.app.entity.Bus;
import org.user.app.entity.BusSchedule;
import org.user.app.entity.Seat;
import org.user.app.entity.SeatAvailability;
import org.user.app.exceptions.BusNotFoundException;
import org.user.app.exceptions.NoAvailableBusesException;
import org.user.app.exceptions.ResourceNotFoundException;
import org.user.app.exceptions.ScheduleNotFoundException;
import org.user.app.repository.BusAvailabilityRepository;
import org.user.app.repository.BusRepository;
import org.user.app.repository.BusScheduleRepository;
import org.user.app.repository.SeatAvailabilityRepository;
import org.user.app.repository.SeatRepository;
import org.user.app.service.BusService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class BusServiceTest {

    @InjectMocks
    private BusService busService;

    @Mock
    private BusRepository busRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private BusScheduleRepository busScheduleRepository;

    @Mock
    private BusAvailabilityRepository busAvailabilityRepository;

    @Mock
    private SeatAvailabilityRepository seatAvailabilityRepository;

    private Bus bus;
    private BusSchedule busSchedule;
    private Seat seat;

    @BeforeEach
    public void setUp() {
        bus = new Bus();
        bus.setId(1L);
        bus.setNumber("Bus123");
        bus.setSeats(new HashSet<>());
        
        busSchedule = new BusSchedule();
        busSchedule.setId(1L);
        bus.setBusSchedule(busSchedule);

        seat = new Seat();
        seat.setId(1L);
        seat.setNumber(1);
        seat.setPrice(100);
        bus.getSeats().add(seat);
    }

    // Test for getting bus details with availability
    @Test
    void givenValidBusIdAndDate_whenGetBusDetailsWithAvailability_thenReturnBusDetailsDto() {
        // Given
        Long busId = 1L;
        LocalDate date = LocalDate.of(2024, 10, 20);

        // Mock bus and associated schedule
        Bus bus = new Bus();
        bus.setId(busId);
        bus.setNumber("BUS-123");

        Seat seat1 = new Seat(1L, 1, 100, bus, null);
        Seat seat2 = new Seat(2L, 2, 150, bus, null);
        Set<Seat> seats = new HashSet<>(Arrays.asList(seat1, seat2));
        bus.setSeats(seats);
        
        BusSchedule busSchedule = new BusSchedule();
        busSchedule.setId(1L);
        bus.setBusSchedule(busSchedule);

        when(busRepository.findById(busId)).thenReturn(Optional.of(bus));
        
        // Mock seat availability
        when(seatAvailabilityRepository.findBySeatAndAvailabilityDate(seat1, date))
                .thenReturn(Optional.of(new SeatAvailability(busId, true, seat2, date)));
        when(seatAvailabilityRepository.findBySeatAndAvailabilityDate(seat2, date))
                .thenReturn(Optional.of(new SeatAvailability(busId, false, seat2, date)));

        // When
        BusDetailsDto busDetailsDto = busService.getBusDetailsWithAvailability(busId, date);

        // Then
        assertNotNull(busDetailsDto);
        assertEquals(busId, busDetailsDto.getBusId());
        assertEquals("BUS-123", busDetailsDto.getBusNumber());
        assertEquals(2, busDetailsDto.getSeats().size());
        assertTrue(busDetailsDto.getSeats().get(0).isAvailable()); // seat 1 is available
        assertFalse(busDetailsDto.getSeats().get(1).isAvailable()); // seat 2 is not available
    }

    @Test
    void givenBusWithoutSchedule_whenGetBusDetailsWithAvailability_thenThrowScheduleNotFoundException() {
        // Given
        Long busId = 1L;
        LocalDate date = LocalDate.of(2024, 10, 20);

        // Mock bus without a schedule
        Bus bus = new Bus();
        bus.setId(busId);
        bus.setNumber("BUS-123");
        bus.setBusSchedule(null); // No schedule

        when(busRepository.findById(busId)).thenReturn(Optional.of(bus));

        // When & Then
        Exception exception = assertThrows(ScheduleNotFoundException.class, () ->
                busService.getBusDetailsWithAvailability(busId, date));

        assertEquals("No Routes found for this bus", exception.getMessage());
    }
    
    @Test
    void givenValidBusIdAndDate_whenSeatAvailabilityNotFound_thenThrowResourceNotFoundException() {
        // Given
        Long busId = 1L;
        LocalDate date = LocalDate.of(2024, 10, 20);

        // Mock bus and associated schedule
        Bus bus = new Bus();
        bus.setId(busId);
        bus.setNumber("BUS-123");

        Seat seat1 = new Seat(1L, 1, 100, bus, null);
        Set<Seat> seats = new HashSet<>(Collections.singletonList(seat1));
        bus.setSeats(seats);

        BusSchedule busSchedule = new BusSchedule();
        busSchedule.setId(1L);
        bus.setBusSchedule(busSchedule);

        when(busRepository.findById(busId)).thenReturn(Optional.of(bus));

        // Mock seat availability to return empty
        when(seatAvailabilityRepository.findBySeatAndAvailabilityDate(seat1, date))
                .thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                busService.getBusDetailsWithAvailability(busId, date));

        assertEquals("Seat availability not found for seat ID: 1 on the requested date", exception.getMessage());
    }
    
    

    @Test
    void givenValidRoute_whenBusesAvailable_thenReturnListOfBuses() {
        // Given
        LocalDate date = LocalDate.of(2024, 10, 20);
        String startingCity = "City A";
        String destination = "City B";

        Bus bus1 = new Bus(); // Create a mock bus with necessary properties
        bus1.setId(1L);
        bus1.setSeats(new HashSet<>(List.of(new Seat(1L, 100, 0, bus1, null), new Seat(2L, 100, 0, bus1, null))));

        BusSchedule schedule = new BusSchedule(); // Create a mock schedule
        schedule.setStartingCity(startingCity);
        schedule.setDestination(destination);
        schedule.setBuses(new HashSet<>(Set.of(bus1)));

        when(busScheduleRepository.findByStartingCityAndDestination(startingCity, destination))
                .thenReturn(List.of(schedule)); // Mock the repository call

        when(busAvailabilityRepository.existsByBusAndAvailabilityDate(any(Bus.class), eq(date)))
                .thenReturn(true); // Mock availability

        when(seatAvailabilityRepository.findBySeatAndAvailabilityDate(any(Seat.class), eq(date)))
                .thenReturn(Optional.of(new SeatAvailability(null, true, seat, date))); // Mock seat availability

        // When
        List<Bus> availableBuses = busService.findAvailableBusesWithSeats(date, startingCity, destination);

        // Then
        assertNotNull(availableBuses);
        assertEquals(1, availableBuses.size());
        assertEquals(bus1.getId(), availableBuses.get(0).getId());
    }
    
    @Test
    void givenValidRoute_whenNoBusesAvailable_thenThrowNoAvailableBusesException() {
        // Given
        LocalDate date = LocalDate.of(2024, 10, 20);
        String startingCity = "City A";
        String destination = "City B";

        // Mock the repository to return an empty schedule
        when(busScheduleRepository.findByStartingCityAndDestination(startingCity, destination))
                .thenReturn(new ArrayList<>()); // No schedules available

        // When & Then
        Exception exception = assertThrows(NoAvailableBusesException.class, () ->
                busService.findAvailableBusesWithSeats(date, startingCity, destination));

        assertEquals("No available buses for the route from City A to City B on 2024-10-20", exception.getMessage());
    }

    // Test for creating a bus
    @Test
    @Transactional
    public void givenBusDetails_whenCreateBus_thenReturnCreatedBus() {
        // Given
        when(busRepository.save(any(Bus.class))).thenReturn(bus);

        // When
        Bus result = busService.createBus("Bus123", "Driver", "1234567890", 10, 100);

        // Then
        assertNotNull(result);
        assertEquals("Bus123", result.getNumber());
    }

    // Test for retrieving all buses
    @Test
    public void whenGetAllBuses_thenReturnBusList() {
        // Given
        when(busRepository.findAll()).thenReturn(Collections.singletonList(bus));

        // When
        List<Bus> result = busService.getAllBuses();

        // Then
        assertEquals(1, result.size());
        assertEquals(bus, result.get(0));
    }

    // Test for updating a bus
    @Test
    public void givenBusIdAndUpdatedBus_whenUpdateBus_thenReturnUpdatedBus() {
        // Given
        Bus updatedBus = new Bus();
        updatedBus.setNumber("UpdatedBus123");
        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(busRepository.save(any(Bus.class))).thenReturn(bus);

        // When
        Bus result = busService.updateBus(1L, updatedBus);

        // Then
        assertEquals("UpdatedBus123", result.getNumber());
    }

    // Test for adding a schedule to a bus
    @Test
    public void givenBusIdAndScheduleId_whenAddScheduleToBus_thenLinkBusWithSchedule() {
        // Given
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(1);
        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(busScheduleRepository.findById(1L)).thenReturn(Optional.of(busSchedule));

        // When
        busService.addScheduleToBus(1L, 1L, startDate, endDate);

        // Then
        assertEquals(busSchedule, bus.getBusSchedule());
        verify(busAvailabilityRepository, times(1)).saveAll(anySet());
        verify(seatAvailabilityRepository, times(1)).saveAll(anySet());
    }

    // Test for removing a schedule from a bus
    @Test
    public void givenBusIdAndScheduleId_whenRemoveScheduleFromBus_thenUnlinkBusFromSchedule() {
        // Given
        LocalDate effectiveDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        // Initialize busSchedule with a start time
        LocalTime startTime = currentTime.minusHours(1); // Ensure startTime is before current time
        busSchedule.setStartTime(startTime);

        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(busScheduleRepository.findById(1L)).thenReturn(Optional.of(busSchedule));

        // When
        busService.removeScheduleFromBus(1L, 1L);

        // Then
        assertNull(bus.getBusSchedule());
        verify(busAvailabilityRepository, times(1)).deleteByBusAndDateAfter(any(), any());
        verify(seatAvailabilityRepository, times(1)).deleteBySeatAndDateAfter(any(), any());
    }

    // Test for getting current schedule for a bus
    @Test
    public void givenBusId_whenGetCurrentScheduleForBus_thenReturnBusSchedule() {
        // Given
        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));

        // When
        BusSchedule result = busService.getCurrentScheduleForBus(1L);

        // Then
        assertEquals(busSchedule, result);
    }

    // Test for adding a seat to a bus
    @Test
    public void givenBusIdAndSeat_whenAddSeatToBus_thenReturnBusWithNewSeat() {
        // Given
        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(busRepository.save(any(Bus.class))).thenReturn(bus);

        // When
        Bus result = busService.addSeatToBus(1L, seat);

        // Then
        assertEquals(1, result.getSeats().size());
        assertTrue(result.getSeats().contains(seat));
    }

    // Test for getting a seat by ID
    @Test
    public void givenSeatId_whenGetSeatById_thenReturnSeat() {
        // Given
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));

        // When
        Seat result = busService.getSeatById(1L);

        // Then
        assertEquals(seat, result);
    }

    // Test for viewing seats in a bus
    @Test
    public void givenBus_whenViewSeats_thenReturnSortedSeatList() {
        // Given
        bus.getSeats().add(seat);
        // Additional seats
        Seat seat2 = new Seat();
        seat2.setId(2L);
        seat2.setNumber(2);
        bus.getSeats().add(seat2);

        // When
        List<Seat> result = busService.viewSeats(bus);

        // Then
        assertEquals(2, result.size());
        assertEquals(seat, result.get(0));
        assertEquals(seat2, result.get(1));
    }

    // Test for deleting a seat
    @Test
    @Transactional
    public void givenBusAndSeatId_whenDeleteSeat_thenReturnBusWithoutSeat() {
        // Given
        bus.getSeats().add(seat);
        when(busRepository.save(any(Bus.class))).thenReturn(bus);

        // When
        Bus result = busService.deleteSeat(bus, seat.getId());

        // Then
        assertEquals(0, result.getSeats().size());
    }

    // Test for getting a bus by ID
    @Test
    public void givenBusId_whenGetBusById_thenReturnBus() {
        // Given
        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));

        // When
        Bus result = busService.getBusById(1L);

        // Then
        assertEquals(bus, result);
    }

    // Test for handling exceptions
    @Test
    public void givenInvalidBusId_whenGetBusById_thenThrowBusNotFoundException() {
        // Given
        when(busRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BusNotFoundException.class, () -> busService.getBusById(1L));
    }
}