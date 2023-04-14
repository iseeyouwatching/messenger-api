package ru.hits.messengerapi.user.dto;

import lombok.*;
import ru.hits.messengerapi.user.entity.UserEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Класс, который представляет DTO для передачи информации о пользователе.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {

    /**
     * UUID пользователя.
     */
    private UUID id;

    /**
     * Логин пользователя.
     */
    private String login;

    /**
     * Email пользователя.
     */
    private String email;

    /**
     * Хэш пароля пользователя.
     */
    private String password;

    /**
     * Полное имя пользователя.
     */
    private String fullName;

    /**
     * Дата рождения пользователя.
     */
    private LocalDate birthDate;

    /**
     * Номер телефона пользователя.
     */
    private String phoneNumber;

    /**
     * Город проживания пользователя.
     */
    private String city;

    /**
     * UUID файла аватара пользователя.
     */
    private UUID avatar;

    /**
     * Дата регистрации пользователя.
     */
    private LocalDateTime registrationDate;

}
