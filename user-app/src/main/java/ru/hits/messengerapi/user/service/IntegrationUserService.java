package ru.hits.messengerapi.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hits.messengerapi.common.exception.NotFoundException;
import ru.hits.messengerapi.user.entity.UserEntity;
import ru.hits.messengerapi.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для интеграционных запросов, которые прилетают в сервис пользователя.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IntegrationUserService {

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
    public Boolean checkUserByIdAndFullName(UUID id, String fullName) {
        log.debug("Проверка пользователя с id {} и полным именем {}", id, fullName);
        boolean isUserPresent = userRepository.findByIdAndFullName(id, fullName).isPresent();
        if (isUserPresent) {
            log.debug("Пользователь с id {} и полным именем {} найден", id, fullName);
        } else {
            log.debug("Пользователь с id {} и полным именем {} не найден", id, fullName);
            throw new NotFoundException("Пользователь с ID " + id + " и ФИО " + fullName + " не найден.");
        }
        return true;
    }

    /**
     * Метод для проверки существования пользователя по его ID.
     *
     * @param id идентификатор пользователя.
     * @return true - если пользователь существует.
     */
    public Boolean checkUserById(UUID id) {
        boolean isUserPresent = userRepository.findById(id).isPresent();
        if (isUserPresent) {
            log.debug("Пользователь с id {} найден", id);
        } else {
            log.debug("Пользователь с id {} не найден", id);
            throw new NotFoundException("Пользователь с ID " + id + " не найден.");
        }
        return true;
    }

    /**
     * Метод для получения ФИО пользователя по его ID.
     *
     * @param id идентификатор пользователя.
     * @return ФИО пользователя.
     */
    public String getFullName(UUID id) {
        log.info("Запрос на получение полного имени пользователя с ID {}", id);

        Optional<UserEntity> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден.");
        }

        String fullName = user.get().getFullName();
        log.info("Полное имя пользователя с ID {} успешно получено: {}", id, fullName);
        return fullName;
    }

    /**
     * Метод для получения ФИО и аватарки пользователя.
     *
     * @param id идентификатор пользователя.
     * @return ФИО и аватарка пользователя.
     */
    public List<String> getFullNameAndAvatar(UUID id) {

        Optional<UserEntity> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден.");
        }

        List<String> result = new ArrayList<>();
        result.add(user.get().getFullName());
        if (user.get().getAvatar() != null) {
            result.add(String.valueOf(user.get().getAvatar()));
        }
        else {
            result.add(null);
        }

        log.info("Получены полное имя и аватар пользователя с ID {}: {}", id, result);
        return result;
    }

}
