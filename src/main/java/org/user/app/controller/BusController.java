package org.user.app.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.user.app.dto.BusDto;
import org.user.app.entity.Bus;
import org.user.app.entity.BusSchedule;
import org.user.app.entity.Seat;
import org.user.app.exceptions.BusNotFoundException;
import org.user.app.exceptions.ResourceNotFoundException;
import org.user.app.exceptions.ScheduleNotFoundException;
import org.user.app.exceptions.SeatNotFoundException;
import org.user.app.service.BusScheduleService;
import org.user.app.service.BusService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/bus")
@Tag(name = "Bus Controller", description = "Endpoints for Bus operations")
public class BusController {

    @Autowired
    private BusService busService;
	
    @Autowired
	private BusScheduleService busScheduleService;

    @GetMapping("/add")
    @Operation(summary = "Show bus creation form")
    public String showCreateBusForm(Model model) {
        model.addAttribute("bus", new BusDto());
        return "BusAdd"; // Return the name of your Thymeleaf template
    }
    
    @PostMapping("/add")
    @Operation(summary = "Create a new bus")
    public String createBus(@Valid @ModelAttribute BusDto bus, BindingResult bindingResult, Model model) {
        // Validate bus data
        if (bindingResult.hasErrors()) {
            model.addAttribute("bus", bus);
            return "BusAdd"; // Return form with error messages
        }

        // Create bus
        busService.createBus(bus.getNumber(), bus.getDriverName(), bus.getDriverPhone(), bus.getSeatCount(), bus.getPrice());
        return "redirect:/admin/home"; // Redirect after successful creation
    }
    
    @GetMapping("/manage")
    @Operation(summary = "View all buses")
    public String viewAllBuses(Model model) {
        List<Bus> buses = busService.getAllBuses();
        model.addAttribute("buses", buses);
        return "BusManage"; // Name of your Thymeleaf template
    }
    
    @GetMapping("/{id}/update")
    @Operation(summary = "Show bus update form")
    public String showUpdateBusForm(@PathVariable Long id, Model model) {
        Bus bus = busService.getBusById(id);
        model.addAttribute("bus", bus);
        return "BusUpdate"; // Update form template
    }
    
    @PostMapping("/{id}/update")
    @Operation(summary = "Update bus details")
    public String updateBus(@PathVariable Long id, 
                            @Valid @ModelAttribute("bus") Bus updatedBus, 
                            BindingResult bindingResult, 
                            RedirectAttributes redirectAttributes) {
        
        // Validate bus data
        if (bindingResult.hasErrors()) {
            return "BusUpdate"; // Return form with errors
        }

        // Update bus
        busService.updateBus(id, updatedBus);
        redirectAttributes.addFlashAttribute("successMessage", "Bus updated successfully!");
        return "redirect:/bus/manage"; // Redirect to bus list
    }

    @GetMapping("/schedules/select/{busId}")
    @Operation(summary = "Get bus schedules")
    public String getBusSchedules(@PathVariable Long busId, Model model) {
        List<BusSchedule> schedules = busScheduleService.getAllBusSchedules();
        model.addAttribute("schedules", schedules);
        model.addAttribute("busId", busId);
        return "BusScheduleSelect"; // Template for selecting schedules
    }
    
    @GetMapping("/schedule/add/{busId}/{scheduleId}")
    @Operation(summary = "Show bus schedule addition form")
    public String getBusScheduleAddForm(@PathVariable Long scheduleId, @PathVariable Long busId, Model model) {
        model.addAttribute("busId", busId);
        model.addAttribute("scheduleId", scheduleId);
        return "BusToScheduleAdd"; // Template for adding schedule
    }
    
