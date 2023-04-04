package ru.hits.messengerapi.user.dto;

import lombok.*;
import ru.hits.messengerapi.user.entity.UserEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {

    private UUID id;

    private String login;

    private String email;

    private String password;

    private String fullName;

    private LocalDate birthDate;

    private String phoneNumber;

    private String city;

    private UUID avatar;

    private LocalDateTime registrationDate;

    public UserDto(UserEntity userEntity) {
        this.id = userEntity.getId();
        this.login = userEntity.getLogin();
        this.email = userEntity.getEmail();
        this.password = userEntity.getPassword();
        this.fullName = userEntity.getFullName();
        this.birthDate = userEntity.getBirthDate();
        this.phoneNumber = userEntity.getPhoneNumber();
        this.city = userEntity.getCity();
        this.avatar = userEntity.getAvatar();
        this.registrationDate = userEntity.getRegistrationDate();
    }

}
