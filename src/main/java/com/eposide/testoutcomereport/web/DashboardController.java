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

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
public class DashboardController {

    private final TestRunRepository testRunRepository;
    private final UserService userService;

    public DashboardController(TestRunRepository testRunRepository, UserService userService) {
        this.testRunRepository = testRunRepository;
        this.userService = userService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
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

        model.addAttribute("currentUser", user);
        model.addAttribute("recentRuns", findRecentRunsByOrganization(organizationId));

        return "dashboard";
    }

    private List<TestRun> findRecentRunsByOrganization(String organizationId) {
        // Find the most recent 10 test runs for the user's organization, sorted by timestamp descending
        return testRunRepository.findByOrganizationIdOrderByTimestampDesc(organizationId)
                .stream()
                .limit(10)
                .toList();
    }
}
