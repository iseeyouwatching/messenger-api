package ru.hits.messengerapi.friends.service;

import ru.hits.messengerapi.friends.dto.blacklist.PaginationWithBlockedUserFiltersDto;
import ru.hits.messengerapi.friends.dto.blacklist.SearchedBlockedUsersDto;
import ru.hits.messengerapi.friends.dto.common.PaginationDto;
import ru.hits.messengerapi.friends.dto.blacklist.BlockedUserDto;
import ru.hits.messengerapi.friends.dto.blacklist.BlockedUsersPageListDto;
import ru.hits.messengerapi.friends.dto.common.AddPersonDto;

import java.util.Map;
import java.util.UUID;

public interface BlacklistServiceInterface {

    BlockedUsersPageListDto getBlockedUsers(PaginationDto paginationDto);

    BlockedUserDto getBlockedUser(UUID blockedUserId);

    BlockedUserDto addToBlacklist(AddPersonDto addPersonDto);

    Map<String, String> syncBlockedUserData(UUID id);

    BlockedUserDto deleteFromBlacklist(UUID blockedUserId);

    SearchedBlockedUsersDto searchBlockedUsers(PaginationWithBlockedUserFiltersDto paginationAndfilters);

    boolean checkIfTheUserBlacklisted(UUID id);

    boolean checkIfTheTargetUserBlacklisted(UUID targetUserId, UUID blockedUserId);

}
