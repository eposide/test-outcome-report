package com.eposide.testoutcomereport.web;

import com.eposide.testoutcomereport.repositories.TestRunRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/run-details")
public class TestRunDetailsController {

    private final TestRunRepository testRunRepository;

    public TestRunDetailsController(TestRunRepository testRunRepository) {
        this.testRunRepository = testRunRepository;
    }

    @GetMapping("/{testId}")
    public String details(
            @PathVariable String testId,
            Model model) {

        testRunRepository.findById(testId).ifPresent(testRun -> {
            model.addAttribute("testRun", testRun);
        });


        return "run-details";
    }
}
