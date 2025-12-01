package com.javahub.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_dashboards")
public class UserDashboard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "user_id", nullable = false, unique = true)
    private int userId;

    @Column(name = "quizzes_taken", nullable = false)
    private int quizzesTaken = 0;

    @Column(name = "total_correct_answers", nullable = false)
    private int totalCorrectAnswers = 0;

    @Column(name = "last_quiz_score")
    private Double lastQuizScore = null;

    @Column(name = "topics_viewed", nullable = false)
    private int topicsViewed = 0;

    @Column(name = "courses_visited", nullable = false)
    private int coursesVisited = 0;

    @Column(name = "practice_attempts", nullable = false)
    private int practiceAttempts = 0;

    @Column(name = "last_compiled_output", columnDefinition = "TEXT")
    private String lastCompiledOutput = null;

    @Column(name = "last_active", columnDefinition = "TIMESTAMP")
    private LocalDateTime lastActive;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (lastActive == null) {
            lastActive = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastActive = LocalDateTime.now();
    }

    // Default constructor (required by JPA)
    public UserDashboard() {
    }

    // Constructor with userId
    public UserDashboard(int userId) {
        this.userId = userId;
        this.quizzesTaken = 0;
        this.totalCorrectAnswers = 0;
        this.topicsViewed = 0;
        this.coursesVisited = 0;
        this.practiceAttempts = 0;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getQuizzesTaken() {
        return quizzesTaken;
    }

    public int getTotalCorrectAnswers() {
        return totalCorrectAnswers;
    }

    public Double getLastQuizScore() {
        return lastQuizScore;
    }

    public int getTopicsViewed() {
        return topicsViewed;
    }

    public int getCoursesVisited() {
        return coursesVisited;
    }

    public int getPracticeAttempts() {
        return practiceAttempts;
    }

    public String getLastCompiledOutput() {
        return lastCompiledOutput;
    }

    public LocalDateTime getLastActive() {
        return lastActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setQuizzesTaken(int quizzesTaken) {
        this.quizzesTaken = quizzesTaken;
    }

    public void setTotalCorrectAnswers(int totalCorrectAnswers) {
        this.totalCorrectAnswers = totalCorrectAnswers;
    }

    public void setLastQuizScore(Double lastQuizScore) {
        this.lastQuizScore = lastQuizScore;
    }

    public void setTopicsViewed(int topicsViewed) {
        this.topicsViewed = topicsViewed;
    }

    public void setCoursesVisited(int coursesVisited) {
        this.coursesVisited = coursesVisited;
    }

    public void setPracticeAttempts(int practiceAttempts) {
        this.practiceAttempts = practiceAttempts;
    }

    public void setLastCompiledOutput(String lastCompiledOutput) {
        this.lastCompiledOutput = lastCompiledOutput;
    }

    public void setLastActive(LocalDateTime lastActive) {
        this.lastActive = lastActive;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}



