package ru.hits.messengerapi.friends.service;

import ru.hits.messengerapi.friends.dto.common.AddPersonDto;
import ru.hits.messengerapi.friends.dto.common.PaginationWithFullNameFilterDto;
import ru.hits.messengerapi.friends.dto.friends.*;

import java.util.Map;
import java.util.UUID;

/**
 *  Интерфейс для сервиса друзей.
 */
public interface FriendsServiceInterface {

    /**
     * Получить друзей целевого пользователя.
     *
     * @param paginationWithFullNameFilterDto информация о пагинации и фильтре по ФИО.
     * @return список друзей, информация о странице и фильтре по ФИО.
     */
    FriendsPageListDto getFriends(PaginationWithFullNameFilterDto paginationWithFullNameFilterDto);

    /**
     * Получить информацию о друге.
     *
     * @param addedUserId id друга.
     * @return полная информация о друге.
     */
    FriendDto getFriend(UUID addedUserId);

    /**
     * Добавить пользователя в друзья.
     *
     * @param addPersonDto информация необходимая для добавления в друзья.
     * @return полная информация о друге.
     */
    FriendDto addToFriends(AddPersonDto addPersonDto);

    /**
     * Удалить пользователя из друзей.
     *
     * @param addedUserId id друга.
     * @return полная информация об удаленном друге.
     */
    FriendDto deleteFriend(UUID addedUserId);

    /**
     * Поиск среди друзей.
     *
     * @param paginationAndFilters информация о пагинации и фильтрах.
     * @return найденные друзья с информацией о странице и фильтрах.
     */
    SearchedFriendsDto searchFriends(PaginationWithFriendFiltersDto paginationAndFilters);

}
