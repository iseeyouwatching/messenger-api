package ru.hits.messengerapi.notifications.dto;

import lombok.*;
import ru.hits.messengerapi.notifications.enumeration.NotificationStatus;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * DTO для обновления статуса прочтения уведомлений.
 * Объект содержит список идентификаторов уведомлений и статус, в который необходимо перевести
 * указанные уведомления.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationsStatusUpdateDTO {

    /**
     * Список идентификаторов уведомлений, для которых необходимо обновить статус прочтения.
     */
    @NotEmpty(message = "Список идентификаторов уведомлений не может быть пустым.")
    private List<UUID> notificationsIDs;

    /**
     * Статус, в который необходимо перевести указанные уведомления.
     */
    @NotNull(message = "Статус прочтения, в который указанные уведомления необходимо перевести, " +
            "обязателен к заполнению.")
    private NotificationStatus status;

}
