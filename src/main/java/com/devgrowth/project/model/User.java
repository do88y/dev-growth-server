
package com.devgrowth.project.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String nickname;

    @Column(name = "github_id", unique = true)
    private String githubId;

    @Column(name = "github_token")
    private String githubToken;

    @Enumerated(EnumType.STRING)
    private UserLevel level;

    @Column(name = "notify_time")
    private LocalTime notifyTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "notify_channel")
    private NotifyChannel notifyChannel;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum UserLevel {
        BEGINNER, INTERMEDIATE, ADVANCED
    }

    public enum NotifyChannel {
        EMAIL, SMS
    }

    public void updateOAuthInfo(String nickname, String email, String githubToken) {
        this.nickname = nickname;
        this.email = email;
        this.githubToken = githubToken;
    }

    public void updateNotificationSettings(LocalTime notifyTime, NotifyChannel notifyChannel) {
        this.notifyTime = notifyTime;
        this.notifyChannel = notifyChannel;
    }
}
