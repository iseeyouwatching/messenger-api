package ru.hits.messengerapi.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hits.messengerapi.user.dto.*;
import ru.hits.messengerapi.user.service.implementation.UserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Пользователь")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Регистрация.")
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

    @Operation(summary = "Аутентификация.")
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

    @Operation(summary = "Список пользователей.")
    @PostMapping
    public ResponseEntity<UsersPageListDto> getUsers(@RequestBody @Valid PaginationDto paginationDto) {
        return new ResponseEntity<>(userService.getUserList(paginationDto), HttpStatus.OK);
    }

    @Operation(summary = "Просмотр профиля пользователя.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{login}")
    public ResponseEntity<UserProfileDto> getUserInfo(@PathVariable("login") String login) {
        return new ResponseEntity<>(userService.getUserInfo(login), HttpStatus.OK);
    }

    @Operation(summary = "Просмотр информации о себе.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public ResponseEntity<UserProfileDto> viewYourProfile() {
        return new ResponseEntity<>(userService.viewYourProfile(), HttpStatus.OK);
    }

    @Operation(summary = "Изменение профиля.", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping()
    public ResponseEntity<UserProfileDto> updateUserInfo(@RequestBody @Valid UpdateUserInfoDto updateUserInfoDto) {
        return new ResponseEntity<>(userService.updateUserInfo(updateUserInfoDto), HttpStatus.OK);
    }

}
