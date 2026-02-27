package com.blackpearl.service;

import com.blackpearl.dto.NotificationDto;
import com.blackpearl.exception.ResourceNotFoundException;
import com.blackpearl.model.Notification;
import com.blackpearl.model.User;
import com.blackpearl.repository.NotificationRepository;
import com.blackpearl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public List<NotificationDto> getNotificationsForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        return notificationRepository.findByUserIdOrBroadcast(user.getId()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<NotificationDto> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificationDto markRead(Long id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        n.setRead(true);
        return convertToDto(notificationRepository.save(n));
    }

    @Transactional
    public void markAllRead() {
        List<Notification> notifications = notificationRepository.findAll();
        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    @Transactional
    public NotificationDto sendNotification(NotificationDto dto) {
        User target = null;
        if (dto.getUserId() != null) {
            target = userRepository.findById(dto.getUserId()).orElse(null);
        }
        Notification n = Notification.builder()
                .user(target)
                .title(dto.getTitle())
                .message(dto.getMessage())
                .type(dto.getType() != null ? dto.getType() : Notification.Type.INFO)
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();
        return convertToDto(notificationRepository.save(n));
    }

    @Transactional
    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Notification not found");
        }
        notificationRepository.deleteById(id);
    }

    public NotificationDto convertToDto(Notification n) {
        return NotificationDto.builder()
                .id(n.getId())
                .userId(n.getUser() != null ? n.getUser().getId() : null)
                .userEmail(n.getUser() != null ? n.getUser().getEmail() : null)
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
