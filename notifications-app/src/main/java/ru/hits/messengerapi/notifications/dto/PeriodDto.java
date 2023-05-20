package ru.hits.messengerapi.notifications.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * DTO, содержащий информацию о периоде времени.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PeriodDto {

    /**
     * Начальное время периода.
     */
    private LocalDate fromDateTime;

    /**
     * Конечное время периода.
     */
    private LocalDate toDateTime;

}
