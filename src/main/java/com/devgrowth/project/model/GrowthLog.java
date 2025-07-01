
package com.devgrowth.project.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "growth_log")
@Getter
@Setter
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
}
