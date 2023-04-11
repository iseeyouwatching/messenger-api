package ru.hits.messengerapi.friends.dto;

import lombok.*;

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

}
