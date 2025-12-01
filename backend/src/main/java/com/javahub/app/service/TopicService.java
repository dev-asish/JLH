package com.javahub.app.service;

import com.javahub.app.model.Topic;
import com.javahub.app.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TopicService {

    @Autowired
    private TopicRepository topicRepository;

    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    public Topic getTopicById(int id) {
        Optional<Topic> topic = topicRepository.findById(id);
        return topic.orElse(null);
    }
}



