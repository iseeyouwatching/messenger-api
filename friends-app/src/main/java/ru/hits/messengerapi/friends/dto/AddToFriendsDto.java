package ru.hits.messengerapi.friends.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddToFriendsDto {

    @NotNull(message = "ID добавляемого пользователя является обязательным к заполнению.")
    private UUID id;

    @NotNull(message = "ФИО пользователя является обязательным к заполнению.")
    @NotBlank(message = "ФИО пользователя не может быть пустым.")
    private String fullName;

}
