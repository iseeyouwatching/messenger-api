package ru.hits.messengerapi.friends.dto;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddToFriendsDto {

    private UUID addedUserId;

    private String friendName;

}
