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

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID>,
        JpaSpecificationExecutor<NotificationEntity> {

    Long countByUserIdAndStatus(UUID userId, NotificationStatus status);

    @Modifying
    @Query("UPDATE NotificationEntity n SET n.status = :status, n.readTime=:readTime WHERE n.id IN :notificationsIDs")
    void markAsRead(List<UUID> notificationsIDs, NotificationStatus status, LocalDateTime readTime);

    @Modifying
    @Query("UPDATE NotificationEntity n SET n.status = :status, n.readTime=:readTime WHERE n.id IN :notificationsIDs")
    void markAsUnread(List<UUID> notificationsIDs, NotificationStatus status, LocalDateTime readTime);

}
