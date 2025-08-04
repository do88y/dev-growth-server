
package com.devgrowth.project.service;

import com.devgrowth.project.model.User;
import com.devgrowth.project.repository.UserRepository;
import com.devgrowth.project.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RestClient restClient;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String githubId = oAuth2User.getAttribute("login");
        String nickname = oAuth2User.getAttribute("name");
        String accessToken = userRequest.getAccessToken().getTokenValue();
        String email = getPrimaryEmail(accessToken);

        User user = userRepository.findByGithubId(githubId)
                .map(existingUser -> {
                    existingUser.updateOAuthInfo(nickname, email, accessToken);
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    return userRepository.save(User.builder()
                            .githubId(githubId)
                            .email(email)
                            .nickname(nickname)
                            .githubToken(accessToken)
                            .createdAt(LocalDateTime.now())
                            .build());
                });

        return new CustomUserDetails(user, oAuth2User.getAttributes());
    }

    private String getPrimaryEmail(String accessToken) {
        List<Map<String, Object>> emails = restClient.get()
                .uri("https://api.github.com/user/emails")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (emails != null) {
            return emails.stream()
                    .filter(emailInfo -> (Boolean) emailInfo.get("primary"))
                    .findFirst()
                    .map(emailInfo -> (String) emailInfo.get("email"))
                    .orElse(null);
        }
        return null;
    }
}
