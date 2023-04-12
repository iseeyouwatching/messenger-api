package ru.hits.messengerapi.friends.dto;

import lombok.*;
import ru.hits.messengerapi.friends.entity.FriendEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FriendInfoDto {

    private UUID id;

    private LocalDateTime addedDate;

    private LocalDateTime deletedDate;

    private UUID addedUserId;

    private String friendName;

    public FriendInfoDto(FriendEntity friend) {
        this.id = friend.getId();
        this.addedDate = friend.getAddedDate();
        this.deletedDate = friend.getDeletedDate();
        this.addedUserId = friend.getAddedUserId();
        this.friendName = friend.getFriendName();
    }

}
