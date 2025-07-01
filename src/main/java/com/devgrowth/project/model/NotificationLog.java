
package com.devgrowth.project.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_log")
@Getter
@Setter
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "send_time")
    private LocalDateTime sendTime;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    public enum NotificationType {
        STREAK_REMINDER, COMMIT_EVALUATION, WEEKLY_REPORT
    }

    public enum NotificationStatus {
        SENT, FAILED
    }
}
