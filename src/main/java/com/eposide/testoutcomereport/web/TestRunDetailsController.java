package com.eposide.testoutcomereport.web;

import com.eposide.testoutcomereport.domain.TestRun;
import com.eposide.testoutcomereport.domain.User;
import com.eposide.testoutcomereport.repositories.TestRunRepository;
import com.eposide.testoutcomereport.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/run-details")
public class TestRunDetailsController {

    private final TestRunRepository testRunRepository;
    private final UserService userService;

    public TestRunDetailsController(TestRunRepository testRunRepository, UserService userService) {
        this.testRunRepository = testRunRepository;
        this.userService = userService;
    }

    @GetMapping("/{testId}")
    public String details(
            @PathVariable String testId,
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

        // Verify the test run belongs to the user's organization
        Optional<TestRun> testRunOptional = testRunRepository.findById(testId);

        if (testRunOptional.isEmpty() || !testRunOptional.get().getOrganizationId().equals(organizationId)) {
            // Test run not found or doesn't belong to user's organization
            return "redirect:/test-history/";
        }

        model.addAttribute("testRun", testRunOptional.get());
        model.addAttribute("currentUser", user);

        return "run-details";
    }
}
