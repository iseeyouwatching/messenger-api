package ru.hits.messengerapi.friends.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.hits.messengerapi.common.security.JwtUserData;
import ru.hits.messengerapi.friends.dto.AddToFriendsDto;
import ru.hits.messengerapi.friends.dto.FriendDto;
import ru.hits.messengerapi.friends.service.implementation.FriendService;

import javax.validation.Valid;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
@Tag(name = "Друзья")
public class FriendController {

    private final FriendService friendService;

    @PostMapping
    public ResponseEntity<FriendDto> addToFriends(@RequestBody @Valid AddToFriendsDto addToFriendsDto) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

//        RestTemplate restTemplate = new RestTemplate();
//        String url = "http://localhost:8191/integration/users";
//        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
//        queryParams.add("id", String.valueOf(addToFriendsDto.getAddedUserId()));
//        queryParams.add("fullName", addToFriendsDto.getFriendName());
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("API_KEY", "ac816388c1c44ac2b2ae431f89c82e7e345d25a0e6474e75a78f9a5ce496060c");
//        headers.set("Authorization", "Bearer " + "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJVc2VyIGRldGFpbHMiLCJpc3MiOiJtZXNzZW5nZXItYXBpIiwiaWQiOiI5YjhiNjQ4Mi05OWRlLTQ1MjEtODgzZi03MTRmNzg0NTU5ZDYiLCJsb2dpbiI6Inh4aG51IiwiZXhwIjoxNjgxMjMxOTExLCJpYXQiOjE2ODEyMjgzMTF9.dE3PrVTs89vECXk9UCrLefihjk3jbaM_Kes6un1x9Dw");
//        System.out.println(headers.get("Authorization"));
//        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(queryParams, headers);
//        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
//                String.class);

        return new ResponseEntity<>(friendService.addToFriends(addToFriendsDto), HttpStatus.OK);
    }

}
