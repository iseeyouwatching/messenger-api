package ru.hits.messengerapi.user.service;

import java.util.UUID;

/**
 * Интерфейс для сервиса для интеграционных запросов.
 */
public interface IntegrationRequestsServiceInterface {

    /**
     * Метод для проверки нахождения пользователя в черном списке другого пользователя.
     *
     * @param targetUserId ID целевого пользователя.
     * @param blockedUserId ID заблокированного пользователя.
     * @return true - пользователь находится в черном списке другого пользователя, false - не находится.
     */
    Boolean checkExistenceInBlacklist(UUID targetUserId, UUID blockedUserId);

}
