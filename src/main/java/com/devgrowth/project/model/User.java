
package com.devgrowth.project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "githubToken")
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

    public void updateGithubToken(String githubToken) {
        this.githubToken = githubToken;
    }

    public void updateProfile(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
    }
}
