package com.eposide.testoutcomereport.web;

import com.eposide.testoutcomereport.domain.ParserFormat;
import com.eposide.testoutcomereport.domain.User;
import com.eposide.testoutcomereport.parsers.ParserRegistry;
import com.eposide.testoutcomereport.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Slf4j
@Controller
public class FileUploadController {

    private final ParserRegistry parserRegistry;
    private final UserService userService;

    public FileUploadController(ParserRegistry parserRegistry, UserService userService) {
        this.parserRegistry = parserRegistry;
        this.userService = userService;
    }

    @GetMapping("/file-upload")
    public String fileUpload(Model model) {
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

        model.addAttribute("frameworks", parserRegistry.getSupportedFrameworks());

        model.addAttribute("formats", ParserFormat.values());

        model.addAttribute("currentUser", user);

        return "file-upload";
    }
}
