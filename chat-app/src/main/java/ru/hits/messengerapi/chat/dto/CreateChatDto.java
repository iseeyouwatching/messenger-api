package ru.hits.messengerapi.chat.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

/**
 * DTO, содержащая информацию для создания чата.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateChatDto {

    /**
     * Название создаваемого чата.
     */
    private String name;

    /**
     * Аватарка создаваемого чата.
     */
    private UUID avatar;

    /**
     * Список уникальных идентификаторов пользователей, которые будут находиться в беседе.
     */
    private List<UUID> users;

}
