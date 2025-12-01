package com.javahub.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "course_content")
public class CourseContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "course_id", nullable = false)
    private int courseId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "order_number", nullable = false)
    private int orderNumber;

    // Default constructor (required by JPA)
    public CourseContent() {
    }

    // Constructor with all fields
    public CourseContent(int courseId, String title, String content, int orderNumber) {
        this.courseId = courseId;
        this.title = title;
        this.content = content;
        this.orderNumber = orderNumber;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }
}






