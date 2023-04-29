package ru.hits.messengerapi.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hits.messengerapi.chat.entity.ChatUserEntity;

import java.util.UUID;

@Repository
public interface ChatUserRepository extends JpaRepository<ChatUserEntity, UUID> {

    void deleteAllByChatId(UUID chatId);

}
