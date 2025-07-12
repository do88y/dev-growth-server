package com.devgrowth.project.controller;

import com.devgrowth.project.model.TrackedRepository;
import com.devgrowth.project.security.CustomUserDetails;
import com.devgrowth.project.service.GitHubService;
import com.devgrowth.project.service.TrackedRepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class RepositoryController {

    private final GitHubService gitHubService;
    private final TrackedRepositoryService trackedRepositoryService;

    @GetMapping("/")
    public String home(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails != null) {
            model.addAttribute("name", userDetails.getUsername());
            model.addAttribute("email", userDetails.getUser().getEmail());
        } else {
            model.addAttribute("name", "Guest");
            model.addAttribute("email", "N/A");
        }
        return "home";
    }

    @GetMapping("/repositories")
    public String repositories(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/";
        }
        List<Map<String, Object>> repos = gitHubService.getRepositories(userDetails.getUser());
        List<String> trackedRepoNames = trackedRepositoryService.findActiveTrackedRepositories(userDetails.getUser())
                .stream()
                .map(TrackedRepository::getRepoName)
                .collect(Collectors.toList());

        model.addAttribute("repos", repos);
        model.addAttribute("trackedRepoNames", trackedRepoNames);
        return "repositories";
    }

    @PostMapping("/repositories/add")
    public String addRepository(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam String repoName) {
        if (userDetails == null) {
            return "redirect:/";
        }
        trackedRepositoryService.addTrackedRepository(userDetails.getUser(), repoName);
        return "redirect:/repositories";
    }

    @PostMapping("/repositories/remove")
    public String removeRepository(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam String repoName) {
        if (userDetails == null) {
            return "redirect:/";
        }
        trackedRepositoryService.removeTrackedRepository(userDetails.getUser(), repoName);
        return "redirect:/repositories";
    }
}