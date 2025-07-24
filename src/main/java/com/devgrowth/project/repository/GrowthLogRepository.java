
package com.devgrowth.project.repository;

import com.devgrowth.project.model.GrowthLog;
import com.devgrowth.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

public interface GrowthLogRepository extends JpaRepository<GrowthLog, Long> {
    Optional<GrowthLog> findByUserIdAndDate(Long userId, LocalDate date);
    List<GrowthLog> findByUserAndDateBetweenOrderByDateDesc(User user, LocalDate startDate, LocalDate endDate);
}
