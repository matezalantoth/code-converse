package com.matezalantoth.codeconverse.repository;

import com.matezalantoth.codeconverse.model.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Optional<Notification> getNotificationById(UUID id);

    Optional<Object> deleteNotificationById(UUID id);
}
