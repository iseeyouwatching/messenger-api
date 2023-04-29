package ru.hits.messengerapi.chat.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateChatDto {

    @NotNull(message = "Идентификатор изменяемого чата является обязательным к заполнению.")
    private UUID id;

    private String name;

    private UUID avatar;

    private List<UUID> users;

}
