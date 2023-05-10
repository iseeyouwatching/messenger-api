package ru.hits.messengerapi.notifications.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.hits.messengerapi.common.enumeration.NotificationType;
import ru.hits.messengerapi.notifications.entity.NotificationEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Класс, представляющий спецификацию для фильтрации уведомлений по различным параметрам.
 */
public class NotificationSpec {

    /**
     * Возвращает спецификацию для поиска уведомлений, дата получения которых позже указанной даты.
     *
     * @param dateTime дата и время, после которых нужно искать уведомления.
     * @return спецификация для поиска уведомлений.
     */
    public static Specification<NotificationEntity> receivedDateAfter(LocalDateTime dateTime) {
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("receiveTime"), dateTime);
    }

    /**
     * Возвращает спецификацию для поиска уведомлений, дата получения которых раньше указанной даты.
     *
     * @param dateTime дата и время, до которых нужно искать уведомления.
     * @return спецификация для поиска уведомлений.
     */
    public static Specification<NotificationEntity> receivedDateBefore(LocalDateTime dateTime) {
        return (root, query, builder) -> builder.lessThanOrEqualTo(root.get("receiveTime"), dateTime);
    }

    /**
     * Возвращает спецификацию для поиска уведомлений, содержащих указанный текст в своем содержимом.
     * Поиск регистронезависимый.
     *
     * @param text текст, который нужно найти в содержимом уведомлений.
     * @return спецификация для поиска уведомлений.
     */
    public static Specification<NotificationEntity> textLike(String text) {
        return (root, query, builder) -> builder.like(builder.lower(root.get("text")), "%" + text.toLowerCase() + "%");
    }

    /**
     * Возвращает спецификацию для поиска уведомлений определенного типа.
     *
     * @param types список типов уведомлений, которые нужно найти
     * @return спецификация для поиска уведомлений
     */
    public static Specification<NotificationEntity> typeIn(List<NotificationType> types) {
        return (root, query, builder) -> root.get("type").in(types);
    }

    /**
     * Возвращает спецификацию для поиска уведомлений, которые относятся к указанному пользователю.
     *
     * @param userId идентификатор пользователя, для которого нужно найти уведомления
     * @return спецификация для поиска уведомлений
     */
    public static Specification<NotificationEntity> userIdEqual(UUID userId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userId"), userId);
    }

}
