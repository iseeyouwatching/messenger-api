package ru.hits.messengerapi.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hits.messengerapi.user.dto.UserIdAndFullNameDto;
import ru.hits.messengerapi.user.service.implementation.IntegrationUserService;

import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/integration/users")
@RequiredArgsConstructor
public class IntegrationUserController {

    private final IntegrationUserService integrationUserService;

    @PostMapping("/check-existence")
    public ResponseEntity<String> checkUserByIdAndFullName(
            @RequestBody Map<String, String> userIdAndFullName) {
        return new ResponseEntity<>(integrationUserService.checkUserByIdAndFullName(
                UUID.fromString(userIdAndFullName.get("id")),
                userIdAndFullName.get("fullName")),
                HttpStatus.OK
        );
    }

    @PostMapping ("/get-full-name")
    public ResponseEntity<String> getFullName(@RequestBody UUID id) {
        return new ResponseEntity<>(integrationUserService.getFullName(id), HttpStatus.OK);
    }

}
