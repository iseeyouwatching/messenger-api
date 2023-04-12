package ru.hits.messengerapi.friends.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.common.security.JwtUserData;
import ru.hits.messengerapi.friends.dto.AddToFriendsDto;
import ru.hits.messengerapi.friends.dto.FriendDto;
import ru.hits.messengerapi.friends.service.implementation.FriendService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static ru.hits.messengerapi.common.security.SecurityConst.HEADER_API_KEY;
import static ru.hits.messengerapi.common.security.SecurityConst.HEADER_JWT;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
@Tag(name = "Друзья")
public class FriendController {

    private final FriendService friendService;

    @PostMapping
    public ResponseEntity<FriendDto> addToFriends(@RequestBody @Valid AddToFriendsDto addToFriendsDto) {
        String token = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization").substring(7);
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8191/integration/users/checkUserByIdAndFullName?id={id}&fullName={fullName}";
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("id", addToFriendsDto.getAddedUserId())
                .queryParam("fullName", addToFriendsDto.getFriendName());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HEADER_API_KEY, "ac816388c1c44ac2b2ae431f89c82e7e345d25a0e6474e75a78f9a5ce496060c");
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
