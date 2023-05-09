package ru.hits.messengerapi.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.hits.messengerapi.chat.entity.ChatEntity;
import ru.hits.messengerapi.chat.entity.MessageEntity;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностью {@link MessageEntity}.
 */
@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {

    /**
     * Возвращает список всех сообщений, относящихся к заданному чату.
     *
     * @param chatId ID чата.
     * @return список сообщений.
     */
    List<MessageEntity> findAllByChatId(UUID chatId);

    /**
     * Возвращает список всех сообщений, отправленных заданным отправителем.
     *
     * @param senderId ID отправителя
     * @return список сообщений
     */
    List<MessageEntity> findAllBySenderId(UUID senderId);

    /**
     * Возвращает последнее отправленное сообщение в заданном чате.
     *
     * @param chat чат, для которого необходимо найти последнее сообщение.
     * @return последнее отправленное сообщение.
     */
    MessageEntity getFirstByChatOrderBySendDateDesc(ChatEntity chat);

    /**
     * Поиск сообщений, содержащих заданную строку поиска в тексте сообщения или имени прикрепленного файла.
     *
     * @param searchString строка поиска.
     * @param authenticatedUserId ID пользователя, который выполняет поиск.
     * @return список найденных сообщений.
     */
    @Query("SELECT DISTINCT m FROM MessageEntity m " +
            "LEFT JOIN FETCH m.attachments a " +
            "JOIN m.chat c " +
            "JOIN ChatUserEntity cu ON  c.id = cu.chatId " +
            "WHERE (lower(m.messageText) like %:searchString% OR lower(a.fileName) like %:searchString%) " +
            "AND cu.userId = :authenticatedUserId ORDER BY m.sendDate DESC")
    List<MessageEntity> searchMessages(String searchString, UUID authenticatedUserId);

    /**
     * Поиск всех сообщений для заданного пользователя без фильтрации по строке поиска.
     *
     * @param authenticatedUserId ID пользователя, для которого необходимо найти сообщения
     * @return список всех сообщений для заданного пользователя
     */
    @Query("SELECT DISTINCT m FROM MessageEntity m " +
            "LEFT JOIN FETCH m.attachments a " +
            "JOIN m.chat c " +
            "JOIN ChatUserEntity cu ON  c.id = cu.chatId " +
            "AND cu.userId = :authenticatedUserId ORDER BY m.sendDate DESC")
    List<MessageEntity> searchMessagesWithoutFilter(UUID authenticatedUserId);

}
