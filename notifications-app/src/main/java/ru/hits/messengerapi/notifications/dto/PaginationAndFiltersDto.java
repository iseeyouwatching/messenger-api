package ru.hits.messengerapi.notifications.dto;

import lombok.*;
import ru.hits.messengerapi.common.dto.PageInfoDto;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaginationAndFiltersDto {

    /**
     * Информация о пагинации.
     */
    @NotNull(message = "Информация о странице является обязательной к заполнению.")
    private PageInfoDto pageInfo;

    /**
     * Фильтры.
     */
    private NotificationsFiltersDto filters;

}
