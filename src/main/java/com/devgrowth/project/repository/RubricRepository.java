
package com.devgrowth.project.repository;

import com.devgrowth.project.model.Rubric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RubricRepository extends JpaRepository<Rubric, Long> {
    List<Rubric> findByIsDefaultTrue();
    List<Rubric> findByUserId(Long userId);
}
