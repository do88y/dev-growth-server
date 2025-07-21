package com.devgrowth.project.service;

import com.devgrowth.project.dto.CommitEvaluationResponse;
import com.devgrowth.project.model.CommitLog;

public interface CommitEvaluationService {
    CommitEvaluationResponse evaluateCommit(CommitLog commitLog);
}
