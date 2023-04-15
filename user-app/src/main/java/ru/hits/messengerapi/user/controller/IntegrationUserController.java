package ru.hits.messengerapi.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hits.messengerapi.user.service.implementation.IntegrationUserService;

import java.util.Map;
import java.util.UUID;

/**
 * Контроллер для работы с интеграционными запросами.
 */
@RestController
@RequestMapping("/integration/users")
@RequiredArgsConstructor
@Slf4j
public class IntegrationUserController {

    /**
     * Сервис интеграционных запросов.
     */
    private final IntegrationUserService integrationUserService;

    /**
     * Метод для проверки существования пользователя по его ФИО и ID.
     *
     * @param userIdAndFullName ФИО и ID пользователя.
     * @return exist - если пользователь существует, dont exist - если пользователя не существует.
     */
    @PostMapping("/check-existence")
    public ResponseEntity<Boolean> checkUserByIdAndFullName(
            @RequestBody Map<String, String> userIdAndFullName) {
        UUID userId = UUID.fromString(userIdAndFullName.get("id"));
        String fullName = userIdAndFullName.get("fullName");
        log.info("Получен запрос на проверку пользователя по id = {} и полному имени '{}'",
                userId, fullName);

        boolean result = integrationUserService.checkUserByIdAndFullName(userId, fullName);
        log.info("Результат проверки пользователя по id = {} и полному имени '{}' равен {}",
                userId, fullName, result);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Метод получения ФИО пользователя по его ID.
     *
     * @param id идентификатор пользователя.
     * @return ФИО пользователя.
     */
    @PostMapping ("/get-full-name")
    public ResponseEntity<String> getFullName(@RequestBody UUID id) {
        log.info("Получен запрос на получение полного имени пользователя с id = {}", id);
        String fullName = integrationUserService.getFullName(id);
        log.info("Получено полное имя '{}' для пользователя с id = {}", fullName, id);

        return new ResponseEntity<>(fullName, HttpStatus.OK);
    }

}
