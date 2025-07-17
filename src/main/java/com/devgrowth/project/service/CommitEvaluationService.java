package com.devgrowth.project.service;

import com.devgrowth.project.model.CommitLog;

public interface CommitEvaluationService {
    String evaluateCommit(CommitLog commitLog);
}
