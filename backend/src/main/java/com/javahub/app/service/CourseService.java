package com.javahub.app.service;

import com.javahub.app.model.Course;
import com.javahub.app.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(int id) {
        Optional<Course> course = courseRepository.findById(id);
        return course.orElse(null);
    }

    public Course addCourse(Course course) {
        return courseRepository.save(course);
    }

    public boolean deleteCourse(int id) {
        if (courseRepository.existsById(id)) {
            courseRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
