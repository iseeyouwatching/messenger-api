package ru.hits.messengerapi.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.hits.messengerapi.chat.entity.ChatEntity;
import ru.hits.messengerapi.chat.entity.MessageEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {

    List<MessageEntity> findAllByChatId(UUID chatId);

    List<MessageEntity> findAllBySenderId(UUID senderId);

    @Query("select m from MessageEntity m where m.chat = :chat order by m.sendDate desc")
    MessageEntity findFirstByOrderBySendDateDesc(ChatEntity chat);

    @Query("SELECT DISTINCT m FROM MessageEntity m " +
            "LEFT JOIN FETCH m.attachments a " +
            "JOIN m.chat c " +
            "JOIN ChatUserEntity cu ON  c.id = cu.chatId " +
            "WHERE (lower(m.messageText) like %:searchString% OR lower(a.fileName) like %:searchString%) " +
            "AND cu.userId = :authenticatedUserId ORDER BY m.sendDate DESC")
    List<MessageEntity> searchMessages(String searchString, UUID authenticatedUserId);

    @Query("SELECT DISTINCT m FROM MessageEntity m " +
            "LEFT JOIN FETCH m.attachments a " +
            "JOIN m.chat c " +
            "JOIN ChatUserEntity cu ON  c.id = cu.chatId " +
            "AND cu.userId = :authenticatedUserId ORDER BY m.sendDate DESC")
    List<MessageEntity> searchMessagesWithoutFilter(UUID authenticatedUserId);

}
