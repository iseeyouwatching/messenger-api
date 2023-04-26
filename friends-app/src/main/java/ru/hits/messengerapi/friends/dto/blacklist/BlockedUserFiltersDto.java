package ru.hits.messengerapi.friends.dto.blacklist;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Класс, представляющий DTO, содержащую фильтры для поиска заблокированных пользователей.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BlockedUserFiltersDto {

    /**
     * Дата добавления в черный список.
     */
    private LocalDate addedDate;

    /**
     * Идентификатор пользователя, которого заблокировали.
     */
    private UUID blockedUserId;

    /**
     * ФИО заблокированного пользователя.
     */
    private String blockedUserName;

}
