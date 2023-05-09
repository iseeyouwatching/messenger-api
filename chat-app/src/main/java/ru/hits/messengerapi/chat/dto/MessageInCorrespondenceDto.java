package ru.hits.messengerapi.chat.dto;

import lombok.*;
import ru.hits.messengerapi.chat.entity.MessageEntity;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO, содержащая информацию о сообщении в переписке.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessageInCorrespondenceDto {

    /**
     * Уникальный идентификатор сообщения.
     */
    private UUID messageId;

    /**
     * Дата и время отправки сообщения.
     */
    private LocalDateTime sendDate;

    /**
     * Текст сообщения.
     */
    private String messageText;

    /**
     * ФИО отправителя.
     */
    private String senderName;

    /**
     * Аватарка отправителя.
     */
    private UUID senderAvatarId;

    /**
     * Создает объект класса {@link MessageInCorrespondenceDto} на основе сущности {@link MessageEntity}.
     *
     * @param message сущность сообщения.
     */
    public MessageInCorrespondenceDto(MessageEntity message) {
        this.messageId = message.getId();
        this.sendDate = message.getSendDate();
        this.messageText = message.getMessageText();
        this.senderName = message.getSenderName();
        this.senderAvatarId = message.getSenderAvatarId();
    }

}
