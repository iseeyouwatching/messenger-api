package ru.hits.messengerapi.user.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.messengerapi.user.entity.UserEntity;
import ru.hits.messengerapi.user.repository.UserRepository;
import ru.hits.messengerapi.user.service.IntegrationUserServiceInterface;

import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для интеграционных запросов, которые прилетают в сервис пользователя.
 */
@Service
@RequiredArgsConstructor
public class IntegrationUserService implements IntegrationUserServiceInterface {

    /**
     * Репозиторий пользователя.
     */
    private final UserRepository userRepository;

    /**
     * Метод для проверки существования пользователя по его ID и ФИО.
     *
     * @param id идентификатор пользователя.
     * @param fullName ФИО пользователя.
     * @return exist - если пользователь существует, dont exist - если пользователя не существует.
     */
    @Override
    public Boolean checkUserByIdAndFullName(UUID id, String fullName) {
        return userRepository.findByIdAndFullName(id, fullName).isPresent();
    }

    /**
     * Метод для получения ФИО пользователя по его ID.
     *
     * @param id идентификатор пользователя.
     * @return ФИО пользователя.
     */
    @Override
    public String getFullName(UUID id) {
        Optional<UserEntity> user = userRepository.findById(id);

        if (user.isEmpty()) {
            return "dont exist";
        }

        return user.get().getFullName();
    }
}
