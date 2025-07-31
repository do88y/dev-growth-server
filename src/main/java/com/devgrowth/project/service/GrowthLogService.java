package com.devgrowth.project.service;

import com.devgrowth.project.model.CommitLog;
import com.devgrowth.project.model.GrowthLog;
import com.devgrowth.project.model.User;
import com.devgrowth.project.repository.CommitLogRepository;
import com.devgrowth.project.repository.GrowthLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GrowthLogService {

    private final GrowthLogRepository growthLogRepository;
    private final CommitLogRepository commitLogRepository;

    public void updateDailyGrowthLog(User user, LocalDate date) {
        val dailyCommits = commitLogRepository.findByUserAndCommitDateBetween(
                user, date.atStartOfDay(), date.plusDays(1).atStartOfDay());

        val avgScore = (float) dailyCommits.stream()
                .filter(commit -> commit.getEvaluationStatus() == CommitLog.EvaluationStatus.EVALUATED && commit.getScore() != null)
                .mapToDouble(CommitLog::getScore)
                .average()
                .orElse(0.0);

        val streakDay = calculateStreak(user, date, !dailyCommits.isEmpty());

        val growthLog = growthLogRepository.findByUserAndDate(user, date)
                .orElseGet(() -> GrowthLog.builder().user(user).date(date).build());

        growthLog.setCommitCount(dailyCommits.size());
        growthLog.setAvgScore(avgScore);
        growthLog.setStreakDay(streakDay);

        growthLogRepository.save(growthLog);
    }

    private int calculateStreak(User user, LocalDate currentDate, boolean hasCommitsToday) {
        val yesterdayLog = growthLogRepository.findByUserAndDate(user, currentDate.minusDays(1));

        return yesterdayLog
                .map(log -> log.getCommitCount() > 0 ? log.getStreakDay() + 1 : (hasCommitsToday ? 1 : 0))
                .orElse(hasCommitsToday ? 1 : 0);
    }

    @Transactional(readOnly = true)
    public List<GrowthLog> findGrowthLogs(User user, LocalDate startDate, LocalDate endDate) {
        return growthLogRepository.findByUserAndDateBetweenOrderByDateDesc(user, startDate, endDate);
    }
}
