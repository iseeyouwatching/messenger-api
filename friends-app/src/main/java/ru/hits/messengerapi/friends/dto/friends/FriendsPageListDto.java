package ru.hits.messengerapi.friends.dto.friends;

import lombok.*;
import ru.hits.messengerapi.friends.dto.common.PageInfoDto;

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
public class FriendsPageListDto {

    /**
     * Список неполной информации о друзьях.
     */
    private List<FriendInfoDto> friends;

    /**
     * Информация о пагинации.
     */
    private PageInfoDto pageInfo;

    /**
     * Фильтр по ФИО.
     */
    private String fullNameFilter;

}
