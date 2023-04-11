package ru.hits.messengerapi.user.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.messengerapi.user.service.implementation.IntegrationUserService;

@RestController
@RequestMapping("/integration/users")
@RequiredArgsConstructor
public class IntegrationUserController {

    private final IntegrationUserService integrationUserService;

    @GetMapping
    public ResponseEntity<String> checkUserByIdAndFullName(@RequestParam("id") String id,
                                                           @RequestParam("fullName") String fullName) {
        return new ResponseEntity<>(integrationUserService.checkUserByIdAndFullName(id, fullName), HttpStatus.OK);
    }
}
