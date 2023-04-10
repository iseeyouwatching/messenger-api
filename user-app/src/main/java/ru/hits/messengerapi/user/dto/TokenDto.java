package ru.hits.messengerapi.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс, который представляет DTO для передачи токена аутентификации.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {

    /**
     * Токен аутентификации.
     */
    private String token;

}
