package ru.hits.messengerapi.friends.dto.blacklist;

import lombok.*;
import ru.hits.messengerapi.common.dto.PageInfoDto;

import java.util.List;

/**
 * Класс, представляющий DTO, содержащую список найденных заблокированных пользователей,
 * информацию о пагинации и фильтрах.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SearchedBlockedUsersDto {

    /**
     * Список неполной информации о заблокированных пользователях.
     */
    private List<BlockedUserInfoDto> blockedUsers;

    /**
     * Информация о пагинации.
     */
    private PageInfoDto pageInfo;

    /**
     * Фильтры.
     */
    private BlockedUserFiltersDto filters;

}
