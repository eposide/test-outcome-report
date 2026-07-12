package com.eposide.testoutcomereport.web;


import com.eposide.testoutcomereport.domain.Organization;
import com.eposide.testoutcomereport.domain.User;
import com.eposide.testoutcomereport.exception.TestOutcomeException;
import com.eposide.testoutcomereport.service.OrganizationService;
import com.eposide.testoutcomereport.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
public class AdminController {

    private final OrganizationService organizationService;
    private final UserService userService;

    private static final String ADMIN_USER_REGISTER = "admin-user-register";

    public AdminController(OrganizationService organizationService, UserService userService) {
        this.organizationService = organizationService;
        this.userService = userService;
    }

    @GetMapping("/admin/organizations")
    public String organizations(Model model) {

        if (!isAdmin()) {
            return "redirect:/login";
        }

        addCurrentUser(model);

        // List the organizations
        model.addAttribute("organizations", organizationService.getAllOrganizations());
        return "admin-organizations";

    }

    @GetMapping("/admin/organization/users/{organizationId}")
    public String organizationUsers(@PathVariable String organizationId, Model model) {
        if (!isAdmin()) {
            return "redirect:/login";
        }

        if (organizationId == null || organizationId.isEmpty()) {
            return "admin-organizations";
        }

        addCurrentUser(model);
        model.addAttribute("users", userService.getUsersByOrganization(organizationId));
        model.addAttribute("organization",  organizationService.getOrganization(organizationId));
        return "admin-org-users";
    }

    /**
     * Display organization registration page (super-user only)
     */
    @GetMapping("/admin/org-register")
    public String adminOrgRegisterPage(Model model) {
        if (!isAdmin()) {
            return "redirect:/login";
        }

        addCurrentUser(model);
        return "admin-org-register";
    }

    /**
     * Handle organization registration (super-user only)
     */
    @PostMapping("/admin/org-register")
    public String registerOrganization(@RequestParam String name,
                                       @RequestParam String description,
                                       @RequestParam String contactName,
                                       @RequestParam String contactEmail,
                                       @RequestParam String contactPhone,
                                       Model model) {
        try {
            if (!isAdmin()) {
                return "redirect:/login";
            }

            // Validate inputs
            if (name == null || name.trim().isEmpty()) {
                model.addAttribute("error", "Organization name is required");
                return "admin-org-register";
            }

            // Register organization
            Organization org = organizationService.registerOrganization(name, description, contactName, contactEmail, contactPhone);
            addCurrentUser(model);
            model.addAttribute("success", "Organization registered successfully! ID: " + org.getId());
            return "admin-org-register";

        } catch (Exception e) {
            log.error("Organization registration error", e);
            model.addAttribute("error", e.getMessage());
            return "admin-org-register";
        }
    }

    /**
     * Display user registration page
     */
    @GetMapping("/admin/user-register")
    public String registerPage(@RequestParam String organizationId, Model model) {

        model.addAttribute("organizationId", organizationId);
        return ADMIN_USER_REGISTER;
    }

    /**
     * Handle user registration
     */
    @PostMapping("/admin/user-register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String confirmPassword,
                               @RequestParam String organizationId,
                               Model model) {
        try {
            // Validate inputs
            if (username == null || username.trim().isEmpty()) {
                model.addAttribute("error", "Username is required");

                return ADMIN_USER_REGISTER;
            }

            if (password == null || password.trim().isEmpty()) {
                model.addAttribute("error", "Password is required");
                return ADMIN_USER_REGISTER;
            }

            if (!password.equals(confirmPassword)) {
                model.addAttribute("error", "Passwords do not match");
                return ADMIN_USER_REGISTER;
            }

            if (organizationId == null || organizationId.trim().isEmpty()) {
                model.addAttribute("error", "Organization selection is required");
                return ADMIN_USER_REGISTER;
            }

            // Register user
            userService.registerUser(username, password, organizationId);
            model.addAttribute("success", "Registration successful! Please login.");
            return ADMIN_USER_REGISTER;

        } catch (Exception e) {
            log.error("User Registration error", e);
            model.addAttribute("error", e.getMessage());
            return ADMIN_USER_REGISTER;
        }
    }


    private boolean isAdmin() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if user is authenticated and has SUPER_ADMIN role
        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"))) {
            return true;
        }
        return false;

    }

    private void addCurrentUser(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new TestOutcomeException("Authentication required");
        }

        String username = authentication.getName();
        Optional<User> userOptional = userService.findByUsername(username);

        if (userOptional.isEmpty()) {
            throw new TestOutcomeException("User not found");
        }
        User user = userOptional.get();

        model.addAttribute("currentUser", user);
    }

}
