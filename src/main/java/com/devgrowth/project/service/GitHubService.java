package com.devgrowth.project.service;

import com.devgrowth.project.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GitHubService {

    private final RestClient restClient;

    public List<Map<String, Object>> getRepositories(User user) {
        String accessToken = user.getGithubToken();
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalStateException("User does not have a valid GitHub token.");
        }

        return restClient.get()
                .uri("https://api.github.com/user/repos?sort=updated&per_page=100")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public List<Map<String, Object>> getCommits(User user, String owner, String repo) {
        String accessToken = user.getGithubToken();
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalStateException("User does not have a valid GitHub token.");
        }

        String url = String.format("https://api.github.com/repos/%s/%s/commits", owner, repo);

        return restClient.get()
                .uri(url)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
