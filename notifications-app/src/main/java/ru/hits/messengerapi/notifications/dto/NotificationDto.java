package ru.hits.messengerapi.notifications.dto;

import lombok.*;
import ru.hits.messengerapi.common.enumeration.NotificationType;
import ru.hits.messengerapi.notifications.entity.NotificationEntity;
import ru.hits.messengerapi.notifications.enumeration.NotificationStatus;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationDto {

    private UUID id;

    private NotificationType type;

    private String text;

    private NotificationStatus status;

    private String receiveTime;

    public static NotificationDto from(NotificationEntity entity) {
        NotificationDto dto = new NotificationDto();
        dto.setId(entity.getId());
        dto.setType(entity.getType());
        dto.setText(entity.getText());
        dto.setStatus(entity.getStatus());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDateTime = entity.getReceiveTime().format(formatter);
        dto.setReceiveTime(formattedDateTime);
        return dto;
    }

}
