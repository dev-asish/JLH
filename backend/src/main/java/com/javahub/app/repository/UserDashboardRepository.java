package com.javahub.app.repository;

import com.javahub.app.model.UserDashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDashboardRepository extends JpaRepository<UserDashboard, Integer> {
    Optional<UserDashboard> findByUserId(int userId);
}






