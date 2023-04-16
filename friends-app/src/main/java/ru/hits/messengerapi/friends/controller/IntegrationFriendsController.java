package ru.hits.messengerapi.friends.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.messengerapi.friends.service.implementation.FriendsService;
import ru.hits.messengerapi.friends.service.implementation.IntegrationRequestsService;

import java.util.Map;
import java.util.UUID;

/**
 * Контроллер для интеграционных запросов, связанных с друзьями.
 */
@RestController
@RequestMapping("/integration/friends")
@RequiredArgsConstructor
@Slf4j
public class IntegrationFriendsController {

    private final IntegrationRequestsService integrationRequestsService;

    /**
     * Метод для синхронизации информации о пользователе, находящегося в друзьях.
     *
     * @param id идентификатор пользователя, находящегося в друзьях.
     * @return сообщение об успешной синхронизации данных.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, String>> syncFriendData(@PathVariable("id") UUID id) {
        Map<String, String> response = integrationRequestsService.syncFriendData(id);
        log.info("Данные друга с идентификатором {} синхронизированы", id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
