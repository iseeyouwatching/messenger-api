package ru.hits.messengerapi.notifications.stream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.hits.messengerapi.common.dto.NewNotificationDto;
import ru.hits.messengerapi.notifications.service.NotificationService;

import java.util.function.Consumer;

/**
 * Класс-обработчик событий, связанных с получением новых уведомлений из очереди RabbitMQ.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class RabbitUserEventListener {

    /**
     * Сервис уведомлений.
     */
    private final NotificationService notificationService;

    /**
     * Возвращает обработчик для событий получения новых уведомлений. Уведомления обрабатываются
     * сервисом {@link NotificationService}, который вызывается при получении нового уведомления
     * из очереди RabbitMQ.
     *
     * @return обработчик для событий получения новых уведомлений.
     */
    @Bean
    public Consumer<NewNotificationDto> newNotificationEvent() {
        return notificationService::getNotification;
    }

}
