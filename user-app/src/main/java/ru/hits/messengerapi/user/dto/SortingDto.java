package ru.hits.messengerapi.user.dto;

import lombok.*;

import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SortingDto {

    @Pattern(regexp = "login|email|fullName|birthDate|phoneNumber|city",
            message = "Некорректное значение. Возможные: login, email, fullName, birthDate, phoneNumber, city")
    private String field;
    @Pattern(regexp = "ASC|DESC", message = "Некорректное значение. Возможные: ASC, DESC")
    private String direction;
}
