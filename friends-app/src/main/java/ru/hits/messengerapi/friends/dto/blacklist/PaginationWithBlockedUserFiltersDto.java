package ru.hits.messengerapi.friends.dto.blacklist;

import lombok.*;
import ru.hits.messengerapi.common.dto.PageInfoDto;
import ru.hits.messengerapi.friends.dto.friends.FriendFiltersDto;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaginationWithBlockedUserFiltersDto {

    @NotNull(message = "Информация о странице является обязательной к заполнению.")
    private PageInfoDto pageInfo;

    private BlockedUserFiltersDto filters;


}
