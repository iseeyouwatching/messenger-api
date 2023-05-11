package ru.hits.messengerapi.friends.stream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.hits.messengerapi.common.dto.UserIdAndFullNameDto;
import ru.hits.messengerapi.friends.service.IntegrationRequestsService;

import java.util.function.Consumer;

/**
 * Класс-обработчик событий, связанных с получением новых уведомлений из очереди RabbitMQ.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class RabbitUserEventListener {
    private final IntegrationRequestsService integrationRequestsService;

    @Bean
    public Consumer<UserIdAndFullNameDto> userDataSynchronizationEvent() {
        return userData -> {
            integrationRequestsService.syncBlockedUserData(userData);
            integrationRequestsService.syncFriendData(userData);
        };
    }

}
