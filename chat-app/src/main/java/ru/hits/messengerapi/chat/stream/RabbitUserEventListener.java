package ru.hits.messengerapi.chat.stream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.hits.messengerapi.chat.entity.MessageEntity;
import ru.hits.messengerapi.chat.repository.MessageRepository;
import ru.hits.messengerapi.common.dto.UserIdAndFullNameDto;

import java.util.List;
import java.util.function.Consumer;

/**
 * Класс-обработчик событий, связанных с получением новых уведомлений из очереди RabbitMQ.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class RabbitUserEventListener {

    /**
     * Репозиторий для работы с сущностью {@link MessageEntity}.
     */
    private final MessageRepository messageRepository;

    /**
     * Обрабатывает событие синхронизации данных пользователя. Обновляет имя отправителя у сообщений,
     * которые были отправлены пользователем с указанным идентификатором.
     *
     * @return consumer, обрабатывающий событие синхронизации данных пользователя.
     */
    @Bean
    public Consumer<UserIdAndFullNameDto> userDataSynchronizationEvent() {
        return userData -> {
            List<MessageEntity> messages = messageRepository.findAllBySenderId(userData.getId());
            messages.forEach(message -> message.setSenderName(userData.getFullName()));
            messageRepository.saveAll(messages);
        };
    }

}
