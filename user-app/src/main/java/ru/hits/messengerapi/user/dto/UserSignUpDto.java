package ru.hits.messengerapi.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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
