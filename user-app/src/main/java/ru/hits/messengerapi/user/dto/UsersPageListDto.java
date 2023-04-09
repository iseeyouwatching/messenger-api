package ru.hits.messengerapi.user.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UsersPageListDto {

    private List<UserProfileDto> users;

    private PageInfoDto pagination;

    private FiltersDto filters;

    private List<SortingDto> sorting;
}
