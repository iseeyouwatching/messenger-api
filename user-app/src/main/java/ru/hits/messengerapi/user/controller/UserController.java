package ru.hits.messengerapi.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hits.messengerapi.user.dto.*;
import ru.hits.messengerapi.user.service.implementation.UserService;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserProfileDto> userSignUp(@RequestBody @Valid UserSignUpDto userSignUpDto) {
        UserProfileAndTokenDto userProfileAndTokenDto = userService.userSignUp(userSignUpDto);
        String token = userProfileAndTokenDto.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        return ResponseEntity.ok()
                .headers(headers)
                .body(userProfileAndTokenDto.getUserProfileDto());
    }

    @PostMapping("/login")
    public ResponseEntity<UserProfileDto> userSignIn(@RequestBody @Valid UserSignInDto userSignInDto) {
        UserProfileAndTokenDto userProfileAndTokenDto = userService.userSignIn(userSignInDto);
        String token = userProfileAndTokenDto.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        return ResponseEntity.ok()
                .headers(headers)
                .body(userProfileAndTokenDto.getUserProfileDto());
    }

    @GetMapping("/{login}")
    public ResponseEntity<UserProfileDto> getUserInfo(@PathVariable("login") String login) {
        return new ResponseEntity<>(userService.getUserInfo(login), HttpStatus.OK);
    }

    @Operation(
            summary = "Просмотр информации о себе.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<UserProfileDto> viewYourProfile() {
        return new ResponseEntity<>(userService.viewYourProfile(), HttpStatus.OK);
    }


//    @PutMapping("/{login}")
//    public ResponseEntity<UserDto> updateUserInfo(@PathVariable("login") String login,
//                                                  @RequestBody @Valid UpdateUserInfoDto updateUserInfoDto) {
//        return new ResponseEntity<>(userService.updateUserInfo(login, updateUserInfoDto), HttpStatus.OK);
//    }

}
