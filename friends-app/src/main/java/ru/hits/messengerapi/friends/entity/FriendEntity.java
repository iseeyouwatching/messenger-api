package ru.hits.messengerapi.friends.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Сущность друга.
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "friend")
public class FriendEntity {

    /**
     * Идентификатор сущности друга.
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    /**
     * Дата добавления в друзья.
     */
    @Column(name = "added_date", nullable = false)
    private LocalDate addedDate;

    /**
     * Дата удаления из друзей.
     */
    @Column(name = "deleted_date")
    private LocalDate deletedDate;

    /**
     * Идентификатор пользователя, который добавил в друзья.
     */
    @Column(name = "target_user_id", nullable = false)
    private UUID targetUserId;

    /**
     * Идентификатор друга.
     */
    @Column(name = "added_user_id", nullable = false)
    private UUID addedUserId;

    /**
     * ФИО друга.
     */
    @Column(name = "friend_name", nullable = false)
    private String friendName;

    /**
     * Проверка на то, удален ли пользователь из друзей или нет.
     */
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

}