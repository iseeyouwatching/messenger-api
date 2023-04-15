package ru.hits.messengerapi.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hits.messengerapi.user.dto.*;
import ru.hits.messengerapi.user.service.implementation.UserService;

import javax.validation.Valid;

/**
 * Контроллер для работы с пользователем.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    /**
     * Сервис пользователя.
     */
    private final UserService userService;

    /**
     * Метод для регистрации пользователя.
     *
     * @param userSignUpDto DTO для регистрации пользователя.
     * @return {@link ResponseEntity} с {@link UserProfileDto} и заголовком авторизации.
     */
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

    /**
     * Метод для аутентификации пользователя.
     *
     * @param userSignInDto DTO для аутентификации пользователя.
     * @return {@link ResponseEntity} с {@link UserProfileDto} и заголовком авторизации.
     */
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

    /**
     * Метод для получения списка пользователей.
     *
     * @param paginationDto DTO для пагинации списка пользователей.
     * @return @link ResponseEntity} с {@link UsersPageListDto}.
     */
    @PostMapping
    public ResponseEntity<UsersPageListDto> getUsers(@RequestBody @Valid PaginationDto paginationDto) {
        return new ResponseEntity<>(userService.getUserList(paginationDto), HttpStatus.OK);
    }

    /**
     * Метод для получения профиля пользователя по логину.
     *
     * @param login логин пользователя.
     * @return {@link ResponseEntity} с {@link UserProfileDto}.
     */
    @GetMapping("/{login}")
    public ResponseEntity<UserProfileDto> getUserInfo(@PathVariable("login") String login) {
        return new ResponseEntity<>(userService.getUserInfo(login), HttpStatus.OK);
    }

    /**
     * Метод для получения информации о профиле текущего пользователя.
     *
     * @return {@link ResponseEntity} с {@link UserProfileDto}.
     */
    @GetMapping
    public ResponseEntity<UserProfileDto> viewYourProfile() {
        return new ResponseEntity<>(userService.viewYourProfile(), HttpStatus.OK);
    }

    /**
     * Метод для обновления информации о профиле текущего пользователя.
     *
     * @param updateUserInfoDto объект с обновленной информацией о пользователе.
     * @return {@link ResponseEntity} с {@link UserProfileDto}.
     */
    @PutMapping()
    public ResponseEntity<UserProfileDto> updateUserInfo(@RequestBody @Valid UpdateUserInfoDto updateUserInfoDto) {
        return new ResponseEntity<>(userService.updateUserInfo(updateUserInfoDto), HttpStatus.OK);
    }

}
