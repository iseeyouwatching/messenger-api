package ru.hits.messengerapi.chat.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hits.messengerapi.chat.entity.ChatUserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatUserRepository extends JpaRepository<ChatUserEntity, UUID> {

    void deleteAllByChatIdAndUserIdNotIn(UUID chatId, List<UUID> users);

    List<ChatUserEntity> findByUserId(UUID userId, Pageable pageable);

    Optional<ChatUserEntity> findByChatIdAndUserId(UUID chatId, UUID userId);

}
