package com.devgrowth.project.dto;

import lombok.Value;

@Value
public class CommitEvaluationResponse {
    Float score;
    String feedback;
}
