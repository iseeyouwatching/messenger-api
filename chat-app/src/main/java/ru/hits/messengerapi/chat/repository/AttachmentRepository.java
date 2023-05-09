package ru.hits.messengerapi.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hits.messengerapi.chat.entity.AttachmentEntity;
import ru.hits.messengerapi.chat.entity.ChatUserEntity;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностью {@link AttachmentEntity}.
 */
@Repository
public interface AttachmentRepository extends JpaRepository<AttachmentEntity, UUID> {

    /**
     * Находит все вложения сообщения по его идентификатору.
     *
     * @param messageId идентификатор сообщения.
     * @return список вложений сообщения.
     */
    List<AttachmentEntity> findAllByMessageId(UUID messageId);
}
