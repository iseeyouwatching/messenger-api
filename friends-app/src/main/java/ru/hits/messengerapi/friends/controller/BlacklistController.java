package ru.hits.messengerapi.friends.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.messengerapi.friends.dto.blacklist.BlockedUserDto;
import ru.hits.messengerapi.friends.dto.blacklist.BlockedUsersPageListDto;
import ru.hits.messengerapi.friends.dto.PaginationDto;
import ru.hits.messengerapi.friends.dto.AddPersonDto;
import ru.hits.messengerapi.friends.dto.friends.FriendDto;
import ru.hits.messengerapi.friends.service.implementation.BlacklistService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/blacklist")
@RequiredArgsConstructor
public class BlacklistController {

    private final BlacklistService blacklistService;

    @PostMapping
    public ResponseEntity<BlockedUsersPageListDto> getBlockedUsers(@RequestBody @Valid PaginationDto paginationDto) {
        return new ResponseEntity<>(blacklistService.getBlockedUsers(paginationDto), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<BlockedUserDto> addToFriends(@RequestBody @Valid AddPersonDto addPersonDto) {
        return new ResponseEntity<>(blacklistService.addToBlacklist(addPersonDto), HttpStatus.OK);
    }

}
