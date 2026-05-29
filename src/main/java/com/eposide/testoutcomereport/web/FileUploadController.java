package com.eposide.testoutcomereport.web;

import com.eposide.testoutcomereport.parsers.ParserRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class FileUploadController {

    private final ParserRegistry parserRegistry;

    public FileUploadController(ParserRegistry parserRegistry) {
        this.parserRegistry = parserRegistry;
    }

    @GetMapping("/file-upload")
    public String fileUpload(
            Model model) {

        model.addAttribute(
                "frameworks",
                parserRegistry.getSupportedFrameworks()
        );

        return "file-upload";
    }
}
