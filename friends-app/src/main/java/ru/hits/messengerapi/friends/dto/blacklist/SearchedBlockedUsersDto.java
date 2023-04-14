package ru.hits.messengerapi.friends.dto.blacklist;

import lombok.*;
import ru.hits.messengerapi.common.dto.PageInfoDto;
import ru.hits.messengerapi.friends.dto.friends.FriendFiltersDto;
import ru.hits.messengerapi.friends.dto.friends.FriendInfoDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SearchedBlockedUsersDto {

    private List<BlockedUserInfoDto> blockedUsers;

    private PageInfoDto pageInfo;

    private BlockedUserFiltersDto filters;

}
