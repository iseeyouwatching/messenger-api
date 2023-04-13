package ru.hits.messengerapi.friends.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.messengerapi.common.security.props.SecurityProps;
import ru.hits.messengerapi.friends.dto.blacklist.BlockedUsersPageListDto;
import ru.hits.messengerapi.friends.dto.PaginationDto;
import ru.hits.messengerapi.friends.service.implementation.BlacklistService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/blacklist")
@RequiredArgsConstructor
public class BlacklistController {

    private final BlacklistService blacklistService;
    private final SecurityProps securityProps;

    @PostMapping
    public ResponseEntity<BlockedUsersPageListDto> getBlockedUsers(@RequestBody @Valid PaginationDto paginationDto) {
        return new ResponseEntity<>(blacklistService.getBlockedUsers(paginationDto), HttpStatus.OK);
    }
}
