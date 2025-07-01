
package com.devgrowth.project.repository;

import com.devgrowth.project.model.GrowthLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.time.LocalDate;

public interface GrowthLogRepository extends JpaRepository<GrowthLog, Long> {
    Optional<GrowthLog> findByUserIdAndDate(Long userId, LocalDate date);
}
