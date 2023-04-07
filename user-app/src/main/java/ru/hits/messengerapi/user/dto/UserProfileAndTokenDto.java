package ru.hits.messengerapi.user.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserProfileAndTokenDto {

    private UserProfileDto userProfileDto;

    private String token;
}
