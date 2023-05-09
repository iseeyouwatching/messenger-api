package ru.hits.messengerapi.chat.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * DTO, содержащая информацию необходимую для изменения чата.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateChatDto {

    /**
     * Уникальный идентификатор изменяемого чата.
     */
    @NotNull(message = "Идентификатор изменяемого чата является обязательным к заполнению.")
    private UUID id;

    /**
     * Название чата.
     */
    private String name;

    /**
     * Аватарка чата.
     */
    private UUID avatar;

    /**
     * Список пользователей чата.
     */
    private List<UUID> users;

}
