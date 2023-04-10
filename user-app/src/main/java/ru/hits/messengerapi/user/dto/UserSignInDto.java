package ru.hits.messengerapi.user.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

/**
 * Класс, который представляет DTO для аутентификации пользователя.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserSignInDto {

    /**
     * Логин пользователя.
     */
    @NotBlank(message = "Логин не может быть пустым.")
    private String login;

    /**
     * Пароль пользователя.
     */
    @NotBlank(message = "Пароль не может быть пустым.")
    private String password;

}
