package ru.hits.messengerapi.friends.service;

import ru.hits.messengerapi.friends.dto.common.AddPersonDto;

import java.util.Map;
import java.util.UUID;

/**
 * Интерфейс для сервиса для интеграционных запросов.
 */
public interface IntegrationRequestsServiceInterface {

    /**
     * Метод для проверки существования пользователя по ФИО и ID.
     *
     * @param addPersonDto информация проверяемого пользователя.
     */
    void checkUserExistence(AddPersonDto addPersonDto);

    /**
     * Метод для получения ФИО пользователя по его ID.
     *
     * @param id идентификатор пользователя.
     * @return ФИО пользователя.
     */
    String getFullName(UUID id);

    /**
     * Синхронизация данных заблокированного пользователя.
     *
     * @param id идентификатор пользователя.
     * @return сообщение об успешной синхронизации.
     */
    Map<String, String> syncBlockedUserData(UUID id);

    /**
     * Синхронизация данных друга.
     *
     * @param id идентификатор пользователя.
     * @return сообщение об успешной синхронизации.
     */
    Map<String, String> syncFriendData(UUID id);

}
