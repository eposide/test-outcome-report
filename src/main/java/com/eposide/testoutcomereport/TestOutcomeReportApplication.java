package com.eposide.testoutcomereport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.eposide.testoutcomereport.repositories")
public class TestOutcomeReportApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestOutcomeReportApplication.class, args);
    }

}
