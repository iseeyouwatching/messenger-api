package ru.hits.messengerapi.notifications.stream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.hits.messengerapi.common.dto.MessageDto;
import ru.hits.messengerapi.notifications.service.NotificationService;

import java.util.function.Consumer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RabbitUserEventListener {

    private final NotificationService notificationService;
    @Bean
    public Consumer<MessageDto> userSuccessfulLoginEvent() {
        return notificationService::getMessage;
    }

}
