package ru.hits.messengerapi.friends.service;

import ru.hits.messengerapi.friends.dto.AddToFriendsDto;
import ru.hits.messengerapi.friends.dto.FriendDto;

public interface FriendServiceInterface {

    FriendDto addToFriends(AddToFriendsDto addToFriendsDto);
}
