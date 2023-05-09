package ru.hits.messengerapi.chat.dto;

import lombok.*;
import ru.hits.messengerapi.common.dto.PageInfoDto;

import javax.validation.constraints.NotNull;

/**
 * DTO, содержащая информацию о странице и фильтре по ФИО.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaginationWithChatNameDto {

    /**
     * Информация о странице.
     */
    @NotNull(message = "Информация о странице является обязательной к заполнению.")
    private PageInfoDto pageInfo;

    /**
     * Фильтр по ФИО.
     */
    private String chatName;

}
