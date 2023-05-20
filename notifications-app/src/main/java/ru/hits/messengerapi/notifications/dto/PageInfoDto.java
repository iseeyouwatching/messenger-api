package ru.hits.messengerapi.notifications.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * Класс, который представляет DTO с информацией о странице.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PageInfoDto {

    /**
     * Номер страницы.
     */
    @Schema(defaultValue = "1")
    private Integer pageNumber;

    /**
     * Размер страницы.
     */
    @Schema(defaultValue = "10")
    private Integer pageSize;

}
