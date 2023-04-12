package ru.hits.messengerapi.friends.service;

import ru.hits.messengerapi.friends.dto.*;

import java.util.Map;
import java.util.UUID;

public interface FriendServiceInterface {

    FriendsPageListDto getFriends(PaginationDto paginationDto);

    FriendDto getFriend(UUID addedUserId);

    FriendDto addToFriends(AddToFriendsDto addToFriendsDto);

    Map<String, String> syncFriendData(UUID id, String fullName);

    FriendDto deleteFriend(UUID addedUserId);

}
