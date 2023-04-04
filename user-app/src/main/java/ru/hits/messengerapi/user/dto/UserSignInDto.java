package ru.hits.messengerapi.user.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserSignInDto {

    @NotBlank(message = "Логин не может быть пустым.")
    private String login;

    @NotBlank(message = "Пароль не может быть пустым.")
    private String password;

}
