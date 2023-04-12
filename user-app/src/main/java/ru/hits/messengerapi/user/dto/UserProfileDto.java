package ru.hits.messengerapi.user.dto;

import lombok.*;
import ru.hits.messengerapi.user.entity.UserEntity;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Класс, который представляет DTO с информацией о профиле пользователя.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserProfileDto {

    /**
     * UUID пользователя.
     */
    private UUID id;

    /**
     * Логин пользователя.
     */
    private String login;

    /**
     * Электронная почта пользователя.
     */
    private String email;

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
    private String phoneNumber;

    /**
     * Город пользователя.
     */
    private String city;

    /**
     * UUID файла аватара пользователя.
     */
    private UUID avatar;

    /**
     * Конструктор для создания экземпляра DTO из объекта класса {@link UserEntity}.
     * @param userEntity объект сущности пользователя.
     */
    public UserProfileDto(UserEntity userEntity) {
        this.id = userEntity.getId();
        this.login = userEntity.getLogin();
        this.email = userEntity.getEmail();
        this.fullName = userEntity.getFullName();
        this.birthDate = userEntity.getBirthDate();
        this.phoneNumber = userEntity.getPhoneNumber();
        this.city = userEntity.getCity();
        this.avatar = userEntity.getAvatar();
    }

}
