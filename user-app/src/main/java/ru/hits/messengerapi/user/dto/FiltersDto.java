package ru.hits.messengerapi.user.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FiltersDto {

    private String login;

    private String email;

    private String fullName;

    private LocalDate birthDate;

    private String phoneNumber;

    private String city;

}
