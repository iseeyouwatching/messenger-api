package ru.hits.messengerapi.notifications.stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.hits.messengerapi.common.dto.UserSuccessfulLoginDto;

import java.util.function.Consumer;

@Slf4j
@Configuration
public class RabbitUserEventListener {
    @Bean
    public Consumer<UserSuccessfulLoginDto> userSuccessfulLoginEvent() {
        return user -> log.info("Выполнен вход в систему {} с IP {}.", user.getDateTimeOfLogin(), user.getIP());
    }

}
