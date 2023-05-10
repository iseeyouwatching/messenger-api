package ru.hits.messengerapi.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hits.messengerapi.user.dto.UserIdAndFullNameDto;
import ru.hits.messengerapi.user.service.IntegrationUserService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Контроллер для работы с интеграционными запросами.
 */
@RestController
@RequestMapping("/integration/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Интеграционные запросы в сервис пользователя.")
public class IntegrationUserController {

    /**
     * Сервис интеграционных запросов.
     */
    private final IntegrationUserService integrationUserService;

    /**
     * Метод для проверки существования пользователя по его ФИО и ID.
     *
     * @param userIdAndFullNameDto DTO с ФИО и ID пользователя.
     * @return exist - если пользователь существует, dont exist - если пользователя не существует.
     */
    @Operation(summary = "Проверить существование пользователя по ID и ФИО.")
    @SecurityRequirement(name = "api_key")
    @PostMapping("/check-existence")
    public ResponseEntity<Boolean> checkUserByIdAndFullName(
            @RequestBody UserIdAndFullNameDto userIdAndFullNameDto) {
        UUID userId = userIdAndFullNameDto.getId();
        String fullName = userIdAndFullNameDto.getFullName();
        log.info("Получен запрос на проверку пользователя по id = {} и полному имени '{}'",
                userId, fullName);

        boolean result = integrationUserService.checkUserByIdAndFullName(userId, fullName);
        log.info("Результат проверки пользователя по id = {} и полному имени '{}' равен {}",
                userId, fullName, result);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Метод для проверки существования пользователя по ID.
     *
     * @param id идентификатор пользователя.
     * @return true - если пользователь с таким ID существует.
     */
    @Operation(summary = "Проверить существование пользователя по ID.")
    @SecurityRequirement(name = "api_key")
    @PostMapping("/check-existence-by-id")
    public ResponseEntity<Boolean> checkUserById(@RequestBody UUID id) {
        boolean result = integrationUserService.checkUserById(id);
        log.info("Результат проверки пользователя по id = {} равен {}",
                id, result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Метод получения ФИО пользователя по его ID.
     *
     * @param id идентификатор пользователя.
     * @return ФИО пользователя.
     */
    @Operation(summary = "Получить ФИО пользователя по его ID.")
    @SecurityRequirement(name = "api_key")
    @PostMapping ("/get-full-name")
    public ResponseEntity<String> getFullName(@RequestBody UUID id) {
        log.info("Получен запрос на получение полного имени пользователя с id = {}", id);
        String fullName = integrationUserService.getFullName(id);
        log.info("Получено полное имя '{}' для пользователя с id = {}", fullName, id);

        return new ResponseEntity<>(fullName, HttpStatus.OK);
    }

    /**
     * Метод для получения ФИО и аватарки пользователя по его ID.
     *
     * @param id идентификатор пользователя.
     * @return ФИО и аватарка пользователя.
     */
    @Operation(summary = "Получить ФИО и аватарку пользователя по его ID.")
    @SecurityRequirement(name = "api_key")
    @PostMapping ("/get-full-name-and-avatar")
    public ResponseEntity<List<String>> getFullNameAndAvatar(@RequestBody UUID id) {
        return new ResponseEntity<>(integrationUserService.getFullNameAndAvatar(id), HttpStatus.OK);
    }

}
