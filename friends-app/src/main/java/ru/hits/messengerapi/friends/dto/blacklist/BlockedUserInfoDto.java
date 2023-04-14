package ru.hits.messengerapi.friends.dto.blacklist;

import lombok.*;
import ru.hits.messengerapi.friends.entity.BlacklistEntity;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Класс, представляющий DTO с неполной информацией о заблокированном пользователе.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BlockedUserInfoDto {

    /**
     * UUID сущности из черного списка.
     */
    private UUID id;

    /**
     * Дата добавления в черный список.
     */
    private LocalDateTime addedDate;

    /**
     * Дата удаления из черного списка.
     */
    private LocalDateTime deletedDate;

    /**
     * Идентификатор заблокированного пользователя.
     */
    private UUID blockedUserId;

    /**
     * ФИО заблокированного пользователя.
     */
    private String blockedUserName;

    /**
     * Конструктор, который создает DTO объект, представляющий неполную информацию заблокированного пользователя
     * на основе сущности заблокированного пользователя.
     *
     * @param blockedUser объект класса {@link BlacklistEntity}, являющийся сущностью заблокированного пользователя.
     */
    public BlockedUserInfoDto(BlacklistEntity blockedUser) {
        this.id = blockedUser.getId();
        this.addedDate = blockedUser.getAddedDate();
        this.deletedDate = blockedUser.getDeletedDate();
        this.blockedUserId = blockedUser.getBlockedUserId();
        this.blockedUserName = blockedUser.getBlockedUserName();
    }

}
