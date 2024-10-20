package org.user.app.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.user.app.entity.Ticket;
import org.user.app.entity.User;
import org.user.app.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
@Tag(name = "Admin Controller", description = "Endpoints for admin operations")
public class AdminController {

    @Autowired
    private UserService userService; // Service for user-related operations

    @GetMapping("/home")
    @Operation(summary = "Get admin home page")
    public String getAdminHome(Model model) {
        // Add any necessary model attributes for the admin home
        return "AdminHome";  // Return the Thymeleaf template name
    }

    @GetMapping("/users")
    @Operation(summary = "Retrieve all users")
    public String getAllUsers(Model model) {
        List<User> users = userService.getAllUsers(); // Fetch all users
        model.addAttribute("users", users); // Add users to the model
        return "UsersList"; // Return the Thymeleaf template name
    }

    @GetMapping("/update/{id}")
    @Operation(summary = "Show the user update form")
    public String showUpdateForm(@PathVariable Long id, Model model){
        User user = userService.getUserById(id); // Retrieve user by ID
        model.addAttribute("user", user); // Add user object to the model
        return "AdminUpdateUser"; // Return the update user form template
    }


    @PostMapping("/update/{id}")
    @Operation(summary = "Handle form submission for updating a user")
    public String updateUser(@PathVariable Long id, @ModelAttribute @Valid User user, BindingResult bindingResult, Model model) throws Exception {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user); // Re-add user object if there are validation errors
            return "AdminUpdateUser"; // Return to the form if there are validation errors
        }
        userService.updateUser(id, user); // Update the user
        return "redirect:/admin/users"; // Redirect to the user list
    }

    @GetMapping("/{userId}/tickets")
    @Operation(summary = "View tickets for a specific user")
    public String viewTickets(@PathVariable Long userId, Model model) {
        User user = userService.getUserById(userId); // Fetch user by ID
        Set<Ticket> tickets = userService.viewTickets(user); // Fetch tickets for the user
        model.addAttribute("tickets", tickets); // Add tickets to the model
        model.addAttribute("userId", userId); // Pass the user ID if needed
        return "AdminTicketList"; // Return the name of the Thymeleaf template
    }
}
