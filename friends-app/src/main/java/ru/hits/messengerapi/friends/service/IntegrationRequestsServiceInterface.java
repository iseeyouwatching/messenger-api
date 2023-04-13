package ru.hits.messengerapi.friends.service;

import ru.hits.messengerapi.friends.dto.AddPersonDto;

import java.util.UUID;

public interface IntegrationRequestsServiceInterface {

    void checkUserExistence(AddPersonDto addPersonDto);

    String getFullName(UUID id);

}
