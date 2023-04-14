package ru.hits.messengerapi.common.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/**
 * Данные, которые можно достать из JWT-токена.
 */
@Getter
@AllArgsConstructor
public class JwtUserData {

    /**
     * Логин пользователя.
     */
    private final String login;

    /**
     * Уникальный идентификатор пользователя.
     */
    private final UUID id;

    /**
     * ФИО пользователя.
     */
    private final String fullName;

}
