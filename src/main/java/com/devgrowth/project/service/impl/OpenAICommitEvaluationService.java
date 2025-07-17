package com.devgrowth.project.service.impl;

import com.devgrowth.project.model.CommitLog;
import com.devgrowth.project.service.CommitEvaluationService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class OpenAICommitEvaluationService implements CommitEvaluationService {

    private final ChatClient chatClient;

    public OpenAICommitEvaluationService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public String evaluateCommit(CommitLog commitLog) {
        String prompt = String.format(
                "You are a code reviewer. Evaluate the following commit based on its message and code changes. " +
                        "Provide constructive feedback focusing on code quality, best practices, potential bugs, and maintainability. " +
                        "Also, assess the clarity and conciseness of the commit message in relation to the code changes. " +
                        "Commit Message: %s\n" +
                        "Lines Added: %d\n" +
                        "Lines Deleted: %d\n" +
                        "Code Changes (diff):\n%s\n" +
                        "Provide a brief, actionable feedback in Korean.",
                commitLog.getMessage(),
                commitLog.getLinesAdded(),
                commitLog.getLinesDeleted(),
                commitLog.getCodeDiff()
        );

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
