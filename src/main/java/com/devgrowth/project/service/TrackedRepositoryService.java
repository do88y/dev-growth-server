package com.devgrowth.project.service;

import com.devgrowth.project.model.CommitLog;
import com.devgrowth.project.model.TrackedRepository;
import com.devgrowth.project.model.User;
import com.devgrowth.project.repository.CommitLogRepository;
import com.devgrowth.project.repository.TrackedRepositoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                            val newRepo = TrackedRepository.builder()
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
        commits.stream()
                .map(commitData -> (String) commitData.get("sha"))
                .filter(commitHash -> !commitLogRepository.existsByCommitHash(commitHash))
                .forEach(commitHash -> {
                    val detailedCommit = gitHubService.getCommitDetails(user, owner, repoName, commitHash);
                    val commitInfo = (Map<String, Object>) detailedCommit.get("commit");
                    val authorInfo = (Map<String, Object>) commitInfo.get("author");
                    val stats = (Map<String, Object>) detailedCommit.get("stats");
                    val files = (List<Map<String, Object>>) detailedCommit.get("files");

                    val codeDiff = files.stream()
                            .filter(file -> file.containsKey("patch"))
                            .map(file -> (String) file.get("patch"))
                            .collect(Collectors.joining("\n"));

                    val commitLog = CommitLog.builder()
                            .user(user)
                            .repoName(owner + "/" + repoName)
                            .commitHash(commitHash)
                            .message((String) commitInfo.get("message"))
                            .commitDate(LocalDateTime.parse((String) authorInfo.get("date"), DateTimeFormatter.ISO_DATE_TIME))
                            .linesAdded((Integer) stats.get("additions"))
                            .linesDeleted((Integer) stats.get("deletions"))
                            .diffUrl((String) detailedCommit.get("html_url"))
                            .codeDiff(codeDiff)
                            .evaluationStatus(CommitLog.EvaluationStatus.PENDING)
                            .build();

                    commitLogRepository.save(commitLog);
                });
    }
}
