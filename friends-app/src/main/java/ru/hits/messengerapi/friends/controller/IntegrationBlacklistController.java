package ru.hits.messengerapi.friends.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hits.messengerapi.friends.service.implementation.BlacklistService;

import java.util.Map;
import java.util.UUID;

/**
 * Контроллер для интеграционных запросов.
 */
@RestController
@RequestMapping("/integration/blacklist")
@RequiredArgsConstructor
public class IntegrationBlacklistController {

    /**
     * Сервис черного списка.
     */
    private final BlacklistService blacklistService;

    /**
     * Проверка на нахождение человека в черном списке другого человека.
     *
     * @param map ID пользователей.
     * @return true - если пользователь находится в чернмо списке другого человека, false - если нет.
     */
    @PostMapping("/check-existence-in-blacklist")
    public ResponseEntity<Boolean> checkIfTheUserBlacklisted(@RequestBody Map<String, UUID> map) {
        return new ResponseEntity<>(blacklistService.checkIfTheTargetUserBlacklisted(
                map.get("targetUserId"),
                map.get("blockedUserId")),
                    HttpStatus.OK
        );
    }


}
