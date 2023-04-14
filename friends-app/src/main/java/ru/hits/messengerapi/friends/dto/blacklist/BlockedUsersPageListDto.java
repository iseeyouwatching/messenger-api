package ru.hits.messengerapi.friends.dto.blacklist;

import lombok.*;
import ru.hits.messengerapi.common.dto.PageInfoDto;

import java.util.List;

/**
 * Класс, представляющий DTO со списком неполной информации найденных пользователей,
 * информации о пагинации и фильтре по ФИО.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BlockedUsersPageListDto {

    /**
     * Список неполной информации о заблокированных пользователях.
     */
    private List<BlockedUserInfoDto> blockedUsers;

    /**
     * Информация о пагинации.
     */
    private PageInfoDto pageInfo;

    /**
     * Фильтр по ФИО.
     */
    private String fullNameFilter;

}
