package ru.hits.messengerapi.chat.dto;

import lombok.*;
import ru.hits.messengerapi.chat.entity.MessageEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessageInCorrespondenceDto {

    private UUID messageId;

    private LocalDateTime sendDate;

    private String messageText;

    private String senderName;

    private UUID senderAvatarId;

    public MessageInCorrespondenceDto(MessageEntity message) {
        this.messageId = message.getId();
        this.sendDate = message.getSendDate();
        this.messageText = message.getMessageText();
        this.senderName = message.getSenderName();
        this.senderAvatarId = message.getSenderAvatarId();
    }

}
