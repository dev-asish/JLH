package com.javahub.app.practice.service;

import com.javahub.app.practice.model.PracticeQuestion;
import com.javahub.app.practice.repository.PracticeQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PracticeQuestionService {

    @Autowired
    private PracticeQuestionRepository practiceQuestionRepository;

    public List<PracticeQuestion> getAllQuestions() {
        return practiceQuestionRepository.findAll();
    }

    public PracticeQuestion getQuestionById(int id) {
        Optional<PracticeQuestion> question = practiceQuestionRepository.findById(id);
        return question.orElse(null);
    }
}



