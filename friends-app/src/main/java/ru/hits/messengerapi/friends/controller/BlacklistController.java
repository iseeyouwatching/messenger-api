package ru.hits.messengerapi.friends.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hits.messengerapi.friends.dto.blacklist.BlockedUserDto;
import ru.hits.messengerapi.friends.dto.blacklist.BlockedUsersPageListDto;
import ru.hits.messengerapi.friends.dto.blacklist.PaginationWithBlockedUserFiltersDto;
import ru.hits.messengerapi.friends.dto.blacklist.SearchedBlockedUsersDto;
import ru.hits.messengerapi.friends.dto.common.PaginationDto;
import ru.hits.messengerapi.friends.dto.common.AddPersonDto;
import ru.hits.messengerapi.friends.dto.friends.PaginationWithFriendFiltersDto;
import ru.hits.messengerapi.friends.dto.friends.SearchedFriendsDto;
import ru.hits.messengerapi.friends.service.implementation.BlacklistService;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/blacklist")
@RequiredArgsConstructor
public class BlacklistController {

    private final BlacklistService blacklistService;

    @PostMapping
    public ResponseEntity<BlockedUsersPageListDto> getBlockedUsers(@RequestBody @Valid PaginationDto paginationDto) {
        return new ResponseEntity<>(blacklistService.getBlockedUsers(paginationDto), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlockedUserDto> getBlockedUser(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(blacklistService.getBlockedUser(id), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<BlockedUserDto> addToFriends(@RequestBody @Valid AddPersonDto addPersonDto) {
        return new ResponseEntity<>(blacklistService.addToBlacklist(addPersonDto), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public void syncBlockedUserData(@PathVariable("id") UUID id) {
        blacklistService.syncBlockedUserData(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BlockedUserDto> deleteFromBlacklist(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(blacklistService.deleteFromBlacklist(id), HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<SearchedBlockedUsersDto> searchBlockedUsers(
            @RequestBody @Valid PaginationWithBlockedUserFiltersDto paginationAndFilters) {
        return new ResponseEntity<>(blacklistService.searchBlockedUsers(paginationAndFilters), HttpStatus.OK);
    }



}
