package ru.hits.messengerapi.friends.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.messengerapi.friends.service.IntegrationFriendsService;

import java.util.List;
import java.util.UUID;

/**
 * Сервис интеграционных запросов, связанных с друзьями.
 */
@RestController
@RequestMapping("/integration/friends")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Интеграционные запросы, связанные с друзьями.")
public class IntegrationFriendsController {

    /**
     * Сервис с логикой интеграционных запросов, связанных с друзьями.
     */
    private final IntegrationFriendsService integrationFriendsService;

    /**
     * Проверка нахождения пользователя в друзьях у другого пользователя.
     *
     * @param uuids список идентификаторов пользователей.
     * @return true - если пользователь находится в друзьях у другого пользователя.
     */
    @Operation(summary = "Проверить нахождение пользователя в друзьях у другого пользователя.")
    @SecurityRequirement(name = "api_key")
    @PostMapping("/check-existence-in-friends")
    public ResponseEntity<Boolean> checkExistenceInFriends(@RequestBody List<UUID> uuids) {
        log.info("Проверка нахождения пользователя в друзьях у другого пользователя: {}", uuids);
        return new ResponseEntity<>(integrationFriendsService.checkExistenceInFriends(uuids.get(0), uuids.get(1)),
                HttpStatus.OK);
    }

    /**
     * Проверка нахождения пользователей в друзьях у другого пользователя.
     *
     * @param uuids список идентификаторов пользователей.
     * @return true - если пользователи находятся в друзьях у другого пользователя.
     */
    @Operation(summary = "Проверить нахождение пользователей в друзьях у другого пользователя.")
    @SecurityRequirement(name = "api_key")
    @PostMapping("/check-multi-existence-in-friends")
    public ResponseEntity<Boolean> checkMultiExistenceInFriends(@RequestBody List<UUID> uuids) {
        log.info("Проверка нахождения пользователей в друзьях у другого пользователя: {}", uuids);
        return new ResponseEntity<>(integrationFriendsService.checkMultiExistenceInFriends(uuids), HttpStatus.OK);
    }
}
