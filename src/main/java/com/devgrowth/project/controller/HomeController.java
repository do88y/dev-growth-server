
package com.devgrowth.project.controller;

import com.devgrowth.project.security.CustomUserDetails;
import com.devgrowth.project.service.GitHubService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final GitHubService gitHubService;

    @GetMapping("/")
    public String home(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails != null) {
            model.addAttribute("name", userDetails.getUsername());
            model.addAttribute("email", userDetails.getUser().getEmail());
        } else {
            model.addAttribute("name", "Guest");
            model.addAttribute("email", "N/A");
        }
        return "home"; // Renders home.html template
    }

    @GetMapping("/repositories")
    public String repositories(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/";
        }
        List<Map<String, Object>> repos = gitHubService.getRepositories(userDetails.getUser());
        model.addAttribute("repos", repos);
        return "repositories"; // Renders repositories.html template
    }
}
