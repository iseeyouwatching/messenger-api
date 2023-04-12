package ru.hits.messengerapi.friends.service;

import ru.hits.messengerapi.friends.dto.*;

public interface FriendServiceInterface {

    FriendsPageListDto getFriends(PaginationDto paginationDto);

    FriendDto addToFriends(AddToFriendsDto addToFriendsDto);
}
