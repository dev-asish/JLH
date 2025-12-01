package com.javahub.app.practice.repository;

import com.javahub.app.practice.model.PracticeQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PracticeQuestionRepository extends JpaRepository<PracticeQuestion, Integer> {
    Optional<PracticeQuestion> findByTitle(String title);
}


