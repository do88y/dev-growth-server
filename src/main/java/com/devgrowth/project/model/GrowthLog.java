
package com.devgrowth.project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "growth_log")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "user")
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
}
