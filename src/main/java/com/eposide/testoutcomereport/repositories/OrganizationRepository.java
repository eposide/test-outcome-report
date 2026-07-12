package com.eposide.testoutcomereport.repositories;

import com.eposide.testoutcomereport.domain.Organization;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrganizationRepository extends MongoRepository<Organization, String> {
}
