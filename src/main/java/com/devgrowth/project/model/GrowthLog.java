
package com.devgrowth.project.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "growth_log")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GrowthLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDate date;

    @Column(name = "commit_count")
    private int commitCount;

    @Column(name = "avg_score")
    private float avgScore;

    @Column(name = "streak_day")
    private int streakDay;

    @Column(columnDefinition = "TEXT")
    private String reflection;

    public void updateReflection(String reflection) {
        this.reflection = reflection;
    }

    public void updateStats(int commitCount, float avgScore, int streakDay) {
        this.commitCount = commitCount;
        this.avgScore = avgScore;
        this.streakDay = streakDay;
    }
}
