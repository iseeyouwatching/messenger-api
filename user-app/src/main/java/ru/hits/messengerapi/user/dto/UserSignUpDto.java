package ru.hits.messengerapi.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.hits.messengerapi.user.enumeration.Sex;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSignUpDto {

    @NotBlank(message = "Имя не может быть пустым.")
    private String name;

    @NotBlank(message = "Фамилия не может быть пустой.")
    private String surname;

    @NotBlank(message = "Отчество не может быть пустым.")
    private String patronymic;

    @NotBlank(message = "Почта не может быть пустой.")
    @Email(message = "Некорректный формат почты.")
    private String email;

    @NotBlank(message = "Логин не может быть пустым.")
    private String login;

    @NotBlank(message = "Пароль не может быть пустым.")
    @Size(min = 8, max = 32, message = "Длина пароля должна быть от 8 до 32 символов.")
    private String password;

    private Sex sex;

    @NotNull(message = "Дата рождения не может быть пустой.")
    private LocalDate birthdate;

}
