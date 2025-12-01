package com.javahub.app.repository;

import com.javahub.app.model.CourseContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseContentRepository extends JpaRepository<CourseContent, Integer> {
    List<CourseContent> findByCourseIdOrderByOrderNumberAsc(int courseId);
}






