package ru.hits.messengerapi.chat.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hits.messengerapi.chat.entity.ChatUserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностью {@link ChatUserEntity}.
 */
@Repository
public interface ChatUserRepository extends JpaRepository<ChatUserEntity, UUID> {

    /**
     * Удаляет всех пользователей, кроме тех, чьи идентификаторы находятся в списке пользователей,
     * из чата с указанным идентификатором.
     *
     * @param chatId идентификатор чата, из которого нужно удалить пользователей.
     * @param users список идентификаторов пользователей, которых не нужно удалять из чата.
     */
    void deleteAllByChatIdAndUserIdNotIn(UUID chatId, List<UUID> users);

    /**
     * Находит всех пользователей с указанным идентификатором пользователя
     * и возвращает их в указанной странице.
     *
     * @param userId идентификатор пользователя, для которого нужно найти все записи в базе данных.
     * @param pageable страница, на которой нужно получить результаты.
     * @return список объектов класса {@link ChatUserEntity}, удовлетворяющих критерию поиска.
     */
    List<ChatUserEntity> findAllByUserId(UUID userId, Pageable pageable);

    /**
     * Находит и возвращает объект класса {@link ChatUserEntity}, соответствующий заданным идентификаторам чата
     * и пользователя.
     *
     * @param chatId идентификатор чата.
     * @param userId идентификатор пользователя.
     * @return объект класса {@link ChatUserEntity}, если он существует в базе данных,
     * или пустое значение {@link Optional}, если его нет.
     */
    Optional<ChatUserEntity> findByChatIdAndUserId(UUID chatId, UUID userId);

}
