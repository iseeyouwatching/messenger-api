package ru.hits.messengerapi.friends.service;

import ru.hits.messengerapi.friends.dto.PaginationDto;
import ru.hits.messengerapi.friends.dto.blacklist.BlockedUserDto;
import ru.hits.messengerapi.friends.dto.blacklist.BlockedUsersPageListDto;
import ru.hits.messengerapi.friends.dto.AddPersonDto;

import java.util.UUID;

public interface BlacklistServiceInterface {

    BlockedUsersPageListDto getBlockedUsers(PaginationDto paginationDto);

    BlockedUserDto getBlockedUser(UUID blockedUserId);

    BlockedUserDto addToBlacklist(AddPersonDto addPersonDto);

    void syncBlockedUserData(UUID id);

    BlockedUserDto deleteFromBlacklist(UUID blockedUserId);

}
