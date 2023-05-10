package ru.hits.messengerapi.notifications.dto;

import lombok.*;
import ru.hits.messengerapi.common.dto.PageInfoDto;

import java.util.List;

/**
 * DTO для постраничного вывода списка уведомлений.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationsPageListDto {

    /**
     * Список уведомлений.
     */
    private List<NotificationDto> notifications;

    /**
     * Информация о пагинации.
     */
    private PageInfoDto pageInfo;

    /**
     * Фильтры.
     */
    private NotificationsFiltersDto filters;

}
