package ru.hits.messengerapi.friends.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.messengerapi.friends.service.implementation.IntegrationFriendsService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/integration/friends")
@RequiredArgsConstructor
@Slf4j
public class IntegrationFriendsController {

    private final IntegrationFriendsService integrationFriendsService;

    @PostMapping("/check-existence-in-friends")
    public ResponseEntity<Boolean> checkExistenceInFriends(@RequestBody List<UUID> uuids) {
        return new ResponseEntity<>(integrationFriendsService.checkExistenceInFriends(uuids.get(0), uuids.get(1)),
                HttpStatus.OK);
    }

    @PostMapping("/check-multi-existence-in-friends")
    public ResponseEntity<Boolean> checkMultiExistenceInFriends(@RequestBody List<UUID> uuids) {
        return new ResponseEntity<>(integrationFriendsService.checkMultiExistenceInFriends(uuids), HttpStatus.OK);
    }
}
