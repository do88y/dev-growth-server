
package com.devgrowth.project.repository;

import com.devgrowth.project.model.CommitLog;
import com.devgrowth.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommitLogRepository extends JpaRepository<CommitLog, Long> {
    Optional<CommitLog> findByCommitHash(String commitHash);
    List<CommitLog> findByUserAndRepoNameOrderByCommitDateDesc(User user, String repoName);
    List<CommitLog> findByUserAndCommitDateBetween(User user, LocalDateTime startOfDay, LocalDateTime endOfDay);
    List<CommitLog> findByUserAndCommitDateBetweenAndEvaluationStatus(User user, LocalDateTime startOfDay, LocalDateTime endOfDay, CommitLog.EvaluationStatus status);
}

