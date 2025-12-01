package com.javahub.app.quiz.repository;

import com.javahub.app.quiz.model.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Integer> {
    List<QuizQuestion> findByTopic(String topic);
    java.util.Optional<QuizQuestion> findByQuestion(String question);
}



