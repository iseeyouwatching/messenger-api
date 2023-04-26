package ru.hits.messengerapi.friends.dto.friends;

import lombok.*;
import ru.hits.messengerapi.friends.entity.FriendEntity;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Класс, представляющий DTO с полной информацией о пользователе, находящемся в друзьях.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FriendDto {

    /**
     * UUID сущности из друзей.
     */
    private UUID id;

    /**
     * Дата добавления в друзья.
     */
    private LocalDate addedDate;

    /**
     * Дата удаления из друзей.
     */
    private LocalDate deletedDate;

    /**
     * Идентификатор пользователя, который добавил в друзья.
     */
    private UUID targetUserId;

    /**
     * Идентификатор пользователя, которого добавили в друзья.
     */
    private UUID addedUserId;

    /**
     * ФИО друга.
     */
    private String friendName;

    /**
     * Конструктор, который создает DTO объект, представляющий полную информацию о друге
     * на основе сущности друга.
     *
     * @param friend объект класса {@link FriendEntity}, являющийся сущностью друга.
     */
    public FriendDto(FriendEntity friend) {
        this.id = friend.getId();
        this.addedDate = friend.getAddedDate();
        this.deletedDate = friend.getDeletedDate();
        this.targetUserId = friend.getTargetUserId();
        this.addedUserId = friend.getAddedUserId();
        this.friendName = friend.getFriendName();
    }

}
