package ru.hits.messengerapi.chat.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessageDto {

    private UUID chatId;

    private String chatName;

    private String messageText;

    private LocalDateTime messageSendDate;

    private List<String> fileNames;

}
