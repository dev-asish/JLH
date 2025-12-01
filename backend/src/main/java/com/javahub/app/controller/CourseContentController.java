package com.javahub.app.controller;

import com.javahub.app.model.CourseContent;
import com.javahub.app.service.CourseContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseContentController {

    @Autowired
    private CourseContentService courseContentService;

    @GetMapping("/{id}/content")
    public ResponseEntity<List<CourseContent>> getCourseContent(@PathVariable int id) {
        List<CourseContent> content = courseContentService.getContentForCourse(id);
        return ResponseEntity.ok(content);
    }
}






