package ru.hits.messengerapi.friends.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.messengerapi.friends.service.FriendService;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
@Tag(name = "Друзья")
public class FriendController {

    private final FriendService friendService;

    @GetMapping
    public String test() {
        return "Hello World";
    }

}
