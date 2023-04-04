package ru.hits.messengerapi.user.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateUserInfoDto {

    private String fullName;

    private LocalDate birthDate;

    private String phoneNumber;

    private String city;

    private UUID avatar;

}
