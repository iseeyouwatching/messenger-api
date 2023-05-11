package ru.hits.messengerapi.common.dto;

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
@Builder
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
