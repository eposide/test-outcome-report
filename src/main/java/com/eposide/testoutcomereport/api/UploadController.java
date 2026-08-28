package com.eposide.testoutcomereport.api;

import com.eposide.testoutcomereport.domain.*;
import com.eposide.testoutcomereport.parsers.ParserContext;
import com.eposide.testoutcomereport.parsers.ParserRegistry;
import com.eposide.testoutcomereport.repositories.ProjectRepository;
import com.eposide.testoutcomereport.repositories.TestRunRepository;
import com.eposide.testoutcomereport.service.OrganizationService;
import com.eposide.testoutcomereport.util.JwtUtil;
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
    private final OrganizationService organizationService;
    private final JwtUtil jwtUtil;


    public UploadController(TestRunRepository testRunRepository, ProjectRepository projectRepository, ParserRegistry parserRegistry, OrganizationService organizationService, JwtUtil jwtUtil) {
        this.testRunRepository = testRunRepository;
        this.projectRepository = projectRepository;
        this.parserRegistry = parserRegistry;
        this.organizationService = organizationService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(value= "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadTestResults(@RequestHeader(value = "Authorization", required = false) String authHeader, @RequestPart UploadRequest request, @RequestPart MultipartFile report) {
        try {
            // Validate file
            if (report.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Report file is required")
                );
            }

            // Validate file size (50MB limit)
            if (report.getSize() > 52428800) { // 50MB in bytes
                return ResponseEntity.status(413).body(
                        Map.of("error", "File size exceeds maximum allowed size (50MB)")
                );
            }

            // Validate file type
            String filename = report.getOriginalFilename();
            if (filename == null || !isAllowedFileType(filename)) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "File type not allowed. Only JSON and XML files are supported")
                );
            }

            // Validate Bearer token
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null || !jwtUtil.isTokenValid(token)) {
                return ResponseEntity.status(401).body(
                        Map.of("error", "Missing or invalid Bearer token")
                );
            }

            if (request.getOrganizationId() == null) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "organizationId is required")
                );
            }

            if (!validateOrganizationId(request.getOrganizationId())) {
                return ResponseEntity.status(403).body(
                        Map.of("error", "Invalid organizationId")
                );
            }

            if (request.getFramework() == null) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "framework is required")
                );
            }

            if (request.getFormat() == null) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "format is required")
                );
            }

            ParserFormat requestFormat = null;

            try {


               requestFormat =  ParserFormat.valueOf(request.getFormat());

            } catch (IllegalArgumentException e) {
                log.warn("Invalid format: {}", request.getFormat());
                return ResponseEntity.badRequest().body(
                        Map.of("error", "format is not valid")
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
            context.setFormat(requestFormat);
            context.setProject(request.getProject());
            context.setBranch(request.getBranch());
            context.setCommitId(request.getCommitId());
            context.setEnvironment(request.getEnvironment());
            context.setSource(request.getSource());
            context.setFileName(report.getOriginalFilename());
            context.setOrganizationId(request.getOrganizationId());

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

    private boolean validateOrganizationId(String organizationId) {

        Organization org = organizationService.getOrganization(organizationId);

        if (org == null) {
            log.warn("Invalid organizationId: {}", organizationId);
            return false;
        }

        if (OrganizationStatus.ACTIVE != org.getStatus()) {
            log.warn("Organization is not active: {}", organizationId);
            return false;
        }

        return true;
    }

    private String processUpload(String reportContent, ParserContext context) throws Exception {
        // Parse into domain model
        TestRun testRun = parserRegistry.parseTestResults(reportContent, context);

        // Save to database
        testRunRepository.save(testRun);

        TestProject testProject = new TestProject();
        testProject.setName(testRun.getProject());
        testProject.setOrganizationId(context.getOrganizationId());

        // Check if the project already exists in the database, if not save it
        List<TestProject> existingProjects = projectRepository.findByName(testRun.getProject());
        if (existingProjects == null || existingProjects.isEmpty()) {
            projectRepository.save(testProject);
        }

        log.info("Test run saved: {} for project: {}", testRun.getId(), testRun.getProject());

        return testRun.getId();
    }

    private boolean isAllowedFileType(String filename) {
        String lowerFilename = filename.toLowerCase();
        return lowerFilename.endsWith(".json") || lowerFilename.endsWith(".xml");
    }

}
