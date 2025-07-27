package com.devgrowth.project.controller;

import com.devgrowth.project.dto.CommitEvaluationResponse;
import com.devgrowth.project.model.CommitLog;
import com.devgrowth.project.model.TrackedRepository;
import com.devgrowth.project.repository.CommitLogRepository;
import com.devgrowth.project.security.CustomUserDetails;
import com.devgrowth.project.service.CommitEvaluationService;
import com.devgrowth.project.service.GitHubService;
import com.devgrowth.project.service.GrowthLogService;
import com.devgrowth.project.service.TrackedRepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class RepositoryController {

    private final GitHubService gitHubService;
    private final TrackedRepositoryService trackedRepositoryService;
    private final CommitEvaluationService commitEvaluationService;
    private final GrowthLogService growthLogService;
    private final CommitLogRepository commitLogRepository;

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

    @GetMapping("/repositories/{owner}/{repo}/commits")
    public String getCommitsPage(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 @PathVariable String owner,
                                 @PathVariable String repo,
                                 Model model) {
        if (userDetails == null) {
            return "redirect:/";
        }

        // 최신 커밋 동기화
        List<Map<String, Object>> githubCommits = gitHubService.getCommits(userDetails.getUser(), owner, repo);
        trackedRepositoryService.saveCommits(userDetails.getUser(), owner, repo, githubCommits);

        // DB에서 첫 페이지 커밋 조회
        Pageable pageable = PageRequest.of(0, 20);
        Page<CommitLog> commitPage = commitLogRepository.findByUserAndRepoNameOrderByCommitDateDesc(userDetails.getUser(), owner + "/" + repo, pageable);

        model.addAttribute("commitPage", commitPage);
        model.addAttribute("repoName", owner + "/" + repo);
        model.addAttribute("owner", owner);
        model.addAttribute("repo", repo);

        return "commits";
    }

    @GetMapping("/api/repositories/{owner}/{repo}/commits")
    @ResponseBody
    public ResponseEntity<Page<CommitLog>> getCommitsApi(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                         @PathVariable String owner,
                                                         @PathVariable String repo,
                                                         @RequestParam(defaultValue = "0") int page) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        Pageable pageable = PageRequest.of(page, 20);
        Page<CommitLog> commitPage = commitLogRepository.findByUserAndRepoNameOrderByCommitDateDesc(userDetails.getUser(), owner + "/" + repo, pageable);

        return ResponseEntity.ok(commitPage);
    }


    @PostMapping("/commits/{commitId}/evaluate")
    public String evaluateCommit(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 @PathVariable Long commitId,
                                 RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/";
        }

        return commitLogRepository.findById(commitId)
                .map(commitLog -> {
                    try {
                        CommitEvaluationResponse evaluationResponse = commitEvaluationService.evaluateCommit(commitLog);
                        commitLog.setEvaluationResult(evaluationResponse.getFeedback());
                        commitLog.setScore(evaluationResponse.getScore());
                        commitLog.setEvaluationStatus(CommitLog.EvaluationStatus.EVALUATED);
                        commitLogRepository.save(commitLog);

                        // GrowthLog 업데이트
                        growthLogService.updateDailyGrowthLog(userDetails.getUser(), commitLog.getCommitDate().toLocalDate());

                        redirectAttributes.addFlashAttribute("successMessage", "Commit evaluated successfully!");
                    } catch (Exception e) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Error evaluating commit: " + e.getMessage());
                    }
                    // Redirect back to the commits page
                    String[] repoParts = commitLog.getRepoName().split("/");
                    return "redirect:/repositories/" + repoParts[0] + "/" + repoParts[1] + "/commits";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Commit not found.");
                    return "redirect:/repositories";
                });
    }
}