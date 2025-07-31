package com.devgrowth.project.scheduler;

import com.devgrowth.project.model.User;
import com.devgrowth.project.repository.UserRepository;
import com.devgrowth.project.service.GrowthLogService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class GrowthLogScheduler {

    private final GrowthLogService growthLogService;
    private final UserRepository userRepository;

    // 매일 자정 (0시 0분 0초)에 실행
    @Scheduled(cron = "0 0 0 * * ?")
    public void dailyGrowthLogUpdate() {
        val today = LocalDate.now();
        userRepository.findAll().forEach(user -> growthLogService.updateDailyGrowthLog(user, today));
    }
}
