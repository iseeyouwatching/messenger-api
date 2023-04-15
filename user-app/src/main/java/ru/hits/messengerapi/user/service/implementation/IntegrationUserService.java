package ru.hits.messengerapi.user.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        log.debug("Проверка пользователя с id {} и полным именем {}", id, fullName);
        boolean isUserPresent = userRepository.findByIdAndFullName(id, fullName).isPresent();
        if (isUserPresent) {
            log.debug("Пользователь с id {} и полным именем {} найден", id, fullName);
        } else {
            log.debug("Пользователь с id {} и полным именем {} не найден", id, fullName);
        }
        return isUserPresent;
    }

    /**
     * Метод для получения ФИО пользователя по его ID.
     *
     * @param id идентификатор пользователя.
     * @return ФИО пользователя.
     */
    @Override
    public String getFullName(UUID id) {
        log.info("Запрос на получение полного имени пользователя с ID {}", id);

        Optional<UserEntity> user = userRepository.findById(id);

        if (user.isEmpty()) {
            log.warn("Пользователь с ID {} не найден", id);
            return "dont exist";
        }

        String fullName = user.get().getFullName();
        log.info("Полное имя пользователя с ID {} успешно получено: {}", id, fullName);
        return fullName;
    }
}
