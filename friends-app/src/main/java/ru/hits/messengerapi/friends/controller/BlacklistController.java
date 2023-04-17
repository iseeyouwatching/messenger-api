package ru.hits.messengerapi.friends.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hits.messengerapi.friends.dto.blacklist.BlockedUserDto;
import ru.hits.messengerapi.friends.dto.blacklist.BlockedUsersPageListDto;
import ru.hits.messengerapi.friends.dto.blacklist.PaginationWithBlockedUserFiltersDto;
import ru.hits.messengerapi.friends.dto.blacklist.SearchedBlockedUsersDto;
import ru.hits.messengerapi.friends.dto.common.PaginationWithFullNameFilterDto;
import ru.hits.messengerapi.friends.dto.common.AddPersonDto;
import ru.hits.messengerapi.friends.service.implementation.BlacklistService;
import ru.hits.messengerapi.friends.service.implementation.IntegrationRequestsService;

import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;

/**
 * Контроллер для работы с черным списком пользователей.
 */
@RestController
@RequestMapping("/api/blacklist")
@RequiredArgsConstructor
@Slf4j
public class BlacklistController {

    /**
     * Сервис для работы с черным списком пользователей.
     */
    private final BlacklistService blacklistService;

    /**
     * Сервис для логики интеграционных запросов.
     */
    private final IntegrationRequestsService integrationRequestsService;

    /**
     * Метод для получения списка пользователей в черном списке.
     *
     * @param paginationWithFullNameFilterDto объект класса {@link PaginationWithFullNameFilterDto},
     *                                        содержащий информацию о постраничном выводе данных.
     * @return список пользователей в черном списке с информацией о пагинации и фильтре.
     */
    @PostMapping
    public ResponseEntity<BlockedUsersPageListDto> getBlockedUsers(
            @RequestBody @Valid PaginationWithFullNameFilterDto paginationWithFullNameFilterDto) {
        log.info("Запрос на получение списка пользователей в черном списке с параметрами: {}",
                paginationWithFullNameFilterDto);
        return new ResponseEntity<>(blacklistService.getBlockedUsers(paginationWithFullNameFilterDto),
                HttpStatus.OK);
    }

    /**
     * Метод для получения информации о пользователе из черного списка.
     *
     * @param id идентификатор пользователя из черного списка.
     * @return информация о пользователе из черного списка.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BlockedUserDto> getBlockedUser(@PathVariable("id") UUID id) {
        log.info("Запрос на получение информации о пользователе из черного списка с id: {}", id);
        return new ResponseEntity<>(blacklistService.getBlockedUser(id), HttpStatus.OK);
    }

    /**
     * Метод для добавления пользователя в черный список.
     *
     * @param addPersonDto объект класса {@link AddPersonDto},
     *                     содержащий информацию о пользователе, которого необходимо добавить в черный список.
     * @return информация о добавленном в черный список пользователе.
     */
    @PostMapping("/add")
    public ResponseEntity<BlockedUserDto> addToBlacklist(@RequestBody @Valid AddPersonDto addPersonDto) {
        log.info("Запрос на добавление пользователя в черный список с параметрами: {}", addPersonDto);
        return new ResponseEntity<>(blacklistService.addToBlacklist(addPersonDto), HttpStatus.OK);
    }

    /**
     * Метод для синхронизации информации о пользователе из черного списка.
     *
     * @param id идентификатор пользователя в черном списке.
     * @return сообщение об успешной синхронизации данных.
     */
    @PatchMapping("/sync/{id}")
    public ResponseEntity<Map<String, String>> syncBlockedUserData(@PathVariable("id") UUID id) {
        log.info("Синхронизация данных заблокированного пользователя с ID: {}", id);
        return new ResponseEntity<>(integrationRequestsService.syncBlockedUserData(id), HttpStatus.OK);
    }

    /**
     * Метод для удаления пользователя из черного списка.
     *
     * @param id идентификатор пользователя в черном списке.
     * @return информация об удаленном из черного списка пользователе.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<BlockedUserDto> deleteFromBlacklist(@PathVariable("id") UUID id) {
        log.info("Запрос на удаление пользователя с идентификатором {} из черного списка", id);
        return new ResponseEntity<>(blacklistService.deleteFromBlacklist(id), HttpStatus.OK);
    }

    /**
     * Метод для поиска заблокированных пользователей.
     *
     * @param paginationAndFilters объект класса {@link PaginationWithBlockedUserFiltersDto},
     *                             содержащий информацию о параметрах пагинации и фильтрации.
     * @return найденные заблокированные пользователи с информацией о пагинации и фильтрах.
     */
    @PostMapping("/search")
    public ResponseEntity<SearchedBlockedUsersDto> searchBlockedUsers(
            @RequestBody @Valid PaginationWithBlockedUserFiltersDto paginationAndFilters) {
        log.info("Поиск заблокированных пользователей с параметрами пагинации и фильтрации: {}",
                paginationAndFilters);
        SearchedBlockedUsersDto searchedBlockedUsers =
                blacklistService.searchBlockedUsers(paginationAndFilters);
        log.info("Найдено {} заблокированных пользователей", searchedBlockedUsers.getBlockedUsers().size());
        return new ResponseEntity<>(searchedBlockedUsers, HttpStatus.OK);
    }

    /**
     * Метод для проверки нахождения пользователя в черном списке.
     *
     * @param id идентификатор пользователя, которого нужно проверить.
     * @return сообщение о нахождении пользователя в черном списке или его отсутствии.
     */
    @GetMapping("/check-existing-in-blacklist/{id}")
    public ResponseEntity<Map<String, String>> checkIfTheUserBlacklisted(@PathVariable("id") UUID id) {
        boolean check = blacklistService.checkIfTheUserBlacklisted(id);
        if (check) {
            log.info("Пользователь с идентификатором {} находится в черном списке.", id);
            return new ResponseEntity<>(
                    Map.of("message", "Пользователь находится в черном списке."), HttpStatus.OK);
        }
        else {
            log.info("Пользователь с идентификатором {} не находится в черном списке.", id);
            return new ResponseEntity<>(
                    Map.of("message", "Пользователь не находится в черном списке."), HttpStatus.OK);
        }
    }

}
