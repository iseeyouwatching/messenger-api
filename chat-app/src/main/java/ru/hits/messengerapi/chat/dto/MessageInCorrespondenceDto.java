package ru.hits.messengerapi.chat.dto;

import lombok.*;
import ru.hits.messengerapi.chat.entity.MessageEntity;

import java.time.LocalDateTime;
import java.util.List;
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
     * Список файлов.
     */
    private List<FileDto> files;

    /**
     * Создает объект класса {@link MessageInCorrespondenceDto} на основе сущности {@link MessageEntity}
     * и списка {@link FileDto}.
     *
     * @param message сущность сообщения.
     * @param fileDto список файлов.
     */
    public MessageInCorrespondenceDto(MessageEntity message, List<FileDto> fileDto) {
        this.messageId = message.getId();
        this.sendDate = message.getSendDate();
        this.messageText = message.getMessageText();
        this.senderName = message.getSenderName();
        this.senderAvatarId = message.getSenderAvatarId();
        this.files = fileDto;
    }

}
