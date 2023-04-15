package ru.hits.messengerapi.friends.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class IntegrationBlacklistController {

    /**
     * Сервис черного списка.
     */
    private final BlacklistService blacklistService;

    /**
     * Проверка на нахождение пользователя в черном списке другого пользователя.
     *
     * @param map ID пользователей.
     * @return true - если пользователь находится в черном списке другого пользователя, false - если нет.
     */
    @PostMapping("/check-existence-in-blacklist")
    public ResponseEntity<Boolean> checkIfTheUserBlacklisted(@RequestBody Map<String, UUID> map) {
        UUID targetUserId = map.get("targetUserId");
        UUID blockedUserId = map.get("blockedUserId");
        boolean isBlacklisted = blacklistService.checkIfTheTargetUserBlacklisted(targetUserId, blockedUserId);
        log.info("Запрос на проверку нахождения пользователя в черном списке другого пользователя э" +
                "с параметрами targetUserId={}, " + "blockedUserId={}, результат={}",
                targetUserId, blockedUserId, isBlacklisted);
        return new ResponseEntity<>(isBlacklisted, HttpStatus.OK);
    }


}
