package ru.hits.messengerapi.friends.dto.friends;

import lombok.*;
import ru.hits.messengerapi.common.dto.PageInfoDto;

import java.util.List;

/**
 * Класс, представляющий DTO, содержащую список найденных друзей,
 * информацию о пагинации и фильтрах.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SearchedFriendsDto {

    /**
     * Список неполной информации о друзьях.
     */
    private List<FriendInfoDto> friends;

    /**
     * Информация о пагинации.
     */
    private PageInfoDto pageInfo;

    /**
     * Фильтры.
     */
    private FriendFiltersDto filters;

}
