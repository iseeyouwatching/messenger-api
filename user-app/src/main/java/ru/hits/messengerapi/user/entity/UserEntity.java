package ru.hits.messengerapi.user.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Класс, представляющий сущность пользователя.
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "_user")
public class UserEntity {

    /**
     * Уникальный идентификатор пользователя.
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    /**
     * Дата регистрации пользователя.
     */
    @Column(name = "registration_date")
    private LocalDateTime registrationDate = LocalDateTime.now();

    /**
     * Логин пользователя.
     */
    @Column(unique = true)
    private String login;

    /**
     * Адрес электронной почты пользователя.
     */
    @Column(unique = true)
    private String email;

    /**
     * Хэш пароля пользователя.
     */
    private String password;

    /**
     * Полное имя пользователя.
     */
    @Column(name = "full_name")
    private String fullName;

    /**
     * Дата рождения пользователя.
     */
    @Column(name = "birth_date")
    private LocalDate birthDate;

    /**
     * Номер телефона пользователя.
     */
    @Column(name = "phone_number")
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
