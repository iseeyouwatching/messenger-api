package ru.hits.messengerapi.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSignInDto {

    @NotBlank(message = "Логин не может быть пустым.")
    private String login;

    @NotBlank(message = "Пароль не может быть пустым.")
    private String password;

}
