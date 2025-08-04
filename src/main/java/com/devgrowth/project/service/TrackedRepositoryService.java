package com.devgrowth.project.service;

import com.devgrowth.project.model.CommitLog;
import com.devgrowth.project.model.TrackedRepository;
import com.devgrowth.project.model.User;
import com.devgrowth.project.repository.CommitLogRepository;
import com.devgrowth.project.repository.TrackedRepositoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class TrackedRepositoryService {

    private final TrackedRepositoryRepository trackedRepositoryRepository;
    private final CommitLogRepository commitLogRepository;
    private final GitHubService gitHubService;

    public void addTrackedRepository(User user, String repoName) {
        trackedRepositoryRepository.findByUserAndRepoName(user, repoName)
                .ifPresentOrElse(
                        TrackedRepository::activate,
                        () -> {
                            TrackedRepository newRepo = TrackedRepository.builder()
                                    .user(user)
                                    .repoName(repoName)
                                    .isActive(true)
                                    .build();
                            trackedRepositoryRepository.save(newRepo);
                        }
                );
    }

    public void removeTrackedRepository(User user, String repoName) {
        trackedRepositoryRepository.findByUserAndRepoName(user, repoName)
                .ifPresent(TrackedRepository::deactivate);
    }

    @Transactional(readOnly = true)
    public List<TrackedRepository> findActiveTrackedRepositories(User user) {
        return trackedRepositoryRepository.findByUserAndIsActive(user, true);
    }

    public void saveCommits(User user, String owner, String repoName, List<Map<String, Object>> commits) {
        for (Map<String, Object> commitData : commits) {
            String commitHash = (String) commitData.get("sha");

            // Check if commit already exists
            if (commitLogRepository.findByCommitHash(commitHash).isEmpty()) {
                // Fetch detailed commit information for lines added/deleted
                Map<String, Object> detailedCommit = gitHubService.getCommitDetails(user, owner, repoName, commitHash);
                Map<String, Object> commitInfo = (Map<String, Object>) detailedCommit.get("commit");
                Map<String, Object> authorInfo = (Map<String, Object>) commitInfo.get("author");
                Map<String, Object> stats = (Map<String, Object>) detailedCommit.get("stats");

                // Extract code diff (patch) from files
                List<Map<String, Object>> files = (List<Map<String, Object>>) detailedCommit.get("files");
                StringBuilder codeDiffBuilder = new StringBuilder();
                if (files != null) {
                    for (Map<String, Object> file : files) {
                        if (file.containsKey("patch")) {
                            codeDiffBuilder.append(file.get("patch")).append("\n");
                        }
                    }
                }

                CommitLog commitLog = CommitLog.builder()
                        .user(user)
                        .repoName(owner + "/" + repoName)
                        .commitHash(commitHash)
                        .message((String) commitInfo.get("message"))
                        .commitDate(LocalDateTime.parse((String) authorInfo.get("date"), DateTimeFormatter.ISO_DATE_TIME))
                        .linesAdded((Integer) stats.get("additions"))
                        .linesDeleted((Integer) stats.get("deletions"))
                        .diffUrl((String) detailedCommit.get("html_url"))
                        .codeDiff(codeDiffBuilder.toString())
                        .evaluationStatus(CommitLog.EvaluationStatus.PENDING)
                        .build();

                commitLogRepository.save(commitLog);
            }
        }
    }
}
