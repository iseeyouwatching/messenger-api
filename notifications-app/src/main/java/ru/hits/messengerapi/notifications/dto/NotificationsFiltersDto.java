package ru.hits.messengerapi.notifications.dto;

import lombok.*;
import ru.hits.messengerapi.common.enumeration.NotificationType;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationsFiltersDto {

    private PeriodDto periodFilter;

    private String textFilter;

    private List<NotificationType> types;

}
