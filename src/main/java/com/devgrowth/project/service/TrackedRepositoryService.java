package com.devgrowth.project.service;

import com.devgrowth.project.model.TrackedRepository;
import com.devgrowth.project.model.User;
import com.devgrowth.project.repository.TrackedRepositoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TrackedRepositoryService {

    private final TrackedRepositoryRepository trackedRepositoryRepository;

    public void addTrackedRepository(User user, String repoName) {
        if (!trackedRepositoryRepository.existsByUserAndRepoName(user, repoName)) {
            TrackedRepository newRepo = new TrackedRepository();
            newRepo.setUser(user);
            newRepo.setRepoName(repoName);
            newRepo.setActive(true);
            trackedRepositoryRepository.save(newRepo);
        }
    }

    public void removeTrackedRepository(User user, String repoName) {
        trackedRepositoryRepository.findByUserAndRepoName(user, repoName)
                .ifPresent(trackedRepositoryRepository::delete);
    }

    @Transactional(readOnly = true)
    public List<TrackedRepository> findActiveTrackedRepositories(User user) {
        return trackedRepositoryRepository.findByUserAndIsActive(user, true);
    }
}
