package ru.hits.messengerapi.friends.dto;

import lombok.*;
import ru.hits.messengerapi.common.dto.PageInfoDto;

import javax.validation.constraints.NotNull;

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

    private String fullNameFilter;

}
