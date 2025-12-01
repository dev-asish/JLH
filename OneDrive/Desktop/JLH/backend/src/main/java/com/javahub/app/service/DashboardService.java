package com.javahub.app.service;

import com.javahub.app.model.UserDashboard;
import com.javahub.app.repository.UserDashboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class DashboardService {

    @Autowired
    private UserDashboardRepository dashboardRepository;

    public UserDashboard createDashboard(int userId) {
        UserDashboard dashboard = buildDefaultDashboard(userId);
        return dashboardRepository.save(dashboard);
    }

    public Optional<UserDashboard> getDashboardByUserId(int userId) {
        return Optional.of(getOrCreateDashboard(userId));
    }

    public UserDashboard getOrCreateDashboard(int userId) {
        return dashboardRepository.findByUserId(userId)
                .orElseGet(() -> dashboardRepository.save(buildDefaultDashboard(userId)));
    }

    private UserDashboard buildDefaultDashboard(int userId) {
        UserDashboard dashboard = new UserDashboard();
        dashboard.setUserId(userId);
        dashboard.setQuizzesTaken(0);
        dashboard.setTotalCorrectAnswers(0);
        dashboard.setLastQuizScore(0.0);
        dashboard.setTopicsViewed(0);
        dashboard.setCoursesVisited(0);
        dashboard.setPracticeAttempts(0);
        dashboard.setLastCompiledOutput(null);
        dashboard.setCreatedAt(LocalDateTime.now());
        dashboard.setLastActive(LocalDateTime.now());
        return dashboard;
    }

    @Transactional
    public void incrementTopicsViewed(int userId) {
        UserDashboard dashboard = getOrCreateDashboard(userId);
        dashboard.setTopicsViewed(dashboard.getTopicsViewed() + 1);
        dashboard.setLastActive(LocalDateTime.now());
        dashboardRepository.save(dashboard);
    }

    @Transactional
    public void incrementCoursesVisited(int userId) {
        UserDashboard dashboard = getOrCreateDashboard(userId);
        dashboard.setCoursesVisited(dashboard.getCoursesVisited() + 1);
        dashboard.setLastActive(LocalDateTime.now());
        dashboardRepository.save(dashboard);
    }

    @Transactional
    public void incrementPracticeAttempts(int userId) {
        UserDashboard dashboard = getOrCreateDashboard(userId);
        dashboard.setPracticeAttempts(dashboard.getPracticeAttempts() + 1);
        dashboard.setLastActive(LocalDateTime.now());
        dashboardRepository.save(dashboard);
    }

    @Transactional
    public void updateQuizStats(int userId, int correctAnswers, double score) {
        UserDashboard dashboard = getOrCreateDashboard(userId);
        dashboard.setQuizzesTaken(dashboard.getQuizzesTaken() + 1);
        dashboard.setTotalCorrectAnswers(dashboard.getTotalCorrectAnswers() + correctAnswers);
        dashboard.setLastQuizScore(score);
        dashboard.setLastActive(LocalDateTime.now());
        dashboardRepository.save(dashboard);
    }

    @Transactional
    public void updateLastCompiledOutput(int userId, String output) {
        UserDashboard dashboard = getOrCreateDashboard(userId);
        dashboard.setLastCompiledOutput(output);
        dashboard.setLastActive(LocalDateTime.now());
        dashboardRepository.save(dashboard);
    }

    @Transactional
    public UserDashboard updateDashboard(int userId, UserDashboard dashboardData, Map<String, Boolean> fieldsToUpdate) {
        UserDashboard dashboard = getOrCreateDashboard(userId);
        
        // Only update fields that were explicitly provided
        if (fieldsToUpdate.getOrDefault("quizzesTaken", false)) {
            dashboard.setQuizzesTaken(dashboardData.getQuizzesTaken());
        }
        if (fieldsToUpdate.getOrDefault("totalCorrectAnswers", false)) {
            dashboard.setTotalCorrectAnswers(dashboardData.getTotalCorrectAnswers());
        }
        if (fieldsToUpdate.getOrDefault("lastQuizScore", false)) {
            dashboard.setLastQuizScore(dashboardData.getLastQuizScore());
        }
        if (fieldsToUpdate.getOrDefault("topicsViewed", false)) {
            dashboard.setTopicsViewed(dashboardData.getTopicsViewed());
        }
        if (fieldsToUpdate.getOrDefault("coursesVisited", false)) {
            dashboard.setCoursesVisited(dashboardData.getCoursesVisited());
        }
        if (fieldsToUpdate.getOrDefault("practiceAttempts", false)) {
            dashboard.setPracticeAttempts(dashboardData.getPracticeAttempts());
        }
        if (fieldsToUpdate.getOrDefault("lastCompiledOutput", false)) {
            dashboard.setLastCompiledOutput(dashboardData.getLastCompiledOutput());
        }
        
        dashboard.setLastActive(LocalDateTime.now());
        return dashboardRepository.save(dashboard);
    }
}

