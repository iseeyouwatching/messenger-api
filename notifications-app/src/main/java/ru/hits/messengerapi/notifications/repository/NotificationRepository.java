package ru.hits.messengerapi.notifications.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.hits.messengerapi.notifications.entity.NotificationEntity;
import ru.hits.messengerapi.notifications.enumeration.NotificationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностью {@link NotificationEntity}.
 */
@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID>,
        JpaSpecificationExecutor<NotificationEntity> {

    /**
     * Возвращает количество уведомлений определенного статуса, принадлежащих пользователю
     * с указанным идентификатором.
     *
     * @param userId идентификатор пользователя.
     * @param status статус уведомления.
     * @return количество уведомлений с указанным статусом и идентификатором пользователя.
     */
    Long countByUserIdAndStatus(UUID userId, NotificationStatus status);

    /**
     * Отмечает указанные уведомления как прочитанные с указанным временем чтения и статусом.
     *
     * @param notificationsIDs список идентификаторов уведомлений.
     * @param status статус, в который необходимо перевести уведомления.
     * @param readTime время прочтения уведомлений.
     */
    @Modifying
    @Query("UPDATE NotificationEntity n SET n.status = :status, n.readTime=:readTime WHERE n.id IN :notificationsIDs")
    void markAsRead(List<UUID> notificationsIDs, NotificationStatus status, LocalDateTime readTime);

    /**
     * Отмечает указанные уведомления как непрочитанные с указанным временем чтения и статусом.
     *
     * @param notificationsIDs список идентификаторов уведомлений.
     * @param status статус, в который необходимо перевести уведомления.
     * @param readTime время чтения уведомлений.
     */
    @Modifying
    @Query("UPDATE NotificationEntity n SET n.status = :status, n.readTime=:readTime WHERE n.id IN :notificationsIDs")
    void markAsUnread(List<UUID> notificationsIDs, NotificationStatus status, LocalDateTime readTime);

}
