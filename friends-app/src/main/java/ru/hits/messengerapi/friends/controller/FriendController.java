package ru.hits.messengerapi.friends.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.common.security.props.SecurityProps;
import ru.hits.messengerapi.friends.dto.*;
import ru.hits.messengerapi.friends.service.implementation.FriendService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
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
                "http://localhost:8191/integration/users/check-existence?id={id}&fullName={fullName}";
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("id", addToFriendsDto.getAddedUserId())
                .queryParam("fullName", addToFriendsDto.getFriendName());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HEADER_API_KEY, securityProps.getIntegrations().getApiKey());
        headers.set(HEADER_JWT, "Bearer " + token);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, requestEntity, String.class);
        if (response.getBody().equals("exist")) {
            return new ResponseEntity<>(friendService.addToFriends(addToFriendsDto), HttpStatus.OK);
        }
        else {
            throw new NotFoundException("Пользователя с id " + addToFriendsDto.getAddedUserId()
                    + " и ФИО " + addToFriendsDto.getFriendName() + " не существует.");
        }
    }

}
