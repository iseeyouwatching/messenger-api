package ru.hits.messengerapi.friends.dto.common;

import lombok.*;
import ru.hits.messengerapi.common.dto.PageInfoDto;

import javax.validation.constraints.NotNull;

/**
 * Класс, представляющий DTO, содержащую информацию о пагинации и фильтре по ФИО.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaginationDto {

    /**
     * Информация о странице.
     */
    @NotNull(message = "Информация о странице является обязательной к заполнению.")
    private PageInfoDto pageInfo;

    /**
     * Фильтр по ФИО.
     */
    private String fullNameFilter;

}
