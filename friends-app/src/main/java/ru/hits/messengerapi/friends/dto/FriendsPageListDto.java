package ru.hits.messengerapi.friends.dto;

import lombok.*;
import ru.hits.messengerapi.common.dto.PageInfoDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FriendsPageListDto {

    private List<FriendInfoDto> friends;

    private PageInfoDto pageInfo;

    private String fullNameFilter;

}
