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
    private final MessageRepository messageRepository;
    @Bean
    public Consumer<UserIdAndFullNameDto> userDataSynchronizationEvent() {
        return userData -> {
            List<MessageEntity> messages = messageRepository.findAllBySenderId(userData.getId());
            messages.forEach(message -> message.setSenderName(userData.getFullName()));
            messageRepository.saveAll(messages);
        };
    }

}
