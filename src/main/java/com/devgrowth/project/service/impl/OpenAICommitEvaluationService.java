package com.devgrowth.project.service.impl;

import com.devgrowth.project.dto.CommitEvaluationResponse;
import com.devgrowth.project.model.CommitLog;
import com.devgrowth.project.service.CommitEvaluationService;
import lombok.val;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class OpenAICommitEvaluationService implements CommitEvaluationService {

    private final ChatClient chatClient;

    public OpenAICommitEvaluationService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public CommitEvaluationResponse evaluateCommit(CommitLog commitLog) {
        val prompt = """
                You are a code reviewer. Evaluate the following commit based on its message and code changes.
                Provide constructive feedback focusing on code quality, best practices, potential bugs, and maintainability.
                Also, assess the clarity and conciseness of the commit message in relation to the code changes.

                Commit Message: %s
                Lines Added: %d
                Lines Deleted: %d
                Code Changes (diff):
                %s

                Provide a brief, actionable feedback in Korean. Ensure the feedback is well-formatted with line breaks for readability.
                Respond in JSON format with 'score' (float from 0.0 to 10.0) and 'feedback' (string) fields.
                """.formatted(commitLog.getMessage(), commitLog.getLinesAdded(), commitLog.getLinesDeleted(), commitLog.getCodeDiff());

        return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(CommitEvaluationResponse.class);
    }
}
