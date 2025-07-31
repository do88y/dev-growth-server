
package com.devgrowth.project.service;

import com.devgrowth.project.model.User;
import com.devgrowth.project.security.CustomUserDetails;
import com.devgrowth.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RestClient restClient;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        val oAuth2User = super.loadUser(userRequest);

        val githubId = oAuth2User.getAttribute("login");
        val nickname = oAuth2User.getAttribute("name");
        val accessToken = userRequest.getAccessToken().getTokenValue();
        val email = getPrimaryEmail(accessToken);

        val user = userRepository.findByGithubId(githubId)
                .map(existingUser -> {
                    existingUser.updateProfile(nickname, email);
                    existingUser.updateGithubToken(accessToken);
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    return User.builder()
                            .githubId(githubId)
                            .email(email)
                            .nickname(nickname)
                            .githubToken(accessToken)
                            .createdAt(LocalDateTime.now())
                            .build();
                });

        return new CustomUserDetails(user, oAuth2User.getAttributes());
    }

    private String getPrimaryEmail(String accessToken) {
        val emails = restClient.get()
                .uri("https://api.github.com/user/emails")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});

        return Optional.ofNullable(emails)
                .orElseGet(List::of)
                .stream()
                .filter(emailInfo -> (Boolean) emailInfo.get("primary"))
                .map(emailInfo -> (String) emailInfo.get("email"))
                .findFirst()
                .orElse(null);
    }
}
