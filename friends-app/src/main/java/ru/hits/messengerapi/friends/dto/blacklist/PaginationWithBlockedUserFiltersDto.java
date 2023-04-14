package ru.hits.messengerapi.friends.dto.blacklist;

import lombok.*;
import ru.hits.messengerapi.common.dto.PageInfoDto;

import javax.validation.constraints.NotNull;

/**
 * Класс, представляющий DTO с информацией о пагинации и фильтрах.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaginationWithBlockedUserFiltersDto {

    /**
     * Информация о пагинации.
     */
    @NotNull(message = "Информация о странице является обязательной к заполнению.")
    private PageInfoDto pageInfo;

    /**
     * Фильтры.
     */
    private BlockedUserFiltersDto filters;

}
