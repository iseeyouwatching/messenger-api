package ru.hits.messengerapi.user.dto;

import lombok.*;

import java.util.UUID;

/**
 * Класс, который представляет DTO идентификатором и ФИО пользователя.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserIdAndFullNameDto {

    /**
     * UUID пользователя.
     */
    private UUID id;

    /**
     * ФИО пользователя.
     */
    private String fullName;

}
