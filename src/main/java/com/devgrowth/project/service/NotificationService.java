package com.devgrowth.project.service;

import com.devgrowth.project.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    public void sendWeeklyRetrospective(User user, String retrospective) {
        // Simulate sending an email or SMS notification
        log.info("===== 주간 회고 알림 전송 =====");
        log.info("수신자: {}", user.getEmail());
        log.info("제목: 주간 성장 회고가 도착했습니다!");
        log.info("내용:\n{}", retrospective);
        log.info("==============================");
    }
}
