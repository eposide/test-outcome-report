package com.eposide.testoutcomereport.web;

import com.eposide.testoutcomereport.dto.CurrentUserDTO;
import com.eposide.testoutcomereport.domain.Organization;
import com.eposide.testoutcomereport.domain.User;
import com.eposide.testoutcomereport.repositories.OrganizationRepository;
import com.eposide.testoutcomereport.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
public class UserController {

    private final UserService userService;
    private final OrganizationRepository organizationRepository;

    public UserController(UserService userService, OrganizationRepository organizationRepository) {
        this.userService = userService;
        this.organizationRepository = organizationRepository;
    }

    /**
     * Display login page
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "expired", required = false) String expired,
                           Model model) {
        if (error != null) {
            log.debug("error on authentication {}",error);
            model.addAttribute("error", "Invalid username or password");
        }
        if (expired != null) {
            model.addAttribute("error", "Session expired. Please login again");
        }
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }



    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String password, @RequestParam String confirmPassword, Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return "redirect:/login";
            }
            String username = auth.getName();

            if (password == null || password.trim().isEmpty()) {
                model.addAttribute("error", "Password is required");
                return "reset-password";
            }
            if (!password.equals(confirmPassword)) {
                model.addAttribute("error", "Passwords do not match");
                return "reset-password";
            }

            userService.resetPassword(username, password);
            model.addAttribute("success", "Password reset successful! Please login.");


        } catch (Exception e) {
            log.error("Reset password error", e);
        }
        return "redirect:/login";
    }

    /**
     * Display user profile page for the currently authenticated user
     */
    @GetMapping("/profile")
    public String profilePage(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        userService.findByUsername(username).ifPresent(user -> model.addAttribute("currentUser", populateCurrentUser(user)));

        return "profile";

    }

    /**
     * Populate the current user object with information to display on the profile page
     * CurrentUserDTO does not include sensitive information like password hash.
     */
    private @Nullable CurrentUserDTO populateCurrentUser(User user) {

            CurrentUserDTO currentUserDTO = new CurrentUserDTO();
            currentUserDTO.setUsername(user.getUsername());
            currentUserDTO.setOrganizationName(getOrganizationName(user.getOrganizationId()));
            currentUserDTO.setRoles(user.getRoles());
            return currentUserDTO;
    }

    private String getOrganizationName(String organizationId) {

        return organizationRepository.findById(organizationId)
                .map(Organization::getName)
                .orElse("Unknown Organization");
    }

}

