package com.javahub.app.quiz.controller;

import com.javahub.app.model.User;
import com.javahub.app.quiz.model.QuizQuestion;
import com.javahub.app.quiz.service.QuizQuestionService;
import com.javahub.app.service.DashboardService;
import com.javahub.app.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/quiz")
public class QuizQuestionController {

    @Autowired
    private QuizQuestionService quizQuestionService;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private UserService userService;

    private int getUserIdFromRequest(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof String) {
                username = (String) authentication.getPrincipal();
            }
        }
        if (username != null) {
            Optional<User> userOpt = userService.findByUsername(username);
            if (userOpt.isPresent()) {
                return userOpt.get().getId();
            }
        }
        return -1; // Return -1 if user not found (shouldn't happen with auth)
    }

    @GetMapping("/questions")
    public ResponseEntity<List<QuizQuestion>> getAllQuestions() {
        List<QuizQuestion> questions = quizQuestionService.getAllQuestions();
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/questions/topic/{topic}")
    public ResponseEntity<List<QuizQuestion>> getQuestionsByTopic(@PathVariable String topic) {
        List<QuizQuestion> questions = quizQuestionService.getQuestionsByTopic(topic);
        return ResponseEntity.ok(questions);
    }

    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitQuiz(@RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> answers = (List<Map<String, Object>>) request.get("answers");
        
        if (answers == null || answers.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Map<String, Object> result = quizQuestionService.evaluateQuiz(answers);
        
        // Track quiz submission in dashboard
        int userId = getUserIdFromRequest(httpRequest);
        if (userId > 0 && result.containsKey("correctAnswers") && result.containsKey("score")) {
            int correctAnswers = ((Number) result.get("correctAnswers")).intValue();
            double score = ((Number) result.get("score")).doubleValue();
            dashboardService.updateQuizStats(userId, correctAnswers, score);
        }
        
        return ResponseEntity.ok(result);
    }
}



