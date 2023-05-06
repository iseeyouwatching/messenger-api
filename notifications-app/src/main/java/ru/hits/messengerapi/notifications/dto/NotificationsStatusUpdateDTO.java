package ru.hits.messengerapi.notifications.dto;

import lombok.*;
import ru.hits.messengerapi.notifications.enumeration.NotificationStatus;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationsStatusUpdateDTO {

    @NotEmpty(message = "Список идентификаторов уведомлений не может быть пустым.")
    private List<UUID> notificationsIDs;

    @NotNull(message = "Статус прочтения, в который указанные уведомления необходимо перевести, обязателен к заполнению.")
    private NotificationStatus status;

}
