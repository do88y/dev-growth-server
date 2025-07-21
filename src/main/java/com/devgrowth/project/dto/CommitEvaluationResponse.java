package com.devgrowth.project.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommitEvaluationResponse {
    private Float score;
    private String feedback;
}
