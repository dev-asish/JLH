package com.javahub.app.controller;

import com.javahub.app.model.Topic;
import com.javahub.app.model.User;
import com.javahub.app.service.DashboardService;
import com.javahub.app.service.TopicService;
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
@RequestMapping("/topics")
public class TopicController {

    @Autowired
    private TopicService topicService;

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
    public ResponseEntity<List<Topic>> getAllTopics() {
        List<Topic> topics = topicService.getAllTopics();
        return ResponseEntity.ok(topics);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Topic> getTopicById(@PathVariable int id, HttpServletRequest request) {
        Topic topic = topicService.getTopicById(id);
        if (topic != null) {
            // Track topic view in dashboard
            int userId = getUserIdFromRequest(request);
            if (userId > 0) {
                dashboardService.incrementTopicsViewed(userId);
            }
            return ResponseEntity.ok(topic);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}



