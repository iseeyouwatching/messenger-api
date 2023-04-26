package ru.hits.messengerapi.friends.dto.friends;

import lombok.*;
import ru.hits.messengerapi.friends.entity.FriendEntity;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Класс, представляющий DTO с неполной информацией о друге.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FriendInfoDto {

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
     * Идентификатор пользователя, которого добавили в друзья.
     */
    private UUID addedUserId;

    /**
     * ФИО друга.
     */
    private String friendName;

    /**
     * Конструктор, который создает DTO объект, представляющий неполную информацию о друге
     * на основе сущности друга.
     *
     * @param friend объект класса {@link FriendEntity}, являющийся сущностью друга.
     */
    public FriendInfoDto(FriendEntity friend) {
        this.id = friend.getId();
        this.addedDate = friend.getAddedDate();
        this.deletedDate = friend.getDeletedDate();
        this.addedUserId = friend.getAddedUserId();
        this.friendName = friend.getFriendName();
    }

}
