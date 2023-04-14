package ru.hits.messengerapi.friends.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import ru.hits.messengerapi.friends.dto.common.AddPersonDto;
import ru.hits.messengerapi.friends.dto.common.PaginationDto;
import ru.hits.messengerapi.friends.dto.friends.*;
import ru.hits.messengerapi.friends.service.implementation.FriendsService;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendsController {

    private final FriendsService friendsService;

    @PostMapping
    public ResponseEntity<FriendsPageListDto> getFriends(@RequestBody @Valid PaginationDto paginationDto) {
        return new ResponseEntity<>(friendsService.getFriends(paginationDto), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FriendDto> getFriend(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(friendsService.getFriend(id), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<FriendDto> addToFriends(@RequestBody @Valid AddPersonDto addPersonDto) {
        return new ResponseEntity<>(friendsService.addToFriends(addPersonDto), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public void syncFriendData(@PathVariable("id") UUID id) {
        friendsService.syncFriendData(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FriendDto> deleteFriend(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(friendsService.deleteFriend(id), HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<SearchedFriendsDto> searchFriends(
            @RequestBody @Valid PaginationWithFriendFiltersDto paginationAndFilters) {
        return new ResponseEntity<>(friendsService.searchFriends(paginationAndFilters), HttpStatus.OK);
    }

}
