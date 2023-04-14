package ru.hits.messengerapi.friends.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import ru.hits.messengerapi.friends.dto.common.AddPersonDto;
import ru.hits.messengerapi.friends.dto.common.PaginationDto;
import ru.hits.messengerapi.friends.dto.friends.*;
import ru.hits.messengerapi.friends.service.implementation.FriendsService;

import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;

/**
 * Контроллер для работы с друзьями пользователя.
 */
@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendsController {

    /**
     * Сервис для работы с друзьями пользователя.
     */
    private final FriendsService friendsService;

    /**
     * Метод для получения списка друзей пользователя.
     *
     * @param paginationDto объект класса {@link PaginationDto} с данными для пагинации.
     * @return список друзей с информацией о странице и фильтре.
     */
    @PostMapping
    public ResponseEntity<FriendsPageListDto> getFriends(@RequestBody @Valid PaginationDto paginationDto) {
        return new ResponseEntity<>(friendsService.getFriends(paginationDto), HttpStatus.OK);
    }

    /**
     * Метод для получения информации о конкретном друге по его идентификатору.
     *
     * @param id идентификатор друга.
     * @return данные друга.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FriendDto> getFriend(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(friendsService.getFriend(id), HttpStatus.OK);
    }

    /**
     * Метод для добавления пользователя по его идентификатору в список друзей.
     *
     * @param addPersonDto объект класса {@link AddPersonDto},
     *                     содержащий информацию о пользователе, которого необходимо добавить в друзья.
     * @return данные добавленного друга.
     */
    @PostMapping("/add")
    public ResponseEntity<FriendDto> addToFriends(@RequestBody @Valid AddPersonDto addPersonDto) {
        return new ResponseEntity<>(friendsService.addToFriends(addPersonDto), HttpStatus.OK);
    }

    /**
     * Метод для синхронизации информации о пользователе, находящегося в друзьях.
     *
     * @param id идентификатор пользователя, находящегося в друзьях.
     * @return сообщение об успешной синхронизации данных.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, String>> syncFriendData(@PathVariable("id") UUID id) {
       return new ResponseEntity<>(friendsService.syncFriendData(id), HttpStatus.OK);
    }

    /**
     * Метод для удаления друга из списка друзей по его идентификатору.
     *
     * @param id идентификатор друга.
     * @return данные удаленного друга.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<FriendDto> deleteFriend(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(friendsService.deleteFriend(id), HttpStatus.OK);
    }

    /**
     * Метод для поиска друзей пользователя.
     *
     * @param paginationAndFilters объект класса {@link PaginationWithFriendFiltersDto}
     *                             с данными для пагинации и фильтрации.
     * @return найденные друзья с информацией о пагинации и фильтрах.
     */
    @PostMapping("/search")
    public ResponseEntity<SearchedFriendsDto> searchFriends(
            @RequestBody @Valid PaginationWithFriendFiltersDto paginationAndFilters) {
        return new ResponseEntity<>(friendsService.searchFriends(paginationAndFilters), HttpStatus.OK);
    }

}
