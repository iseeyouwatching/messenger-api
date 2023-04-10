package ru.hits.messengerapi.user.dto;

import lombok.*;

import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Класс, который представляет DTO для обновления информации о пользователе.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateUserInfoDto {

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
    @Pattern(regexp = "^\\+[0-9]{10,12}$", message = "Некорректный номер телефона.")
    private String phoneNumber;

    /**
     * Город пользователя.
     */
    private String city;

    /**
     * UUID файла аватара пользователя.
     */
    private UUID avatar;

}
