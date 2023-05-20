package ru.hits.messengerapi.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Класс, который представляет DTO для регистрации пользователя.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserSignUpDto {

    /**
     * Логин пользователя.
     */
    @NotBlank(message = "Логин не может быть пустым.")
    @Size(min = 5, max = 32, message = "Длина логина должна быть от 5 до 32 символов.")
    private String login;

    /**
     * Пароль пользователя.
     */
    @NotBlank(message = "Пароль не может быть пустым.")
    @Size(min = 8, max = 32, message = "Длина пароля должна быть от 8 до 32 символов.")
    private String password;

    /**
     * Почта пользователя.
     */
    @NotBlank(message = "Почта не может быть пустой.")
    @Email(message = "Некорректный формат почты.")
    @Schema(description = "Почта", example = "string@gmail.com")
    private String email;

    /**
     * Полное имя пользователя.
     */
    @NotBlank(message = "ФИО не может быть пустым.")
    private String fullName;

    /**
     * Дата рождения пользователя.
     */
    private LocalDate birthDate;

    /**
     * Номер телефона пользователя.
     */
    @Pattern(regexp = "^\\+[0-9]{10,12}$", message = "Некорректный номер телефона.")
    private String phoneNumber;

    /**
     * Город пользователя.
     */
    private String city;

    /**
     * UUID файла аватара пользователя.
     */
    private UUID avatar;

}
