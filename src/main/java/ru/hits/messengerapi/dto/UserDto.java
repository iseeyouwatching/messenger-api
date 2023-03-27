package ru.hits.messengerapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.hits.messengerapi.enumeration.Sex;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private UUID id;

    private String name;

    private String surname;

    private String patronymic;

    private String login;

    private String password;

    private Sex sex;

    private LocalDate birthdate;

    private LocalDate registrationDate;

}
