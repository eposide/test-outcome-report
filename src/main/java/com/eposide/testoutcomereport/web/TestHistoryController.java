package com.eposide.testoutcomereport.web;

import com.eposide.testoutcomereport.domain.TestProject;
import com.eposide.testoutcomereport.domain.TestRun;
import com.eposide.testoutcomereport.repositories.ProjectRepository;
import com.eposide.testoutcomereport.repositories.TestRunRepository;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


/**
 * This controller is intended to handle any requests related to a specific test project.
 * It will search for the project name in the URL and then provide a list of test runs for that project.
 */
@Slf4j
@Controller
@RequestMapping("/test-history")
public class TestHistoryController {

    private final TestRunRepository testRunRepository;
    private final ProjectRepository projectRepository;


    public TestHistoryController(TestRunRepository testRunRepository, ProjectRepository projectRepository) {
        this.testRunRepository = testRunRepository;
        this.projectRepository = projectRepository;
    }

    @GetMapping("/")
    public String getProjects(Model model) {

        model.addAttribute("projects", getProjectNames());

        return "test-history";

    }

    private List<String> getProjectNames() {
        return projectRepository.findAll()
                .stream()
                .map(TestProject::getProjectName)
                .toList();
    }

    @GetMapping("/{projectName}")
    public String getProjectHistory(
            @PathVariable String projectName,
            @RequestParam(required = false) @Nullable String branch,
            Model model) {

        model.addAttribute("testRuns", findProjectHistory(projectName));

        model.addAttribute("projects", getProjectNames());

        return "test-history";

    }

    private List<TestRun> findProjectHistory(String projectName) {
        return testRunRepository.findByProjectOrderByTimestampDesc(projectName);
    }




}
