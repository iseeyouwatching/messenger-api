package ru.hits.messengerapi.friends.dto.friends;

import lombok.*;
import ru.hits.messengerapi.friends.entity.FriendEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FriendDto {

    private UUID id;

    private LocalDateTime addedDate;

    private LocalDateTime deletedDate;

    private UUID targetUserId;

    private UUID addedUserId;

    private String friendName;

    public FriendDto(FriendEntity friend) {
        this.id = friend.getId();
        this.addedDate = friend.getAddedDate();
        this.deletedDate = friend.getDeletedDate();
        this.targetUserId = friend.getTargetUserId();
        this.addedUserId = friend.getAddedUserId();
        this.friendName = friend.getFriendName();
    }

}
