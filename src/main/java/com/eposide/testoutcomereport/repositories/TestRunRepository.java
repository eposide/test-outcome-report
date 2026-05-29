package com.eposide.testoutcomereport.repositories;

import com.eposide.testoutcomereport.domain.TestRun;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TestRunRepository extends MongoRepository<TestRun, String> {
    List<TestRun> findByProject(String project);
    List<TestRun> findByProjectAndBranch(String project, String branch);
    List<TestRun> findAllByOrderByTimestampDesc();
    List<TestRun> findByProjectOrderByTimestampDesc(String project);
    List<TestRun> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    List<TestRun> findByFramework(String framework);
}
