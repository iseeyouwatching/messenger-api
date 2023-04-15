package ru.hits.messengerapi.friends.service;

import ru.hits.messengerapi.friends.dto.blacklist.PaginationWithBlockedUserFiltersDto;
import ru.hits.messengerapi.friends.dto.blacklist.SearchedBlockedUsersDto;
import ru.hits.messengerapi.friends.dto.common.PaginationWithFullNameFilterDto;
import ru.hits.messengerapi.friends.dto.blacklist.BlockedUserDto;
import ru.hits.messengerapi.friends.dto.blacklist.BlockedUsersPageListDto;
import ru.hits.messengerapi.friends.dto.common.AddPersonDto;

import java.util.Map;
import java.util.UUID;

/**
 *  Интерфейс для сервиса черного списка.
 */
public interface BlacklistServiceInterface {

    /**
     * Получить заблокированных пользователей с информацией о странице и фильтре по ФИО для целевого пользователя.
     *
     * @param paginationWithFullNameFilterDto объект {@link PaginationWithFullNameFilterDto}, содержащий информацию о странице и фильтре по ФИО.
     * @return объект {@link BlockedUsersPageListDto} содержащий список
     * заблокированных пользователей, информацию о странице и фильтре по ФИО.
     */
    BlockedUsersPageListDto getBlockedUsers(PaginationWithFullNameFilterDto paginationWithFullNameFilterDto);

    /**
     * Получить информацию о заблокированном пользователе.
     *
     * @param blockedUserId id заблокированного пользователя.
     * @return полная информация о заблокированном пользователе.
     */
    BlockedUserDto getBlockedUser(UUID blockedUserId);

    /**
     * Добавить пользователя в черный список.
     *
     * @param addPersonDto DTO, содержащая информацию о добавляемом в черный список пользователе
     * @return полная информация о заблокированном пользователе
     */
    BlockedUserDto addToBlacklist(AddPersonDto addPersonDto);

    /**
     * Синхронизация данных заблокированного пользователя.
     *
     * @param id идентификатор заблокированного пользователя.
     * @return сообщение об успешной синхронизации.
     */
    Map<String, String> syncBlockedUserData(UUID id);

    /**
     * Удалить пользователя из черного списка.
     *
     * @param blockedUserId id заблокированного пользователя.
     * @return полная информация о заблокированном пользователе.
     */
    BlockedUserDto deleteFromBlacklist(UUID blockedUserId);

    /**
     * Поиск среди заблокированных пользователей.
     *
     * @param paginationAndFilters информация о пагинации и фильтрах.
     * @return найденные заблокированные пользователи с информацией о странице и фильтрах.
     */
    SearchedBlockedUsersDto searchBlockedUsers(PaginationWithBlockedUserFiltersDto paginationAndFilters);

    /**
     * Проверка нахождения пользователя в черном списке.
     *
     * @param blockedUserId id заблокированного пользователя
     * @return булевая переменная, которая показывает находится пользователь в черном списке или нет.
     */
    boolean checkIfTheUserBlacklisted(UUID blockedUserId);

    /**
     * Проверка нахождения целевого пользователя в черном списке у добавляемого в друзья пользователе.
     *
     * @param targetUserId id целевого пользователя.
     * @param blockedUserId id заблокированного пользователя.
     * @return булевая переменная, которая показывает находится пользователь в черном списке или нет.
     */
    boolean checkIfTheTargetUserBlacklisted(UUID targetUserId, UUID blockedUserId);

}
