package ru.hits.messengerapi.friends.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FriendFiltersDto {

    private LocalDateTime addedDate;

    private UUID addedUserId;

    private LocalDateTime deletedDate;

    private String friendName;

}
