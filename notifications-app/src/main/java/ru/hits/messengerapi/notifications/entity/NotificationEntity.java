package ru.hits.messengerapi.notifications.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import ru.hits.messengerapi.notifications.enumeration.NotificationStatus;
import ru.hits.messengerapi.common.enumeration.NotificationType;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сущность уведомления.
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "notification")
public class NotificationEntity {

    /**
     * Идентификатор сущности уведомления.
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
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
     * Идентификатор пользователя, для которого предназначено уведомление.
     */
    @Column(name = "user_id")
    private UUID userId;

    /**
     * Статус уведомления.
     */
    private NotificationStatus status;

    /**
     * Время, когда уведомление было отмечено как прочитанное.
     */
    @Column(name = "read_time")
    private LocalDateTime readTime;

    /**
     * Время, когда уведомление было получено.
     */
    @Column(name = "receive_time")
    private LocalDateTime receiveTime;

}
