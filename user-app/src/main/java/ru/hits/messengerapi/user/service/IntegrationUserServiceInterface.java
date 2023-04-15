package ru.hits.messengerapi.user.service;

import java.util.UUID;

/**
 * Интерфейс сервиса для интеграционных запросов, которые прилетают в сервис пользователя.
 */
public interface IntegrationUserServiceInterface {

    /**
     * Метод для проверки существования пользователя по его ID и ФИО.
     *
     * @param id идентификатор пользователя.
     * @param fullName ФИО пользователя.
     * @return true - если пользователь существует, false - если пользователя не существует.
     */
    Boolean checkUserByIdAndFullName(UUID id, String fullName);

    /**
     * Метод для получения ФИО пользователя по его ID.
     *
     * @param id идентификатор пользователя.
     * @return ФИО пользователя.
     */
    String getFullName(UUID id);
}
