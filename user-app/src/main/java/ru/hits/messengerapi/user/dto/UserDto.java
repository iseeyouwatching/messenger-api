package ru.hits.messengerapi.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.hits.messengerapi.user.entity.UserEntity;
import ru.hits.messengerapi.user.enumeration.Sex;

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

    private String email;

    private String login;

    private String password;

    private Sex sex;

    private LocalDate birthdate;

    private LocalDate registrationDate;

    public UserDto(UserEntity userEntity) {
        this.id = userEntity.getId();
        this.name = userEntity.getName();
        this.surname = userEntity.getSurname();
        this.patronymic = userEntity.getPatronymic();
        this.email = userEntity.getEmail();
        this.login = userEntity.getLogin();
        this.password = userEntity.getPassword();
        this.sex = userEntity.getSex();
        this.birthdate = userEntity.getBirthdate();
        this.registrationDate = userEntity.getRegistrationDate();
    }

}
