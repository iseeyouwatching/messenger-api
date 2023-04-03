package ru.hits.messengerapi.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.hits.messengerapi.user.enumeration.Sex;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSignUpDto {

    @NotBlank(message = "Логин не может быть пустым.")
    private String login;

    @NotBlank(message = "Пароль не может быть пустым.")
    private String password;

    @NotBlank(message = "Почта не может быть пустой.")
    @Email(message = "Некорректный формат почты.")
    private String email;

    private String fullName;

    private LocalDate birthDate;

    private String phoneNumber;

    private String city;

    private UUID avatar;
}
