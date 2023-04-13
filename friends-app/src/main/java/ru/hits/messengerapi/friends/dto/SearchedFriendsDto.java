package ru.hits.messengerapi.friends.dto;

import lombok.*;
import ru.hits.messengerapi.common.dto.PageInfoDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SearchedFriendsDto {

    private List<FriendInfoDto> friends;

    private PageInfoDto pageInfo;

    private FriendFiltersDto filters;

}
