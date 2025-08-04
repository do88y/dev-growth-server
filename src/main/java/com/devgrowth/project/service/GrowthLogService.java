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

        // 2. 평균 AI 평가 점수 계산
        float avgScore = (float) dailyCommits.stream()
                .filter(commit -> commit.getEvaluationStatus() == CommitLog.EvaluationStatus.EVALUATED && commit.getScore() != null)
                .mapToDouble(CommitLog::getScore)
                .average()
                .orElse(0.0);

        // 3. 연속 커밋 일수 계산
        int streakDay = calculateStreak(user, date, !dailyCommits.isEmpty());

        // 4. GrowthLog 업데이트 또는 생성
        GrowthLog growthLog = growthLogRepository.findByUserAndDate(user, date)
                .orElseGet(() -> GrowthLog.builder().user(user).date(date).build());

        growthLog.updateStats(commitCount, avgScore, streakDay);

        growthLogRepository.save(growthLog);
    }

    private int calculateStreak(User user, LocalDate currentDate, boolean hasCommitsToday) {
        Optional<GrowthLog> yesterdayLog = growthLogRepository.findByUserAndDate(user, currentDate.minusDays(1));

        return yesterdayLog
                .map(log -> log.getCommitCount() > 0 ? log.getStreakDay() + 1 : (hasCommitsToday ? 1 : 0))
                .orElse(hasCommitsToday ? 1 : 0);
    }

    @Transactional(readOnly = true)
    public List<GrowthLog> findGrowthLogs(User user, LocalDate startDate, LocalDate endDate) {
        return growthLogRepository.findByUserAndDateBetweenOrderByDateDesc(user, startDate, endDate);
    }
}
