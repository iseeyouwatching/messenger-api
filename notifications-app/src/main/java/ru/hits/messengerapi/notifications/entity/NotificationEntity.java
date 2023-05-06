package ru.hits.messengerapi.notifications.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import ru.hits.messengerapi.notifications.enumeration.NotificationStatus;
import ru.hits.messengerapi.common.enumeration.NotificationType;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

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

    private NotificationType type;

    private String text;

    @Column(name = "user_id")
    private UUID userId;

    private NotificationStatus status;

    @Column(name = "read_time")
    private LocalDateTime readTime;

    @Column(name = "receive_time")
    private LocalDateTime receiveTime;

}
