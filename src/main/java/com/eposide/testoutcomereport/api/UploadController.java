package com.eposide.testoutcomereport.api;

import com.eposide.testoutcomereport.domain.TestProject;
import com.eposide.testoutcomereport.domain.TestRun;
import com.eposide.testoutcomereport.parsers.ParserContext;
import com.eposide.testoutcomereport.parsers.ParserRegistry;
import com.eposide.testoutcomereport.repositories.ProjectRepository;
import com.eposide.testoutcomereport.repositories.TestRunRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/test-runs")
@CrossOrigin(origins = "*")
public class UploadController {
    private final TestRunRepository testRunRepository;
    private final ProjectRepository projectRepository;
    private final ParserRegistry parserRegistry;


    public UploadController(TestRunRepository testRunRepository, ProjectRepository projectRepository, ParserRegistry parserRegistry) {
        this.testRunRepository = testRunRepository;
        this.projectRepository = projectRepository;
        this.parserRegistry = parserRegistry;
    }

    @PostMapping(value= "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadTestResults(@RequestPart UploadRequest request, @RequestPart MultipartFile report) {
        try {
            if (request.getFramework() == null) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "framework is required")
                );
            }

            String reportContent =
                    new String(
                            report.getBytes(),
                            StandardCharsets.UTF_8
                    );

            // Build context
            ParserContext context = new ParserContext();
            context.setFramework(request.getFramework());
            context.setProject(request.getProject());
            context.setBranch(request.getBranch());
            context.setCommitId(request.getCommitId());
            context.setEnvironment(request.getEnvironment());
            context.setSource(request.getSource());
            context.setFileName(report.getOriginalFilename());

            String runId = processUpload(reportContent, context);

            return ResponseEntity.ok(Map.of(
                    "id", runId,
                    "message", "Test results uploaded successfully"
            ));
        } catch (Exception e) {
            log.error("Error uploading test results", e);
            return ResponseEntity.internalServerError().body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    private String processUpload(String reportContent, ParserContext context) throws Exception {
        // Parse into domain model
        TestRun testRun = parserRegistry.parseTestResults(reportContent, context);

        // Save to database
        testRunRepository.save(testRun);

        TestProject testProject = new TestProject();
        testProject.setProjectName(testRun.getProject());

        // Check if the project already exists in the database, if not save it
        List<TestProject> existingProjects = projectRepository.findByProjectName(testRun.getProject());
        if (existingProjects == null || existingProjects.isEmpty()) {
            projectRepository.save(testProject);
        }

        log.info("Test run saved: {} for project: {}", testRun.getId(), testRun.getProject());

        return testRun.getId();
    }

}
