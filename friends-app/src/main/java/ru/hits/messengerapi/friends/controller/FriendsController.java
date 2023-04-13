package ru.hits.messengerapi.friends.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.common.security.props.SecurityProps;
import ru.hits.messengerapi.friends.dto.*;
import ru.hits.messengerapi.friends.service.implementation.FriendsService;

import javax.validation.Valid;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static ru.hits.messengerapi.common.security.SecurityConst.HEADER_API_KEY;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendsController {

    private final FriendsService friendsService;
    private final SecurityProps securityProps;

    @PostMapping
    public ResponseEntity<FriendsPageListDto> getFriends(@RequestBody @Valid PaginationDto paginationDto) {
        return new ResponseEntity<>(friendsService.getFriends(paginationDto), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FriendDto> getFriend(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(friendsService.getFriend(id), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<FriendDto> addToFriends(@RequestBody @Valid AddToFriendsDto addToFriendsDto) {
        RestTemplate restTemplate = new RestTemplate();
        String url =
                "http://localhost:8191/integration/users/check-existence";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HEADER_API_KEY, securityProps.getIntegrations().getApiKey());
        HttpEntity<AddToFriendsDto> requestEntity = new HttpEntity<>(addToFriendsDto, headers);

        ResponseEntity<String> responseEntity = restTemplate
                .exchange(url, HttpMethod.POST, requestEntity, String.class);

        if (Objects.equals(responseEntity.getBody(), "exist")) {
            return new ResponseEntity<>(friendsService.addToFriends(addToFriendsDto), HttpStatus.OK);
        }
        else {
            throw new NotFoundException("Пользователя с id " + addToFriendsDto.getId()
                    + " и ФИО " + addToFriendsDto.getFullName() + " не существует.");
        }
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
