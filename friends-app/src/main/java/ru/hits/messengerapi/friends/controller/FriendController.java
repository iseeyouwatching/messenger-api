package ru.hits.messengerapi.friends.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.common.security.props.SecurityProps;
import ru.hits.messengerapi.friends.dto.*;
import ru.hits.messengerapi.friends.service.implementation.FriendService;

import javax.validation.Valid;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static ru.hits.messengerapi.common.security.SecurityConst.HEADER_API_KEY;
import static ru.hits.messengerapi.common.security.SecurityConst.HEADER_JWT;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;
    private final SecurityProps securityProps;

    @PostMapping
    public ResponseEntity<FriendsPageListDto> getFriends(@RequestBody @Valid PaginationDto paginationDto) {
        return new ResponseEntity<>(friendService.getFriends(paginationDto), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FriendDto> getFriend(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(friendService.getFriend(id), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<FriendDto> addToFriends(@RequestBody @Valid AddToFriendsDto addToFriendsDto) {
        String token = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()
                .getHeader("Authorization").substring(7);
        RestTemplate restTemplate = new RestTemplate();
        String url =
                "http://localhost:8191/integration/users/check-existence";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HEADER_API_KEY, securityProps.getIntegrations().getApiKey());
        headers.set(HEADER_JWT, "Bearer " + token);
        HttpEntity<AddToFriendsDto> requestEntity = new HttpEntity<>(addToFriendsDto, headers);

        ResponseEntity<String> responseEntity = restTemplate
                .exchange(url, HttpMethod.POST, requestEntity, String.class);

        if (Objects.equals(responseEntity.getBody(), "exist")) {
            return new ResponseEntity<>(friendService.addToFriends(addToFriendsDto), HttpStatus.OK);
        }
        else {
            throw new NotFoundException("Пользователя с id " + addToFriendsDto.getId()
                    + " и ФИО " + addToFriendsDto.getFullName() + " не существует.");
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, String>> syncFriendData(@PathVariable("id") UUID id) {
        String token = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()
                .getHeader("Authorization").substring(7);
        RestTemplate restTemplate = new RestTemplate();
        String url =
                "http://localhost:8191/integration/users/get-full-name";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HEADER_API_KEY, securityProps.getIntegrations().getApiKey());
        headers.set(HEADER_JWT, "Bearer " + token);
        HttpEntity<UUID> requestEntity = new HttpEntity<>(id, headers);

        ResponseEntity<String> responseEntity = restTemplate
                .exchange(url, HttpMethod.POST, requestEntity, String.class);

        if (!Objects.equals(responseEntity.getBody(), "dont exist")) {
            return new ResponseEntity<>(friendService
                    .syncFriendData(id, responseEntity.getBody()), HttpStatus.OK);
        }
        else {
            throw new NotFoundException("Пользователя с id " + id + " не существует.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FriendDto> deleteFriend(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(friendService.deleteFriend(id), HttpStatus.OK);
    }

}
