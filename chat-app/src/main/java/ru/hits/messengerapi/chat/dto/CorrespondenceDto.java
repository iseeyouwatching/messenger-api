package ru.hits.messengerapi.chat.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO для представления информации о переписке в списке диалогов.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CorrespondenceDto {

    /**
     * Идентификатор чата, связанной с перепиской.
     */
    private UUID chatId;

    /**
     * Название переписки.
     */
    private String name;

    /**
     * Текст последнего сообщения в переписке.
     */
    private String lastMessageText;

    /**
     * Признак наличия файлов-вложений.
     */
    private Boolean fileAvailability;

    /**
     * Дата и время отправки последнего сообщения в переписке.
     */
    private LocalDateTime lastMessageSendDate;

    /**
     * Идентификатор отправителя последнего сообщения в переписке.
     */
    private UUID lastMessageSenderId;

}
