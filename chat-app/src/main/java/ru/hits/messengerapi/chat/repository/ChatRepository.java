package ru.hits.messengerapi.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hits.messengerapi.chat.entity.ChatEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностью {@link ChatEntity}
 */
@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, UUID> {

    /**
     * Найти чат с указанным отправителем и получателем.
     *
     * @param senderId идентификатор отправителя.
     * @param receiverId идентификатор получателя.
     * @return найденный чат или null, если такой не найден.
     */
    Optional<ChatEntity> findBySenderIdAndReceiverId(UUID senderId, UUID receiverId);

}
