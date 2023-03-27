package ru.hits.messengerapi.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import ru.hits.messengerapi.enumeration.Sex;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "_user")
public class User {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    private String login;

    private String name;

    private String surname;

    private String patronymic;

    private String password;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    private LocalDate birthdate;

    private LocalDate registrationDate;
}
