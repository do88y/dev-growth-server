
package com.devgrowth.project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "commit_log")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "user")
public class CommitLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "repo_name")
    private String repoName;

    @Column(name = "commit_hash")
    private String commitHash;

    @Column(name = "commit_date")
    private LocalDateTime commitDate;

    @Column(length = 1000)
    private String message;

    @Column(name = "lines_added")
    private int linesAdded;

    @Column(name = "lines_deleted")
    private int linesDeleted;

    @Column(name = "diff_url")
    private String diffUrl;

    @Lob
    @Column(name = "code_diff")
    private String codeDiff;

    @Enumerated(EnumType.STRING)
    @Column(name = "evaluation_status")
    private EvaluationStatus evaluationStatus;

    private Float score;

    @Lob
    @Column(name = "evaluation_result")
    private String evaluationResult;

    public enum EvaluationStatus {
        PENDING, EVALUATED, SKIPPED
    }

    public void updateEvaluation(EvaluationStatus status, Float score, String result) {
        this.evaluationStatus = status;
        this.score = score;
        this.evaluationResult = result;
    }
}
