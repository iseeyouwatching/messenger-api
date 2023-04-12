package ru.hits.messengerapi.user.dto;

import lombok.*;
import ru.hits.messengerapi.common.dto.PageInfoDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Класс, который представляет DTO с информацией о странице, фильтрах и сортировке.
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
     * Фильтры.
     */
    private FiltersDto filters;

    /**
     * Список параметров сортировки.
     */
    @Valid
    private List<SortingDto> sortings;

}
