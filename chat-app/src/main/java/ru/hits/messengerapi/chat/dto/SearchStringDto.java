package ru.hits.messengerapi.chat.dto;

import lombok.*;

/**
 * DTO, содержащая информацию о поисковой строке.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SearchStringDto {

    /**
     * Поисковая строка.
     */
    private String searchString;
}
