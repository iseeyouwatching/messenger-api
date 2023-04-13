package ru.hits.messengerapi.friends.dto.blacklist;

import lombok.*;
import ru.hits.messengerapi.friends.entity.BlacklistEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BlockedUserInfoDto {

    private UUID id;

    private LocalDateTime addedDate;

    private LocalDateTime deletedDate;

    private UUID blockedUserId;

    private String blockedUserName;

    public BlockedUserInfoDto(BlacklistEntity blockedUser) {
        this.id = blockedUser.getId();
        this.addedDate = blockedUser.getAddedDate();
        this.deletedDate = blockedUser.getDeletedDate();
        this.blockedUserId = blockedUser.getBlockedUserId();
        this.blockedUserName = blockedUser.getBlockedUserName();
    }

}
