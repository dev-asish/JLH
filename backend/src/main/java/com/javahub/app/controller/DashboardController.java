package com.javahub.app.controller;

import com.javahub.app.model.User;
import com.javahub.app.model.UserDashboard;
import com.javahub.app.service.DashboardService;
import com.javahub.app.service.UserService;
import com.javahub.app.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

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
        throw new RuntimeException("User not found");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyDashboard(HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request);
            Optional<UserDashboard> dashboardOpt = dashboardService.getDashboardByUserId(userId);
            
            if (dashboardOpt.isPresent()) {
                UserDashboard dashboard = dashboardOpt.get();
                Map<String, Object> response = new HashMap<>();
                response.put("quizzesTaken", dashboard.getQuizzesTaken());
                response.put("totalCorrectAnswers", dashboard.getTotalCorrectAnswers());
                response.put("lastQuizScore", dashboard.getLastQuizScore());
                response.put("topicsViewed", dashboard.getTopicsViewed());
                response.put("coursesVisited", dashboard.getCoursesVisited());
                response.put("practiceAttempts", dashboard.getPracticeAttempts());
                response.put("lastCompiledOutput", dashboard.getLastCompiledOutput());
                response.put("lastActive", dashboard.getLastActive());
                response.put("createdAt", dashboard.getCreatedAt());
                return ResponseEntity.ok(response);
            } else {
                // Return empty dashboard for new user
                Map<String, Object> response = new HashMap<>();
                response.put("quizzesTaken", 0);
                response.put("totalCorrectAnswers", 0);
                response.put("lastQuizScore", null);
                response.put("topicsViewed", 0);
                response.put("coursesVisited", 0);
                response.put("practiceAttempts", 0);
                response.put("lastCompiledOutput", null);
                response.put("lastActive", null);
                response.put("createdAt", null);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch dashboard: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateDashboard(@RequestBody Map<String, Object> dashboardData, HttpServletRequest request) {
        try {
            int userId = getUserIdFromRequest(request);
            UserDashboard dashboard = new UserDashboard();
            Map<String, Boolean> fieldsToUpdate = new HashMap<>();
            
            if (dashboardData.containsKey("quizzesTaken")) {
                dashboard.setQuizzesTaken(((Number) dashboardData.get("quizzesTaken")).intValue());
                fieldsToUpdate.put("quizzesTaken", true);
            }
            if (dashboardData.containsKey("totalCorrectAnswers")) {
                dashboard.setTotalCorrectAnswers(((Number) dashboardData.get("totalCorrectAnswers")).intValue());
                fieldsToUpdate.put("totalCorrectAnswers", true);
            }
            if (dashboardData.containsKey("lastQuizScore")) {
                Object score = dashboardData.get("lastQuizScore");
                if (score != null) {
                    dashboard.setLastQuizScore(((Number) score).doubleValue());
                } else {
                    dashboard.setLastQuizScore(null);
                }
                fieldsToUpdate.put("lastQuizScore", true);
            }
            if (dashboardData.containsKey("topicsViewed")) {
                dashboard.setTopicsViewed(((Number) dashboardData.get("topicsViewed")).intValue());
                fieldsToUpdate.put("topicsViewed", true);
            }
            if (dashboardData.containsKey("coursesVisited")) {
                dashboard.setCoursesVisited(((Number) dashboardData.get("coursesVisited")).intValue());
                fieldsToUpdate.put("coursesVisited", true);
            }
            if (dashboardData.containsKey("practiceAttempts")) {
                dashboard.setPracticeAttempts(((Number) dashboardData.get("practiceAttempts")).intValue());
                fieldsToUpdate.put("practiceAttempts", true);
            }
            if (dashboardData.containsKey("lastCompiledOutput")) {
                dashboard.setLastCompiledOutput((String) dashboardData.get("lastCompiledOutput"));
                fieldsToUpdate.put("lastCompiledOutput", true);
            }
            
            UserDashboard updated = dashboardService.updateDashboard(userId, dashboard, fieldsToUpdate);
            
            Map<String, Object> response = new HashMap<>();
            response.put("quizzesTaken", updated.getQuizzesTaken());
            response.put("totalCorrectAnswers", updated.getTotalCorrectAnswers());
            response.put("lastQuizScore", updated.getLastQuizScore());
            response.put("topicsViewed", updated.getTopicsViewed());
            response.put("coursesVisited", updated.getCoursesVisited());
            response.put("practiceAttempts", updated.getPracticeAttempts());
            response.put("lastCompiledOutput", updated.getLastCompiledOutput());
            response.put("lastActive", updated.getLastActive());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update dashboard: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}

