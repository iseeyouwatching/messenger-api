package ru.hits.messengerapi.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hits.messengerapi.user.dto.UpdateUserInfoDto;
import ru.hits.messengerapi.user.dto.UserDto;
import ru.hits.messengerapi.user.dto.UserSignUpDto;
import ru.hits.messengerapi.user.service.implementation.UserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> userSignUp(@RequestBody @Valid UserSignUpDto userSignUpDto) {
        return new ResponseEntity<>(userService.userSignUp(userSignUpDto), HttpStatus.OK);
    }

    @GetMapping("/{login}")
    public ResponseEntity<UserDto> getUserInfo(@PathVariable("login") String login) {
        return new ResponseEntity<>(userService.getUserInfo(login), HttpStatus.OK);
    }

//    @PutMapping("/{login}")
//    public ResponseEntity<UserDto> updateUserInfo(@PathVariable("login") String login,
//                                                  @RequestBody @Valid UpdateUserInfoDto updateUserInfoDto) {
//        return new ResponseEntity<>(userService.updateUserInfo(login, updateUserInfoDto), HttpStatus.OK);
//    }

}
