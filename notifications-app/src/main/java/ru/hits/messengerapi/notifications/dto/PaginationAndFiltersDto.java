package ru.hits.messengerapi.notifications.dto;

import lombok.*;
import ru.hits.messengerapi.common.dto.PageInfoDto;

/**
 * DTO, содержащая информацию о пагинации и фильтры для получения списка уведомлений.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaginationAndFiltersDto {

    /**
     * Информация о пагинации.
     */
    private PageInfoDto pageInfo;

    /**
     * Фильтры.
     */
    private NotificationsFiltersDto filters;

}
