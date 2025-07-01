
package com.devgrowth.project.repository;

import com.devgrowth.project.model.CommitLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommitLogRepository extends JpaRepository<CommitLog, Long> {
    Optional<CommitLog> findByCommitHash(String commitHash);
}
