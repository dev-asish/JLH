package com.javahub.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "topics")
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "difficulty", length = 20)
    private String difficulty;

    @Column(name = "category", length = 100)
    private String category;

    // Default constructor (required by JPA)
    public Topic() {
    }

    // Constructor with all fields
    public Topic(String title, String content, String difficulty) {
        this.title = title;
        this.content = content;
        this.difficulty = difficulty;
    }

    // Constructor with category
    public Topic(String title, String content, String difficulty, String category) {
        this.title = title;
        this.content = content;
        this.difficulty = difficulty;
        this.category = category;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getCategory() {
        return category;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}



