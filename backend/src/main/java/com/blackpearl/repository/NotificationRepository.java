package com.blackpearl.repository;

import com.blackpearl.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Get notifications for a specific user OR broadcast (user = null)
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId OR n.user IS NULL ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdOrBroadcast(Long userId);

    List<Notification> findByUserIsNull(); // broadcast notifications
}
