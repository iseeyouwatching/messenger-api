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
public class BlockedUserDto {

    private UUID id;

    private LocalDateTime addedDate;

    private LocalDateTime deletedDate;

    private UUID targetUserId;

    private UUID blockedUserId;

    private String blockedUserName;

    public BlockedUserDto(BlacklistEntity blockedUser) {
        this.id = blockedUser.getId();
        this.addedDate = blockedUser.getAddedDate();
        this.deletedDate = blockedUser.getDeletedDate();
        this.targetUserId = blockedUser.getTargetUserId();
        this.blockedUserId = blockedUser.getBlockedUserId();
        this.blockedUserName = blockedUser.getBlockedUserName();
    }

}
