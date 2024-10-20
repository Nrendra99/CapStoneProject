package org.user.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.user.app.dto.BusScheduleDto;
import org.user.app.entity.BusSchedule;
import org.user.app.service.BusScheduleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.format.DateTimeFormatter;


@Controller
@RequestMapping("/busSchedule")
@Tag(name = "Bus Schedule Controller", description = "Endpoints for Bus Schedule operations")
public class BusScheduleController {

    @Autowired
    private BusScheduleService busScheduleService;

    @GetMapping("/add")
    @Operation(summary = "Show form to create a new bus schedule")
    public String showCreateBusScheduleForm(Model model) {
        // Create a new BusSchedule object to bind to the form
        BusSchedule busSchedule = new BusSchedule();
        
        // Add the BusSchedule object to the model
        model.addAttribute("busSchedule", busSchedule);
        
        // Return the name of the Thymeleaf template for creating the bus schedule
        return "BusScheduleAdd"; // This should match the name of your Thymeleaf template file
    }

    @PostMapping("/add")
    @Operation(summary = "Create a new bus schedule")
    public String createBusSchedule(@Valid @ModelAttribute BusSchedule busSchedule, 
                                     BindingResult bindingResult, 
                                     Model model) {
        // Validate input and return errors if any
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> 
                errors.put(error.getField(), error.getDefaultMessage()));
            model.addAttribute("errors", errors); // Add errors to model
            return "BusScheduleAdd"; // Return to the bus schedule creation page
        }

        // Create new bus schedule
        busScheduleService.createNewSchedule(
            busSchedule.getStartingCity(),
            busSchedule.getStartTime(),
            busSchedule.getDestination(),
            busSchedule.getEndTime() 
        );

        return "redirect:/busSchedule/schedules"; // Redirect to list of bus schedules
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get details of a bus schedule by ID")
    public String getBusSchedule(@PathVariable Long id, Model model) {
        // Retrieve bus schedule details for the given ID
        BusSchedule busSchedule = busScheduleService.getBusScheduleById(id);
        model.addAttribute("busSchedule", busSchedule);
        return "busScheduleDetails"; // Return the details view of the bus schedule
    }

    @GetMapping("/{id}/updateSchedule")
    @Operation(summary = "Show form to update bus schedule by ID")
    public String getBusScheduleUpdateForm(@PathVariable Long id, Model model) {
        // Fetch the current bus schedule by ID
        BusSchedule busSchedule = busScheduleService.getBusScheduleById(id);

        // Define the time formatter
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // Format the startTime and endTime
        String formattedStartTime = busSchedule.getStartTime().format(timeFormatter);
        String formattedEndTime = busSchedule.getEndTime().format(timeFormatter);

        // Create the DTO and set formatted times
        BusScheduleDto busScheduleUpdateDto = new BusScheduleDto();
        busScheduleUpdateDto.setId(busSchedule.getId());
        busScheduleUpdateDto.setStartingCity(busSchedule.getStartingCity());
        busScheduleUpdateDto.setDestination(busSchedule.getDestination());
        busScheduleUpdateDto.setStartTime(formattedStartTime);
        busScheduleUpdateDto.setEndTime(formattedEndTime);

        // Add the DTO to the model so Thymeleaf can populate the form
        model.addAttribute("busSchedule", busScheduleUpdateDto);

        // Return the edit schedule template
        return "BusScheduleUpdate";
    }
    
    @PostMapping("/{id}/updateSchedule")
    @Operation(summary = "Update bus schedule by ID")
    public String updateBusSchedule(
            @PathVariable Long id, 
            @Valid @ModelAttribute BusSchedule busSchedule, 
            BindingResult bindingResult, 
            Model model) {
        
        // Validate input and return errors if any
        if (bindingResult.hasErrors()) {
            return "BusScheduleUpdate"; // Return to the edit schedule page if there are errors
        }

        // Update the bus schedule in the database using the service
        busScheduleService.updateBusSchedule(id, busSchedule);

        // Redirect to the schedules list after successful update
        return "redirect:/busSchedule/schedules";
    }

    @GetMapping("/schedules")
    @Operation(summary = "Get list of all bus schedules")
    public String getBusSchedules(Model model) {
        // Assuming busService.getAllSchedules() returns a list of all bus schedules
        List<BusSchedule> schedules = busScheduleService.getAllBusSchedules();
        model.addAttribute("schedules", schedules);
        return "BusSchedulesManage"; // BusSchedules is the name of the Thymeleaf template
    }
}