package ru.hits.messengerapi.notifications.dto;

import lombok.*;
import ru.hits.messengerapi.common.enumeration.NotificationType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationsFiltersDto {

    private PeriodDto periodFilter;

    private String textFilter;

    private NotificationType typeFilter;

}
