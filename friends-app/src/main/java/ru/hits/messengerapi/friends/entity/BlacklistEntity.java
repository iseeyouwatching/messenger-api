package ru.hits.messengerapi.friends.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Сущность пользователя из черного списка.
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "blacklist")
public class BlacklistEntity {

    /**
     * Идентификатор сущности из черного списка.
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    /**
     * Дата добавления в черный список.
     */
    @Column(name = "added_date", nullable = false)
    private LocalDate addedDate;

    /**
     * Дата удаления из черного списка.
     */
    @Column(name = "deleted_date")
    private LocalDate deletedDate;

    /**
     * Идентификатор пользователя, который добавил в черный список.
     */
    @Column(name = "target_user_id", nullable = false)
    private UUID targetUserId;

    /**
     * Идентификатор пользователя, которого заблокировали.
     */
    @Column(name = "blocked_user_id", nullable = false)
    private UUID blockedUserId;

    /**
     * ФИО пользователя, которого заблокировали.
     */
    @Column(name = "blocked_user_name", nullable = false)
    private String blockedUserName;

    /**
     * Проверка на то, удален ли пользователь из черного списка или нет.
     */
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

}
