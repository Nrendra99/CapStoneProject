package org.user.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.user.app.entity.BusSchedule;
import org.user.app.exceptions.ScheduleNotFoundException;
import org.user.app.repository.BusScheduleRepository;
import org.user.app.service.BusScheduleService;

class BusScheduleServiceTest {

    @Mock
    private BusScheduleRepository busScheduleRepository;

    @InjectMocks
    private BusScheduleService busScheduleService;

    private BusSchedule busSchedule;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        busSchedule = new BusSchedule();
        busSchedule.setId(1L);
        busSchedule.setStartingCity("City A");
        busSchedule.setStartTime(LocalTime.of(10, 0));
        busSchedule.setDestination("City B");
        busSchedule.setEndTime(LocalTime.of(12, 0));
    }

    @Test
    void givenBusScheduleDetails_whenCreateNewSchedule_thenReturnSavedSchedule() {
        // Given
        String startingCity = "City A";
        LocalTime startTime = LocalTime.of(10, 0);
        String destination = "City B";
        LocalTime endTime = LocalTime.of(12, 0);
        when(busScheduleRepository.save(any(BusSchedule.class))).thenReturn(busSchedule);

        // When
        BusSchedule createdSchedule = busScheduleService.createNewSchedule(startingCity, startTime, destination, endTime);

        // Then
        assertNotNull(createdSchedule);
        assertEquals(busSchedule.getStartingCity(), createdSchedule.getStartingCity());
        assertEquals(busSchedule.getDestination(), createdSchedule.getDestination());
        verify(busScheduleRepository, times(1)).save(any(BusSchedule.class));
    }

    @Test
    void givenValidId_whenGetBusScheduleById_thenReturnBusSchedule() {
        // Given
        when(busScheduleRepository.findById(1L)).thenReturn(Optional.of(busSchedule));

        // When
        BusSchedule foundSchedule = busScheduleService.getBusScheduleById(1L);

        // Then
        assertNotNull(foundSchedule);
        assertEquals(busSchedule.getId(), foundSchedule.getId());
        verify(busScheduleRepository, times(1)).findById(1L);
    }

    @Test
    void givenInvalidId_whenGetBusScheduleById_thenThrowScheduleNotFoundException() {
        // Given
        when(busScheduleRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(ScheduleNotFoundException.class, () -> {
            busScheduleService.getBusScheduleById(1L);
        });
        assertEquals("Bus Schedule not found with id: 1", exception.getMessage());
        verify(busScheduleRepository, times(1)).findById(1L);
    }

    @Test
    void givenValidIdAndUpdatedSchedule_whenUpdateBusSchedule_thenReturnUpdatedSchedule() {
        // Given
        when(busScheduleRepository.findById(1L)).thenReturn(Optional.of(busSchedule));
        when(busScheduleRepository.save(any(BusSchedule.class))).thenReturn(busSchedule); // Mocking save to return the existing schedule

        BusSchedule updatedSchedule = new BusSchedule();
        updatedSchedule.setStartingCity("City A Updated");
        updatedSchedule.setStartTime(LocalTime.of(11, 0));
        updatedSchedule.setDestination("City B Updated");
        updatedSchedule.setEndTime(LocalTime.of(13, 0));

        // When
        BusSchedule updatedBusSchedule = busScheduleService.updateBusSchedule(1L, updatedSchedule);

        // Then
        assertNotNull(updatedBusSchedule);
        assertEquals(updatedSchedule.getStartingCity(), updatedBusSchedule.getStartingCity());
        assertEquals(updatedSchedule.getDestination(), updatedBusSchedule.getDestination());
        verify(busScheduleRepository, times(1)).findById(1L);
        verify(busScheduleRepository, times(1)).save(busSchedule);
    }

    @Test
    void givenInvalidId_whenUpdateBusSchedule_thenThrowResourceNotFoundException() {
        // Given
        when(busScheduleRepository.findById(1L)).thenReturn(Optional.empty());
        BusSchedule updatedSchedule = new BusSchedule();

        // When & Then
        Exception exception = assertThrows(ScheduleNotFoundException.class, () -> {
            busScheduleService.updateBusSchedule(1L, updatedSchedule);
        });
        assertEquals("Bus Schedule not found with id: 1", exception.getMessage());
        verify(busScheduleRepository, times(1)).findById(1L);
    }

    @Test
    void whenGetAllBusSchedules_thenReturnListOfSchedules() {
        // Given
        when(busScheduleRepository.findAll()).thenReturn(Collections.singletonList(busSchedule));

        // When
        List<BusSchedule> schedules = busScheduleService.getAllBusSchedules();

        // Then
        assertNotNull(schedules);
        assertEquals(1, schedules.size());
        verify(busScheduleRepository, times(1)).findAll();
    }
}