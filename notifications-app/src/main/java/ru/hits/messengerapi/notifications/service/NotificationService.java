package ru.hits.messengerapi.notifications.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.messengerapi.common.dto.NewNotificationDto;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.common.security.JwtUserData;
import ru.hits.messengerapi.notifications.dto.NotificationsStatusUpdateDTO;
import ru.hits.messengerapi.notifications.entity.NotificationEntity;
import ru.hits.messengerapi.notifications.enumeration.NotificationStatus;
import ru.hits.messengerapi.notifications.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void getNotification(NewNotificationDto newNotificationDto) {
        NotificationEntity notification = NotificationEntity.builder()
                .type(newNotificationDto.getType())
                .text(newNotificationDto.getText())
                .userId(newNotificationDto.getUserId())
                .status(NotificationStatus.UNREAD)
                .receiveTime(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
    }

    public Long getUnreadCount() {
        return notificationRepository.countByUserIdAndStatus(getAuthenticatedUserId(), NotificationStatus.UNREAD);
    }

    @Transactional
    public Long markNotificationsAsReadOrUnread(NotificationsStatusUpdateDTO notificationsStatusUpdateDTO) {
        List<UUID> invalidIds = new ArrayList<>();
        for (UUID id: notificationsStatusUpdateDTO.getNotificationsIDs()) {
            if (notificationRepository.findById(id).isEmpty()) {
                invalidIds.add(id);
            }
        }

        if (!invalidIds.isEmpty()) {
            throw new NotFoundException("Уведомления с ID " + invalidIds + " не существуют.");
        }

        if (notificationsStatusUpdateDTO.getStatus().equals(NotificationStatus.READ)) {
            notificationRepository.markAsRead(notificationsStatusUpdateDTO.getNotificationsIDs(),
                    notificationsStatusUpdateDTO.getStatus(), LocalDateTime.now());
        } else if (notificationsStatusUpdateDTO.getStatus().equals(NotificationStatus.UNREAD)) {
            notificationRepository.markAsUnread(notificationsStatusUpdateDTO.getNotificationsIDs(),
                    notificationsStatusUpdateDTO.getStatus(), null);
        }
        return getUnreadCount();
    }

    /**
     * Метод для получения ID аутентифицированного пользователя.
     *
     * @return ID аутентифицированного пользователя.
     */
    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserData userData = (JwtUserData) authentication.getPrincipal();
        return userData.getId();
    }

}
