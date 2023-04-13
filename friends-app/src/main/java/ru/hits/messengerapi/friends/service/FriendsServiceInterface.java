package ru.hits.messengerapi.friends.service;

import ru.hits.messengerapi.friends.dto.*;
import ru.hits.messengerapi.friends.dto.friends.*;

import java.util.UUID;

public interface FriendsServiceInterface {

    FriendsPageListDto getFriends(PaginationDto paginationDto);

    FriendDto getFriend(UUID addedUserId);

    FriendDto addToFriends(AddPersonDto addPersonDto);

    void syncFriendData(UUID id);

    FriendDto deleteFriend(UUID addedUserId);

    SearchedFriendsDto searchFriends(PaginationWithFriendFiltersDto filters);

}
