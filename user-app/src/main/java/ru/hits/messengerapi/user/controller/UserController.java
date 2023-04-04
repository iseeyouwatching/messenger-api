package ru.hits.messengerapi.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hits.messengerapi.user.dto.*;
import ru.hits.messengerapi.user.service.implementation.UserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> userSignUp(@RequestBody @Valid UserSignUpDto userSignUpDto) {
        return new ResponseEntity<>(userService.userSignUp(userSignUpDto), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> userSignIn(@RequestBody @Valid UserSignInDto userSignInDto) {
        return new ResponseEntity<>(userService.userSignIn(userSignInDto), HttpStatus.OK);
    }

    @GetMapping("/{login}")
    public ResponseEntity<UserProfileDto> getUserInfo(@PathVariable("login") String login) {
        return new ResponseEntity<>(userService.getUserInfo(login), HttpStatus.OK);
    }

//    @PutMapping("/{login}")
//    public ResponseEntity<UserDto> updateUserInfo(@PathVariable("login") String login,
//                                                  @RequestBody @Valid UpdateUserInfoDto updateUserInfoDto) {
//        return new ResponseEntity<>(userService.updateUserInfo(login, updateUserInfoDto), HttpStatus.OK);
//    }

}
