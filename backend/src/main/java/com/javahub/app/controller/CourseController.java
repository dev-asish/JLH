package com.javahub.app.controller;

import com.javahub.app.model.Course;
import com.javahub.app.model.User;
import com.javahub.app.service.CourseService;
import com.javahub.app.service.DashboardService;
import com.javahub.app.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

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

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses(HttpServletRequest request) {
        List<Course> courses = courseService.getAllCourses();
        // Track course list view (counts as visiting courses)
        int userId = getUserIdFromRequest(request);
        if (userId > 0 && !courses.isEmpty()) {
            dashboardService.incrementCoursesVisited(userId);
        }
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable int id, HttpServletRequest request) {
        Course course = courseService.getCourseById(id);
        if (course != null) {
            // Track course view in dashboard
            int userId = getUserIdFromRequest(request);
            if (userId > 0) {
                dashboardService.incrementCoursesVisited(userId);
            }
            return ResponseEntity.ok(course);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Course> addCourse(@RequestBody Course course) {
        Course newCourse = courseService.addCourse(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCourse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable int id) {
        boolean deleted = courseService.deleteCourse(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}



