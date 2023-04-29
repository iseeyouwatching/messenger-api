package ru.hits.messengerapi.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hits.messengerapi.chat.entity.ChatEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, UUID> {

    Optional<ChatEntity> findBySenderIdAndReceiverId(UUID senderId, UUID receiverId);

}