    @PostMapping("/schedule/add/{busId}/{scheduleId}")
    @Operation(summary = "Add schedule to bus")
    public String addScheduleToBus(@PathVariable Long busId,
                                    @PathVariable Long scheduleId,
                                    @RequestParam LocalDate startDate,
                                    @RequestParam LocalDate endDate,
                                    Model model) {
        try {
            busService.addScheduleToBus(busId, scheduleId, startDate, endDate);
            model.addAttribute("SuccessMessage", "Bus successfully added to schedule!");
        } catch (BusNotFoundException | ScheduleNotFoundException e) {
            model.addAttribute("ErrorMessage", e.getMessage()); // Get the message from the service method
        }
        return "BusToScheduleAdd"; // Return same view with success or error message
    }

    @PostMapping("/schedule/remove/{busId}/{scheduleId}")
    @Operation(summary = "Remove schedule from bus")
    public String removeScheduleFromBus(@PathVariable Long busId,
                                        @PathVariable Long scheduleId,
                                        Model model) {
        try {
            busService.removeScheduleFromBus(busId, scheduleId);
            model.addAttribute("SuccessMessage", "Schedule removed from bus successfully.");
        } catch (BusNotFoundException | ScheduleNotFoundException e) {
            model.addAttribute("ErrorMessage", e.getMessage()); // Get the message from the service method
        }
        return "BusScheduleManage"; // Return to schedule management view
    }

    @GetMapping("/schedule/{busId}")
    @Operation(summary = "Get current schedules for bus")
    public String getCurrentSchedulesForBus(@PathVariable Long busId, Model model) {
        try {
            BusSchedule schedule = busService.getCurrentScheduleForBus(busId);
            model.addAttribute("schedule", schedule);
        } catch (ResourceNotFoundException e) {
            model.addAttribute("ErrorMessage", "Schedule not found for the bus.");
        }
        return "BusScheduleManage"; // Return same view for current schedules
    }
    
    @GetMapping("/seat/{seatId}")
    @Operation(summary = "Get seat details by ID")
    public String getSeatById(@PathVariable Long seatId, Model model) {
        try {
            Seat seat = busService.getSeatById(seatId);
            model.addAttribute("seat", seat);
            model.addAttribute("message", "Seat found successfully.");
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", "Seat not found with ID: " + seatId);
        }
        return "seat-details"; // Template for seat details
    }

    @GetMapping("/manageSeats/{busId}")
    @Operation(summary = "View all seats of a specific bus")
    public String viewSeats(@PathVariable Long busId, Model model) {
        try {
            Bus bus = busService.getBusById(busId);
            List<Seat> seats = busService.viewSeats(bus);
            model.addAttribute("seats", seats);
            model.addAttribute("busId", busId);
        } catch (ResourceNotFoundException e) {
            model.addAttribute("ErrorMessage", "Bus not found with ID: " + busId);
        }
        return "BusViewSeats"; // Template for displaying seats
    }
    
    @PostMapping("/addSeats/{busId}")
    @Operation(summary = "Add seats to a bus")
    public String addSeatToBus(@PathVariable Long busId, @ModelAttribute Seat seat, RedirectAttributes redirectAttributes) {
        try {
            busService.addSeatToBus(busId, seat);
            redirectAttributes.addFlashAttribute("SuccessMessage", "Seat added to the bus successfully.");
        } catch (BusNotFoundException e) {
            redirectAttributes.addFlashAttribute("ErrorMessage", "Bus not found with ID: " + busId);
        }
        return "redirect:/bus/manageSeats/{busId}"; // Redirect after processing
    }

    @PostMapping("/seat/delete/{busId}/{seatId}")
    @Operation(summary = "Delete a seat from a specific bus")
    public String deleteSeat(@PathVariable Long busId, @PathVariable Long seatId, RedirectAttributes redirectAttributes) {
        try {
            Bus bus = busService.getBusById(busId);
            busService.deleteSeat(bus, seatId);
            redirectAttributes.addFlashAttribute("SuccessMessage", "Seat deleted successfully.");
        } catch (BusNotFoundException | SeatNotFoundException e) {
            redirectAttributes.addFlashAttribute("ErrorMessage", "Bus or Seat not found with ID: " + busId);
        }
        return "redirect:/bus/manageSeats/{busId}"; // Redirect after processing
    }
}