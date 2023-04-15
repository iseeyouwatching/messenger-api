package ru.hits.messengerapi.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        log.info("Получен запрос на регистрацию нового пользователя с email {}", userSignUpDto.getEmail());
        UserProfileAndTokenDto userProfileAndTokenDto = userService.userSignUp(userSignUpDto);

        String token = userProfileAndTokenDto.getToken();
        log.info("Сгенерирован токен для нового пользователя с login {}", userSignUpDto.getLogin());

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
        log.info("Получен запрос на вход для пользователя с login {}", userSignInDto.getLogin());
        UserProfileAndTokenDto userProfileAndTokenDto = userService.userSignIn(userSignInDto);

        String token = userProfileAndTokenDto.getToken();
        log.info("Сгенерирован токен для пользователя с login {}", userSignInDto.getLogin());

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
        log.info("Получен запрос на получение списка пользователей с параметрами: " +
                "страница {}, размер страницы {}, фильтры {}, сортировки",
                paginationDto.getPageInfo().getPageNumber(),
                paginationDto.getPageInfo().getPageSize(), paginationDto.getFilters(),
                paginationDto.getSortings());
        UsersPageListDto usersPageListDto = userService.getUserList(paginationDto);
        log.info("Получен список пользователей размером {}", usersPageListDto.getUsers().size());

        return ResponseEntity.ok().body(usersPageListDto);
    }

    /**
     * Метод для получения профиля пользователя по логину.
     *
     * @param login логин пользователя.
     * @return {@link ResponseEntity} с {@link UserProfileDto}.
     */
    @GetMapping("/{login}")
    public ResponseEntity<UserProfileDto> getUserInfo(@PathVariable("login") String login) {
        log.info("Получен запрос на получение информации о пользователе с логином {}", login);
        UserProfileDto userProfileDto = userService.getUserInfo(login);
        log.info("Получена информация о пользователе {}", userProfileDto);

        return ResponseEntity.ok().body(userProfileDto);
    }

    /**
     * Метод для получения информации о профиле текущего пользователя.
     *
     * @return {@link ResponseEntity} с {@link UserProfileDto}.
     */
    @GetMapping
    public ResponseEntity<UserProfileDto> viewYourProfile() {
        log.info("Вызов метода для просмотра профиля текущего пользователя");
        UserProfileDto userProfileDto = userService.viewYourProfile();
        log.info("Профиль пользователя успешно получен: {}", userProfileDto);
        return ResponseEntity.ok().body(userProfileDto);
    }

    /**
     * Метод для обновления информации о профиле текущего пользователя.
     *
     * @param updateUserInfoDto объект с обновленной информацией о пользователе.
     * @return {@link ResponseEntity} с {@link UserProfileDto}.
     */
    @PutMapping()
    public ResponseEntity<UserProfileDto> updateUserInfo(@RequestBody @Valid UpdateUserInfoDto updateUserInfoDto) {
        log.info("Вызов метода для обновления информации о пользователе");
        UserProfileDto userProfileDto = userService.updateUserInfo(updateUserInfoDto);
        log.info("Информация о пользователе успешно обновлена: {}", userProfileDto);
        return ResponseEntity.ok().body(userProfileDto);
    }

}
