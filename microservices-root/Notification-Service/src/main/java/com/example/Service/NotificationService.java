package com.example.Service;

import com.example.Model.Entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface NotificationService {
    Slice<Notification> findAll(int page);
    Optional<Notification> getNotificationById(String id);
    Notification saveNotification(Notification notification);
    void deleteNotificationById(String id);
}
