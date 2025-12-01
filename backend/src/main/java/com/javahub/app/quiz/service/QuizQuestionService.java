package com.javahub.app.quiz.service;

import com.javahub.app.quiz.model.QuizQuestion;
import com.javahub.app.quiz.repository.QuizQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuizQuestionService {

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    public List<QuizQuestion> getAllQuestions() {
        return quizQuestionRepository.findAll();
    }

    public List<QuizQuestion> getQuestionsByTopic(String topic) {
        return quizQuestionRepository.findByTopic(topic);
    }

    public Map<String, Object> evaluateQuiz(List<Map<String, Object>> answers) {
        int totalQuestions = 0;
        int correctAnswers = 0;
        Map<Integer, QuizQuestion> questionMap = new HashMap<>();
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> detailedResults = new java.util.ArrayList<>();

        // Load all questions that were answered
        for (Map<String, Object> answer : answers) {
            Integer questionId = (Integer) answer.get("id");
            if (questionId != null) {
                QuizQuestion question = quizQuestionRepository.findById(questionId).orElse(null);
                if (question != null) {
                    questionMap.put(questionId, question);
                }
            }
        }

        // Evaluate each answer
        for (Map<String, Object> answer : answers) {
            Integer questionId = (Integer) answer.get("id");
            String selectedOption = (String) answer.get("selectedOption");
            
            if (questionId == null || selectedOption == null) {
                continue;
            }

            QuizQuestion question = questionMap.get(questionId);
            if (question == null) {
                continue;
            }

            totalQuestions++;
            boolean isCorrect = question.getCorrectOption().equalsIgnoreCase(selectedOption);
            
            if (isCorrect) {
                correctAnswers++;
            }

            Map<String, Object> detail = new HashMap<>();
            detail.put("questionId", questionId);
            detail.put("question", question.getQuestion());
            detail.put("selectedOption", selectedOption);
            detail.put("correctOption", question.getCorrectOption());
            detail.put("isCorrect", isCorrect);
            detail.put("explanation", question.getExplanation());
            detailedResults.add(detail);
        }

        result.put("totalQuestions", totalQuestions);
        result.put("correctAnswers", correctAnswers);
        result.put("score", totalQuestions > 0 ? (correctAnswers * 100.0 / totalQuestions) : 0);
        result.put("results", detailedResults);

        return result;
    }
}



