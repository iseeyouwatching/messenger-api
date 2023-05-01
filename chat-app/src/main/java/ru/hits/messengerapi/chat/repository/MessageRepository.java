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
    List<MessageEntity> findLastMessage(ChatEntity chat);

}
