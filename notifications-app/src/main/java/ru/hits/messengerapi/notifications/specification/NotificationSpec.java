package ru.hits.messengerapi.notifications.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.hits.messengerapi.common.enumeration.NotificationType;
import ru.hits.messengerapi.notifications.entity.NotificationEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class NotificationSpec {

    public static Specification<NotificationEntity> receivedDateAfter(LocalDateTime dateTime) {
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("receiveTime"), dateTime);
    }

    public static Specification<NotificationEntity> receivedDateBefore(LocalDateTime dateTime) {
        return (root, query, builder) -> builder.lessThanOrEqualTo(root.get("receiveTime"), dateTime);
    }

    public static Specification<NotificationEntity> textLike(String text) {
        return (root, query, builder) -> builder.like(builder.lower(root.get("text")), "%" + text.toLowerCase() + "%");
    }

    public static Specification<NotificationEntity> typeIn(List<NotificationType> types) {
        return (root, query, builder) -> root.get("type").in(types);
    }

    public static Specification<NotificationEntity> userIdEqual(UUID userId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userId"), userId);
    }


}
