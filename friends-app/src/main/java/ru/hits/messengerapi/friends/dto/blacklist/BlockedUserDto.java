package ru.hits.messengerapi.friends.dto.blacklist;

import lombok.*;
import ru.hits.messengerapi.friends.entity.BlacklistEntity;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Класс, представляющий DTO с полной информацией о заблокированном пользователе.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BlockedUserDto {

    /**
     * UUID сущности из черного списка.
     */
    private UUID id;

    /**
     * Дата добавления в черный список.
     */
    private LocalDate addedDate;

    /**
     * Дата удаления из черного списка.
     */
    private LocalDate deletedDate;

    /**
     * Идентификатор заблокировавшего пользователя.
     */
    private UUID targetUserId;

    /**
     * Идентификатор пользователя, которого заблокировали.
     */
    private UUID blockedUserId;

    /**
     * ФИО заблокированного пользователя.
     */
    private String blockedUserName;

    /**
     * Конструктор, который создает DTO объект, представляющий полную информацию заблокированного пользователя
     * на основе сущности заблокированного пользователя.
     *
     * @param blockedUser объект класса {@link BlacklistEntity}, являющийся сущностью заблокированного пользователя.
     */
    public BlockedUserDto(BlacklistEntity blockedUser) {
        this.id = blockedUser.getId();
        this.addedDate = blockedUser.getAddedDate();
        this.deletedDate = blockedUser.getDeletedDate();
        this.targetUserId = blockedUser.getTargetUserId();
        this.blockedUserId = blockedUser.getBlockedUserId();
        this.blockedUserName = blockedUser.getBlockedUserName();
    }

}
