package ru.hits.messengerapi.user.controller;

import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<String> checkUserByIdAndFullName(
            @RequestBody Map<String, String> userIdAndFullName) {
        return new ResponseEntity<>(integrationUserService.checkUserByIdAndFullName(
                UUID.fromString(userIdAndFullName.get("id")),
                userIdAndFullName.get("fullName")),
                HttpStatus.OK
        );
    }

    /**
     * Метод получения ФИО пользователя по его ID.
     *
     * @param id идентификатор пользователя.
     * @return ФИО пользователя.
     */
    @PostMapping ("/get-full-name")
    public ResponseEntity<String> getFullName(@RequestBody UUID id) {
        return new ResponseEntity<>(integrationUserService.getFullName(id), HttpStatus.OK);
    }

}
