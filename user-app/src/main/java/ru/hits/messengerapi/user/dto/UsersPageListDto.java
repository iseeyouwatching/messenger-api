package ru.hits.messengerapi.user.dto;

import lombok.*;

import java.util.List;

/**
 * Класс, который представляет DTO со списком пользователей, информацией о странице, фильтрах и сортировках.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UsersPageListDto {

    /**
     * Список с информацией о пользователях.
     */
    private List<UserProfileDto> users;

    /**
     * Информация о странице.
     */
    private PageInfoDto pageInfo;

    /**
     * Фильтры.
     */
    private FiltersDto filters;

    /**
     * Сортировки.
     */
    private List<SortingDto> sortings;
}
