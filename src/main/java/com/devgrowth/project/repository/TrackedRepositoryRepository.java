
package com.devgrowth.project.repository;

import com.devgrowth.project.model.TrackedRepository;
import com.devgrowth.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrackedRepositoryRepository extends JpaRepository<TrackedRepository, Long> {
    List<TrackedRepository> findByUserAndIsActive(User user, boolean isActive);
    Optional<TrackedRepository> findByUserAndRepoName(User user, String repoName);
    boolean existsByUserAndRepoName(User user, String repoName);
}
