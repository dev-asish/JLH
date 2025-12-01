package com.javahub.app.practice.controller;

import com.javahub.app.model.User;
import com.javahub.app.practice.model.PracticeQuestion;
import com.javahub.app.practice.service.PracticeQuestionService;
import com.javahub.app.service.DashboardService;
import com.javahub.app.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/practice")
public class PracticeQuestionController {

    @Autowired
    private PracticeQuestionService practiceQuestionService;

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
    public ResponseEntity<List<PracticeQuestion>> getAllQuestions() {
        List<PracticeQuestion> questions = practiceQuestionService.getAllQuestions();
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/questions/{id}")
    public ResponseEntity<PracticeQuestion> getQuestionById(@PathVariable int id, HttpServletRequest request) {
        PracticeQuestion question = practiceQuestionService.getQuestionById(id);
        if (question != null) {
            // Track practice attempt in dashboard
            int userId = getUserIdFromRequest(request);
            if (userId > 0) {
                dashboardService.incrementPracticeAttempts(userId);
            }
            return ResponseEntity.ok(question);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}



