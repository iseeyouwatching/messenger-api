package ru.hits.messengerapi.chat.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO, содержащая информацию о сообщении.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessageDto {

    /**
     * Уникальный идентификатор переписки.
     */
    private UUID chatId;

    /**
     * Название переписки.
     */
    private String chatName;

    /**
     * Текст сообщения.
     */
    private String messageText;

    /**
     * Признак наличия файлов-вложений.
     */
    private Boolean fileAvailability;

    /**
     * Дата и время отправки сообщения.
     */
    private LocalDateTime messageSendDate;

    /**
     * Название файлов, прикрепленных к данному сообщению.
     */
    private List<String> fileNames;

}
