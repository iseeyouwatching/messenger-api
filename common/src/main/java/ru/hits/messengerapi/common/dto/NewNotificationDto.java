package ru.hits.messengerapi.common.dto;

import lombok.Builder;
import lombok.Data;
import ru.hits.messengerapi.common.enumeration.NotificationType;

import java.util.UUID;

/**
 * DTO для передачи информации о новом уведомлении пользователя.
 */
@Data
@Builder
public class NewNotificationDto {

    /**
     * Идентификатор пользователя.
     */
    private UUID userId;

    /**
     * Тип уведомления.
     */
    private NotificationType type;

    /**
     * Текст уведомления.
     */
    private String text;

}
