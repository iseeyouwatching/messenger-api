package ru.hits.messengerapi.friends.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hits.messengerapi.friends.dto.blacklist.BlockedUserDto;
import ru.hits.messengerapi.friends.dto.blacklist.BlockedUsersPageListDto;
import ru.hits.messengerapi.friends.dto.blacklist.PaginationWithBlockedUserFiltersDto;
import ru.hits.messengerapi.friends.dto.blacklist.SearchedBlockedUsersDto;
import ru.hits.messengerapi.friends.dto.common.PaginationDto;
import ru.hits.messengerapi.friends.dto.common.AddPersonDto;
import ru.hits.messengerapi.friends.service.implementation.BlacklistService;

import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;

/**
 * Контроллер для работы с черным списком пользователей.
 */
@RestController
@RequestMapping("/api/blacklist")
@RequiredArgsConstructor
public class BlacklistController {

    /**
     * Сервис для работы с черным списком пользователей.
     */
    private final BlacklistService blacklistService;


    /**
     * Метод для получения списка пользователей в черном списке.
     *
     * @param paginationDto объект класса {@link PaginationDto}, содержащий информацию о постраничном выводе данных.
     * @return список пользователей в черном списке с информацией о пагинации и фильтре.
     */
    @PostMapping
    public ResponseEntity<BlockedUsersPageListDto> getBlockedUsers(@RequestBody @Valid PaginationDto paginationDto) {
        return new ResponseEntity<>(blacklistService.getBlockedUsers(paginationDto), HttpStatus.OK);
    }

    /**
     * Метод для получения информации о пользователе из черного списка.
     *
     * @param id идентификатор пользователя из черного списка.
     * @return информация о пользователе из черного списка.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BlockedUserDto> getBlockedUser(@PathVariable("id") UUID id) {
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
    public ResponseEntity<BlockedUserDto> addToFriends(@RequestBody @Valid AddPersonDto addPersonDto) {
        return new ResponseEntity<>(blacklistService.addToBlacklist(addPersonDto), HttpStatus.OK);
    }

    /**
     * Метод для синхронизации информации о пользователе из черного списка.
     *
     * @param id идентификатор пользователя в черном списке.
     * @return сообщение об успешной синхронизации данных.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, String>> syncBlockedUserData(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(blacklistService.syncBlockedUserData(id), HttpStatus.OK);
    }

    /**
     * Метод для удаления пользователя из черного списка.
     *
     * @param id идентификатор пользователя в черном списке.
     * @return информация об удаленном из черного списка пользователе.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<BlockedUserDto> deleteFromBlacklist(@PathVariable("id") UUID id) {
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
        return new ResponseEntity<>(blacklistService.searchBlockedUsers(paginationAndFilters), HttpStatus.OK);
    }

    /**
     * Метод для проверки нахождения пользователя в черном списке.
     *
     * @param id идентификатор пользователя, которого нужно проверить.
     * @return сообщение о нахождении пользователя в черном списке или его отсутствии.
     */
    @GetMapping("/{id}/check")
    public ResponseEntity<Map<String, String>> checkIfTheUserBlacklisted(@PathVariable("id") UUID id) {
        boolean check = blacklistService.checkIfTheUserBlacklisted(id);
        if (check) {
            return new ResponseEntity<>(
                    Map.of("message", "Пользователь находится в черном списке."), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(
                    Map.of("message", "Пользователь не находится в черном списке."), HttpStatus.OK);
        }

    }


}
