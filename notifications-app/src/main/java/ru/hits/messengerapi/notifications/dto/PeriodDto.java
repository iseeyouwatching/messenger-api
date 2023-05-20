package ru.hits.messengerapi.notifications.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @Schema(description = "Начальное время периода.", example = "2023-05-19 00:00")
    private LocalDateTime fromDateTime;

    /**
     * Конечное время периода.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @Schema(description = "Конечное время периода.", example = "2023-05-19 00:00")
    private LocalDateTime toDateTime;

}
