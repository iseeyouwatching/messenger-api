package ru.hits.messengerapi.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hits.messengerapi.user.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностью {@link UserEntity}.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    /**
     * Метод, который находит пользователя по его логину.
     *
     * @param login логин пользователя.
     * @return найденный пользователь.
     */
    Optional<UserEntity> findByLogin(String login);

    /**
     * Метод, который находит пользователя по его почте.
     *
     * @param email почта пользователя.
     * @return найденный пользователь.
     */
    Optional<UserEntity> findByEmail(String email);
}
