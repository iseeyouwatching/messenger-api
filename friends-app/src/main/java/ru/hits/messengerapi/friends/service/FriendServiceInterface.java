package ru.hits.messengerapi.friends.service;

import ru.hits.messengerapi.friends.dto.*;

import java.util.UUID;

public interface FriendServiceInterface {

    FriendsPageListDto getFriends(PaginationDto paginationDto);

    FriendDto getFriend(UUID addedUserId);

    FriendDto addToFriends(AddToFriendsDto addToFriendsDto);

    FriendDto deleteFriend(UUID addedUserId);

}
