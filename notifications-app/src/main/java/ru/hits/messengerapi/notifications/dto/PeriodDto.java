package ru.hits.messengerapi.notifications.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PeriodDto {

    private LocalDateTime fromDateTime;

    private LocalDateTime toDateTime;

}
