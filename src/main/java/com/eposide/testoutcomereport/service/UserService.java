package com.eposide.testoutcomereport.service;

import com.eposide.testoutcomereport.domain.User;
import com.eposide.testoutcomereport.domain.UserRoles;
import com.eposide.testoutcomereport.exception.TestOutcomeException;
import com.eposide.testoutcomereport.repositories.OrganizationRepository;
import com.eposide.testoutcomereport.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, OrganizationRepository organizationRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Load user by username for Spring Security
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return buildUserDetails(user.get());
    }

    /**
     * Register a new user under an existing organization
     */
    public User registerUser(String username, String password, String organizationId) throws TestOutcomeException {
        // Check if user already exists
        if (userRepository.existsByUsername(username)) {
            throw new TestOutcomeException("Username already exists");
        }

        // Verify organization exists
        if (!organizationRepository.existsById(organizationId)) {
            throw new TestOutcomeException("Organization not found");
        }

        // Create new user
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));

        user.setOrganizationId(organizationId);
        user.setRoles(Set.of(UserRoles.USER));

        return userRepository.save(user);
    }

    /**
     * Reset the user password
     */
    public void resetPassword(String username, String password) throws TestOutcomeException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new TestOutcomeException("User not found with username: " + username);
        }

        user.get().setPasswordHash(passwordEncoder.encode(password));

        userRepository.save(user.get());
    }


    /**
     * Find user by username
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Get user by ID
     */
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    /**
     * Check if username exists
     */
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Get all users in an organization
     */
    public java.util.List<User> getUsersByOrganization(String organizationId) {
        return userRepository.findByOrganizationId(organizationId);
    }

    /**
     * Build UserDetails from User entity
     */
    private UserDetails buildUserDetails(User user) {
        Set<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}

