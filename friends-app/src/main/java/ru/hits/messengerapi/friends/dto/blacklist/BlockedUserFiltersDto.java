package ru.hits.messengerapi.friends.dto.blacklist;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BlockedUserFiltersDto {

    private LocalDateTime addedDate;

    private UUID blockedUserId;

    private LocalDateTime deletedDate;

    private String blockedUserName;

}
