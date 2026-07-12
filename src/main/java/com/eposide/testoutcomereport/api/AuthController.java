package com.eposide.testoutcomereport.api;

import com.eposide.testoutcomereport.domain.User;
import com.eposide.testoutcomereport.dto.ApiLoginRequest;
import com.eposide.testoutcomereport.service.UserService;
import com.eposide.testoutcomereport.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody ApiLoginRequest request) {
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // Fetch user details
            java.util.Optional<User> userOptional = userService.findByUsername(request.getUsername());
            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "User not found")
                );
            }

            User user = userOptional.get();

            // Generate JWT token
            String token = jwtUtil.generateToken(user.getUsername(), user.getOrganizationId());

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "username", user.getUsername(),
                    "organizationId", user.getOrganizationId()
            ));

        } catch (AuthenticationException e) {
            log.warn("Authentication failed for user: {}", request.getUsername());
            return ResponseEntity.status(401).body(
                    Map.of("error", "Invalid username or password")
            );
        } catch (Exception e) {
            log.error("Error during login", e);
            return ResponseEntity.internalServerError().body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    @GetMapping("/token")
    public ResponseEntity<?> getToken() {
        try {

            log.debug("generating token for user ");
            // Get currently authenticated user from SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body(
                        Map.of("error", "User not authenticated")
                );
            }

            String username = authentication.getName();

            // Fetch full user details to get organizationId
            java.util.Optional<User> userOptional = userService.findByUsername(username);
            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "User not found")
                );
            }

            User user = userOptional.get();
            String token = jwtUtil.generateToken(user.getUsername(), user.getOrganizationId());

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "username", user.getUsername(),
                    "organizationId", user.getOrganizationId()
            ));

        } catch (Exception e) {
            log.error("Error generating token", e);
            return ResponseEntity.internalServerError().body(
                    Map.of("error", e.getMessage())
            );
        }
    }

}
