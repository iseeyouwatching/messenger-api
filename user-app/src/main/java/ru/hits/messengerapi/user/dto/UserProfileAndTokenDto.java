package ru.hits.messengerapi.user.dto;

import lombok.*;

/**
 * Класс, который представляет DTO с информацией о профиле пользователя и токене аутентификации.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserProfileAndTokenDto {

    /**
     * Объект класса {@link UserProfileDto}, который содержит информацию о профиле пользователя.
     */
    private UserProfileDto userProfileDto;

    /**
     * Токен аутентификации.
     */
    private String token;
}
