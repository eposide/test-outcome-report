package com.eposide.testoutcomereport.web;

import com.eposide.testoutcomereport.domain.TestRun;
import com.eposide.testoutcomereport.repositories.TestRunRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Controller
public class DashboardController {

    private final TestRunRepository testRunRepository;

    public DashboardController(TestRunRepository testRunRepository) {
        this.testRunRepository = testRunRepository;
    }

    @GetMapping("/")
    public String dashboard(Model model) {

        model.addAttribute(
                "recentRuns",
                findRecentRuns()
        );
        return "dashboard";
    }

    private List<TestRun> findRecentRuns() {

        //find the most recent 10 test runs, sorted by timestamp descending
        return testRunRepository.findAllByOrderByTimestampDesc().stream().limit(10).toList();

    }
}
