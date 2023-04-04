package ru.hits.messengerapi.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.hits.messengerapi.user.entity.UserEntity;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto {

    private String login;

    private String email;

    private String fullName;

    private LocalDate birthDate;

    private String phoneNumber;

    private String city;

    private UUID avatar;

    public UserProfileDto(UserEntity userEntity) {
        this.login = userEntity.getLogin();
        this.email = userEntity.getEmail();
        this.fullName = userEntity.getFullName();
        this.birthDate = userEntity.getBirthDate();
        this.phoneNumber = userEntity.getPhoneNumber();
        this.city = userEntity.getCity();
        this.avatar = userEntity.getAvatar();
    }

}
