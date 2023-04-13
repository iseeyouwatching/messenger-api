package ru.hits.messengerapi.friends.dto.blacklist;

import lombok.*;
import ru.hits.messengerapi.common.dto.PageInfoDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BlockedUsersPageListDto {

    private List<BlockedUserInfoDto> blockedUsers;

    private PageInfoDto pageInfo;

    private String fullNameFilter;

}
