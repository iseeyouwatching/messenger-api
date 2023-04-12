package ru.hits.messengerapi.user.dto;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserIdAndFullNameDto {

    private UUID id;

    private String fullName;

}
