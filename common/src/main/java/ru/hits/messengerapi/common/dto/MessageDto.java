package ru.hits.messengerapi.common.dto;

import lombok.Builder;
import lombok.Data;
import ru.hits.messengerapi.common.enumeration.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class MessageDto {

    private UUID userId;
    private NotificationType type;

    private String text;

}
