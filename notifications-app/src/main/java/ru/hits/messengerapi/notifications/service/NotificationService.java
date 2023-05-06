package ru.hits.messengerapi.notifications.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hits.messengerapi.common.dto.MessageDto;
import ru.hits.messengerapi.notifications.entity.NotificationEntity;
import ru.hits.messengerapi.notifications.enumeration.NotificationStatus;
import ru.hits.messengerapi.notifications.repository.NotificationRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void getMessage(MessageDto messageDto) {
        NotificationEntity notification = NotificationEntity.builder()
                .type(messageDto.getType())
                .text(messageDto.getText())
                .userId(messageDto.getUserId())
                .status(NotificationStatus.UNREAD)
                .receiveTime(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
    }

}
