package ru.hits.messengerapi.user.service;

import java.util.UUID;

public interface IntegrationUserServiceInterface {

    String checkUserByIdAndFullName(UUID id, String fullName);

    String getFullName(UUID id);
}
