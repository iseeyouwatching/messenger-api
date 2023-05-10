package ru.hits.messengerapi.notifications.dto;

import lombok.*;
import ru.hits.messengerapi.common.enumeration.NotificationType;
import ru.hits.messengerapi.notifications.entity.NotificationEntity;
import ru.hits.messengerapi.notifications.enumeration.NotificationStatus;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * DTO для уведомления, содержащая основную информацию о нем,
 * такую как идентификатор, тип, текст, статус и время получения.
 * Также класс содержит статический метод from, который создает экземпляр класса {@link NotificationDto}
 * на основе сущности {@link NotificationEntity}.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationDto {

    /**
     * Уникальный идентификатор уведомления.
     */
    private UUID id;

    /**
     * Тип уведомления.
     */
    private NotificationType type;

    /**
     * Текст уведомления.
     */
    private String text;

    /**
     * Статус уведомления (прочитано/непрочитано).
     */
    private NotificationStatus status;

    /**
     * Время получения уведомления в формате "yyyy-MM-dd HH:mm".
     */
    private String receiveTime;

    /**
     * Статический метод, который создает экземпляр класса {@link NotificationDto}
     * на основе сущности {@link NotificationEntity}.
     * @param entity сущность уведомления, на основе которой нужно создать объект DTO.
     * @return экземпляр класса {@link NotificationDto}.
     */
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
