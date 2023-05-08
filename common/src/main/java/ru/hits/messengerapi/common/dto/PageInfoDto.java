package ru.hits.messengerapi.common.dto;

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
    private Integer pageNumber;

    /**
     * Размер страницы.
     */
    private Integer pageSize;

}
