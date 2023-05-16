package ru.hits.messengerapi.common.dto;

import lombok.*;
import ru.hits.messengerapi.common.enumeration.NotificationType;

import java.util.UUID;

/**
 * DTO для передачи информации о новом уведомлении пользователя.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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
