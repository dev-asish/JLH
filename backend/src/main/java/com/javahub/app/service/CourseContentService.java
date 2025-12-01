package com.javahub.app.service;

import com.javahub.app.model.CourseContent;
import com.javahub.app.repository.CourseContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseContentService {

    @Autowired
    private CourseContentRepository courseContentRepository;

    public List<CourseContent> getContentForCourse(int courseId) {
        return courseContentRepository.findByCourseIdOrderByOrderNumberAsc(courseId);
    }
}






