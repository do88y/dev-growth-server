package com.devgrowth.project.service;

import com.devgrowth.project.model.GrowthLog;
import com.devgrowth.project.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RetrospectiveService {

    private final GrowthLogService growthLogService;
    private final ChatClient chatClient;

    public RetrospectiveService(GrowthLogService growthLogService, ChatClient.Builder chatClientBuilder) {
        this.growthLogService = growthLogService;
        this.chatClient = chatClientBuilder.build();
    }

    public String generateWeeklyRetrospective(User user) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY).minusWeeks(1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        List<GrowthLog> weeklyLogs = growthLogService.findGrowthLogs(user, startOfWeek, endOfWeek);

        if (weeklyLogs.isEmpty()) {
            return "지난 주 활동 기록이 없습니다.";
        }

        String weeklyDataSummary = weeklyLogs.stream()
                .map(log -> String.format("날짜: %s, 커밋 수: %d, 평균 점수: %.2f, 연속 커밋: %d일",
                        log.getDate(), log.getCommitCount(), log.getAvgScore(), log.getStreakDay()))
                .collect(Collectors.joining("\n"));

        String prompt = String.format(
                "You are a friendly coach for a developer. Based on the following weekly activity data, " +
                        "generate a supportive and insightful weekly retrospective. " +
                        "Highlight achievements, identify patterns, and provide actionable advice for the next week. " +
                        "Weekly Data:\n%s\n" +
                        "Please provide the retrospective in Korean, using Markdown for formatting.",
                weeklyDataSummary
        );

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
