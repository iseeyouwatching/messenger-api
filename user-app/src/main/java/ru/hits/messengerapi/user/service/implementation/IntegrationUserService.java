package ru.hits.messengerapi.user.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.user.entity.UserEntity;
import ru.hits.messengerapi.user.repository.UserRepository;
import ru.hits.messengerapi.user.service.IntegrationUserServiceInterface;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IntegrationUserService implements IntegrationUserServiceInterface {

    private final UserRepository userRepository;

    @Override
    public String checkUserByIdAndFullName(String id, String fullName) {
        Optional<UserEntity> user = userRepository.findByIdAndFullName(UUID.fromString(id), fullName);

        if (user.isEmpty()) {
            throw new NotFoundException("Пользователя с id " + id + " и ФИО " + fullName + " не существует.");
        }

        return "exist";
    }
}
