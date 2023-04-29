package ru.hits.messengerapi.chat.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateChatDto {

    private String name;

    private UUID avatar;

    private List<UUID> users;

}
