package com.example.Service.Impl;

import com.example.Constant.AppConstant;
import com.example.Model.Entity.Notification;
import com.example.Repository.NotificationRepository;
import com.example.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    @Override
    public Slice<Notification> findAll(int page) {
        Pageable pageable = PageRequest.of(
                page,
                AppConstant.NUMBER_OF_PAGE,
                Sort.by("timestamp")
        );
        return notificationRepository.findAll(pageable);
    }

    @Override
    public Optional<Notification> getNotificationById(String id) {
        return notificationRepository.findById(id);
    }

    @Override
    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public void deleteNotificationById(String id) {
        notificationRepository.deleteById(id);
    }
}
