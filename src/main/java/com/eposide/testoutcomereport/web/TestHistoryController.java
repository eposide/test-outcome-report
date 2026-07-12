package com.eposide.testoutcomereport.web;

import com.eposide.testoutcomereport.domain.TestProject;
import com.eposide.testoutcomereport.domain.TestRun;
import com.eposide.testoutcomereport.domain.User;
import com.eposide.testoutcomereport.repositories.ProjectRepository;
import com.eposide.testoutcomereport.repositories.TestRunRepository;
import com.eposide.testoutcomereport.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;


/**
 * This controller is intended to handle any requests related to a specific test project.
 * It will search for the project name in the URL and then provide a list of test runs for that project.
 * Data is filtered by the user's organization for security.
 */
@Slf4j
@Controller
@RequestMapping("/test-history")
public class TestHistoryController {

    private final TestRunRepository testRunRepository;
    private final ProjectRepository projectRepository;
    private final UserService userService;


    public TestHistoryController(TestRunRepository testRunRepository, ProjectRepository projectRepository, UserService userService) {
        this.testRunRepository = testRunRepository;
        this.projectRepository = projectRepository;
        this.userService = userService;
    }

    @GetMapping("/")
    public String getProjects(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        Optional<User> userOptional = userService.findByUsername(username);

        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOptional.get();
        String organizationId = user.getOrganizationId();

        model.addAttribute("projects", getProjectNamesByOrganization(organizationId));
        model.addAttribute("currentUser", user);

        return "test-history";
    }

    private List<String> getProjectNamesByOrganization(String organizationId) {
        List<TestProject> testProjects = projectRepository.findByOrganizationId(organizationId);
        log.debug("number of testProjects for organization {}: {}", organizationId, testProjects.size());
        return testProjects
                .stream()
                .map(TestProject::getName)
                .toList();
    }

    @GetMapping("/{projectName}")
    public String getProjectHistory(
            @PathVariable String projectName,
            @RequestParam(required = false) @Nullable String branch,
            Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        Optional<User> userOptional = userService.findByUsername(username);

        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        User user = userOptional.get();
        String organizationId = user.getOrganizationId();

        model.addAttribute("testRuns", findProjectHistoryByOrganization(organizationId, projectName));
        model.addAttribute("projects", getProjectNamesByOrganization(organizationId));
        model.addAttribute("currentUser", user);

        return "test-history";
    }

    private List<TestRun> findProjectHistoryByOrganization(String organizationId, String projectName) {
        return testRunRepository.findByOrganizationIdAndProjectOrderByTimestampDesc(organizationId, projectName);
    }

}
