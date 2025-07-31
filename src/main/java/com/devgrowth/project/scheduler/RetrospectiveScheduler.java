package com.devgrowth.project.scheduler;

import com.devgrowth.project.model.User;
import com.devgrowth.project.repository.UserRepository;
import com.devgrowth.project.service.NotificationService;
import com.devgrowth.project.service.RetrospectiveService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RetrospectiveScheduler {

    private final UserRepository userRepository;
    private final RetrospectiveService retrospectiveService;
    private final NotificationService notificationService;

    // 매주 월요일 오전 9시에 실행
    @Scheduled(cron = "0 0 9 * * MON")
    public void generateAndSendWeeklyRetrospectives() {
        userRepository.findAll().forEach(user -> {
            val retrospective = retrospectiveService.generateWeeklyRetrospective(user);
            notificationService.sendWeeklyRetrospective(user, retrospective);
        });
    }
}
