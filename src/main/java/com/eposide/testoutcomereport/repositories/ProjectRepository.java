package com.eposide.testoutcomereport.repositories;

import com.eposide.testoutcomereport.domain.TestProject;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProjectRepository extends MongoRepository<TestProject, String> {

        List<TestProject> findByProjectName(String projectName);
        List<TestProject> findByProjectDescriptionContaining(String keyword);
}
