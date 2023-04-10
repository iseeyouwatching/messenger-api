package ru.hits.messengerapi.user.dto;

import lombok.*;

import java.time.LocalDate;

/**
 * Класс, который представляет DTO с фильтрами пользователей.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FiltersDto {

    /**
     * Логин пользователя.
     */
    private String login;

    /**
     * Электронная почта пользователя.
     */
    private String email;

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
     * Город пользователя.
     */
    private String city;

}
