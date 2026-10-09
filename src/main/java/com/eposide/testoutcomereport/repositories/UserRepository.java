package com.eposide.testoutcomereport.repositories;

import com.eposide.testoutcomereport.domain.User;
import com.eposide.testoutcomereport.domain.UserRoles;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByRoles(Set<UserRoles> roles);
    boolean existsByUsername(String username);
    List<User> findByOrganizationId(String organizationId);

}

