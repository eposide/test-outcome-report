package com.eposide.testoutcomereport.boot;

import com.eposide.testoutcomereport.domain.Organization;
import com.eposide.testoutcomereport.domain.OrganizationStatus;
import com.eposide.testoutcomereport.domain.User;
import com.eposide.testoutcomereport.domain.UserRoles;
import com.eposide.testoutcomereport.repositories.OrganizationRepository;
import com.eposide.testoutcomereport.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;


    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${initial.organization.name}")
    private String initialOrganizationName;

    public DataInitializer(
            UserRepository userRepository,
            OrganizationRepository organizationRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void run(String... args) throws Exception {

        if (!testConfig()) {
            log.error("Missing required environment variables. Please set admin.username, admin.password, and initial.organization.name.");
            return;
        }

        Organization initialOrganization = organizationRepository.findByName(initialOrganizationName);

        if (initialOrganization == null) {
            Organization org = new Organization();
            org.setName(initialOrganizationName);
            org.setStatus(OrganizationStatus.ACTIVE);
            initialOrganization = organizationRepository.save(org);
            log.info("Initial organization created: {}", initialOrganization.getName());
        }

        // if a super admin user already exists do not create a new one
        Optional<User> adminUser = userRepository.findByRoles(Set.of(UserRoles.SUPER_ADMIN));
        if  (adminUser.isEmpty()) {
            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            admin.setOrganizationId(initialOrganization.getId());
            admin.setRoles(Set.of(UserRoles.SUPER_ADMIN));
            userRepository.save(admin);
            log.info("Admin user created: {}", adminUsername);
        } else {
            log.warn("Not creating admin user. Admin user already exists: {}, {}", adminUser.get().getId(), adminUser.get().getUsername());
        }
    }

    private boolean testConfig() {
        return adminUsername != null && !adminUsername.trim().isEmpty() &&
               adminPassword != null && !adminPassword.trim().isEmpty() &&
               initialOrganizationName != null && !initialOrganizationName.trim().isEmpty();
    }
}
