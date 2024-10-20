package org.user.app.controller;


import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.user.app.entity.Passenger;
import org.user.app.entity.Ticket;
import org.user.app.entity.User;
import org.user.app.service.UserService;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@RequestMapping("/users")
@Tag(name = "User Management", description = "Endpoints for managing users")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Operation(summary = "Show user registration form")
    @GetMapping("/add")
    public String showUserForm(Model model) {
        User user = new User(); // Create a new User object for the form
        model.addAttribute("user", user); // Add the user object to the model
        return "UserAdd"; 
    }

    @Operation(summary = "Create a new user")
    @PostMapping("/add")
    public String createUser(@ModelAttribute @Valid User user, 
                             BindingResult bindingResult, 
                             Model model, 
                             RedirectAttributes redirectAttributes) {
        // If there are validation errors, return to the form
        if (bindingResult.hasErrors()) {
            return "UserAdd"; // Return to the form if validation fails
        }
        
        // Create the new user
        userService.createUser(user);
        
        // Add a flash attribute for success message
        redirectAttributes.addFlashAttribute("SuccessMessage", "You have successfully registered. Please log in!");

        // Redirect to the login page
        return "redirect:/login";
    }

    @Operation(summary = "Show user update form")
    @GetMapping("/update")
    public String showUpdateForm(Authentication authentication, Model model) throws Exception {
        String email = authentication.getName(); // Get the current authenticated user's email
        User user = userService.getUserByEmail(email); // Retrieve user details
        model.addAttribute("user", user); // Add the user object to the model
        return "UserUpdate"; 
    }

    @Operation(summary = "Update existing user")
    @PostMapping("/update")
    public String updateUser( @ModelAttribute @Valid User user, BindingResult bindingResult, Model model ,Authentication authentication , Long id) throws Exception {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user); // Re-add user object if validation errors exist
            return "UserUpdate"; // Return to the form if there are validation errors
        }
        String email = authentication.getName(); // Get the current authenticated user's email
        id= userService.getUserByEmail(email).getId();// Get the current authenticated user's id
        userService.updateUser(id, user); // Update the user
        return "redirect:/users/details"; // Redirect to the user details page
    }

    @Operation(summary = "View user details")
    @GetMapping("/details")
    public String getUserDetails(Authentication authentication, Model model) throws Exception {
        String email = authentication.getName(); // Get the current authenticated user's email
        User user = userService.getUserByEmail(email); // Retrieve user details
        model.addAttribute("user", user); // Add the user object to the model
        return "UserView"; // Return the Thymeleaf template for displaying user details
    }
    
    @Operation(summary = "View tickets of the user")
    @GetMapping("/tickets")
    public String viewTickets(Authentication authentication, Model model) throws Exception {
        String email = authentication.getName(); // Get the current authenticated user's email
        User user = userService.getUserByEmail(email); // Retrieve user details
        model.addAttribute("user", user); // Add the user object to the model
        Set<Ticket> tickets = userService.viewTickets(user); // Fetch tickets for the user
        model.addAttribute("tickets", tickets); // Add tickets to the model
        return "TicketsList"; // Return the Thymeleaf template for displaying tickets
    }
    
    @Operation(summary = "View passengers of a ticket")
    @GetMapping("/passengers/{ticketId}")
    public String viewPassengers(@PathVariable Long ticketId, Model model) {
        Ticket ticket = userService.getTicketById(ticketId); // Retrieve ticket details
        List<Passenger> passengers = userService.viewPassengers(ticketId); // Fetch passengers for the ticket
        model.addAttribute("passengers", passengers); // Add passengers to the model
        model.addAttribute("ticket", ticket); // Add ticket to the model
        return "PassengersList"; // Return the Thymeleaf template for displaying passengers
    }
    
    @Operation(summary = "User home page")
    @GetMapping("/home")
    public String userHome(Model model) {
        return "UserHome"; // Return the Thymeleaf template for the user's home page
    }
}