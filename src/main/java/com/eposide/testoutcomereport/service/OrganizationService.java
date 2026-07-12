package com.eposide.testoutcomereport.service;

import com.eposide.testoutcomereport.domain.Organization;
import com.eposide.testoutcomereport.repositories.OrganizationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    public OrganizationService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public Organization getOrganization(String organizationId) {
        return organizationRepository.findById(organizationId).orElse(null);
    }

    public List<Organization> getAllOrganizations() {
        return organizationRepository.findAll();
    }

    /**
     * Register a new organization (super-user only)
     */
    public Organization registerOrganization(String name, String description, String contactName, String contactEmail, String contactPhone) {
        Organization org = new Organization();
        org.setName(name);
        org.setDescription(description);
        org.setContactName(contactName);
        org.setContactEmail(contactEmail);
        org.setContactPhone(contactPhone);
        org.setStatus(com.eposide.testoutcomereport.domain.OrganizationStatus.ACTIVE);

        return organizationRepository.save(org);
    }


}
