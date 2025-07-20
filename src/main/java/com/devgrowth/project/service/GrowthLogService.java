package com.devgrowth.project.service;

import com.devgrowth.project.model.CommitLog;
import com.devgrowth.project.model.GrowthLog;
import com.devgrowth.project.model.User;
import com.devgrowth.project.repository.CommitLogRepository;
import com.devgrowth.project.repository.GrowthLogRepository;
import com.devgrowth.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class GrowthLogService {

    private final GrowthLogRepository growthLogRepository;
    private final CommitLogRepository commitLogRepository;
    private final UserRepository userRepository;

    public void updateDailyGrowthLog(User user, LocalDate date) {
        // 1. 해당 날짜의 커밋 수 집계
        List<CommitLog> dailyCommits = commitLogRepository.findByUserAndCommitDateBetween(user,
                date.atStartOfDay(), date.plusDays(1).atStartOfDay());
        int commitCount = dailyCommits.size();

        // 2. 평균 AI 평가 점수 계산 (나중에 구현)
        float avgScore = 0.0f; // TODO: Implement average score calculation

        // 3. 연속 커밋 일수 계산
        int streakDay = calculateStreak(user, date);

        // 4. GrowthLog 업데이트 또는 생성
        GrowthLog growthLog = growthLogRepository.findByUserIdAndDate(user.getId(), date)
                .orElse(new GrowthLog());

        growthLog.setUser(user);
        growthLog.setDate(date);
        growthLog.setCommitCount(commitCount);
        growthLog.setAvgScore(avgScore);
        growthLog.setStreakDay(streakDay);

        growthLogRepository.save(growthLog);
    }

    private int calculateStreak(User user, LocalDate currentDate) {
        int streak = 0;
        LocalDate previousDate = currentDate.minusDays(1);

        Optional<GrowthLog> previousDayLog = growthLogRepository.findByUserIdAndDate(user.getId(), previousDate);

        // If there was a commit yesterday, continue the streak
        if (previousDayLog.isPresent() && previousDayLog.get().getCommitCount() > 0) {
            streak = previousDayLog.get().getStreakDay() + 1;
        } else {
            // If no commit yesterday, check if there's a commit today to start a new streak
            List<CommitLog> todayCommits = commitLogRepository.findByUserAndCommitDateBetween(user,
                    currentDate.atStartOfDay(), currentDate.plusDays(1).atStartOfDay());
            if (!todayCommits.isEmpty()) {
                streak = 1;
            }
        }
        return streak;
    }
}
