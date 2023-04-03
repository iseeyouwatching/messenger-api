package ru.hits.messengerapi.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.hits.messengerapi.user.enumeration.Sex;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserInfoDto {

    private String name;

    private String surname;

    private String patronymic;

    @Email(message = "Некорректный формат почты.")
    private String email;

    @Size(min = 8, max = 32, message = "Длина пароля должна быть от 8 до 32 символов.")
    private String password;

    private Sex sex;

    private LocalDate birthdate;

}
