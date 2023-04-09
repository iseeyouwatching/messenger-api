package ru.hits.messengerapi.user.dto;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaginationDto {

    @NotNull(message = "Информация о странице является обязательной к заполнению.")
    private PageInfoDto pageInfo;

    private FiltersDto filters;

    @Valid
    private List<SortingDto> sorting;

}
