package ru.hits.messengerapi.chat.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CorrespondenceDto {

    private UUID chatId;

    private String name;

    private String lastMessageText;

    private LocalDateTime lastMessageSendDate;

    private UUID lastMessageSenderId;

}
