
package com.devgrowth.project.repository;

import com.devgrowth.project.model.TrackedRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackedRepositoryRepository extends JpaRepository<TrackedRepository, Long> {
    List<TrackedRepository> findByUserIdAndIsActive(Long userId, boolean isActive);
}
