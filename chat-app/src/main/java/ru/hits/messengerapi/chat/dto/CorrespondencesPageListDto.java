package ru.hits.messengerapi.chat.dto;

import lombok.*;

import java.util.List;

/**
 * DTO, содержащая список переписок, информацию о странице и фильтр по названию переписки.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CorrespondencesPageListDto {

    /**
     * Список переписок.
     */
    private List<CorrespondenceDto> correspondences;

    /**
     * Информация о странице.
     */
    private PageInfoDto pageInfo;

    /**
     * Фильтр по названию переписки.
     */
    private String chatNameFilter;

}
