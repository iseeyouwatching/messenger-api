package ru.hits.messengerapi.friends.service;

import ru.hits.messengerapi.friends.dto.PaginationDto;
import ru.hits.messengerapi.friends.dto.blacklist.BlockedUsersPageListDto;

public interface BlacklistServiceInterface {

    BlockedUsersPageListDto getBlockedUsers(PaginationDto paginationDto);

}
