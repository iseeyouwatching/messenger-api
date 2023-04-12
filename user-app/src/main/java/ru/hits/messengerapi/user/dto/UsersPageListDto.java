package ru.hits.messengerapi.user.dto;

import lombok.*;
import ru.hits.messengerapi.common.dto.PageInfoDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UsersPageListDto {

    private List<UserProfileDto> users;

    private PageInfoDto pageInfo;

    private FiltersDto filters;

    private List<SortingDto> sortings;
}
