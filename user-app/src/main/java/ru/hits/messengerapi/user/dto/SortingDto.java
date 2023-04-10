package ru.hits.messengerapi.user.dto;

import lombok.*;

import javax.validation.constraints.Pattern;

/**
 * Класс, который представляет DTO с информацией о сортировке.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SortingDto {

    /**
     * Поле, по которому происходит сортировка.
     */
    @Pattern(regexp = "login|email|fullName|birthDate|phoneNumber|city",
            message = "Некорректное значение. Возможные: login, email, fullName, birthDate, phoneNumber, city")
    private String field;

    /**
     * Направление сортировки.
     */
    @Pattern(regexp = "ASC|DESC", message = "Некорректное значение. Возможные: ASC, DESC")
    private String direction;
}
